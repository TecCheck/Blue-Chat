package io.github.teccheck.bluechat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class ChatClient extends Thread {

    private final String address;
    private final ChatListener listener;

    private final ArrayList<Message> messageQue = new ArrayList<>();
    private RemoteChatClient client;

    private boolean run = true;

    public ChatClient(String address, ChatListener listener) {
        this.address = address;
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(Constants.SERVER_UUID);
            socket.connect();
            client = new RemoteChatClient(socket, "Name");

            while (run) {
                try {
                    if (client.hasMessage()) {
                        Message message = client.getMessage();
                        Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
                        mainThreadHandler.post(() -> listener.onMessageReceived(message));
                    }

                    if(!messageQue.isEmpty()){
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
    }

    public void send(Message message) {
        messageQue.add(message);
    }

    public void exit() throws IOException {
        run = false;
        client.close();
    }
}
