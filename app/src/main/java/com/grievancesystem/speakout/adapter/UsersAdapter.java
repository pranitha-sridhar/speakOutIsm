package com.grievancesystem.speakout.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.grievancesystem.speakout.R;
import com.grievancesystem.speakout.models.User;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.viewHolder> {
    List<User> list;
    Context context;
    UsersListener mListener;

    public UsersAdapter(Context context, List<User> list) {
        this.context = context;
        this.list = list;
    }

    public void setUpOnUserListener(UsersAdapter.UsersListener mListener) {
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UsersAdapter.viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_list, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        User user = list.get(position);
        holder.username.setText("@" + user.getUsername());
        holder.mail.setText(user.getEmail());
        holder.displayname.setText(user.getDisplayName());
        if (user.getProfileUri() != null)
            Glide.with(context).load(user.getProfileUri()).into(holder.circularImageView);
        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null)
                    mListener.optionsClicked(user, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    public interface UsersListener {
        void optionsClicked(User user, int position);
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView username, mail, displayname;
        ImageView options;
        CircularImageView circularImageView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username3);
            mail = itemView.findViewById(R.id.email);
            displayname = itemView.findViewById(R.id.display_name2);
            options = itemView.findViewById(R.id.more_hori);
            circularImageView = itemView.findViewById(R.id.circular_image);
        }
    }
}


