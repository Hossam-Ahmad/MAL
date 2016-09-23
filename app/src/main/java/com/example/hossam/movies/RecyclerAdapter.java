package com.example.hossam.movies;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>
{

    private ArrayList<String> reviewList;

    public RecyclerAdapter(ArrayList<String> x)
    {
        this.reviewList = x;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView title;

        public MyViewHolder(View view)
        {
            super(view);
            title = (TextView) view.findViewById(R.id.textView);
        }
    }

    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.MyViewHolder holder, int position)
    {
        holder.title.setText(reviewList.get(position));
    }

    @Override
    public int getItemCount()
    {
        return reviewList.size();
    }
}
