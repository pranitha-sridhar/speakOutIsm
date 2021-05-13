package com.example.appitup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appitup.Database.Prefs;
import com.example.appitup.R;
import com.example.appitup.models.Comment;

import java.util.ArrayList;

public class AdapterChatMessages extends RecyclerView.Adapter {

    ArrayList<Comment> list;
    Context context;

    public AdapterChatMessages(ArrayList<Comment> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getUsername().equals(Prefs.getUser(context).getUsername()))
            return 1;
        else
            return 2;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sent, parent, false);
            return new viewHolderSentMsgs(view);
        } else if (viewType == 2) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received, parent, false);
            return new viewHolderReceivedMsgs(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Comment chatMessages = list.get(position);
        if (isMyMessage(chatMessages.getUsername())) {
            ((viewHolderSentMsgs) holder).messageBody.setText(chatMessages.getComment());
            // TODO : Add Date Time in Comment
            //((viewHolderSentMsgs)holder).time.setText(chatMessages.getTime());
        } else {
            ((viewHolderReceivedMsgs) holder).messageBody.setText(chatMessages.getComment());
            // TODO : Add Date Time in Comment
            //((viewHolderReceivedMsgs)holder).time.setText(chatMessages.getTime());
            ((viewHolderReceivedMsgs) holder).name.setText(chatMessages.getUsername());
        }
    }

    private boolean isMyMessage(String username) {
        return username.equals(Prefs.getUser(context).getUsername());
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class viewHolderReceivedMsgs extends RecyclerView.ViewHolder {
        // private ImageView profileImg;
        TextView name, messageBody, time;

        public viewHolderReceivedMsgs(@NonNull View itemView) {
            super(itemView);
            //profileImg = itemView.findViewById(R.id.image_message_profile);
            name = itemView.findViewById(R.id.text_message_name);
            time = itemView.findViewById(R.id.text_message_time);
            messageBody = itemView.findViewById(R.id.text_message_body);
        }
    }

    public class viewHolderSentMsgs extends RecyclerView.ViewHolder {
        TextView messageBody, time;
        //ImageView msgStatus;

        public viewHolderSentMsgs(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.text_message_time);
            messageBody = itemView.findViewById(R.id.text_message_body);
            // msgStatus = itemView.findViewById(R.id.msg_status);
        }
    }
}
