package com.grievancesystem.speakout.fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.grievancesystem.speakout.Database.Prefs;
import com.grievancesystem.speakout.R;
import com.grievancesystem.speakout.adapter.CommentsAdapter;
import com.grievancesystem.speakout.models.Comment;
import com.grievancesystem.speakout.models.Complaints;
import com.grievancesystem.speakout.models.Notification;
import com.grievancesystem.speakout.models.User;
import com.grievancesystem.speakout.utility.Helper;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.text.DateFormat.getDateTimeInstance;

public class CommentsDialogFragment extends BottomSheetDialogFragment {

    private static final String TAG = "CommentsDialogFragment";

    ImageView send;
    EditText editTextMessage;
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String msg = editTextMessage.getText().toString().trim();
            if (msg.isEmpty()) send.setVisibility(View.GONE);
            else if (Prefs.getUser(getContext()).getUserType() == Helper.USER_STUDENT)
                send.setVisibility(View.VISIBLE);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    TashieLoader progressLoader;
    RecyclerView recyclerView;
    Complaints complaint;
    ArrayList<Comment> list = new ArrayList<>();
    TextView no_data_found, textViewComplaintTitle;
    CommentsAdapter adapter;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();

        if (dialog != null) {
            View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            bottomSheet.getLayoutParams().height = 1300;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_bottom_sheet_comments, container, false);

        recyclerView = view.findViewById(R.id.comments_recycler);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        progressLoader = view.findViewById(R.id.progressLoader);
        send = view.findViewById(R.id.send);
        no_data_found = view.findViewById(R.id.no_data_found);
        textViewComplaintTitle = view.findViewById(R.id.textViewComplaintTitle);

        Bundle mArgs = getArguments();
        complaint = (Complaints) mArgs.getSerializable("complaint");

        textViewComplaintTitle.setText(complaint.getSubject());
        list.clear();
        //list.addAll(complaint.getListOfCommenter());

        initRecyclerView();
        takeUpdatesOfComments();

        if (Prefs.getUser(getContext()).getUserType() == Helper.USER_ADMINISTRATOR) {
            editTextMessage.setText("Only students can participate in this comment");
            editTextMessage.setEnabled(false);
        } else editTextMessage.addTextChangedListener(textWatcher);

        editTextMessage.addTextChangedListener(textWatcher);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentMsg = editTextMessage.getText().toString().trim();
                if (commentMsg.isEmpty()) {
                    editTextMessage.setError("Please provide some message");
                    editTextMessage.requestFocus();
                    return;
                } else {
                    editTextMessage.setError(null);
                }

                Map map = new HashMap();
                map.put("timeStamp", ServerValue.TIMESTAMP);

                Comment comment = new Comment(Prefs.getUser(getContext()).getUsername(), commentMsg, map);
                addComment(comment);
            }
        });
        return view;
    }

    /*
    private void loadComments() {
        Query query = FirebaseDatabase.getInstance().getReference("Complaints").child(complaint.getComplaintId()).child("listOfCommenter");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String commentId = ds.child("commentId").getValue().toString();
                    String comment = ds.child("comment").getValue().toString();
                    String username = ds.child("username").getValue().toString();
                    String time = null;
                    long timeStamp = 0;
                    Map<String, Long> map = new HashMap();
                    if (ds.child("timeStampMap").child("timeStamp").exists()) {
                        timeStamp = (long) ds.child("timeStampMap").child("timeStamp").getValue();
                        DateFormat dateFormat = getDateTimeInstance();
                        Date netDate = (new Date(timeStamp));
                        time = dateFormat.format(netDate);
                        map.put("timeStamp", timeStamp);
                    }
                    list.add(new Comment(username, commentId, comment, map));
                }
                if (list.isEmpty()) {
                    Helper.toast(getContext(), "No Comments Found for this complaint");
                }
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(list.size());
                progressLoader.setVisibility(View.GONE);
                takeUpdatesOfComments();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
     */

    private void takeUpdatesOfComments() {
        //list.clear();
        Query query = FirebaseDatabase.getInstance().getReference("Complaints").child(complaint.getComplaintId()).child("listOfCommenter");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String s) {
                Log.i(TAG, "onChildAdded: called");
                if (ds.exists() && list != null) {
                    String commentId = String.valueOf(ds.child("commentId").getValue());
                    String comment = String.valueOf(ds.child("comment").getValue());
                    String username = String.valueOf(ds.child("username").getValue());
                    String time = null;
                    long timeStamp = 0;
                    Map<String, Long> map = new HashMap();
                    if (ds.child("timeStampMap").child("timeStamp").exists()) {
                        timeStamp = (long) ds.child("timeStampMap").child("timeStamp").getValue();
                        DateFormat dateFormat = getDateTimeInstance();
                        Date netDate = (new Date(timeStamp));
                        time = dateFormat.format(netDate);
                        map.put("timeStamp", timeStamp);
                    }
                    list.add(new Comment(username, commentId, comment, map));
                }

                if (adapter != null && recyclerView != null && progressLoader != null) {
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(list.size());
                    progressLoader.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i(TAG, "onChildChanged: ");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "onChildRemoved: ");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i(TAG, "onChildMoved: ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: ");
            }
        });
    }

    private void addComment(Comment comment) {
        editTextMessage.setText(null);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Complaints").child(complaint.getComplaintId()).child("listOfCommenter");
        String commentId = reference.push().getKey();
        comment.setCommentId(commentId);

        reference.child(commentId).setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("Comment Frag", "onComplete: Commented");
                    User user = Prefs.getUser(getContext());
                    if (!complaint.getUsername().equals(user.getUsername())) {
                        Notification notification = new Notification("Complaint : " + complaint.getSubject(), "@" + user.getUsername() + " commented : " + comment.getComment()
                                , complaint.getComplaintId(), comment.getCommentId(), user.getProfileUri(), false);
                        Helper.sendNotificationToUser(complaint.getUsername(), notification);
                    }
                } else Log.i("Comment Frag", "Comment Error : " + task.getException().getMessage());
            }
        });
    }

    private void initRecyclerView() {
        adapter = new CommentsAdapter(list, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }
}
