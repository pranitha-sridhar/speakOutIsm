package com.example.appitup.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appitup.Database.Prefs;
import com.example.appitup.R;
import com.example.appitup.models.Complaints;
import com.example.appitup.utility.Helper;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.viewHolder> implements Filterable {
    Context context;
    List<Complaints> list;
    List<Complaints> filteredList1;
    ComplaintsListener mListener;

    public ComplaintsAdapter(Context context, List<Complaints> list) {
        this.context = context;
        this.list = list;
        filteredList1=list;
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
        holder.textUserName.setText("@" + complaints.getUsername());
        holder.textViewTitle.setText(complaints.getSubject());
        holder.textViewBody.setText(complaints.getBody());
        holder.textViewDateTime.setText(complaints.getTimeStampStr());
        //holder.textViewDateTime.setText(complaints.getDateTime());
        holder.chipStatus.setText(complaints.getStatus());
        if (complaints.getStatus().equals(Helper.PENDING)) {
            holder.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.pending_color)));
        } else if (complaints.getStatus().equals(Helper.IN_PROGRESS)) {
            holder.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.inprogress_color)));
        } else if (complaints.getStatus().equals(Helper.RESOLVED)) {
            holder.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.resolved_color)));
        }
        holder.chipCategory.setText(complaints.getCategory());
        holder.chipSubcategory.setText(complaints.getSubcategory());

        if (complaints.getListOfUpVoter() == null)
            complaints.setListOfUpVoter(new ArrayList<>());
        if (complaints.getListOfDownVoter() == null)
            complaints.setListOfDownVoter(new ArrayList<>());
        if (complaints.getListOfCommenter() == null)
            complaints.setListOfCommenter(new ArrayList<>());

        int upVoters = (complaints.getListOfUpVoter() != null) ? complaints.getListOfUpVoter().size() : 0;
        int downVoters = (complaints.getListOfDownVoter() != null) ? complaints.getListOfDownVoter().size() : 0;
        int commenter = (complaints.getListOfCommenter() != null) ? complaints.getListOfCommenter().size() : 0;

        holder.upVoteNumber.setText(upVoters + " upvotes");
        holder.downVoteNumber.setText(downVoters + " downvotes");
        holder.commentNumber.setText(commenter + " comments");

        int status = 0;
        if (complaints.getListOfUpVoter() != null && complaints.getListOfUpVoter().contains(Prefs.getUser(context).getUsername())) {
            holder.upVote.setImageResource(R.drawable.ic_upvote_filled);
            holder.downVote.setImageResource(R.drawable.ic_downvote_outlined);
            status = 1;
        }

        if (complaints.getListOfDownVoter() != null && complaints.getListOfDownVoter().contains(Prefs.getUser(context).getUsername())) {
            holder.downVote.setImageResource(R.drawable.ic_downvote_filled);
            holder.upVote.setImageResource(R.drawable.ic_upvote_outlined);
            status = -1;
        }

        int finalStatus = status;

        holder.upVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalStatus == -1) {
                    holder.upVoteNumber.setText(upVoters + 1 + " upvotes");
                    holder.downVoteNumber.setText(downVoters - 1 + " downvotes");
                } else if (finalStatus == 0) {
                    holder.upVoteNumber.setText(upVoters + 1 + " upvotes");
                    holder.downVoteNumber.setText(downVoters + " downvotes");
                } else if (finalStatus == 1) {
                    holder.upVoteNumber.setText(upVoters + " upvotes");
                    holder.downVoteNumber.setText(downVoters + " downvotes");
                }

                holder.upVote.setImageResource(R.drawable.ic_upvote_filled);
                holder.downVote.setImageResource(R.drawable.ic_downvote_outlined);
                if (mListener != null)
                    mListener.upVoteClicked(complaints);
            }
        });

        holder.downVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalStatus == 1) {
                    holder.upVoteNumber.setText(upVoters - 1 + " upvotes");
                    holder.downVoteNumber.setText(downVoters + 1 + " downvotes");
                } else if (finalStatus == 0) {
                    holder.upVoteNumber.setText(upVoters + " upvotes");
                    holder.downVoteNumber.setText(downVoters + 1 + " downvotes");
                } else if (finalStatus == -1) {
                    holder.upVoteNumber.setText(upVoters + " upvotes");
                    holder.downVoteNumber.setText(downVoters + " downvotes");
                }

                holder.downVote.setImageResource(R.drawable.ic_downvote_filled);
                holder.upVote.setImageResource(R.drawable.ic_upvote_outlined);
                if (mListener != null)
                    mListener.downVoteClicked(complaints);
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.commentsClicked(complaints);
            }
        });

        holder.textUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.usernameClicked(complaints);
            }
        });
    }

    private int getStatus(Complaints complaints) {
        int status = 0;
        if (complaints.getListOfUpVoter() != null && complaints.getListOfUpVoter().contains(Prefs.getUser(context).getUsername()))
            status = 1;
        if (complaints.getListOfDownVoter() != null && complaints.getListOfDownVoter().contains(Prefs.getUser(context).getUsername()))
            status = -1;
        return status;
    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Complaints> filteredList=new ArrayList<>();
            if(charSequence == null || getItemCount()==0){
                filteredList.addAll(filteredList1);
            }
            else{
                list.size();
                String filterPattern=charSequence.toString().toLowerCase().trim();
                for(Complaints complaint:filteredList1){
                    if(complaint.getStatus().toLowerCase().contains(filterPattern))
                        filteredList.add(complaint);
                }
            }
            FilterResults filterResults=new FilterResults();
            filterResults.values=filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            list.clear();
            list.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public interface ComplaintsListener {
        void upVoteClicked(Complaints complaint);

        void downVoteClicked(Complaints complaint);

        void commentsClicked(Complaints complaint);

        void usernameClicked(Complaints complaint);
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView textUserName, textViewDateTime, textViewTitle, textViewBody, upVoteNumber, downVoteNumber, commentNumber;
        Chip chipCategory, chipSubcategory, chipStatus;
        ImageButton upVote, downVote, comment;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            textUserName = itemView.findViewById(R.id.textViewUsername);
            textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewBody = itemView.findViewById(R.id.textViewBody);
            chipCategory = itemView.findViewById(R.id.chipCategory);
            chipSubcategory = itemView.findViewById(R.id.chipSubcategory);
            chipStatus = itemView.findViewById(R.id.chipStatus);
            upVoteNumber = itemView.findViewById(R.id.upVoteNumber);
            downVoteNumber = itemView.findViewById(R.id.downVoteNumber);
            commentNumber = itemView.findViewById(R.id.commentNumber);
            upVote = itemView.findViewById(R.id.upVote);
            downVote = itemView.findViewById(R.id.downVote);
            comment = itemView.findViewById(R.id.comment);
        }
    }
}
