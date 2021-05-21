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
import com.grievancesystem.speakout.utility.Helper;

import java.util.List;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.viewHolder> {
    Context context;
    List<Complaints> list;

    public TrendingAdapter(Context context, List<Complaints> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trending_complaint_card, parent, false);
        return new TrendingAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Complaints complaints = list.get(position);
        holder.textViewNumber.setText(getItemCount() - position + ".");
        holder.textViewCategory.setText(complaints.getCategory() + " .Trending");
        holder.textViewTitle.setText(complaints.getSubject());
        holder.textViewUpvotes.setText(complaints.getUpvotes() + " Upvotes");
        if (complaints.getStatus().equals(Helper.PENDING))
            holder.cardViewAppStatus.setCardBackgroundColor(context.getResources().getColor(R.color.pending_color));
        if (complaints.getStatus().equals(Helper.IN_PROGRESS))
            holder.cardViewAppStatus.setCardBackgroundColor(context.getResources().getColor(R.color.inprogress_color));
        if (complaints.getStatus().equals(Helper.RESOLVED))
            holder.cardViewAppStatus.setCardBackgroundColor(context.getResources().getColor(R.color.resolved_color));


    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView textViewNumber, textViewCategory, textViewUpvotes, textViewTitle;
        CardView cardViewAppStatus;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNumber = itemView.findViewById(R.id.textViewNumber);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewUpvotes = itemView.findViewById(R.id.textViewUpvotes);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            cardViewAppStatus = itemView.findViewById(R.id.cardViewAppStatus);
        }
    }
}
