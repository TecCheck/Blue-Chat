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

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BT = 0xABCD;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevicesListAdapter recyclerAdapter;

    RecyclerView recyclerView;
    TextView deviceNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        recyclerView = findViewById(R.id.list_devices);
        deviceNameText = findViewById(R.id.text_device_name);

        registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

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
        super.onDestroy();
        unregisterReceiver(receiver);
        Log.d(getLocalClassName(), "onDestroy()");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK)
            onBluetoothEnabled();
    }

    private void onBluetoothEnabled() {
        recyclerAdapter = new BluetoothDevicesListAdapter(bluetoothAdapter.getBondedDevices(), this::onItemClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recyclerAdapter);
        deviceNameText.setText(bluetoothAdapter.getName());

        if (bluetoothAdapter.isDiscovering())
            bluetoothAdapter.cancelDiscovery();
        bluetoothAdapter.startDiscovery();
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

    // If the user taps on a device
    public void onItemClick(int position, Object value) {
        // TODO: Start Chat with client parameters
        Log.d(getLocalClassName(), "unregisterReceiver");
        unregisterReceiver(receiver);
    }
}