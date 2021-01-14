package io.github.teccheck.bluechat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;

public class ChatActivity extends AppCompatActivity implements ChatListener, View.OnClickListener {

    RecyclerView recyclerView;
    ChatMessagesListAdapter adapter;

    ImageButton sendButton;
    TextInputEditText messageText;

    ChatClient client;
    ChatServer server;
    int chatMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recycler_view);
        adapter = new ChatMessagesListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        messageText = findViewById(R.id.text_input);
        sendButton = findViewById(R.id.button_send);
        sendButton.setOnClickListener(this);

        Intent intent = getIntent();
        chatMode = intent.getIntExtra(MainActivity.EXTRA_CHAT_MODE, -1);

        if (chatMode == MainActivity.CHAT_MODE_CLIENT)
            setupClient(intent.getStringExtra(MainActivity.EXTRA_BLUETOOTH_ADDRESS));
        else if (chatMode == MainActivity.CHAT_MODE_SERVER)
            setupServer(intent.getStringExtra(MainActivity.EXTRA_ROOM_NAME));
    }

    @Override
    protected void onDestroy() {
        try {
            if (client != null)
                client.exit();
            if(server != null)
                server.exit();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    private void setupClient(String address) {
        Log.d(getLocalClassName(), String.format("Started as Client. Server address is %s", address));
        client = new ChatClient(address, this);
        client.start();
    }

    private void setupServer(String roomName) {
        Log.d(getLocalClassName(), String.format("Started as Server. Room name is %s", roomName));
        server = new ChatServer(roomName, this);
        server.start();
    }

    @Override
    public void onClick(View v) {
        Message message = new Message("Me", messageText.getText().toString(), System.currentTimeMillis());
        if (chatMode == MainActivity.CHAT_MODE_CLIENT)
            client.send(message);
        else if (chatMode == MainActivity.CHAT_MODE_SERVER)
            server.send(message);
    }

    @Override
    public void onMessageReceived(Message message) {
        adapter.addMessage(message);
    }

    @Override
    public void onDeviceConnected(String name, String address) {
    }

    @Override
    public void onDeviceDisconnected(String name, String address) {
    }

    @Override
    public void onError(String message) {
    }
}