package com.example.appitup.fragments;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.example.appitup.Database.Prefs;
import com.example.appitup.R;
import com.example.appitup.activities.ConversationActivity;
import com.example.appitup.activities.MainActivity;
import com.example.appitup.adapter.ComplaintsAdapter;
import com.example.appitup.adapter.UsersAdapter;
import com.example.appitup.models.Complaints;
import com.example.appitup.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AllUsersFragment extends Fragment implements UsersAdapter.UsersListener {
    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    Unbinder unbinder;
    ArrayList<User> list = new ArrayList<>();
    UsersAdapter adapter;
    FirebaseAuth mAuth;
    AlertDialog alertDialog,alertDialogDeleteBlock;
    TextView pdChangeStatusTitle, pdDialogDeleteBlockTitle, pdDialogDeleteBlockMessage;
    MaterialButton pdChangeStatusSubmitButton, pdDialogDeleteBlockSubmitButton;
    TashieLoader pdChangeStatusLoader, pdDeleteComplaintLoader;
    TextInputLayout textInputLayoutPassword;
    int deleteBlockState = 0;


    public AllUsersFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_all_users, container, false);
        unbinder = ButterKnife.bind(this, view);

        adapter = new UsersAdapter(getContext(), list);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadData();

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        adapter.setUpOnUserListener(this);


        return view;
    }

    @Override
    public void optionsClicked(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialogue_all_users_options, null);
        builder.setView(v);

        TextView block=v.findViewById(R.id.block);
        alertDialog=builder.create();
        alertDialog.show();

        alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        alertDialog.getWindow().setGravity(Gravity.BOTTOM);

        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialogueDeleteBlock();
            }
        });
    }

    public  void loadData(){
        list.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("StudentUsers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    String username=ds.child("username").getValue().toString();
                    String mail=ds.child("email").getValue().toString();
                    String display=ds.child("displayName").getValue().toString();
                    String uid=ds.child("uid").getValue().toString();
                    String profileUri=null;
                    if(ds.child("profileUri").getValue()!=null)profileUri=ds.child("profileUri").getValue().toString();
                    int userType= Integer.parseInt( ds.child("userType").getValue().toString());
                    list.add(new User(username,mail,display,profileUri,uid,userType));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void showProgressDialogueDeleteBlock() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialogue_delete, null);
        builder.setView(v);
        pdDialogDeleteBlockSubmitButton = v.findViewById(R.id.submitButton);
        pdDeleteComplaintLoader = v.findViewById(R.id.progressLoader);
        pdDialogDeleteBlockTitle = v.findViewById(R.id.textViewTitle);
        pdDialogDeleteBlockMessage = v.findViewById(R.id.textViewMessage);
        textInputLayoutPassword = v.findViewById(R.id.textInputPassword);

        pdDialogDeleteBlockTitle.setText("Are you sure you want to block this user?");
        pdDialogDeleteBlockMessage.setText("Once this user get blocked he will be logout from the logged in devices and will not be able to login again until get unblocked by the admin.");

        alertDialogDeleteBlock = builder.create();
        alertDialogDeleteBlock.show();

        alertDialogDeleteBlock.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        alertDialogDeleteBlock.getWindow().setBackgroundDrawable(null);
        alertDialogDeleteBlock.getWindow().setGravity(Gravity.BOTTOM);

        pdDialogDeleteBlockSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (deleteBlockState) {
                    case 0:
                            pdDialogDeleteBlockTitle.setText("You are blocking this User?\nAuthenticate First!!");

                        pdDialogDeleteBlockMessage.setText("For the Security reasons please retype your password for Authentication.");
                        textInputLayoutPassword.setVisibility(View.VISIBLE);
                        deleteBlockState = 1;
                        break;
                    case 1:
                        String pass = textInputLayoutPassword.getEditText().getText().toString().trim();
                        if (pass.isEmpty()) {
                            textInputLayoutPassword.setError("Password is Required");
                            textInputLayoutPassword.requestFocus();
                            return;
                        } else textInputLayoutPassword.setError(null);

                        alertDialogDeleteBlock.setCancelable(false);
                        String email = Prefs.getUser(getContext()).getEmail();
                        if (email == null || email.isEmpty()) {
                            deleteBlockState = 3;
                            textInputLayoutPassword.setVisibility(View.GONE);
                            pdDeleteComplaintLoader.setVisibility(View.GONE);
                            pdDialogDeleteBlockMessage.setVisibility(View.GONE);
                            pdDialogDeleteBlockTitle.setText("Oops Error occurred unable to find you email. Please logout and login again.");
                            pdDialogDeleteBlockSubmitButton.setText("Dismiss");
                            return;
                        }

                        pdDialogDeleteBlockMessage.setText("Please Wait We are verifying the details...");
                        pdDeleteComplaintLoader.setVisibility(View.VISIBLE);
                        textInputLayoutPassword.setVisibility(View.GONE);
                        pdDialogDeleteBlockSubmitButton.setVisibility(View.GONE);
                        authenticate(pass, email);
                        break;
                    case 3:
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                }

            }
        });
    }

    private void authenticate(String pass, String email) {
        AuthCredential credentials = EmailAuthProvider.getCredential(email, pass);
        FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credentials).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    alertDialogDeleteBlock.setCancelable(false);

                        pdDialogDeleteBlockTitle.setText("User Blocking Process");
                        pdDialogDeleteBlockMessage.setText("Authentication Success!!\nBlocking the user...");

                    textInputLayoutPassword.setVisibility(View.GONE);
                    pdDialogDeleteBlockSubmitButton.setVisibility(View.GONE);
                    deleteBlockState = 3;
                        blockUser();
                } else setResultsDelUI(task.getException().getMessage());
            }
        });
    }

    private void blockUser() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setResultsDelUI("Blocked the User Successfully.");
            }
        }, 2000);
    }

    private void setResultsDelUI(String message) {
        if (deleteBlockState == 1) {
            alertDialogDeleteBlock.setCancelable(true);
            pdDialogDeleteBlockSubmitButton.setVisibility(View.VISIBLE);
            textInputLayoutPassword.setVisibility(View.VISIBLE);
            pdDeleteComplaintLoader.setVisibility(View.GONE);
            pdDialogDeleteBlockMessage.setText(message);
        } else if (deleteBlockState == 3) {
            textInputLayoutPassword.setVisibility(View.GONE);
            pdDeleteComplaintLoader.setVisibility(View.GONE);
            pdDialogDeleteBlockMessage.setText(message);
            pdDialogDeleteBlockSubmitButton.setText("Dismiss");
            pdDialogDeleteBlockSubmitButton.setVisibility(View.VISIBLE);
        }
    }
}