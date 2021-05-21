package com.example.appitup.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.appitup.R;
import com.example.appitup.utility.Helper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AboutUsFragment extends Fragment {
    @BindView(R.id.intro_link1)
    TextView mail1;
    @BindView(R.id.intro_phone1)
    TextView phone1;
    @BindView(R.id.bhavesh)
    RelativeLayout bhavesh_profile;
    @BindView(R.id.pranitha)
    RelativeLayout pranitha_profile;
    @BindView(R.id.card1)
    CardView college_profile;


    Unbinder unbinder;

    public AboutUsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null)
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
        View view = inflater.inflate(R.layout.fragment_aboutus, container, false);
        unbinder = ButterKnife.bind(this, view);

        mail1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String data = mail1.getText().toString();
                ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", data);
                cm.setPrimaryClip(clipData);
                Helper.toast(getContext(), "Copied to clipboard");
                return false;
            }
        });

        mail1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:dean_acad@iitism.ac.in"));
                startActivity(Intent.createChooser(emailIntent, "SHOUT OUT"));
            }
        });
        phone1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String data = phone1.getText().toString();
                ClipboardManager cm = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", data);
                cm.setPrimaryClip(clipData);
                Helper.toast(getContext(), "Copied to clipboard");
                return false;
            }
        });

        phone1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:" + phone1.getText().toString().trim()));
                startActivity(phoneIntent);
            }
        });

        bhavesh_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.linkedin.com/in/bhavesh-sharma-0ba5061a4/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        pranitha_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.linkedin.com/in/pranitha-sridhar-8b42221a4/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        college_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.iitism.ac.in/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        return view;
    }
}