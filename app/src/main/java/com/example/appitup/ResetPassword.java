package com.example.appitup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.PullInLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.Unbinder;

public class ResetPassword extends AppCompatActivity {
EditText mail;
Button reset;
FirebaseAuth mAuth;
TextView progressDialogueTitle;
PullInLoader progressDialogueLoader;
MaterialButton progressDialogueDismissButton;
AlertDialog alertDialogProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mail=findViewById(R.id.editTextTextEmailAddress);
        reset=findViewById(R.id.button2);
        mAuth=FirebaseAuth.getInstance();
        //Toast.makeText(this, ""+mAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(ResetPassword.this, "button clicked", Toast.LENGTH_SHORT).show();
                String emailid = mail.getText().toString();
                if (emailid.isEmpty()) {
                    mail.setError("Field required");
                    mail.requestFocus();
                    return;
                }
                showProgressDialogue();
                mAuth.sendPasswordResetEmail(emailid).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            alertDialogProgress.dismiss();
                            finish();
                            //Toast.makeText(getApplicationContext(), "Password link is sent to your mail id. Please check", Toast.LENGTH_SHORT).show();
                        } else{
                            //Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

    }
    private void showProgressDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ResetPassword.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogue_loading, null);
        builder.setView(view);
        progressDialogueTitle = view.findViewById(R.id.textViewTitle);
        progressDialogueDismissButton = view.findViewById(R.id.dismissButton);
        progressDialogueLoader = view.findViewById(R.id.progressLoader);

        progressDialogueTitle.setText("Please wait, we are sending the verification link for password change...");

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