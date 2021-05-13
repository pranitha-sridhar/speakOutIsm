package com.example.appitup.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appitup.Database.Prefs;
import com.example.appitup.R;
import com.example.appitup.models.Complaints;
import com.example.appitup.utility.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class Register_Complaint extends AppCompatActivity {

    @BindView(R.id.chip_group1)
    ChipGroup chipGroup1;
    @BindView(R.id.chip_group2)
    ChipGroup chipGroup2;
    @BindView(R.id.switchmat)
    Switch switchh;
    @BindView(R.id.box)
    CheckBox checkBox;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.textInputTitle)
    TextInputLayout title;
    @BindView(R.id.textInputBody)
    TextInputLayout bodyinput;
    @BindView(R.id.progressBar2)
    ProgressBar progressBar;

    FirebaseAuth mAuth;
    Unbinder unbinder;
    String category, subcategory=null;
    int k=3;
    String[] subcatTitle = {"null", "null", "null", "null", "null", "null"};

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__complaint);
        unbinder = ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Create a complaint");
        actionBar.show();

        mAuth=FirebaseAuth.getInstance();

        //final ChipGroup[] chipGroup2 = {new ChipGroup(this)};
        //chipGroup1.isSingleSelection();

        Context context=this;
        subcatTitle[0]=("Pre-Registration");subcatTitle[1]=("Fees Issue");subcatTitle[2]=("Others");
        category ="Registration";
        //subcategory ="Pre-Registration";
        fun(context);

        chipGroup2.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                Chip chip=chipGroup2.findViewById(checkedId);
                subcategory =chip.getText().toString();
            }
        });

        chipGroup1.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if(checkedId==-1){
                    return;
                }
                chipGroup2.removeAllViews();
                subcategory=null;
                if(checkedId==R.id.registration){
                    category ="Registration";
                    subcatTitle[0]=("Pre-Registration");subcatTitle[1]=("Fees Issue");subcatTitle[2]=("Others");
                    k=3;
                }
                if(checkedId==R.id.academics){
                    category ="Academics";
                    subcatTitle[0]=("Lecture Timings");subcatTitle[1]=("Paper Evaluation");subcatTitle[2]=("Attendance");subcatTitle[3]=("Others");
                    k=4;
                }
                if(checkedId==R.id.dsw){
                    category ="DSW";
                    subcatTitle[0]=("Scholarships");subcatTitle[1]=("Clubs");subcatTitle[2]=("Sports");subcatTitle[3]=("Guest Rooms");
                    k=4;
                }
                if(checkedId==R.id.vendors){
                    category ="Vendors";
                    subcatTitle[0]=("RD");subcatTitle[1]=("Barista");subcatTitle[2]=("Guruji");
                    k=3;
                }
                if(checkedId==R.id.mis){
                    category ="MIS";
                    subcatTitle[0]=("Technical Issue");
                    k=1;
                }
                if(checkedId==R.id.hostel){
                    category ="Hostel";
                    subcatTitle[0]=("Electricity");subcatTitle[1]=("Hygiene");subcatTitle[2]=("Warden");subcatTitle[3]="Mess";
                    k=4;
                }
                if(checkedId==R.id.health){
                    category ="Health";
                    subcatTitle[0]=("Hygiene");subcatTitle[1]=("Doctor Unavailability");subcatTitle[2]=("Medicine Unavailability");subcatTitle[3]="Beds Unavailability";subcatTitle[4]="Ambulance Issue";subcatTitle[5]="Others";
                    k=6;
                }
                if(checkedId==R.id.library){
                    category ="Library";
                    subcatTitle[0]=("Timing");subcatTitle[1]=("Issue/Re-Issue/Submit");subcatTitle[2]=("Fine");subcatTitle[3]="Others";
                    k=4;
                }
                if(checkedId==R.id.personal){
                    category ="Personal";
                    subcatTitle[0]="Personal";
                    k=1;
                }
                fun(context);
            }
        });

        switchh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) checkBox.setVisibility(View.VISIBLE);
                else checkBox.setVisibility(View.INVISIBLE);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid=mAuth.getUid();
                String subject=title.getEditText().getText().toString();
                String body= bodyinput.getEditText().getText().toString();
                if(subject.isEmpty()){
                    title.setError("Title should not be empty");
                    title.requestFocus();
                    return;
                }else title.setError(null);
                if(body.isEmpty()){
                    bodyinput.setError("Body should not be empty");
                    bodyinput.requestFocus();
                    return;
                }else bodyinput.setError(null);
                boolean public_private=false;
                if(switchh.isChecked())public_private=true;
                String anonymous="false";
                if(checkBox.isChecked())anonymous="true";
                String visibility="public";
                if(!public_private)visibility="private";

                if(subcategory ==null){
                    Toast.makeText(context, "Choose a sub-category", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Toast.makeText(context, ""+uid+" "+heading+" "+message+" "+permission+" "+cat+" "+sub, Toast.LENGTH_SHORT).show();
                String status = "PENDING";
                String username = Prefs.getUser(Register_Complaint.this).getUsername();
                //Toast.makeText(context, ""+username, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Complaints");
                String complaintId = reference.push().getKey();
                Map map=new HashMap();
                map.put("timeStamp",ServerValue.TIMESTAMP);

                Complaints complaints = new Complaints(complaintId, username, uid, subject, body, category, subcategory, visibility, status, anonymous, map);
                reference.child(complaintId).setValue(complaints).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        //Toast.makeText(getApplicationContext(), "Submitted", Toast.LENGTH_SHORT).show();
                        Helper.toast(getApplicationContext(),"Submitted");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, 1000);
                    }
                });
            }
        });




    }

    public void fun(Context context){
        for(int j=0;j<k;j++){
            Chip chip1=new Chip(context);
            chip1.setText(subcatTitle[j]);
            chip1.setChipBackgroundColorResource(R.color.stroke_tint);
            chip1.setCheckable(true);
            chip1.setFocusable(true);
            chip1.setClickable(true);
            chipGroup2.addView(chip1);
        }
    }

}