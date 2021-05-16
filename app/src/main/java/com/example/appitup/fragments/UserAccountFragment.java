package com.example.appitup.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.appitup.Database.Prefs;
import com.example.appitup.R;
import com.example.appitup.models.User;
import com.example.appitup.utility.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class UserAccountFragment extends Fragment {
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.display_name)
    TextView display_name;
    @BindView(R.id.edit_icon)
    ImageView edit_icon;
    @BindView(R.id.mailid)
    TextView mailid;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.display_name_edittext)
    EditText editText;
    @BindView(R.id.checked_icon)
    ImageView check_icon;
    @BindView(R.id.profile_picture)
    CircularImageView circularImageView;

    Unbinder unbinder;
    String display,pictureUri,usernameString,mailString,uid;
    boolean name_changed=false,uri_changed=false;
    FirebaseAuth mAuth;
    final int IMAGE_REQUEST = 100;
    Uri filepath;
    DatabaseReference reference;
    int userType;
    StorageReference srefer;

    public UserAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_account, container, false);
        unbinder = ButterKnife.bind(this,view);
        mAuth=FirebaseAuth.getInstance();

        usernameString=Prefs.getUser(getContext()).getUsername();
        username.setText(usernameString);
        display=Prefs.getUser(getContext()).getDisplayName();
        display_name.setText(display);
        mailString=Prefs.getUser(getContext()).getEmail();
        mailid.setText(mailString);
        uid=Prefs.getUser(getContext()).getUid();
        pictureUri=Prefs.getUser(getContext()).getProfileUri();
        userType=Prefs.getUser(getContext()).getUserType();

        if(pictureUri!=null && !pictureUri.isEmpty()){
            Glide.with(view).load(pictureUri).into(circularImageView);
        }
        else pictureUri="";

        edit_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                display_name.setVisibility(View.GONE);
                edit_icon.setVisibility(View.GONE);
                editText.setVisibility(View.VISIBLE);
                editText.setText(display_name.getText());
                check_icon.setVisibility(View.VISIBLE);
            }
        });

        check_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                display=editText.getText().toString();
                check_icon.setVisibility(View.GONE);
                editText.setVisibility(View.GONE);
                display_name.setVisibility(View.VISIBLE);
                display_name.setText(display);
                edit_icon.setVisibility(View.VISIBLE);
                name_changed=true;
            }
        });

        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),IMAGE_REQUEST);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filepath != null) {
                    upload_picture_in_cloud();
                }
                else if(name_changed){
                    change_in_DB();
                    change_in_PREF();
                }
                //Helper.toast(getContext(),""+usernameString);
            }
        });
        return view;

    }

    public void change_in_DB(){
        if(Prefs.getUser(getContext()).getUserType()==Helper.USER_STUDENT)
            reference = FirebaseDatabase.getInstance().getReference("StudentUsers").child(mAuth.getCurrentUser().getUid());
        if(Prefs.getUser(getContext()).getUserType()==Helper.USER_ADMINISTRATOR)
            reference = FirebaseDatabase.getInstance().getReference("AdministratorUsers").child(mAuth.getCurrentUser().getUid());
        reference.child("displayName").setValue(display).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Helper.toast(getActivity(), "Updated");
                }
            }
        });
        reference.child("profileUri").setValue(pictureUri).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //
            }
        });
    }

   public void change_in_PREF(){
        User user1=new User(usernameString,mailString,display, pictureUri,uid,userType);
        Prefs.setUserData(getContext(),user1);
    }

    public void upload_picture_in_cloud(){
        srefer = FirebaseStorage.getInstance().getReference().child("images/" + mAuth.getCurrentUser().getUid());
        //progressBar.setVisibility(View.VISIBLE);
        srefer.delete();
        srefer = FirebaseStorage.getInstance().getReference().child("images/" + mAuth.getCurrentUser().getUid());
        srefer.putFile(filepath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    srefer.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            pictureUri = uri.toString();
                            uri_changed=true;
                            //Toast.makeText(getContext(), "Picture Uploaded", Toast.LENGTH_SHORT).show();
                            change_in_DB();
                            change_in_PREF();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Helper.toast(getContext(),"Error");
                                }
                            });
                    //progressBar.setVisibility(View.GONE);
                }
                else{
                    //progressBar.setVisibility(View.GONE);
                    Helper.toast(getContext(),"Picture failed to be Uploaded");
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            filepath = data.getData();
            Glide.with(this).load(filepath).into(circularImageView);
            uri_changed=true;
        }
    }
}