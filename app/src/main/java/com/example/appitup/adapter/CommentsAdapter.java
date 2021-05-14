package com.example.appitup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appitup.R;
import com.example.appitup.models.Comment;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.viewHolderReceivedMsgs> {

    ArrayList<Comment> list;
    Context context;

    public CommentsAdapter(ArrayList<Comment> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolderReceivedMsgs onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received, parent, false);
        return new viewHolderReceivedMsgs(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolderReceivedMsgs holder, int position) {
        Comment chatMessages = list.get(position);
        holder.messageBody.setText(chatMessages.getComment());
        holder.name.setText(chatMessages.getUsername());
        // TODO : Add Date Time in Comment
        //holder.time.setText(chatMessages.getTime());
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class viewHolderReceivedMsgs extends RecyclerView.ViewHolder {
        TextView name, messageBody, time;

        public viewHolderReceivedMsgs(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_message_name);
            time = itemView.findViewById(R.id.text_message_time);
            messageBody = itemView.findViewById(R.id.text_message_body);
        }
    }
}
