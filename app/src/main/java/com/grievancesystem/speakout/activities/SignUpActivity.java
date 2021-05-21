package com.grievancesystem.speakout.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.agrawalsuneet.dotsloader.loaders.PullInLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.grievancesystem.speakout.R;
import com.grievancesystem.speakout.models.User;
import com.grievancesystem.speakout.utility.Helper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SignUpActivity extends AppCompatActivity {
    @BindView(R.id.textInputUsername)
    TextInputLayout textInputUsername;
    @BindView(R.id.textInputEmail)
    TextInputLayout textInputEmail;
    @BindView(R.id.textInputDisplayName)
    TextInputLayout textInputDisplayName;
    @BindView(R.id.button_signup)
    TextView buttonSignUp;
    @BindView(R.id.textInputPassword)
    TextInputLayout textInputPassword;
    //  @BindView(R.id.rGroupUserType)
    //  RadioGroup rGroupUserType;
    TextView term_conditions;
    TextView progressDialogueTitle;
    PullInLoader progressDialogueLoader;
    MaterialButton progressDialogueDismissButton;

    Unbinder unbinder;
    int userType = 1; // 1->student : 2->administrator
    AlertDialog alertDialogProgress;
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
    private FirebaseAuth mAuth;

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
        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        parentLayout = findViewById(android.R.id.content);
        unbinder = ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

       /* rGroupUserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.student) {
                    userType = 1;
                    textInputUsername.setHelperText("Admission number");
                } else if (checkedId == R.id.administrator) {
                    userType = 2;
                    textInputUsername.setHelperText("Employee ID");
                }

            }
        });*/
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = textInputUsername.getEditText().getText().toString().trim();
                String email = textInputEmail.getEditText().getText().toString().trim();
                String name = textInputDisplayName.getEditText().getText().toString().trim();
                String password = textInputPassword.getEditText().getText().toString().trim();

                if (userName.isEmpty()) {
                    textInputUsername.setError("User Name is Required");
                    textInputUsername.requestFocus();
                    return;
                }  /*else if (userType == 2 && !isEmployeeId(userName)) {
                    textInputUsername.setError("User Name is Not Valid");
                    textInputUsername.requestFocus();
                    return;
                } else textInputUsername.setError(null);*/

                if (email.isEmpty()) {
                    textInputEmail.setError("Email is Required");
                    textInputEmail.requestFocus();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    textInputEmail.setError("Email ID is invalid");
                    textInputEmail.requestFocus();
                    return;
                } else textInputEmail.setError(null);

                if (name.isEmpty()) {
                    textInputDisplayName.setError("Name is Required");
                    textInputDisplayName.requestFocus();
                    return;
                } else textInputDisplayName.setError(null);

                if (password.isEmpty()) {
                    textInputPassword.setError("Password is Required");
                    textInputPassword.requestFocus();
                    return;
                } else textInputPassword.setError(null);

                if (!email.endsWith(".iitism.ac.in")) {
                    textInputEmail.setError("Only ISM mail ids are allowed");
                    textInputEmail.requestFocus();
                    return;
                } else textInputEmail.setError(null);

                if (!email.toLowerCase().contains(userName.toLowerCase())) {
                    textInputUsername.setError("Admission Number should be set as User Name");
                    textInputUsername.requestFocus();
                    return;
                } else textInputUsername.setError(null);

                showProgressDialogue();
                checkIsAlreadyExist(userName, email, name, password);

            }
        });
    }

    private void signUp(String userName, String email, String name, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                setResultsUI("User Registered Successfully!! We had sent a verification link to email. Please verify your email to Login");
                            } else
                                setResultsUI("User Registered Successfully. But failed to send verification Link to Email.\n Error : " + task.getException().getMessage());
                            FirebaseAuth.getInstance().signOut();
                        }
                    });
                    createUserInDB(userName, email, name, mAuth.getCurrentUser().getUid());
                } else {
                    setResultsUI(task.getException().getMessage());
                }

            }
        });
    }

    private void createUserInDB(String userName, String email, String name, String uid) {
        User user = new User(userName, email, name, "", uid);
        if (userType == 1) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("StudentUsers");
            db.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // User Data saved in DB
                        Log.i("TAG", "onComplete: Data Added");
                    } else {
                        Log.i("TAG", "onComplete: Error - " + task.getException().getMessage());
                    }
                }
            });
        } /*else {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("AdminUsers");
            db.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // User Data saved in DB
                        Log.i("TAG", "onComplete: Data Added");
                    } else {
                        Log.i("TAG", "onComplete: Error - " + task.getException().getMessage());
                    }
                }
            });
        }*/
    }

    /*private boolean isEmployeeId(String userName) {
        return true;
    }*/

    private void checkIsAlreadyExist(String userName, String email, String name, String password) {
        Query query = FirebaseDatabase.getInstance().getReference("StudentUsers").orderByChild("username").equalTo(userName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    setResultsUI("This username already exist.");
                else
                    signUp(userName, email, name, password);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setResultsUI(error.getMessage());
            }
        });
    }

    public void setResultsUI(String message) {
        progressDialogueTitle.setText(message);
        progressDialogueLoader.setVisibility(View.GONE);
        progressDialogueDismissButton.setVisibility(View.VISIBLE);
    }

    private void showProgressDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogue_loading, null);
        builder.setView(view);
        progressDialogueTitle = view.findViewById(R.id.textViewTitle);
        progressDialogueDismissButton = view.findViewById(R.id.dismissButton);
        progressDialogueLoader = view.findViewById(R.id.progressLoader);

        progressDialogueTitle.setText("Please wait, we are creating your Account...");

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