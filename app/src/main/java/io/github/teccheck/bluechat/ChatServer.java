package io.github.teccheck.bluechat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class ChatServer extends Thread {

    private final String roomName;
    private final ChatListener listener;

    private final ArrayList<Message> messageQue = new ArrayList<>();
    private final ArrayList<RemoteChatClient> clients = new ArrayList<>();

    AcceptThread acceptThread;
    private boolean run = true;

    public ChatServer(String roomName, ChatListener listener) {
        this.roomName = roomName;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            BluetoothServerSocket socket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(MainActivity.SERVER_NAME, MainActivity.SERVER_UUID);
            acceptThread = new AcceptThread(socket);
            acceptThread.start();

            while (run) {
                try {
                    for (RemoteChatClient client : clients) {
                        if (client.hasMessage()) {
                            // Receive message
                            Message message = client.getMessage();
                            // Notify UI
                            Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
                            mainThreadHandler.post(() -> listener.onMessageReceived(message));
                            // Redistribute Message
                            for (RemoteChatClient client1 : clients)
                                if (client != client1)
                                    client1.send(message);
                        }
                    }

                    if (!messageQue.isEmpty()) {
                        for (RemoteChatClient client : clients)
                            client.send(messageQue.get(0));
                        messageQue.remove(0);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            acceptThread.exit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Message message) {
        messageQue.add(message);
    }

    public void exit() throws IOException {
        run = false;
        for (RemoteChatClient client : clients)
            client.close();
    }

    private class AcceptThread extends Thread {

        private BluetoothServerSocket serverSocket;
        private boolean run = true;

        private AcceptThread(BluetoothServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            while (run) {
                try {
                    BluetoothSocket socket = serverSocket.accept();
                    if (socket != null) {
                        RemoteChatClient client = new RemoteChatClient(socket, "Name");
                        // TODO: Send Room information and older messages
                        clients.add(client);
                        Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
                        mainThreadHandler.post(() -> listener.onDeviceConnected(client.getName(), client.getAddress()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void exit() throws IOException {
            run = false;
            serverSocket.close();
        }
    }
}
