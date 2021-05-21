package com.grievancesystem.speakout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.grievancesystem.speakout.Database.Prefs;
import com.grievancesystem.speakout.R;
import com.grievancesystem.speakout.models.Comment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.text.DateFormat.getDateTimeInstance;

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

        String time = null;
        long timeStamp = 0;
        if (chatMessages.getTimeStampMap() != null) {
            timeStamp = (long) chatMessages.getTimeStampMap().get("timeStamp");
            DateFormat dateFormat = getDateTimeInstance();
            Date netDate = (new Date(timeStamp));
            time = dateFormat.format(netDate);
        }
        holder.time.setText(time);

        if (chatMessages.getUsername().equals(Prefs.getUser(context).getUsername())) {
            holder.cardViewPhoto.setCardBackgroundColor(context.getResources().getColor(R.color.student_color));
            holder.cardText.setText("S");
        } else {
            holder.cardViewPhoto.setCardBackgroundColor(context.getResources().getColor(R.color.users_color));
            holder.cardText.setText("U");
        }
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class viewHolderReceivedMsgs extends RecyclerView.ViewHolder {
        TextView name, messageBody, time, cardText;
        CardView cardViewPhoto;

        public viewHolderReceivedMsgs(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_message_name);
            time = itemView.findViewById(R.id.text_message_time);
            messageBody = itemView.findViewById(R.id.text_message_body);
            cardText = itemView.findViewById(R.id.cardText);
            cardViewPhoto = itemView.findViewById(R.id.cardViewPhoto);
        }
    }
}
