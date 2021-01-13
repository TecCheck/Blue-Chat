package io.github.teccheck.bluechat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

public class ChatActivity extends AppCompatActivity implements ChatListener {

    RecyclerView recyclerView;
    ChatMessagesListAdapter adapter;
    ChatClient client;
    int chatMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recycler_view);
        adapter = new ChatMessagesListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

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
        // TODO
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