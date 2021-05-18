package com.example.appitup.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appitup.Database.Prefs;
import com.example.appitup.R;
import com.example.appitup.activities.ConversationActivity;
import com.example.appitup.adapter.ComplaintsAdapter;
import com.example.appitup.adapter.TrendingAdapter;
import com.example.appitup.models.Comment;
import com.example.appitup.models.Complaints;
import com.example.appitup.models.Vote;
import com.example.appitup.utility.Helper;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static java.text.DateFormat.getDateTimeInstance;

public class TrendingFragment extends Fragment implements OnChartValueSelectedListener {
    @BindView(R.id.complaints_recycler2)
    RecyclerView recyclerView;
    @BindView(R.id.shimmer2)
    ShimmerFrameLayout shimmerFrameLayout;
    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.resolved)
    TextView resolved;
    @BindView(R.id.inprogress)
    TextView inprogress;
    @BindView(R.id.pending)
    TextView pending;
    @BindView(R.id.chart)
    PieChart chart;

    Unbinder unbinder;
    ArrayList<Complaints> list = new ArrayList<>();
    TrendingAdapter adapter;
    FirebaseAuth mAuth;
    String[] xData={"Registration","Academics","DSW","Vendors of ISM","MIS/Parents Portal","Hostel","Health Centre","Library","Personal"};
    float res=0f;
    int i=0;
    List<PieEntry> entries1 = new ArrayList<>();
    PieDataSet dataSet;

    public TrendingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null)
            unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trending, container, false);

        unbinder = ButterKnife.bind(this, view);

        adapter = new TrendingAdapter(getContext(), list);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadYData();
        loadTop10Data();
        loadCardData();
        //loadYData();
        //chart_settings();

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        //adapter.setUpOnComplaintListener(this);
        chart.setOnChartValueSelectedListener(this);


        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 10, 5, 5);
        chart.setDragDecelerationFrictionCoef(0.95f);
        chart.setCenterText("Category Wise");
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);
        chart.setDrawCenterText(true);
        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);
        chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        //Legend l = chart.getLegend();
        chart.getLegend().setEnabled(false);
        chart.setEntryLabelColor(Color.BLACK);
        //chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(10f);
        //setData();
        //loadYData();

        return view;
    }

    public void add(){
        {
            Toast.makeText(getContext(), ""+entries1.size(), Toast.LENGTH_SHORT).show();
            dataSet = new PieDataSet(entries1, "Category Wise Complaints");

            dataSet.setDrawIcons(false);
            dataSet.setSliceSpace(3f);
            dataSet.setIconsOffset(new MPPointF(0, 40));
            dataSet.setSelectionShift(5f);
            ArrayList<Integer> colors = new ArrayList<>();
            for (int c : ColorTemplate.VORDIPLOM_COLORS)
                colors.add(c);
            for (int c : ColorTemplate.JOYFUL_COLORS)
                colors.add(c);
            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);
            for (int c : ColorTemplate.LIBERTY_COLORS)
                colors.add(c);
            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);
            colors.add(ColorTemplate.getHoloBlue());

            dataSet.setColors(colors);
            dataSet.setSelectionShift(0f);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter());
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.BLACK);
            //data.setValueTypeface(tfLight);
            //progressBar.setVisibility(View.GONE);
            chart.setData(data);
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            //chart.getData().notifyDataChanged();
            //chart.notifyDataSetChanged();
            // undo all highlights
            //chart.highlightValues(null);
            chart.invalidate();}
    }

    public void loadYData(){
        entries1.clear();
        i=0;
        res=0;
                    Query ref = FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("category").equalTo("Registration");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    res= Float.parseFloat(String.valueOf((snapshot.getChildrenCount())));
                    if(res>0) entries1.add(new PieEntry(res,xData[0]));
                    i++;if(i==9)add();
                    //Toast.makeText(getContext(), " "+res, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        ref=  FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("category").equalTo("Academics");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                res = Float.parseFloat(String.valueOf(snapshot.getChildrenCount()));
                if(res>0) entries1.add(new PieEntry(res,xData[1]));
                i++;if(i==9)add();
                //add();
                //Toast.makeText(getContext(), " "+res, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref=  FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("category").equalTo("DSW");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                res=Float.parseFloat(String.valueOf(snapshot.getChildrenCount()));
                if(res>0) entries1.add(new PieEntry(res,xData[2]));
                i++;if(i==9)add();

               // Toast.makeText(getContext(), " "+res, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
            ref= FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("category").equalTo("Vendors of ISM");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                res= Float.parseFloat(String.valueOf(snapshot.getChildrenCount()));
                if(res>0) entries1.add(new PieEntry(res,xData[3]));
                i++;if(i==9)add();
                //Toast.makeText(getContext(), " "+res, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
             ref=  FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("category").equalTo("MIS/Parents Portal");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                res=Float.parseFloat(String.valueOf(snapshot.getChildrenCount()));
                if(res>0) entries1.add(new PieEntry(res,xData[4]));
                i++;if(i==9)add();
                //Toast.makeText(getContext(), " "+res, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
             ref=  FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("category").equalTo("Hostel");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                res=Float.parseFloat(String.valueOf(snapshot.getChildrenCount()));
                if(res>0) entries1.add(new PieEntry(res,xData[5]));
                i++;if(i==9)add();
                //Toast.makeText(getContext(), " "+res, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
             ref= FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("category").equalTo("Health Centre");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                res= Float.parseFloat(String.valueOf(snapshot.getChildrenCount()));
               // Toast.makeText(getContext(), " "+res, Toast.LENGTH_SHORT).show();
                if(res>0) entries1.add(new PieEntry(res,xData[6]));
                i++;if(i==9)add();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

             ref=  FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("category").equalTo("Library");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                res=Float.parseFloat(String.valueOf(snapshot.getChildrenCount()));
                //Toast.makeText(getContext(), " "+res, Toast.LENGTH_SHORT).show();
                if(res>0) entries1.add(new PieEntry(res,xData[7]));
                i++;if(i==9)add();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
            ref=  FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("category").equalTo("Personal");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                res=Float.parseFloat(String.valueOf(snapshot.getChildrenCount()));
                if(res>0) entries1.add(new PieEntry(res,xData[8]));
                i++;if(i==9)add();
               // Toast.makeText(getContext(), " "+res, Toast.LENGTH_SHORT).show();
               //chart_settings();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void loadTop10Data(){
        Query query=FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("upvotes").limitToFirst(10);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    String status = ds.child("status").getValue().toString();
                    if(status.equals("private"))continue;
                    String complaintId = ds.child("complaintId").getValue().toString();
                    String username = ds.child("username").getValue().toString();
                    String uid = ds.child("uid").getValue().toString();
                    String subject = ds.child("subject").getValue().toString();
                    String body = ds.child("body").getValue().toString();
                    String category = ds.child("category").getValue().toString();
                    String subcategory = ds.child("subcategory").getValue().toString();
                    String visibility = ds.child("visibility").getValue().toString();

                    String anonymous = ds.child("anonymous").getValue().toString();

                    // Get Upvotes and Downvotes
                    long upvotes = 0;
                    long downvotes = 0;
                    if (ds.hasChild("upvotes"))
                        upvotes = (long) ds.child("upvotes").getValue();
                    if (ds.hasChild("downvotes"))
                        downvotes = (long) ds.child("downvotes").getValue();

                    // Get Timestamp
                    String time = null;
                    long timeStamp = 0;
                    Map<String, Long> map = new HashMap();
                    if (ds.child("timeStampmap").child("timeStamp").exists()) {
                        timeStamp = (long) ds.child("timeStampmap").child("timeStamp").getValue();
                        DateFormat dateFormat = getDateTimeInstance();
                        Date netDate = (new Date(timeStamp));
                        time = dateFormat.format(netDate);
                        map.put("timeStamp", timeStamp);
                    } else {
                        if (ds.child("timeStampStr").exists()) {
                            time = ds.child("timeStampStr").getValue().toString();
                        }
                    }

                    // Get Upvoters List
                    ArrayList<Vote> upvoters = new ArrayList<>();
                    if (ds.hasChild("listOfUpvoters"))
                        for (DataSnapshot s : ds.child("listOfUpvoters").getChildren())
                            upvoters.add(new Vote(s.child("complaint_id").getValue().toString(), s.child("username").getValue().toString()));

                    // Get Downvoters List
                    ArrayList<Vote> downvoters = new ArrayList<>();
                    if (ds.hasChild("listOfDownvoters"))
                        for (DataSnapshot s : ds.child("listOfDownvoters").getChildren())
                            downvoters.add(new Vote(s.child("complaint_id").getValue().toString(), s.child("username").getValue().toString()));

                    // Get Comments List
                    ArrayList<Comment> commenters = new ArrayList<>();
                    if (ds.hasChild("listOfCommenter"))
                        for (DataSnapshot s : ds.child("listOfCommenter").getChildren())
                            commenters.add(new Comment(s.child("username").getValue().toString(), s.child("comment").getValue().toString()));

                    // Check for anonymous users
                    if (anonymous.equals("true")) username = "Anonymous";

                    //check vote status of complaint
                    int voteStatus = Helper.NOT_VOTED;
                    String loggedInUsername = Prefs.getUser(getContext()).getUsername();
                    if (ds.child("listOfUpvoters").hasChild(loggedInUsername))
                        voteStatus = Helper.UPVOTED;
                    if (ds.child("listOfDownvoters").hasChild(loggedInUsername))
                        voteStatus = Helper.DOWNVOTED;


                    // add complaint to list
                    list.add(new Complaints(complaintId, username, uid, subject, body, category, subcategory, visibility, status,
                            anonymous, upvotes, downvotes, commenters, upvoters, downvoters, map, time, voteStatus));

                }

                if (list.isEmpty()) {
                    /*shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);*/
                    Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
                    return;
                }
                adapter.notifyDataSetChanged();
                /*shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadCardData(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Complaints");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                total.setText(Long.toString(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query ref= FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("status").equalTo("RESOLVED");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resolved.setText(Long.toString(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref=  FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("status").equalTo("IN-PROGRESS");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                inprogress.setText(Long.toString(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref=  FirebaseDatabase.getInstance().getReference("Complaints").orderByChild("status").equalTo("PENDING");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pending.setText(Long.toString(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());

    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }
}