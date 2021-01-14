package io.github.teccheck.bluechat;

import android.bluetooth.BluetoothSocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class RemoteChatClient {

    private BluetoothSocket socket;
    private InputStream input;
    private OutputStream output;
    private String name;

    public RemoteChatClient(BluetoothSocket socket, String name) throws IOException {
        this.socket = socket;
        this.name = name;
        input = socket.getInputStream();
        output = socket.getOutputStream();
    }

    public String getName() {
        return name;
    }

    public boolean hasMessage() throws IOException {
        return input.available() != 0;
    }

    public Message getMessage() throws IOException, JSONException {
        byte[] buffer;
        // Get the length of the message
        input.read(buffer = new byte[4], 0, 4);
        int len = ByteBuffer.wrap(buffer).getInt();

        // Get the message text
        input.read(buffer = new byte[len], 0, len);
        String json = new String(buffer);

        return Message.fromJson(new JSONObject(json));
    }

    public void send(Message message) throws IOException, JSONException {
        String json = message.getJson().toString();

        byte[] bytes = json.getBytes();
        byte[] len = ByteBuffer.allocate(4).putInt(bytes.length).array();

        output.write(len);
        output.write(bytes);
    }

    public String getAddress() {
        return socket.getRemoteDevice().getAddress();
    }

    public void close() throws IOException {
        socket.close();
    }
}
