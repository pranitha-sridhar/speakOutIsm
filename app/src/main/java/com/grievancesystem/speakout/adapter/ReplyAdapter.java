package com.grievancesystem.speakout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.grievancesystem.speakout.R;
import com.grievancesystem.speakout.models.Complaints;
import com.grievancesystem.speakout.models.Reply;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.text.DateFormat.getDateTimeInstance;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.viewHolder> {

    ArrayList<Reply> list;
    Context context;
    Complaints complaint;

    public ReplyAdapter(ArrayList<Reply> list, Context context, Complaints complaint) {
        this.list = list;
        this.context = context;
        this.complaint = complaint;
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
        if (isStudentMessage(chatMessages.getSent_from(), complaint.getUsername())) {
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
        String time = null;
        long timeStamp = 0;
        if (chatMessages.getTimeStampMap() != null) {
            timeStamp = (long) chatMessages.getTimeStampMap().get("timeStamp");
            DateFormat dateFormat = getDateTimeInstance();
            Date netDate = (new Date(timeStamp));
            time = dateFormat.format(netDate);
        }
        holder.textViewDateTime.setText(time);
    }

    private boolean isStudentMessage(String reply_by, String complaint_owner) {
        return reply_by.equals(complaint_owner);

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
