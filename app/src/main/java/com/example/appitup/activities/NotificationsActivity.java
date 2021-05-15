package com.example.appitup.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appitup.R;
import com.example.appitup.adapter.NotificationsAdapter;
import com.example.appitup.adapter.UsersAdapter;
import com.example.appitup.models.Notification;
import com.example.appitup.models.User;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NotificationsActivity extends AppCompatActivity implements  NotificationsAdapter.NotificationsListener{
    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    Unbinder unbinder;
    ArrayList<Notification> list = new ArrayList<>();
    NotificationsAdapter adapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        unbinder = ButterKnife.bind(this);

        adapter = new NotificationsAdapter(getApplicationContext(), list);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadData();

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        adapter.setUpOnNotificationListener(this);
    }

    public void loadData(){

    }

    @Override
    public void chipClicked(Notification notification) {

    }
}