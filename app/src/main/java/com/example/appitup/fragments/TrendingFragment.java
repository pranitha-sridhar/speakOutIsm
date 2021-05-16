package com.example.appitup.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appitup.Database.Prefs;
import com.example.appitup.R;
import com.example.appitup.activities.ConversationActivity;
import com.example.appitup.adapter.ComplaintsAdapter;
import com.example.appitup.models.Comment;
import com.example.appitup.models.Complaints;
import com.example.appitup.models.Vote;
import com.example.appitup.utility.Helper;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.appitup.utility.Helper.IN_PROGRESS;
import static com.example.appitup.utility.Helper.PENDING;
import static com.example.appitup.utility.Helper.RESOLVED;
import static java.text.DateFormat.getDateTimeInstance;

public class TrendingFragment extends Fragment implements ComplaintsAdapter.ComplaintsListener {
    @BindView(R.id.complaints_recycler2)
    RecyclerView recyclerView;
    /*@BindView(R.id.shimmer2)
    ShimmerFrameLayout shimmerFrameLayout;*/
    @BindView(R.id.total)
            TextView total;
    @BindView(R.id.resolved)
            TextView resolved;
    @BindView(R.id.inprogress)
            TextView inprogress;
    @BindView(R.id.pending)
            TextView pending;

    Unbinder unbinder;
    ArrayList<Complaints> list = new ArrayList<>();
    ComplaintsAdapter adapter;
    FirebaseAuth mAuth;

    public TrendingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null)
            unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trending, container, false);

        unbinder = ButterKnife.bind(this, view);

        adapter = new ComplaintsAdapter(getContext(), list);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadTop10Data();
        loadCardData();

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        adapter.setUpOnComplaintListener(this);
        return view;
    }

    public void loadTop10Data(){
        Query query=FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("upvotes").limitToFirst(10);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    String status = ds.child("status").getValue().toString();
                    if(status.equals("private"))continue;
                    String complaintId = ds.child("complaintId").getValue().toString();
                    String username = ds.child("username").getValue().toString();
                    String uid = ds.child("uid").getValue().toString();
                    String subject = ds.child("subject").getValue().toString();
                    String body = ds.child("body").getValue().toString();
                    String category = ds.child("category").getValue().toString();
                    String subcategory = ds.child("subcategory").getValue().toString();
                    String visibility = ds.child("visibility").getValue().toString();

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
                        for (DataSnapshot s : ds.child("listOfCommenter").getChildren())
                            commenters.add(new Comment(s.child("username").getValue().toString(), s.child("comment").getValue().toString()));

                    // Check for anonymous users
                    if (anonymous.equals("true")) username = "Anonymous";

                    //check vote status of complaint
                    int voteStatus = Helper.NOT_VOTED;
                    String loggedInUsername = Prefs.getUser(getContext()).getUsername();
                    if (ds.child("listOfUpvoters").hasChild(loggedInUsername))
                        voteStatus = Helper.UPVOTED;
                    if (ds.child("listOfDownvoters").hasChild(loggedInUsername))
                        voteStatus = Helper.DOWNVOTED;


                    // add complaint to list
                    list.add(new Complaints(complaintId, username, uid, subject, body, category, subcategory, visibility, status,
                            anonymous, upvotes, downvotes, commenters, upvoters, downvoters, map, time, voteStatus));

                }

                if (list.isEmpty()) {
                    /*shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);*/
                    Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                    return;
                }
                adapter.notifyDataSetChanged();
                /*shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void upVoteClicked(Complaints complaint) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference upVotesRef = databaseReference.child("Complaints").child(complaint.getComplaintId()).child("listOfUpvoters");
        DatabaseReference downVotesRef = databaseReference.child("Complaints").child(complaint.getComplaintId()).child("listOfDownvoters");
        DatabaseReference complaintRef = databaseReference.child("Complaints").child(complaint.getComplaintId());

        downVotesRef.child(Prefs.getUser(getContext()).getUsername()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Vote upvote = new Vote(complaint.getComplaintId(), Prefs.getUser(getContext()).getUsername());
                    upVotesRef.child(Prefs.getUser(getContext()).getUsername()).setValue(upvote).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    @Override
    public void downVoteClicked(Complaints complaint) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference upVotesRef = databaseReference.child("Complaints").child(complaint.getComplaintId()).child("listOfUpvoters");
        DatabaseReference downVotesRef = databaseReference.child("Complaints").child(complaint.getComplaintId()).child("listOfDownvoters");
        DatabaseReference complaintRef = databaseReference.child("Complaints").child(complaint.getComplaintId());

        upVotesRef.child(Prefs.getUser(getContext()).getUsername()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Vote downvote = new Vote(complaint.getComplaintId(), Prefs.getUser(getContext()).getUsername());
                    downVotesRef.child(Prefs.getUser(getContext()).getUsername()).setValue(downvote).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    @Override
    public void commentsClicked(Complaints complaint) {
        CommentsDialogFragment commentsDialogFragment = new CommentsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("complaint_id", complaint.getComplaintId());
        bundle.putString("complaint_title", complaint.getSubject());
        commentsDialogFragment.setArguments(bundle);
        commentsDialogFragment.show(getChildFragmentManager(), "TAG");
    }

    @Override
    public void onCardClicked(Complaints complaints) {
        Intent intent = new Intent(getContext(), ConversationActivity.class);
        intent.putExtra("complaint", complaints);
        startActivity(intent);
    }

    @Override
    public void usernameClicked(Complaints complaint) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_profile,null));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        CircularImageView imageView=alertDialog.findViewById(R.id.profile_picture1);
        TextView username=alertDialog.findViewById(R.id.username1);
        TextView displayName=alertDialog.findViewById(R.id.display_name1);
        TextView mail=alertDialog.findViewById(R.id.mailid1);
        Button close=alertDialog.findViewById(R.id.closeButton);

        String uid=complaint.getUid();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("StudentUsers").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username.setText(snapshot.child("username").getValue().toString());
                displayName.setText(snapshot.child("displayName").getValue().toString());
                mail.setText(snapshot.child("email").getValue().toString());
                Glide.with(getContext()).load(snapshot.child("profileUri").getValue().toString()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    public void loadCardData(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Complaints");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                total.setText(Long.toString(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query ref= FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("status").equalTo("RESOLVED");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resolved.setText(Long.toString(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref=  FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("status").equalTo("IN-PROGRESS");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                inprogress.setText(Long.toString(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref=  FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("status").equalTo("PENDING");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pending.setText(Long.toString(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}