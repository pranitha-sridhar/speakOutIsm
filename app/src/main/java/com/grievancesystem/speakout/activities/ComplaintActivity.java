package com.grievancesystem.speakout.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.grievancesystem.speakout.Database.Prefs;
import com.grievancesystem.speakout.R;
import com.grievancesystem.speakout.adapter.CommentsAdapter;
import com.grievancesystem.speakout.models.Comment;
import com.grievancesystem.speakout.models.Complaints;
import com.grievancesystem.speakout.models.Notification;
import com.grievancesystem.speakout.models.User;
import com.grievancesystem.speakout.models.Vote;
import com.grievancesystem.speakout.utility.Helper;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static java.text.DateFormat.getDateTimeInstance;

public class ComplaintActivity extends AppCompatActivity {
    private static final String TAG = "CommentsDialogFragment";
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String msg = editTextMessage.getText().toString().trim();
            if (msg.isEmpty()) send.setVisibility(View.GONE);
            else if (Prefs.getUser(getApplicationContext()).getUserType() == Helper.USER_STUDENT)
                send.setVisibility(View.VISIBLE);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    @BindView(R.id.back)
    ImageView backArrow;
    @BindView(R.id.reply)
    ImageView reply;
    @BindView(R.id.textViewUsername)
    TextView textViewUsername;
    @BindView(R.id.textViewTitle)
    TextView textViewTitle;
    @BindView(R.id.textViewBody)
    TextView textViewBody;
    @BindView(R.id.textViewDateTime)
    TextView textViewDateTime;
    @BindView(R.id.chipStatus)
    Chip chipStatus;
    @BindView(R.id.chipCategory)
    Chip chipCategory;
    @BindView(R.id.chipSubcategory)
    Chip chipSubcategory;
    @BindView(R.id.upVote)
    ImageView upVote;
    @BindView(R.id.downVote)
    ImageView downVote;
    @BindView(R.id.comment)
    ImageView comment;
    @BindView(R.id.upVoteNumber)
    TextView upVoteNumber;
    @BindView(R.id.downVoteNumber)
    TextView downVoteNumber;
    @BindView(R.id.commentNumber)
    TextView commentNumber;
    @BindView(R.id.share)
    ImageView share_in_card;
    @BindView(R.id.share_toolbar)
    ImageView share;
    @BindView(R.id.shimmer2)
    ShimmerFrameLayout shimmerFrameLayout;
    @BindView(R.id.shimmer3)
    ShimmerFrameLayout shimmerFrameLayout2;
    @BindView(R.id.constraintLayout)
    ConstraintLayout constraintLayout;



    Unbinder unbinder;
    Complaints complaint;
    String complaintId;
    View view_complaint,view_comment;
    ImageView send;
    EditText editTextMessage;
    int commenter = 0;
    TashieLoader progressLoader;
    RecyclerView recyclerView;
    ArrayList<Comment> list = new ArrayList<>();
    TextView no_data_found, textViewComplaintTitle;
    CommentsAdapter adapter;

