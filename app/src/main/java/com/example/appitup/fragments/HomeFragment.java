package com.example.appitup.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agrawalsuneet.dotsloader.loaders.PullInLoader;
import com.bumptech.glide.Glide;
import com.example.appitup.Database.Prefs;
import com.example.appitup.R;
import com.example.appitup.activities.ConversationActivity;
import com.example.appitup.adapter.ComplaintsAdapter;
import com.example.appitup.models.Comment;
import com.example.appitup.models.Complaints;
import com.example.appitup.utility.Helper;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
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
import java.util.Queue;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.example.appitup.utility.Helper.IN_PROGRESS;
import static com.example.appitup.utility.Helper.PENDING;
import static com.example.appitup.utility.Helper.RESOLVED;
import static java.text.DateFormat.getDateTimeInstance;

public class HomeFragment extends Fragment implements ComplaintsAdapter.ComplaintsListener  {
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
    String category=null;
    int j;

    public HomeFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);

        adapter = new ComplaintsAdapter(getContext(), list);
        //((LinearLayoutManager) layoutManager).setReverseLayout(true);
        //((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        Prefs.setFilter_selectedChip(getContext(),-1);
        loadData();

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if(Prefs.getFilter_selectedChip(getContext())==-1)
                loadData();
                else {
                    int i=Prefs.getFilter_selectedChip(getContext());
                    if(i==0)category="Registration";
                    else if(i==1)category="Academics";else if(i==2)category="DSW";
                    else if(i==3)category="Vendors Of ISM";else if(i==4)category="MIS/Parents Portal";
                    else if(i==5)category="Hostel";else if(i==6)category="Health Centre";
                    else if(i==7)category="Library";else if(i==8)category="Personal";
                    firebase_query(category);
                }
                adapter.notifyDataSetChanged();
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        adapter.setUpOnComplaintListener(this);

        filter_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter_icon();
            }
        });

        return view;
    }

    private void loadData() {
        //showProgressDialogue();
        if(Prefs.getUser(getContext()).getUserType()==Helper.USER_ADMINISTRATOR){
            loadAllDataAdmin();
            return;
        }
        // after getting data from firebase add this to list and cal ' adapter.notifyDataSetChanged(); '
        Query query= FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("visibility").equalTo("public");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
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
                    String time=null;
                    long timeStamp= 0;
                    Map<String, Long> map=new HashMap();

                    if(ds.child("timeStampmap").child("timeStamp").exists())
                    {
                        timeStamp= (long) ds.child("timeStampmap").child("timeStamp").getValue();
                        DateFormat dateFormat = getDateTimeInstance();
                        Date netDate = (new Date(timeStamp));
                        time= dateFormat.format(netDate);
                        map.put("timeStamp",timeStamp);
                    }
                    ArrayList<String> upvoters = new ArrayList<>();
                    for (DataSnapshot s : ds.child("listOfUpVoter").getChildren())
                        upvoters.add(s.getValue().toString());
                    ArrayList<String> downvoters = new ArrayList<>();
                    for (DataSnapshot s : ds.child("listOfDownVoter").getChildren())
                        downvoters.add(s.getValue().toString());
                    ArrayList<Comment> commenters = new ArrayList<>();
                    for (DataSnapshot s : ds.child("listOfCommenter").getChildren())
                        commenters.add(new Comment(s.child("username").getValue().toString(), s.child("comment").getValue().toString()));

                    //if (anonymous.equals("true")) username = "Anonymous";
                    list.add(new Complaints(complaintId, username, uid, subject, body, category, subcategory, visibility, status, anonymous, upvoters, downvoters, commenters,map,time));

                }
                if (list.isEmpty()) {
                    Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(chipGroup.getCheckedChipId()==R.id.pending){
                    adapter.getFilter().filter(PENDING);
                }
                if(chipGroup.getCheckedChipId()==R.id.inprogress){
                    adapter.getFilter().filter(IN_PROGRESS);
                }
                if(chipGroup.getCheckedChipId()==R.id.resolved){
                    adapter.getFilter().filter(RESOLVED);
                }
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void upVoteClicked(Complaints complaint) {
        if(Prefs.getUser(getContext()).getUserType()==Helper.USER_STUDENT) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Complaints").child(complaint.getComplaintId());
            String username = Prefs.getUser(getContext()).getUsername();
            complaint.getListOfUpVoter().remove(username);
            complaint.getListOfDownVoter().remove(username);
            complaint.getListOfUpVoter().add(username);
            reference.updateChildren(Helper.getHashMap(complaint)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.i("Home Frag", "onComplete: Upvoted");
                    } else
                        Log.i("Home Frag", "Upvoted Error : " + task.getException().getMessage());
                }
            });
        }
    }

    @Override
    public void downVoteClicked(Complaints complaint) {
        if(Prefs.getUser(getContext()).getUserType()==Helper.USER_STUDENT) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Complaints").child(complaint.getComplaintId());
            String username = Prefs.getUser(getContext()).getUsername();
            complaint.getListOfUpVoter().remove(username);
            complaint.getListOfDownVoter().remove(username);
            complaint.getListOfDownVoter().add(username);
            reference.updateChildren(Helper.getHashMap(complaint)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.i("Home Frag", "onComplete: downvoted");
                    } else
                        Log.i("Home Frag", "downvote Error : " + task.getException().getMessage());
                }
            });
        }
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
       //TODO if(Prefs.getUser(getContext()).getUserType()==Helper.USER_ADMINISTRATOR)
        {
            Intent intent = new Intent(getContext(), ConversationActivity.class);
            intent.putExtra("complaint", complaints);
            startActivity(intent);
        }
    }

    @Override
    public void usernameClicked(Complaints complaint){
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

    public void filter_icon(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = this.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.layout_filter_dialog,null));

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button filterBtn = alertDialog.findViewById(R.id.sort);
        ChipGroup chipGroup=alertDialog.findViewById(R.id.chip_group3);
        category="Registration";

        int i=Prefs.getFilter_selectedChip(getContext());
        if(i==0)chipGroup.check(R.id.registration1);
        else if(i==1)chipGroup.check(R.id.academics1);else if(i==2)chipGroup.check(R.id.dsw1);
        else if(i==3)chipGroup.check(R.id.vendors1);else if(i==4)chipGroup.check(R.id.mis1);
        else if(i==5)chipGroup.check(R.id.hostel1);else if(i==6)chipGroup.check(R.id.health1);
        else if(i==7)chipGroup.check(R.id.library1);else if(i==8)chipGroup.check(R.id.personal1);

        j=i;
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chipGroup.getCheckedChipId()==R.id.registration1) { category = "Registration";j = 0; }
                else if(chipGroup.getCheckedChipId()==R.id.academics1){category="Academics";j=1;}
                else if(chipGroup.getCheckedChipId()==R.id.dsw1){category="DSW";j=2;}
                else if(chipGroup.getCheckedChipId()==R.id.vendors1){category="Vendors of ISM";j=3;}
                else if(chipGroup.getCheckedChipId()==R.id.mis1){category="MIS/Parents Portal";j=4;}
                else if(chipGroup.getCheckedChipId()==R.id.hostel1){category="Hostel";j=5;}
                else if(chipGroup.getCheckedChipId()==R.id.health1){category="Health Centre";j=6;}
                else if(chipGroup.getCheckedChipId()==R.id.library1){category="Library";j=7;}
                else if(chipGroup.getCheckedChipId()==R.id.personal1){category="Personal";j=8;}
                else {category="null";j=-1;}
                firebase_query(category);
                Prefs.setFilter_selectedChip(getContext(),j);
                alertDialog.dismiss();
            }
        });
    }



    public void firebase_query(String category){
        shimmerFrameLayout.startShimmer();
        if(category.equals("null"))loadData();
        list.clear();
        //adapter.notifyDataSetChanged();
        Query query=FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("category").equalTo(category);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    String status = ds.child("status").getValue().toString();
                    if (Prefs.getUser(getContext()).getUserType()==Helper.USER_STUDENT && status.equals("private"))continue;
                    String complaintId = ds.child("complaintId").getValue().toString();
                    String username = ds.child("username").getValue().toString();
                    String uid = ds.child("uid").getValue().toString();
                    String subject = ds.child("subject").getValue().toString();
                    String body = ds.child("body").getValue().toString();
                    String category = ds.child("category").getValue().toString();
                    String subcategory = ds.child("subcategory").getValue().toString();
                    String visibility = ds.child("visibility").getValue().toString();
                    String anonymous = ds.child("anonymous").getValue().toString();
                    String time=null;
                    long timeStamp= 0;
                    Map<String, Long> map=new HashMap();
                    if(ds.child("timeStampmap").child("timeStamp").exists())
                    {
                        timeStamp= (long) ds.child("timeStampmap").child("timeStamp").getValue();
                        DateFormat dateFormat = getDateTimeInstance();
                        Date netDate = (new Date(timeStamp));
                        time= dateFormat.format(netDate);
                        map.put("timeStamp",timeStamp);
                    }
                    //time+=(""+timeStamp);
                    ArrayList<String> upvoters = new ArrayList<>();
                    for (DataSnapshot s : ds.child("listOfUpVoter").getChildren())
                        upvoters.add(s.getValue().toString());
                    ArrayList<String> downvoters = new ArrayList<>();
                    for (DataSnapshot s : ds.child("listOfDownVoter").getChildren())
                        downvoters.add(s.getValue().toString());
                    ArrayList<Comment> commenters = new ArrayList<>();
                    for (DataSnapshot s : ds.child("listOfCommenter").getChildren())
                        commenters.add(new Comment(s.child("username").getValue().toString(), s.child("comment").getValue().toString()));
                    list.add(new Complaints(complaintId, username, uid, subject, body, category, subcategory, visibility, status, anonymous, upvoters, downvoters, commenters,map,time));
                }
                if (list.isEmpty()) {
                    //alertDialogProgress.dismiss();
                    Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    return;
                }
                if(chipGroup.getCheckedChipId()==R.id.pending){
                    adapter.getFilter().filter(PENDING);
                }
                if(chipGroup.getCheckedChipId()==R.id.inprogress){
                    adapter.getFilter().filter(IN_PROGRESS);
                }
                if(chipGroup.getCheckedChipId()==R.id.resolved){
                    adapter.getFilter().filter(RESOLVED);
                }
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadAllDataAdmin(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Complaints");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
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
                    String time=null;
                    long timeStamp= 0;
                    Map<String, Long> map=new HashMap();

                    if(ds.child("timeStampmap").child("timeStamp").exists())
                    {
                        timeStamp= (long) ds.child("timeStampmap").child("timeStamp").getValue();
                        DateFormat dateFormat = getDateTimeInstance();
                        Date netDate = (new Date(timeStamp));
                        time= dateFormat.format(netDate);
                        map.put("timeStamp",timeStamp);
                    }
                    ArrayList<String> upvoters = new ArrayList<>();
                    for (DataSnapshot s : ds.child("listOfUpVoter").getChildren())
                        upvoters.add(s.getValue().toString());
                    ArrayList<String> downvoters = new ArrayList<>();
                    for (DataSnapshot s : ds.child("listOfDownVoter").getChildren())
                        downvoters.add(s.getValue().toString());
                    ArrayList<Comment> commenters = new ArrayList<>();
                    for (DataSnapshot s : ds.child("listOfCommenter").getChildren())
                        commenters.add(new Comment(s.child("username").getValue().toString(), s.child("comment").getValue().toString()));

                    //if (anonymous.equals("true")) username = "Anonymous";
                    list.add(new Complaints(complaintId, username, uid, subject, body, category, subcategory, visibility, status, anonymous, upvoters, downvoters, commenters,map,time));

                }
                if (list.isEmpty()) {
                    Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(chipGroup.getCheckedChipId()==R.id.pending){
                    adapter.getFilter().filter(PENDING);
                }
                if(chipGroup.getCheckedChipId()==R.id.inprogress){
                    adapter.getFilter().filter(IN_PROGRESS);
                }
                if(chipGroup.getCheckedChipId()==R.id.resolved){
                    adapter.getFilter().filter(RESOLVED);
                }
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}