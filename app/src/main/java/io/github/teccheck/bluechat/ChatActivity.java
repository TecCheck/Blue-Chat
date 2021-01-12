package io.github.teccheck.bluechat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        int chatMode = intent.getIntExtra(MainActivity.EXTRA_CHAT_MODE, -1);

        if (chatMode == MainActivity.CHAT_MODE_CLIENT)
            setupClient(intent.getStringExtra(MainActivity.EXTRA_BLUETOOTH_ADDRESS));
        else if (chatMode == MainActivity.CHAT_MODE_SERVER)
            setupServer(intent.getStringExtra(MainActivity.EXTRA_ROOM_NAME));
    }

    private void setupClient(String address) {
        Log.d(getLocalClassName(), String.format("Started as Client. Server address is %s", address));
        // TODO
    }

    private void setupServer(String roomName) {
        Log.d(getLocalClassName(), String.format("Started as Server. Room name is %s", roomName));
        // TODO
    }
}