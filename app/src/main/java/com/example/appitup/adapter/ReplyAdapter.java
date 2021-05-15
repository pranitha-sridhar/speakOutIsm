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
import com.example.appitup.models.Reply;

import java.util.ArrayList;

public class ReplyAdapter extends RecyclerView.Adapter {

    ArrayList<Reply> list;
    Context context;
    boolean isUserSegregated = true;

    public ReplyAdapter(ArrayList<Reply> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public ReplyAdapter(ArrayList<Reply> list, Context context, boolean isUserSegregated) {
        this.list = list;
        this.context = context;
        this.isUserSegregated = isUserSegregated;
    }

    @Override
    public int getItemViewType(int position) {
        if (!isUserSegregated) return 2;

        if (list.get(position).getSent_from().equals(Prefs.getUser(context).getUsername()))
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
        Reply chatMessages = list.get(position);
        if (isMyMessage(chatMessages.getSent_from()) && isUserSegregated) {
            ((viewHolderSentMsgs) holder).messageBody.setText(chatMessages.getMessage());
            // TODO : Add Date Time in Reply
            ((viewHolderSentMsgs)holder).time.setText(chatMessages.getTimeStampStr());
        } else {
            ((viewHolderReceivedMsgs) holder).messageBody.setText(chatMessages.getMessage());
            // TODO : Add Date Time in Reply
            ((viewHolderReceivedMsgs)holder).time.setText(chatMessages.getTimeStampStr());
            ((viewHolderReceivedMsgs) holder).name.setText(chatMessages.getSent_from());
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
