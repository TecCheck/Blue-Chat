package io.github.teccheck.bluechat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BT = 0xABCD;
    public static final int CHAT_MODE_CLIENT = 8;
    public static final int CHAT_MODE_SERVER = 0;
    public static final String EXTRA_CHAT_MODE = "io.github.teccheck.bluechat.extra_chat_mode";
    public static final String EXTRA_ROOM_NAME = "io.github.teccheck.bluechat.extra_room_name";
    public static final String EXTRA_BLUETOOTH_ADDRESS = "io.github.teccheck.bluechat.extra_bluetooth_address";
    public static final String SERVER_NAME = "BlueChat";
    public static final UUID SERVER_UUID = UUID.fromString("77d3f8ec-35b8-44f9-be93-94365f520844");

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevicesListAdapter recyclerAdapter;

    RecyclerView recyclerView;
    LinearLayout newRoomLayout;
    TextView deviceNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        newRoomLayout = findViewById(R.id.layout_new_room);
        recyclerView = findViewById(R.id.list_devices);
        deviceNameText = findViewById(R.id.text_device_name);

        // Check Bluetooth availability
        if (bluetoothAdapter == null) {
            // Display a dialog that says that the device has no bluetooth functionality
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle(R.string.dialog_no_bluetooth_title);
            builder.setMessage(R.string.dialog_no_bluetooth_message);
            builder.setCancelable(true);
            builder.setNeutralButton(R.string.ok, (d, w) -> finish());
            builder.setOnCancelListener(dialog -> finish());

            builder.create().show();
            return;
        }

        // Check if Bluetooth is on
        if (!bluetoothAdapter.isEnabled()) {
            // Display a dialog that asks the user to enable Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }

        // If Bluetooth is available and already on
        onBluetoothEnabled();
    }

    @Override
    protected void onDestroy() {
        Log.d(getLocalClassName(), "onDestroy()");

        unregisterReceiver(receiver);
        if (bluetoothAdapter.isDiscovering())
            bluetoothAdapter.cancelDiscovery();

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d(getLocalClassName(), "onPause()");

        unregisterReceiver(receiver);
        if (bluetoothAdapter.isDiscovering())
            bluetoothAdapter.cancelDiscovery();

        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(getLocalClassName(), "onResume()");

        registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        if (bluetoothAdapter.isDiscovering())
            bluetoothAdapter.cancelDiscovery();
        bluetoothAdapter.startDiscovery();

        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK)
            onBluetoothEnabled();
    }

    private void onBluetoothEnabled() {
        newRoomLayout.setOnClickListener((v) -> newRoom());
        recyclerAdapter = new BluetoothDevicesListAdapter(bluetoothAdapter.getBondedDevices(), this::onItemClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);
        deviceNameText.setText(bluetoothAdapter.getName());

        if (bluetoothAdapter.isDiscovering())
            bluetoothAdapter.cancelDiscovery();
        bluetoothAdapter.startDiscovery();

        registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        Log.d(getLocalClassName(), bluetoothAdapter.isDiscovering() ? "Discovering ..." : "Discovery failed");
    }

    // For finding nearby Bluetooth devices
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                recyclerAdapter.addDevice(device);
            }
        }
    };

    // If the user taps on new room
    private void newRoom() {
        View view = getLayoutInflater().inflate(R.layout.edit_text_new_room, null);
        TextInputEditText editText = view.findViewById(R.id.edit_text_room_name);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.dialog_room_name_title);
        builder.setView(view);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra(EXTRA_CHAT_MODE, CHAT_MODE_SERVER);
            intent.putExtra(EXTRA_ROOM_NAME, editText.getText().toString());
            startActivity(intent);
        });

        builder.create().show();
    }

    // If the user taps on a device
    public void onItemClick(int position, Object value) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(EXTRA_CHAT_MODE, CHAT_MODE_CLIENT);
        intent.putExtra(EXTRA_BLUETOOTH_ADDRESS, ((BluetoothDevice) value).getAddress());
        startActivity(intent);
    }
}