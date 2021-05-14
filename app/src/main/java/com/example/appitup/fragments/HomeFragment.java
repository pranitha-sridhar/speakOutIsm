package com.example.appitup.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agrawalsuneet.dotsloader.loaders.PullInLoader;
import com.example.appitup.Database.Prefs;
import com.example.appitup.R;
import com.example.appitup.activities.ConversationActivity;
import com.example.appitup.adapter.ComplaintsAdapter;
import com.example.appitup.models.Comment;
import com.example.appitup.models.Complaints;
import com.example.appitup.utility.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static java.text.DateFormat.getDateTimeInstance;

public class HomeFragment extends Fragment implements ComplaintsAdapter.ComplaintsListener  {
    @BindView(R.id.complaints_recycler)
    RecyclerView recyclerView;

    Unbinder unbinder;
    ArrayList<Complaints> list = new ArrayList<>();
    ComplaintsAdapter adapter;
    FirebaseAuth mAuth;

    TextView progressDialogueTitle;
    PullInLoader progressDialogueLoader;
    MaterialButton progressDialogueDismissButton;
    AlertDialog alertDialogProgress;

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
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        adapter.setUpOnComplaintListener(this);

        loadData();

        return view;
    }

    private void loadData() {
        showProgressDialogue();
        //TODO: after getting data from firebase add this to list and cal ' adapter.notifyDataSetChanged(); '
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

                    else{
                        if(ds.child("timeStampStr").exists()){
                            time=ds.child("timeStampStr").getValue().toString();
                        }
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

                    if (anonymous.equals("true")) username = "Anonymous";
                    list.add(new Complaints(complaintId, username, uid, subject, body, category, subcategory, visibility, status, anonymous, upvoters, downvoters, commenters,map,time));

                }

                //Helper.toast(getContext(),"null "+time);

                if (list.isEmpty()) {
                    alertDialogProgress.dismiss();
                    Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                    return;
                }
                adapter.notifyDataSetChanged();
                alertDialogProgress.dismiss();
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

    public void setResultsUI(String message) {
        progressDialogueTitle.setText(message);
        progressDialogueLoader.setVisibility(View.GONE);
        progressDialogueDismissButton.setVisibility(View.VISIBLE);
    }

    private void showProgressDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialogue_loading, null);
        builder.setView(v);
        progressDialogueTitle = v.findViewById(R.id.textViewTitle);
        progressDialogueDismissButton = v.findViewById(R.id.dismissButton);
        progressDialogueLoader = v.findViewById(R.id.progressLoader);

        progressDialogueTitle.setText("Loading...");

        alertDialogProgress = builder.create();
        alertDialogProgress.setCancelable(false);
        alertDialogProgress.show();

        alertDialogProgress.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        alertDialogProgress.getWindow().setBackgroundDrawable(null);
        alertDialogProgress.getWindow().setGravity(Gravity.BOTTOM);

        progressDialogueDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogProgress.dismiss();
            }
        });
    }


}