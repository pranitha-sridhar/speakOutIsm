package com.example.appitup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appitup.Database.Prefs;
import com.example.appitup.R;
import com.example.appitup.models.Reply;

import java.util.ArrayList;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.viewHolder> {

    ArrayList<Reply> list;
    Context context;

    public ReplyAdapter(ArrayList<Reply> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_reply_messages, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Reply chatMessages = list.get(position);
        if (isMyMessage(chatMessages.getSent_from())) {
            holder.cardText.setText("S");
            holder.cardViewPhoto.setCardBackgroundColor(context.getResources().getColor(R.color.student_color));
            holder.textViewUsername.setText("@" + chatMessages.getSent_from() + "\nStudent");
        } else {
            holder.cardViewPhoto.setCardBackgroundColor(context.getResources().getColor(R.color.admin_color));
            holder.cardText.setText("A");
            holder.textViewUsername.setText("@" + chatMessages.getSent_from() + "\nAdmin");
        }
        holder.textViewMessage.setText(chatMessages.getMessage());
        //TODO: Add Date Time in Reply
        //holder.textViewDateTime.setText("");
    }

    private boolean isMyMessage(String username) {
        return username.equals(Prefs.getUser(context).getUsername());

    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView cardText, textViewUsername, textViewDateTime, textViewMessage;
        CardView cardViewPhoto;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewPhoto = itemView.findViewById(R.id.cardViewPhoto);
            cardText = itemView.findViewById(R.id.cardText);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
        }
    }
}
