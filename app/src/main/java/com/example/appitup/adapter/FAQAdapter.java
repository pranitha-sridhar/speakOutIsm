package com.example.appitup.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.example.appitup.R;

public class FAQAdapter extends BaseAdapter {
    private final String[] questions;
    private final String[] answers;
    Context context;

    public FAQAdapter(Context context, String[] questions, String[] answers) {
        this.context = context;
        this.questions = questions;
        this.answers = answers;
    }

    @Override
    public int getCount() {
        return questions.length;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        FAQAdapter.ViewHolder viewHolder;

        if (view == null) {
            viewHolder = new FAQAdapter.ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.layout_faq_card, viewGroup, false);
            viewHolder.question = view.findViewById(R.id.question);
            viewHolder.answer = view.findViewById(R.id.answer);
            viewHolder.arrow_down = view.findViewById(R.id.arrow_down);
            viewHolder.arrow_up = view.findViewById(R.id.arrow_up);
            viewHolder.cardView = view.findViewById(R.id.cardview);
            view.setTag(viewHolder);
        } else {
            viewHolder = (FAQAdapter.ViewHolder) view.getTag();
        }
        viewHolder.question.setText(questions[i]);
        viewHolder.answer.setText(answers[i]);
        viewHolder.arrow_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.answer.setVisibility(View.VISIBLE);
                viewHolder.arrow_up.setVisibility(View.VISIBLE);
                viewHolder.arrow_down.setVisibility(View.GONE);
                viewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.faq_color));
            }
        });
        viewHolder.arrow_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.answer.setVisibility(View.GONE);
                viewHolder.arrow_up.setVisibility(View.GONE);
                viewHolder.arrow_down.setVisibility(View.VISIBLE);
                viewHolder.cardView.setCardBackgroundColor(Color.WHITE);
            }
        });
        return view;
    }

    public static class ViewHolder {
        TextView question;
        TextView answer;
        ImageView arrow_down;
        ImageView arrow_up;
        CardView cardView;
    }
}
