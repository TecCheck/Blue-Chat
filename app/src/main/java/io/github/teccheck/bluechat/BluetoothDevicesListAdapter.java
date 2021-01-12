package io.github.teccheck.bluechat;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothDevicesListAdapter extends RecyclerView.Adapter<BluetoothDevicesListAdapter.ViewHolder> {
    ArrayList<BluetoothDevice> devices = new ArrayList<>();
    OnItemClickListener listener;

    public BluetoothDevicesListAdapter(Set<BluetoothDevice> deviceSet, OnItemClickListener listener) {
        devices.addAll(deviceSet);
        this.listener = listener;
    }

    public void addDevice(BluetoothDevice device){
        devices.add(device);
        notifyItemChanged(devices.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_bluetooth_devices, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BluetoothDevice device = devices.get(position);
        View root = holder.itemView;
        TextView name = root.findViewById(R.id.device_name);

        root.setOnClickListener(view -> listener.onItemClick(position, device));
        name.setText(device.getName());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View view) {
            super(view);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, Object value);
    }
}

