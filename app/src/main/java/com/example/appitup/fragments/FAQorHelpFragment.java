package com.example.appitup.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.appitup.Database.Prefs;
import com.example.appitup.R;
import com.example.appitup.adapter.FAQAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FAQorHelpFragment extends Fragment {
    @BindView(R.id.listview)
    ListView listView;
    @BindView(R.id.title)
    TextView textView;

    Unbinder unbinder;
    ListAdapter adapter;

    public FAQorHelpFragment() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_faq_hlp, container, false);
        unbinder = ButterKnife.bind(this, view);

        textView.setText("Hey @" + Prefs.getUser(getContext()).getDisplayName() + ", how can we help you?");

        final String[] questions = new String[]{"How to raise a complaint?", "How much time does it take for my complaint to be resolved", "Is my data secure?", "When should I post as 'Anonymous'?", "How can I post a complaint which isn't visible to any other student?", "How will I know if status of any of my complaint has been changed?", "What to do if my complaint has been deleted?", "How to sort the complaints based on categories?", "How to re-raise a complaint that has been resolved?", "How to change my User Profile details?", "Facing any Problem?"};
        final String[] answers = new String[]{"A new complaint can be registered by clicking on the floating button 'File a Complaint'. You then have to fill in the subject, message, choose the category and sub-category of the complaint and select the visibility. After filling in all the details, click on the SUBMIT button and your complaint has been raised successfully.",
                "The administrator and the concerned person responsible for the system tries to reply back and take an action as soon as possible. Your complaint may take maximum of one week for the status to be updated or replied.",
                "All the data is being stored safely in Google Firebase as of now. There is no chance of data leakage to occur. The system has been set up such that only the students and the higher officials of the institute will be able to access the application.",
                "Posting a complaint as Anonymous will hide your username, but the complaint will still be visible to other users. Students will be able to upvote, downvote or comment on your complaint.",
                "If a complaint is not set to be public, it will be stored as PRIVATE. None of the other users except the admin will be able to view or access your complaint. It won't be available in the Home Page, will instead be in 'Your Complaints' section.",
                "Whenever the status of your complaint is updated by the admin, you will receive a notification in your phone. You will also receive notification when someone has commented to your complaint or the admin has replied you back or if you have been blocked/unblocked.",
                "Your complaint would have been deleted by the admin if it would have not followed the rules and principles of the institute. Please contact the Dean/Associate Dean for further details if a mistake has been done.",
                "Please use the filter that is provided in the Home & Status section. You will sort the complaints by any of the complaints.",
                "If your complaint has been solved, but you still want to discuss over few things, please message the admin followed by the complaint. The admin will surely change the status and will let you know about further details",
                "Your username and email id are non-editable fields, whereas you can update your Display Name and your photo as you wish.",
                "If any problem arises, please contact our admin by the mail id: 'admin@iitism.ac.in'"};

        adapter = new FAQAdapter(getContext(), questions, answers);
        listView.setAdapter(adapter);

        return view;
    }
}


