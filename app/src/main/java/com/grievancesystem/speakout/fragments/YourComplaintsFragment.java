package com.grievancesystem.speakout.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.grievancesystem.speakout.Database.Prefs;
import com.grievancesystem.speakout.R;
import com.grievancesystem.speakout.activities.ConversationActivity;
import com.grievancesystem.speakout.adapter.ComplaintsAdapter;
import com.grievancesystem.speakout.models.Comment;
import com.grievancesystem.speakout.models.Complaints;
import com.grievancesystem.speakout.models.Vote;
import com.grievancesystem.speakout.utility.Helper;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.grievancesystem.speakout.utility.Helper.IN_PROGRESS;
import static com.grievancesystem.speakout.utility.Helper.PENDING;
import static com.grievancesystem.speakout.utility.Helper.RESOLVED;
import static java.text.DateFormat.getDateTimeInstance;

public class YourComplaintsFragment extends Fragment implements ComplaintsAdapter.ComplaintsListener {
    @BindView(R.id.complaints_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.chip_group)
    ChipGroup chipGroup;
    @BindView(R.id.shimmer)
    ShimmerFrameLayout shimmerFrameLayout;
    @BindView(R.id.filter)
    ImageView filter_image;

    Unbinder unbinder;
    ArrayList<Complaints> list = new ArrayList<>();
    ComplaintsAdapter adapter;
    FirebaseAuth mAuth;
    String category = null;
    int j;

