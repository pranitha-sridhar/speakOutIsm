package com.example.appitup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.example.appitup.Database.Prefs;
import com.example.appitup.R;
import com.example.appitup.adapter.ReplyAdapter;
import com.example.appitup.models.Complaints;
import com.example.appitup.models.Reply;
import com.example.appitup.utility.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ConversationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int DELETING_THE_COMPLAINT = 101;
    private static final int BLOCKING_THE_USER = 102;

    @BindView(R.id.imageViewArrowBack)
    ImageView imageViewArrowBack;
    @BindView(R.id.imageViewBlockUser)
    ImageView imageViewBlockUser;
    @BindView(R.id.imageViewDeleteConvo)
    ImageView imageViewDeleteConvo;
    @BindView(R.id.imageViewStatus)
    ImageView imageViewStatus;
    @BindView(R.id.textViewComplaintTitle)
    TextView textViewComplaintTitle;
    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    @BindView(R.id.complaints_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.send)
    ImageView send;
    @BindView(R.id.editTextMessage)
    EditText editTextMessage;
    @BindView(R.id.progressLoader)
    TashieLoader progressLoader;
    Unbinder unbinder;
    Complaints complaint;
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String msg = editTextMessage.getText().toString().trim();
            if (msg.isEmpty()) send.setVisibility(View.GONE);
            else send.setVisibility(View.VISIBLE);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    ReplyAdapter adapter;
    TashieLoader pdChangeStatusLoader, pdDeleteComplaintLoader;
    ArrayList<Reply> list = new ArrayList<>();
    AlertDialog alertDialogChangeStatus, alertDialogDeleteBlock;
    RadioGroup pdChangeStatusRadioGroup;
    TextInputLayout textInputLayoutPassword;
    MaterialButton pdChangeStatusSubmitButton, pdDialogDeleteBlockSubmitButton;
    int changeStatusState = 0, deleteBlockState = 0;
    TextView pdChangeStatusTitle, pdDialogDeleteBlockTitle, pdDialogDeleteBlockMessage;
    int flag = -1;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        unbinder = ButterKnife.bind(this);
        complaint = (Complaints) getIntent().getSerializableExtra("complaint");
        textViewComplaintTitle.setText(complaint.getSubject());

        imageViewArrowBack.setOnClickListener(this);
        imageViewBlockUser.setOnClickListener(this);
        imageViewDeleteConvo.setOnClickListener(this);
        imageViewStatus.setOnClickListener(this);
        send.setOnClickListener(this);

        initRecyclerView();
        editTextMessage.addTextChangedListener(textWatcher);
        loadReplies();
        
        /*
        TODO : Remove this comment so that right thig can work after testing completed
        if(Prefs.getUser(this).getUserType()!=Helper.USER_ADMINISTRATOR
                && !Prefs.getUser(this).getUsername().equals(complaint.getUsername())){
            editTextMessage.setEnabled(false);
            editTextMessage.setText("Only Admin or the owner of the complaint can participate to this conversation");
            editTextMessage.setTextColor(Color.BLACK);
        }
        */
    }

    private void initRecyclerView() {
        adapter = new ReplyAdapter(list, ConversationActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ConversationActivity.this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewArrowBack:
                onBackPressed();
                break;
            case R.id.imageViewStatus:
                changeStatusState = 0;
                showProgressDialogueChangeStatus();
                break;
            case R.id.imageViewDeleteConvo:
                deleteBlockState = 0;
                flag = DELETING_THE_COMPLAINT;
                showProgressDialogueDeleteBlock();
                break;
            case R.id.imageViewBlockUser:
                deleteBlockState = 0;
                flag = BLOCKING_THE_USER;
                showProgressDialogueDeleteBlock();
                break;
            case R.id.send:
                sendMessage();
                break;
        }
    }

    private void loadReplies() {
        Query query = FirebaseDatabase.getInstance().getReference("Reply").child(complaint.getComplaintId());

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String s) {
                String replyId = ds.child("reply_id").getValue().toString();
                String sent_from = ds.child("sent_from").getValue().toString();
                String conversation_id = ds.child("conversation_id").getValue().toString();
                String message = ds.child("message").getValue().toString();

                list.add(new Reply(replyId, sent_from, conversation_id, message));
                adapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(list.size());
                progressLoader.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage() {
        String message = editTextMessage.getText().toString().trim();
        editTextMessage.setText(null);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Reply").child(complaint.getComplaintId());
        String replyId = reference.push().getKey();
        Reply reply = new Reply(replyId, Prefs.getUser(this).getUsername(), complaint.getComplaintId(), message);

        reference.child(replyId).setValue(reply).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("Convo Activity", "onComplete: replied");
                } else
                    Log.i("Convo Activity", "Comment Error : " + task.getException().getMessage());
            }
        });
    }

    private void showProgressDialogueChangeStatus() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialogue_change_complaint_status, null);
        builder.setView(v);
        pdChangeStatusSubmitButton = v.findViewById(R.id.submitButton);
        pdChangeStatusLoader = v.findViewById(R.id.progressLoader);
        pdChangeStatusRadioGroup = v.findViewById(R.id.radioGroup);
        pdChangeStatusTitle = v.findViewById(R.id.textViewTitle);

        if (complaint.getStatus().equals(Helper.IN_PROGRESS))
            pdChangeStatusRadioGroup.check(R.id.radio_in_progress);
        else if (complaint.getStatus().equals(Helper.RESOLVED))
            pdChangeStatusRadioGroup.check(R.id.radio_resolved);

        alertDialogChangeStatus = builder.create();
        alertDialogChangeStatus.show();

        alertDialogChangeStatus.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        alertDialogChangeStatus.getWindow().setBackgroundDrawable(null);
        alertDialogChangeStatus.getWindow().setGravity(Gravity.BOTTOM);

        pdChangeStatusSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (changeStatusState) {
                    case 0:
                        String status = Helper.IN_PROGRESS;
                        if (pdChangeStatusRadioGroup.getCheckedRadioButtonId() == R.id.radio_resolved)
                            status = Helper.RESOLVED;
                        pdChangeStatusTitle.setText("Please wait we are changing the complaint status...");
                        pdChangeStatusLoader.setVisibility(View.VISIBLE);
                        pdChangeStatusSubmitButton.setVisibility(View.GONE);
                        pdChangeStatusRadioGroup.setVisibility(View.GONE);
                        changeStatusState = 1;
                        alertDialogChangeStatus.setCancelable(false);
                        changeStatus(status);
                        break;
                    case 2:
                        alertDialogChangeStatus.dismiss();
                        break;
                }
            }
        });
    }

    private void showProgressDialogueDeleteBlock() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialogue_delete, null);
        builder.setView(v);
        pdDialogDeleteBlockSubmitButton = v.findViewById(R.id.submitButton);
        pdDeleteComplaintLoader = v.findViewById(R.id.progressLoader);
        pdDialogDeleteBlockTitle = v.findViewById(R.id.textViewTitle);
        pdDialogDeleteBlockMessage = v.findViewById(R.id.textViewMessage);
        textInputLayoutPassword = v.findViewById(R.id.textInputPassword);

        if (flag == DELETING_THE_COMPLAINT) {
            pdDialogDeleteBlockTitle.setText("Are you sure you want to delete this complaint?");
            pdDialogDeleteBlockMessage.setText("Once deleted the following complaint cannot be restored.It will delete its whole data including it's replies, upvotes, downvotes, comments, etc.After deleting this complaint a notification will be sent to the user regarding this delete of his complaint but he will never be able to find it again from anywhere.");
        } else if (flag == BLOCKING_THE_USER) {
            pdDialogDeleteBlockTitle.setText("Are you sure you want to block this user?");
            pdDialogDeleteBlockMessage.setText("Once this user get blocked he will be logout from the logged in devices and will not be able to login again until get unblocked by the admin.");
        }
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
                        if (flag == DELETING_THE_COMPLAINT) {
                            pdDialogDeleteBlockTitle.setText("You are deleting this Complaint Completely?\nAuthenticate First!!");
                        } else if (flag == BLOCKING_THE_USER) {
                            pdDialogDeleteBlockTitle.setText("You are blocking this User?\nAuthenticate First!!");
                        }
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
                        String email = Prefs.getUser(ConversationActivity.this).getEmail();
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
                        Intent intent = new Intent(ConversationActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                }

            }
        });
    }

    private void authenticate(String pass, String email) {
        AuthCredential credentials = EmailAuthProvider.getCredential(email, pass);
        FirebaseAuth.getInstance().getCurrentUser().reauthenticate(credentials).addOnCompleteListener(ConversationActivity.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    alertDialogDeleteBlock.setCancelable(false);
                    if (flag == DELETING_THE_COMPLAINT) {
                        pdDialogDeleteBlockTitle.setText("Complaint Deleting Process");
                        pdDialogDeleteBlockMessage.setText("Authentication Success!!\nDeleting the Complaint...");
                    } else if (flag == BLOCKING_THE_USER) {
                        pdDialogDeleteBlockTitle.setText("User Blocking Process");
                        pdDialogDeleteBlockMessage.setText("Authentication Success!!\nBlocking the user...");
                    }
                    textInputLayoutPassword.setVisibility(View.GONE);
                    pdDialogDeleteBlockSubmitButton.setVisibility(View.GONE);
                    deleteBlockState = 3;
                    if (flag == DELETING_THE_COMPLAINT)
                        deleteUserComplaint();
                    else if (flag == BLOCKING_THE_USER)
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

    private void deleteUserComplaint() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setResultsDelUI("Complaint Deleted Successfully.");
            }
        }, 2000);
       /* DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Complaints").child(complaint.getComplaintId());

        reference.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    setResultsDelUI("Complaint Deleted Successfully.");
                } else setResultsDelUI("Error In Deleting Complaint : "+task.getException().getMessage());
            }
        });

        reference = FirebaseDatabase.getInstance().getReference().child("Reply").child(complaint.getComplaintId());
        reference.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("Convo Activity", "onComplete: replies deleted");
                } else Log.i("Convo Activity", "replies deleted Error : " + task.getException().getMessage());
            }
        });*/
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

    private void changeStatus(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Complaints").child(complaint.getComplaintId());
        complaint.setStatus(status);
        reference.updateChildren(Helper.getHashMap(complaint)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    setResultsUI("Complaint Status Changed Successfully");
                } else
                    setResultsUI("Faile to change status \n Error : " + task.getException().getMessage());
            }
        });
    }

    private void setResultsUI(String message) {
        changeStatusState = 2;
        pdChangeStatusLoader.setVisibility(View.GONE);
        pdChangeStatusSubmitButton.setVisibility(View.VISIBLE);
        pdChangeStatusTitle.setText(message);
        pdChangeStatusSubmitButton.setText("Dismiss");
    }
}