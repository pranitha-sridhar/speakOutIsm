package com.example.appitup.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appitup.Database.Prefs;
import com.example.appitup.R;
import com.example.appitup.adapter.ComplaintsAdapter;
import com.example.appitup.models.Comment;
import com.example.appitup.models.Complaints;
import com.example.appitup.utility.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends Fragment implements ComplaintsAdapter.ComplaintsListener {
    @BindView(R.id.complaints_recycler)
    RecyclerView recyclerView;

    Unbinder unbinder;
    ArrayList<Complaints> list = new ArrayList<>();
    ComplaintsAdapter adapter;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth=FirebaseAuth.getInstance();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        progressBar=view.findViewById(R.id.progress);

        adapter = new ComplaintsAdapter(getContext(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        adapter.setUpOnComplaintListener(this);

        loadData();

        return view;
    }

    private void loadData() {
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
                    ArrayList<String> upvoters = new ArrayList<>();
                    for (DataSnapshot s : ds.child("listOfUpVoter").getChildren())
                        upvoters.add(s.getValue().toString());
                    ArrayList<String> downvoters = new ArrayList<>();
                    for (DataSnapshot s : ds.child("listOfDownVoter").getChildren())
                        downvoters.add(s.getValue().toString());
                    ArrayList<Comment> commenters = new ArrayList<>();
                    for (DataSnapshot s : ds.child("listOfCommenter").getChildren())
                        commenters.add(new Comment(s.child("username").getValue().toString(), s.child("comment").getValue().toString()));

                    if (anonymous.equals("true")) username = "Anonymous";
                    list.add(new Complaints(complaintId, username, uid, subject, body, category, subcategory, visibility, status, anonymous, upvoters, downvoters, commenters));

                }
                if (list.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                    return;
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void upVoteClicked(Complaints complaint) {
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
                } else Log.i("Home Frag", "Upvoted Error : " + task.getException().getMessage());
            }
        });
    }

    @Override
    public void downVoteClicked(Complaints complaint) {
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
                } else Log.i("Home Frag", "downvote Error : " + task.getException().getMessage());
            }
        });
    }

    @Override
    public void commentsClicked(Complaints complaint) {

    }
}