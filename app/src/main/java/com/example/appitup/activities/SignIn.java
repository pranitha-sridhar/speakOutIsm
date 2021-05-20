package com.example.appitup.activities;

import android.content.Context;
import android.content.Intent;
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
import com.example.appitup.Database.Prefs;
import com.example.appitup.R;
import com.example.appitup.models.User;
import com.example.appitup.utility.Helper;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SignIn extends AppCompatActivity {
    FirebaseAuth mAuth;
    @BindView(R.id.textInputUsernameLogin)
    TextInputLayout username;
    @BindView(R.id.textInputPassword2)
    TextInputLayout password;
    @BindView(R.id.button)
    TextView signInButton;
    String userNameStr;
    @BindView(R.id.textView2)
    TextView forgot_password;
    @BindView(R.id.textView)
    TextView signup;
    TextView progressDialogueTitle;
    PullInLoader progressDialogueLoader;
    MaterialButton progressDialogueDismissButton;
    AlertDialog alertDialogProgress;

    Unbinder unbinder;
    int userType=1000;
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
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        parentLayout = findViewById(android.R.id.content);
        unbinder = ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userNameStr = username.getEditText().getText().toString();
                String password_str = password.getEditText().getText().toString();

                if (userNameStr.isEmpty()) {
                    username.setError("Username should not be empty");
                    username.requestFocus();
                    return;
                }
                if (password_str.isEmpty()) {
                    password.setError("Password should not be empty");
                    password.requestFocus();
                    return;
                }

                showProgressDialogue();
                check_student(userNameStr, password_str);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, ResetPassword.class);
                startActivity(intent);
            }
        });
    }

    private void showProgressDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialogue_loading, null);
        builder.setView(v);
        progressDialogueTitle = v.findViewById(R.id.textViewTitle);
        progressDialogueDismissButton = v.findViewById(R.id.dismissButton);
        progressDialogueLoader = v.findViewById(R.id.progressLoader);

        progressDialogueTitle.setText("Please wait, we are verifying details...");

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

    public void check_student(String username,String passwords){
        Query query = FirebaseDatabase.getInstance().getReference("StudentUsers").orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("TAG", "onDataChange: check_student");
                if(snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        User user = getUserModelFromDS(ds);
                        user.setUserType(Helper.USER_STUDENT);
                        signIn(user, passwords);
                        break;
                    }
                }
                else{
                    check_admin(username,passwords);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                setResultsUI(error.getMessage());
            }
        });
    }

    private User getUserModelFromDS(DataSnapshot ds) {
        User user = new User((String) ds.child("username").getValue(), (String) ds.child("email").getValue(),
                (String) ds.child("displayName").getValue(), (String) ds.child("profileUri").getValue(), (String) ds.child("uid").getValue());
        if (ds.hasChild("isBlocked") && ds.child("isBlocked").getValue().equals(true))
            user.setBlocked(true);
        return user;
    }

    public void check_admin(String username, String passwords) {
        Query query = FirebaseDatabase.getInstance().getReference("AdminUsers").orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("TAG", "onDataChange: check_admin");
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        User user = getUserModelFromDS(ds);
                        userType=2000;
                        user.setUserType(Helper.USER_ADMINISTRATOR);
                        signIn(user, passwords);
                        break;
                    }
                }
                else{
                    setResultsUI("User not Registered\nPlease Create account to signIn");
                }
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


    public void signIn(User user, String password_str) {
        String email_id = user.getEmail();

        if (email_id == null || email_id.equals("Invalid") || !Patterns.EMAIL_ADDRESS.matcher(email_id).matches()) {
            setResultsUI("There is No Proper Email Id found which is Linked with this Username");
            return;
        }

        mAuth.signInWithEmailAndPassword(email_id, password_str).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if ((Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified() && userType==Helper.USER_STUDENT)||(userType==Helper.USER_ADMINISTRATOR)) {
                        if (!user.isBlocked()) {
                            alertDialogProgress.dismiss();
                            Prefs.setUserData(SignIn.this, user);
                            Prefs.setUserLoggedIn(SignIn.this, true);

                            Intent intent = new Intent(SignIn.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            Helper.toast(SignIn.this, "Signed In Success!!");
                            getFCMToken();
                        } else {
                            // user is blocked by the admin
                            Helper.signOutUser(SignIn.this, false);
                            setResultsUI("This User is blocked by the Admin.\nPlease contact to your college and ask them to unblock this user.");
                        }
                    } else {
                        Helper.signOutUser(SignIn.this, false);
                        setResultsUI("Email Id Not Verified\nPlease verify you mail id by clicking on the link sent to your email when you had created the account");
                    }
                } else setResultsUI(task.getException().getMessage());
            }
        });
    }

    private void getFCMToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.d("Task : ", "Hi getInstanceId failed * " + task.getException());
                    return;
                }
                String token = task.getResult().getToken();
                Log.d("FCM_TOKEN", token);
                saveTokenToServer(token);
            }
        });
    }

    private void saveTokenToServer(String token) {
        User user = Prefs.getUser(this);
        DatabaseReference databaseReference;
        if (user.getUserType() == Helper.USER_STUDENT)
            databaseReference = FirebaseDatabase.getInstance().getReference("StudentUsers");
        else databaseReference = FirebaseDatabase.getInstance().getReference("AdminUsers");

        databaseReference.child(user.getUid()).child("fcm_token").setValue(token).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // successfully saved
                    Prefs.getUser(SignIn.this).setFcm_token(token);
                } else {
                    // error
                }
            }
        });
    }
}