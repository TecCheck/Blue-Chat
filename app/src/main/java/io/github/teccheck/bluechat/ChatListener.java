package io.github.teccheck.bluechat;

public interface ChatListener {
    void onMessageReceived(Message message);
    void onDeviceConnected(String name, String address);
    void onDeviceDisconnected(String name, String address);
    void onError(String message);
}
