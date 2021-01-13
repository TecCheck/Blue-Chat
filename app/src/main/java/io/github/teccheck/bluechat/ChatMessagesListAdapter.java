package io.github.teccheck.bluechat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatMessagesListAdapter extends RecyclerView.Adapter<ChatMessagesListAdapter.ViewHolder> {

    ArrayList<Message> messages = new ArrayList<>();

    public ChatMessagesListAdapter() {
    }

    public void addMessage(Message message){
        messages.add(message);
        notifyItemChanged(messages.size() - 1);
    }

    @NonNull
    @Override
    public ChatMessagesListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatMessagesListAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_message, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessagesListAdapter.ViewHolder holder, int position) {
        Message message = messages.get(position);
        View root = holder.itemView;
        TextView text = root.findViewById(R.id.message_text);

        text.setText(message.text);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View view) {
            super(view);
        }
    }
}
