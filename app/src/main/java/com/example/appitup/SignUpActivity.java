package com.example.appitup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.agrawalsuneet.dotsloader.loaders.PullInLoader;
import com.example.appitup.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    @BindView(R.id.buttonSignUp)
    Button buttonSignUp;
    @BindView(R.id.textInputPassword)
    TextInputLayout textInputPassword;
    @BindView(R.id.rGroupUserType)
    RadioGroup rGroupUserType;
    TextView term_conditions;
    TextView progressDialogueTitle;
    PullInLoader progressDialogueLoader;
    MaterialButton progressDialogueDismissButton;

    Unbinder unbinder;
    int userType = 1; // 1->student : 2->administrator
    AlertDialog alertDialogProgress;
    private FirebaseAuth mAuth;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        unbinder = ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        rGroupUserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.student) {
                    userType = 0;
                    textInputUsername.setHelperText("Admission number");
                } else if (checkedId == R.id.administrator) {
                    userType = 1;
                    textInputUsername.setHelperText("Employee ID");
                }

            }
        });
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
                } else if (userType == 1 && !isAdmNo(userName)) {
                    textInputUsername.setError("User Name is Not Valid");
                    textInputUsername.requestFocus();
                    return;
                } else if (userType == 2 && !isEmployeeId(userName)) {
                    textInputUsername.setError("User Name is Not Valid");
                    textInputUsername.requestFocus();
                    return;
                } else textInputUsername.setError(null);

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

                if(!email.endsWith(".iitism.ac.in")){
                    textInputEmail.setError("Only ISM mail ids are allowed");
                    textInputEmail.requestFocus();
                    return;
                }else textInputEmail.setError(null);

                signUp(userName, email, name, password);
            }
        });
    }

    private void signUp(String userName, String email, String name, String password) {
        showProgressDialogue();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                setResultsUI("User Registered Successfully!! We had sent a verification link to email. Please verify your email to Login");
                                Intent intent = new Intent(SignUpActivity.this, SignIn.class);
                                startActivity(intent);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                //Toast.makeText(getApplicationContext(), "Signed In.", Toast.LENGTH_SHORT).show();
                                finish();

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
        User user = new User(userName, email, name, null, uid);
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
        } else {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("AdministratorUsers");
            db.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // User Data saved in DB
                    }
                }
            });
        }
    }

    private boolean isEmployeeId(String userName) {
        return true;
    }

    private boolean isAdmNo(String userName) {
        return true;
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