package com.example.appitup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SignIn extends AppCompatActivity {
    FirebaseAuth mAuth;
    TextInputLayout username, password;
    Button signInButton;
    String userNameStr;
    TextView forgot_password, signup;
    TextView progressDialogueTitle;
    PullInLoader progressDialogueLoader;
    MaterialButton progressDialogueDismissButton;
    AlertDialog alertDialogProgress;

    @Override
    protected void onStart() {
        if (mAuth.getCurrentUser() != null && Prefs.isUserLoggedIn(this) && Prefs.getUser(this).getUsername() != null) {
            startActivity(new Intent(SignIn.this, MainActivity.class));
        }
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.textInputUsername2);
        password = findViewById(R.id.textInputPassword2);
        signInButton = findViewById(R.id.button);
        forgot_password = findViewById(R.id.textView2);
        signup = findViewById(R.id.textView);

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
                finish();
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, ResetPassword.class);
                startActivity(intent);
                finish();
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

            }
        });

    }

    private User getUserModelFromDS(DataSnapshot ds) {
        User user = new User((String) ds.child("username").getValue(), (String) ds.child("email").getValue(),
                (String) ds.child("displayName").getValue(), (String) ds.child("profileUri").getValue(), (String) ds.child("uid").getValue());
        return user;
    }

    public void check_admin(String username, String passwords) {
        Query query = FirebaseDatabase.getInstance().getReference("AdminUsers").orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        User user = getUserModelFromDS(ds);
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
                    if (Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified()) {
                        alertDialogProgress.dismiss();
                        Prefs.setUserData(SignIn.this, user);
                        Prefs.setUserLoggedIn(SignIn.this, true);

                        Intent intent = new Intent(SignIn.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        Helper.toast(SignIn.this, "Signed In Success!!");
                    } else {
                        FirebaseAuth.getInstance().signOut();
                        Prefs.setUserLoggedIn(SignIn.this, false);
                        setResultsUI("Email Id Not Verified\nPlease verify you mail id by clicking on the link sent to your email when you had created the account");
                    }
                } else setResultsUI(task.getException().getMessage());
            }
        });
    }


}