    boolean isConnected = true;
    boolean monitoringConnectivity = false;
    View parentLayout;
    private final ConnectivityManager.NetworkCallback connectivityCallback
            = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            showBackOnlineUI();
            isConnected = true;
        }

        @Override
        public void onLost(Network network) {
            showNoInternetUI();
            isConnected = false;
        }
    };
    String string = null;

    private void showBackOnlineUI() {
        Snackbar snackbar = Snackbar.make(parentLayout, "Back Online", Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(android.R.color.holo_green_light))
                .setTextColor(getResources().getColor(android.R.color.white));
        snackbar.show();
    }

    private void showNoInternetUI() {
        Snackbar snackbar = Snackbar.make(parentLayout, "No Internet Connection Available", Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(android.R.color.black))
                .setTextColor(getResources().getColor(android.R.color.white));
        snackbar.show();
    }

    @Override
    protected void onPause() {
        if (monitoringConnectivity) {
            final ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(connectivityCallback);
            monitoringConnectivity = false;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectivity();
    }

    private void checkConnectivity() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

        if (!isConnected) {
            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .build(), connectivityCallback);
            monitoringConnectivity = true;
        }

    }

    @Override
    protected void onStart() {
        if (!Helper.isInternetAvailable(this)) {
            showNoInternetUI();
        }
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        unbinder = ButterKnife.bind(this);

        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
       // Log.i(TAG, "onCreate: " + appLinkAction + "  " + appLinkData + " " + appLinkData.getQueryParameter("complaintId"));
        if (appLinkData != null) {
            if (!Prefs.isUserLoggedIn(this)) {
                Helper.toast(this, "Please login to Check Complaints");
                startActivity(new Intent(this, SignIn.class));
                return;
            }
            if (appLinkData.getQueryParameter("complaintId") != null)
                complaintId = appLinkData.getQueryParameter("complaintId");
            else {
                Helper.toast(this, "Requested URL Not Found");
                return;
            }
        } else {
            complaintId = getIntent().getStringExtra("complaintId");
        }

        loadComplaint();

        recyclerView = findViewById(R.id.comments_recycler);
        editTextMessage = findViewById(R.id.editTextMessage);
        progressLoader = findViewById(R.id.progressLoader);
        send = findViewById(R.id.send);
        no_data_found = findViewById(R.id.no_data_found);
        textViewComplaintTitle = findViewById(R.id.textViewComplaintTitle);
        view_complaint=findViewById(R.id.complaint);
        view_comment=findViewById(R.id.comments);

        textViewComplaintTitle.setVisibility(View.GONE);
        share_in_card.setVisibility(View.GONE);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (complaint != null) {
                    Intent intent = new Intent(ComplaintActivity.this, ConversationActivity.class);
                    intent.putExtra("complaint", complaint);
                    startActivity(intent);
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (complaint!=null) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Speak Out");
                    String shareMessage = "\nGo through this grievance and react\n\n";
                    shareMessage = shareMessage + "https://grievancesystem.speakout/complaint/?complaintId=" + complaint.getComplaintId() + "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Choose One"));
                }
            }
        });

    }

    public void loadComplaint() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Complaints").child(complaintId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                String complaintId = ds.child("complaintId").getValue().toString();
                String username = ds.child("username").getValue().toString();
                String uid = ds.child("uid").getValue().toString();
                String subject = ds.child("subject").getValue().toString();
                String body = ds.child("body").getValue().toString();
                String category = ds.child("category").getValue().toString();
                String subcategory = ds.child("subcategory").getValue().toString();
                String visibility = ds.child("visibility").getValue().toString();
                String status = ds.child("status").getValue().toString();
                String anonymous = ds.child("anonymous").getValue().toString();

                // Get Upvotes and Downvotes
                long upvotes = 0;
                long downvotes = 0;
                if (ds.hasChild("upvotes"))
                    upvotes = (long) ds.child("upvotes").getValue();
                if (ds.hasChild("downvotes"))
                    downvotes = (long) ds.child("downvotes").getValue();

                // Get Timestamp
                String time = null;
                long timeStamp = 0;
                Map<String, Long> map = new HashMap();
                if (ds.child("timeStampmap").child("timeStamp").exists()) {
                    timeStamp = (long) ds.child("timeStampmap").child("timeStamp").getValue();
                    DateFormat dateFormat = getDateTimeInstance();
                    Date netDate = (new Date(timeStamp));
                    time = dateFormat.format(netDate);
                    map.put("timeStamp", timeStamp);
                } else {
                    if (ds.child("timeStampStr").exists()) {
                        time = ds.child("timeStampStr").getValue().toString();
                    }
                }

                // Get Upvoters List
                ArrayList<Vote> upvoters = new ArrayList<>();
                if (ds.hasChild("listOfUpvoters"))
                    for (DataSnapshot s : ds.child("listOfUpvoters").getChildren())
                        upvoters.add(new Vote(s.child("complaint_id").getValue().toString(), s.child("username").getValue().toString()));

                // Get Downvoters List
                ArrayList<Vote> downvoters = new ArrayList<>();
                if (ds.hasChild("listOfDownvoters"))
                    for (DataSnapshot s : ds.child("listOfDownvoters").getChildren())
                        downvoters.add(new Vote(s.child("complaint_id").getValue().toString(), s.child("username").getValue().toString()));

                // Get Comments List
                ArrayList<Comment> commenters = new ArrayList<>();
                if (ds.hasChild("listOfCommenter"))
                    for (DataSnapshot s : ds.child("listOfCommenter").getChildren()) {
                        long timeStamp2 = 0;
                        Map<String, Long> map2 = new HashMap();
                        timeStamp2 = (long) s.child("timeStampMap").child("timeStamp").getValue();
                        map2.put("timeStamp", timeStamp2);
                        commenters.add(new Comment(s.child("username").getValue().toString(), s.child("commentId").getValue().toString(), s.child("comment").getValue().toString(), map2));
                    }

                // Check for anonymous users
                //if (anonymous.equals("true")) username = "Anonymous";

                //check vote status of complaint
                int voteStatus = Helper.NOT_VOTED;
                String loggedInUsername = Prefs.getUser(getApplicationContext()).getUsername();
                if (ds.child("listOfUpvoters").hasChild(loggedInUsername))
                    voteStatus = Helper.UPVOTED;
                if (ds.child("listOfDownvoters").hasChild(loggedInUsername))
                    voteStatus = Helper.DOWNVOTED;


                // add complaint to list
                complaint = new Complaints(complaintId, username, uid, subject, body, category, subcategory, visibility, status,
                        anonymous, upvotes, downvotes, commenters, upvoters, downvoters, map, time, voteStatus);

                display_card();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void display_card() {
        textViewUsername.setText("@" + complaint.getUsername());
        if (complaint.getAnonymous().equals("true")) textViewUsername.setText("@Anonymous");
        textViewDateTime.setText(complaint.getTimeStampStr());
        textViewTitle.setText(complaint.getSubject());
        textViewBody.setText(complaint.getBody());
        chipStatus.setText(complaint.getStatus());
        if (complaint.getStatus().equals(Helper.PENDING)) {
            chipStatus.setChipBackgroundColor(ColorStateList.valueOf(getApplicationContext().getResources().getColor(R.color.pending_color)));
        } else if (complaint.getStatus().equals(Helper.IN_PROGRESS)) {
            chipStatus.setChipBackgroundColor(ColorStateList.valueOf(getApplicationContext().getResources().getColor(R.color.inprogress_color)));
        } else if (complaint.getStatus().equals(Helper.RESOLVED)) {
            chipStatus.setChipBackgroundColor(ColorStateList.valueOf(getApplicationContext().getResources().getColor(R.color.resolved_color)));
        }
        chipCategory.setText(complaint.getCategory());
        chipSubcategory.setText(complaint.getSubcategory());

        final long[] upVoters = {complaint.getUpvotes()};
        final long[] downVoters = {complaint.getDownvotes()};
        commenter = (complaint.getListOfCommenter() != null) ? complaint.getListOfCommenter().size() : 0;

        upVoteNumber.setText(upVoters[0] + " upvotes");
        downVoteNumber.setText(downVoters[0] + " downvotes");
        commentNumber.setText(commenter + " comments");

        if (complaint.getVoteStatus() == Helper.UPVOTED) {
            upVote.setImageResource(R.drawable.ic_upvote_filled);
            downVote.setImageResource(R.drawable.ic_downvote_outlined);
        } else if (complaint.getVoteStatus() == Helper.DOWNVOTED) {
            downVote.setImageResource(R.drawable.ic_downvote_filled);
            upVote.setImageResource(R.drawable.ic_upvote_outlined);
        }

        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        view_complaint.setVisibility(View.VISIBLE);

        upVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Prefs.getUser(getApplicationContext()).getUserType() == Helper.USER_STUDENT) {
                    if (complaint.getVoteStatus() == Helper.DOWNVOTED) {
                        upVoteNumber.setText(upVoters[0] + 1 + " upvotes");
                        downVoteNumber.setText(downVoters[0] - 1 + " downvotes");
                    } else if (complaint.getVoteStatus() == Helper.NOT_VOTED) {
                        upVoteNumber.setText(upVoters[0] + 1 + " upvotes");
                        downVoteNumber.setText(downVoters[0] + " downvotes");
                    } else if (complaint.getVoteStatus() == Helper.UPVOTED) {
                        upVoteNumber.setText(upVoters[0] + " upvotes");
                        downVoteNumber.setText(downVoters[0] + " downvotes");
                    }
                    upVoteClicked(complaint);

                }
            }
        });

        downVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Prefs.getUser(getApplicationContext()).getUserType() == Helper.USER_STUDENT) {
                    if (complaint.getVoteStatus() == Helper.UPVOTED) {
                        upVoteNumber.setText(upVoters[0] - 1 + " upvotes");
                        downVoteNumber.setText(downVoters[0] + 1 + " downvotes");
                    } else if (complaint.getVoteStatus() == Helper.NOT_VOTED) {
                        upVoteNumber.setText(upVoters[0] + " upvotes");
                        downVoteNumber.setText(downVoters[0] + 1 + " downvotes");
                    } else if (complaint.getVoteStatus() == Helper.DOWNVOTED) {
                        upVoteNumber.setText(upVoters[0] + " upvotes");
                        downVoteNumber.setText(downVoters[0] + " downvotes");
                    }

                    downVote.setImageResource(R.drawable.ic_downvote_filled);
                    upVote.setImageResource(R.drawable.ic_upvote_outlined);
                    downVoteClicked(complaint);
                }
            }
        });

        textViewUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usernameClicked(complaint);
            }
        });

        initRecyclerView();
        list.clear();
        list.addAll(complaint.getListOfCommenter());

        shimmerFrameLayout2.stopShimmer();
        shimmerFrameLayout2.setVisibility(View.GONE);
        view_comment.setVisibility(View.VISIBLE);

        if (Prefs.getUser(getApplicationContext()).getUserType() == Helper.USER_ADMINISTRATOR) {
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

                Comment comment = new Comment(Prefs.getUser(getApplicationContext()).getUsername(), commentMsg, map);
                addComment(comment);
            }
        });
    }


    public void upVoteClicked(Complaints complaint) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference upVotesRef = databaseReference.child("Complaints").child(complaint.getComplaintId()).child("listOfUpvoters");
        DatabaseReference downVotesRef = databaseReference.child("Complaints").child(complaint.getComplaintId()).child("listOfDownvoters");
        DatabaseReference complaintRef = databaseReference.child("Complaints").child(complaint.getComplaintId());

        downVotesRef.child(Prefs.getUser(getApplicationContext()).getUsername()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Vote upvote = new Vote(complaint.getComplaintId(), Prefs.getUser(getApplicationContext()).getUsername());
                    upVotesRef.child(Prefs.getUser(getApplicationContext()).getUsername()).setValue(upvote).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            complaintRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    HashMap<String, Object> data = new HashMap<>();
                                    if (task.getResult().hasChild("listOfDownvoters"))
                                        data.put("downvotes", task.getResult().child("listOfDownvoters").getChildrenCount());
                                    else data.put("downvotes", 0);
                                    if (task.getResult().hasChild("listOfUpvoters"))
                                        data.put("upvotes", task.getResult().child("listOfUpvoters").getChildrenCount());
                                    else data.put("upvotes", 0);
                                    complaintRef.updateChildren(data);
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public void downVoteClicked(Complaints complaint) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference upVotesRef = databaseReference.child("Complaints").child(complaint.getComplaintId()).child("listOfUpvoters");
        DatabaseReference downVotesRef = databaseReference.child("Complaints").child(complaint.getComplaintId()).child("listOfDownvoters");
        DatabaseReference complaintRef = databaseReference.child("Complaints").child(complaint.getComplaintId());

        upVotesRef.child(Prefs.getUser(getApplicationContext()).getUsername()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Vote downvote = new Vote(complaint.getComplaintId(), Prefs.getUser(getApplicationContext()).getUsername());
                    downVotesRef.child(Prefs.getUser(getApplicationContext()).getUsername()).setValue(downvote).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            complaintRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    HashMap<String, Object> data = new HashMap<>();
                                    if (task.getResult().hasChild("listOfDownvoters"))
                                        data.put("downvotes", task.getResult().child("listOfDownvoters").getChildrenCount());
                                    else data.put("downvotes", 0);
                                    if (task.getResult().hasChild("listOfUpvoters"))
                                        data.put("upvotes", task.getResult().child("listOfUpvoters").getChildrenCount());
                                    else data.put("upvotes", 0);
                                    complaintRef.updateChildren(data);
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public void usernameClicked(Complaints complaint) {
        if (complaint.getAnonymous().equals("true") && Prefs.getUser(ComplaintActivity.this).getUserType() == Helper.USER_STUDENT) {
            Helper.toast(ComplaintActivity.this, "Anonymous User");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ComplaintActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_profile, null));
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(null);
        alertDialog.show();

        CircularImageView imageView = alertDialog.findViewById(R.id.profile_picture1);
        TextView username=alertDialog.findViewById(R.id.username1);
        TextView displayName=alertDialog.findViewById(R.id.display_name1);
        TextView mail = alertDialog.findViewById(R.id.mailid1);
        TashieLoader progressLoader = alertDialog.findViewById(R.id.progressLoader);

        String uid=complaint.getUid();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("StudentUsers").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressLoader.setVisibility(View.GONE);
                username.setText("@" + snapshot.child("username").getValue().toString());
                displayName.setText(snapshot.child("displayName").getValue().toString());
                mail.setText(snapshot.child("email").getValue().toString());
                Glide.with(ComplaintActivity.this).load(snapshot.child("profileUri").getValue().toString()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initRecyclerView() {
        adapter = new CommentsAdapter(list, getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
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
                    User user = Prefs.getUser(getApplicationContext());
                    if (!complaint.getUsername().equals(user.getUsername())) {
                        Notification notification = new Notification("Complaint : " + complaint.getSubject(), "@" + user.getUsername() + " commented : " + comment.getComment()
                                , complaint.getComplaintId(), comment.getCommentId(), user.getProfileUri(), false);
                        Helper.sendNotificationToUser(complaint.getUsername(), notification);
                    }
                    //takeUpdatesOfComments();
                    commentNumber.setText(++commenter + " comments");

                } else Log.i("Comment Frag", "Comment Error : " + task.getException().getMessage());
            }
        });
    }
}