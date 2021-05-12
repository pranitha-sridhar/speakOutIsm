package com.example.appitup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.agrawalsuneet.dotsloader.loaders.PullInLoader;
import com.example.appitup.R;
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

public class SignIn extends AppCompatActivity {
FirebaseAuth mAuth;
TextInputLayout username,password;
Button signin;
ProgressBar progressBar;
String username_str;
int userType=1;
int type=1;
TextView forgot_password, signup;
TextView progressDialogueTitle;
PullInLoader progressDialogueLoader;
MaterialButton progressDialogueDismissButton;
AlertDialog alertDialogProgress;

    @Override
    protected void onStart() {
        super.onStart();
        {
            if (mAuth.getCurrentUser() != null) {
                    if(getSharedPreferences("logged",MODE_PRIVATE).getInt("type",0)==1){
                        startActivity(new Intent(SignIn.this, MainActivity.class));
                        finish();
                    }
                    else if(getSharedPreferences("logged",MODE_PRIVATE).getInt("type",0)==2){
                        Toast.makeText(this, "Admin Page", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(SignIn.this, MainActivity.class));
                        //finish();
                    }
                    /*DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("StudentUsers");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                               //startActivity(new Intent(SignIn.this, MainActivity.class));
                            } else {
                               startActivity(new Intent(SignIn.this, MainActivity.class));
                               finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });*/
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        type=getSharedPreferences("logged",MODE_PRIVATE).getInt("type",0);

        mAuth=FirebaseAuth.getInstance();
        username=findViewById(R.id.textInputUsername2);
        password=findViewById(R.id.textInputPassword2);
        signin=findViewById(R.id.button);
        progressBar=findViewById(R.id.progressBar);
        forgot_password=findViewById(R.id.textView2);
        signup=findViewById(R.id.textView);

       //Toast.makeText(this, ""+mAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username_str=username.getEditText().getText().toString();
                String password_str=password.getEditText().getText().toString();
                //final String[] email_id = new String[1];
                //Toast.makeText(SignIn.this, ""+username_str, Toast.LENGTH_SHORT).show();
                if(username_str.isEmpty()){
                    username.setError("Username should not be empty");
                    username.requestFocus();
                    return;
                }
                if (password_str.isEmpty()) {
                    password.setError("Password should not be empty");
                    password.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                /*AlertDialog.Builder builder = new AlertDialog.Builder(SignIn.this);
                LayoutInflater inflater = getParent().getLayoutInflater();
                View v = inflater.inflate(R.layout.dialogue_loading, null);
                builder.setView(v);
                progressDialogueTitle = v.findViewById(R.id.textViewTitle);
                progressDialogueDismissButton = v.findViewById(R.id.dismissButton);
                progressDialogueLoader = v.findViewById(R.id.progressLoader);

                progressDialogueTitle.setText("Please wait, logging in...");

                alertDialogProgress = builder.create();
                alertDialogProgress.setCancelable(false);
                alertDialogProgress.show();

                alertDialogProgress.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                alertDialogProgress.getWindow().setBackgroundDrawable(null);
                alertDialogProgress.getWindow().setGravity(Gravity.BOTTOM);*/

                check_student(username_str,password_str);

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


    public void check_student(String username,String passwords){
        final String[] mail = new String[1];
        Query query = FirebaseDatabase.getInstance().getReference("StudentUsers").orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        mail[0] = ds.child("email").getValue().toString();
                        //Toast.makeText(SignIn.this, mail[0], Toast.LENGTH_SHORT).show();
                        signin(mail[0],passwords);
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

    public void check_admin(String username,String passwords){
        final String[] mail = new String[1];
        Query query = FirebaseDatabase.getInstance().getReference("AdminUsers").orderByChild("username").equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()) {
                    userType=2;
                    mail[0] =snapshot.child("email").getValue().toString();
                    signin(mail[0],passwords);
                    }
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SignIn.this, "User not registered", Toast.LENGTH_SHORT).show();
                    //setResultsUI("User not registered");
                    mail[0]="Invalid";
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


    public void signin(String email_id,String password_str){


        if(email_id ==null) {
            Toast.makeText(SignIn.this, "Null", Toast.LENGTH_SHORT).show();
            return;
        }
        if(email_id.equals("Invalid")) {
            Toast.makeText(SignIn.this, "Invalid", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email_id).matches()) {
            username.setError("Username is invalid");
            username.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email_id,password_str).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if (mAuth.getCurrentUser().isEmailVerified()) {
                        progressBar.setVisibility(View.GONE);
                        //alertDialogProgress.dismiss();
                        getSharedPreferences("username",MODE_PRIVATE).edit().putString("username",username_str).apply();
                        finish();
                        //setResultsUI("Signed In");
                        if(userType==1) {
                            getSharedPreferences("logged", MODE_PRIVATE).edit().putInt("type", 1).apply();
                            type=1;
                            Intent intent = new Intent(SignIn.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else {
                            getSharedPreferences("logged", MODE_PRIVATE).edit().putInt("type", 2).apply();
                            type=2;
                            //Intent intent = new Intent(SignIn.this, HomeM.class);
                            //startActivity(intent);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        }
                        Toast.makeText(getApplicationContext(), "Signed In ", Toast.LENGTH_SHORT).show();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Please Verify your Email first!!", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        //setResultsUI("Please verify you mail id");
                    }
                } else Helper.toast(SignIn.this, task.getException().getMessage());
            }
        });
    }


}