package com.example.appitup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appitup.R;
import com.example.appitup.models.Complaints;
import com.example.appitup.utility.Helper;
import com.google.android.material.chip.Chip;

import java.util.List;

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.viewHolder> {
    Context context;
    List<Complaints> list;
    ComplaintsListener mListener;

    public ComplaintsAdapter(Context context, List<Complaints> list) {
        this.context = context;
        this.list = list;
    }

    public void setUpOnComplaintListener(ComplaintsListener mListener) {
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_complaints, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Complaints complaints = list.get(position);
        holder.textUserName.setText(complaints.getUsername());
        holder.textViewTitle.setText(complaints.getSubject());
        holder.textViewBody.setText(complaints.getBody());
        //holder.textViewDateTime.setText(complaints.getDateTime());
        holder.chipStatus.setText(complaints.getStatus());
        if (complaints.getStatus().equals(Helper.PENDING)) {
            holder.chipStatus.setBackgroundColor(context.getResources().getColor(R.color.pending_color));
        } else if (complaints.getStatus().equals(Helper.IN_PROGRESS)) {
            holder.chipStatus.setBackgroundColor(context.getResources().getColor(R.color.inprogress_color));
        } else if (complaints.getStatus().equals(Helper.RESOLVED)) {
            holder.chipStatus.setBackgroundColor(context.getResources().getColor(R.color.resolved_color));
        }
        holder.chipCategory.setText(complaints.getCategory());
        holder.chipSubcategory.setText(complaints.getSubcategory());
    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    interface ComplaintsListener {
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView textUserName, textViewDateTime, textViewTitle, textViewBody;
        Chip chipCategory, chipSubcategory, chipStatus;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.textViewUsername);
            textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewBody = itemView.findViewById(R.id.textViewBody);
            chipCategory = itemView.findViewById(R.id.chipCategory);
            chipSubcategory = itemView.findViewById(R.id.chipSubcategory);
            chipStatus = itemView.findViewById(R.id.chipStatus);
        }
    }
}