    public YourComplaintsFragment() {
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        unbinder = ButterKnife.bind(this, view);
        mAuth = FirebaseAuth.getInstance();

        adapter = new ComplaintsAdapter(getContext(), list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        adapter.setUpOnComplaintListener(this);

        Prefs.setFilter_selectedChip(getContext(), -1);
        loadData();

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (adapter != null && chipGroup != null) {

                    ((Chip) chipGroup.getChildAt(0)).setTextColor(Color.BLACK);
                    ((Chip) chipGroup.getChildAt(1)).setTextColor(Color.BLACK);
                    ((Chip) chipGroup.getChildAt(2)).setTextColor(Color.BLACK);

                    if (chipGroup.getCheckedChipId() == R.id.pending) {
                        adapter.getFilter().filter(PENDING);
                        ((Chip) chipGroup.getChildAt(0)).setTextColor(Color.WHITE);
                    } else if (chipGroup.getCheckedChipId() == R.id.inprogress) {
                        adapter.getFilter().filter(IN_PROGRESS);
                        ((Chip) chipGroup.getChildAt(1)).setTextColor(Color.WHITE);
                    } else if (chipGroup.getCheckedChipId() == R.id.resolved) {
                        adapter.getFilter().filter(RESOLVED);
                        ((Chip) chipGroup.getChildAt(2)).setTextColor(Color.WHITE);
                    }
                }
            }
        });

        /*
        TODO : Implement Later
        filter_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter_icon();
            }
        });
         */

        return view;
    }

    private void loadData() {
        list.clear();
        adapter.getFilter().filter("");

        Query query = FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("uid").equalTo(mAuth.getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) return;
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String complaintId = String.valueOf(ds.child("complaintId").getValue());
                    String username = String.valueOf(ds.child("username").getValue());
                    String uid = String.valueOf(ds.child("uid").getValue());
                    String subject = String.valueOf(ds.child("subject").getValue());
                    String body = String.valueOf(ds.child("body").getValue());
                    String category = String.valueOf(ds.child("category").getValue());
                    String subcategory = String.valueOf(ds.child("subcategory").getValue());
                    String visibility = String.valueOf(ds.child("visibility").getValue());
                    String status = String.valueOf(ds.child("status").getValue());
                    String anonymous = String.valueOf(ds.child("anonymous").getValue());

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

                if (adapter != null && chipGroup != null && shimmerFrameLayout != null) {
                    adapter.notifyDataSetChanged();
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);

                    if (list.isEmpty()) {
                        Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (chipGroup.getCheckedChipId() == R.id.pending) {
                        adapter.getFilter().filter(PENDING);
                    }
                    if (chipGroup.getCheckedChipId() == R.id.inprogress) {
                        adapter.getFilter().filter(IN_PROGRESS);
                    }
                    if (chipGroup.getCheckedChipId() == R.id.resolved) {
                        adapter.getFilter().filter(RESOLVED);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (shimmerFrameLayout != null) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    Helper.toast(getContext(), error.getMessage());
                }
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
        bundle.putSerializable("complaint", complaint);
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
        // Users own complaint no need to show details
    }

    @Override
    public void shareClicked(Complaints complaint) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Speak Out");
        String shareMessage = "\n*SpeakOut*\nGo through this grievance and react\n";
        shareMessage = shareMessage + "https://grievancesystem.speakout/complaint/?complaintId="+complaint.getComplaintId()+"\n\n";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "Choose One"));
    }

    public void filter_icon() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.layout_filter_dialog, null));

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        alertDialog.getWindow().setBackgroundDrawable(null);
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);
        alertDialog.setCancelable(false);
        alertDialog.show();

        Button filterBtn = alertDialog.findViewById(R.id.sort);
        ChipGroup chipGroup = alertDialog.findViewById(R.id.chip_group3);
        category = "no_filter";

        int i = Prefs.getFilter_selectedChip(getContext());
        if (i == -1) chipGroup.check(R.id.no_filter);
        else if (i == 0) chipGroup.check(R.id.registration1);
        else if (i == 1) chipGroup.check(R.id.academics1);
        else if (i == 2) chipGroup.check(R.id.dsw1);
        else if (i == 3) chipGroup.check(R.id.vendors1);
        else if (i == 4) chipGroup.check(R.id.mis1);
        else if (i == 5) chipGroup.check(R.id.hostel1);
        else if (i == 6) chipGroup.check(R.id.health1);
        else if (i == 7) chipGroup.check(R.id.library1);
        else if (i == 8) chipGroup.check(R.id.personal1);

        j = i;
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chipGroup.getCheckedChipId() == R.id.no_filter) {
                    category = "no_filter";
                    j = -1;
                } else if (chipGroup.getCheckedChipId() == R.id.registration1) {
                    category = "Registration";
                    j = 0;
                } else if (chipGroup.getCheckedChipId() == R.id.academics1) {
                    category = "Academics";
                    j = 1;
                } else if (chipGroup.getCheckedChipId() == R.id.dsw1) {
                    category = "DSW";
                    j = 2;
                } else if (chipGroup.getCheckedChipId() == R.id.vendors1) {
                    category = "Vendors of ISM";
                    j = 3;
                } else if (chipGroup.getCheckedChipId() == R.id.mis1) {
                    category = "MIS/Parents Portal";
                    j = 4;
                } else if (chipGroup.getCheckedChipId() == R.id.hostel1) {
                    category = "Hostel";
                    j = 5;
                } else if (chipGroup.getCheckedChipId() == R.id.health1) {
                    category = "Health Centre";
                    j = 6;
                } else if (chipGroup.getCheckedChipId() == R.id.library1) {
                    category = "Library";
                    j = 7;
                } else if (chipGroup.getCheckedChipId() == R.id.personal1) {
                    category = "Personal";
                    j = 8;
                }
                adapter.getFilter().filter(category);
                Prefs.setFilter_selectedChip(getContext(), j);
                alertDialog.dismiss();
            }
        });
    }

    public void firebase_query(String category) {
        list.clear();
        adapter.getFilter().filter("");
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmer();
        if (category.equals("no_filter")) {
            loadData();
            return;
        }
        Query query = FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("category").equalTo(category);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) return;
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String status = String.valueOf(ds.child("status").getValue());
                    if (Prefs.getUser(getContext()).getUserType() == Helper.USER_STUDENT && status.equals("private"))
                        continue;
                    String complaintId = String.valueOf(ds.child("complaintId").getValue());
                    String username = String.valueOf(ds.child("username").getValue());
                    String uid = String.valueOf(ds.child("uid").getValue());
                    String subject = String.valueOf(ds.child("subject").getValue());
                    String body = String.valueOf(ds.child("body").getValue());
                    String category = String.valueOf(ds.child("category").getValue());
                    String subcategory = String.valueOf(ds.child("subcategory").getValue());
                    String visibility = String.valueOf(ds.child("visibility").getValue());
                    String anonymous = String.valueOf(ds.child("anonymous").getValue());

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
                if (adapter != null && chipGroup != null && shimmerFrameLayout != null) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);

                    if (list.isEmpty()) {
                        Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (chipGroup.getCheckedChipId() == R.id.pending) {
                        adapter.getFilter().filter(PENDING);
                    }
                    if (chipGroup.getCheckedChipId() == R.id.inprogress) {
                        adapter.getFilter().filter(IN_PROGRESS);
                    }
                    if (chipGroup.getCheckedChipId() == R.id.resolved) {
                        adapter.getFilter().filter(RESOLVED);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (shimmerFrameLayout != null) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    Helper.toast(getContext(), error.getMessage());
                }
            }
        });
    }
}