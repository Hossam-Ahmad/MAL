package com.example.hossam.movies;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Details_Fragment extends Fragment
{
    int ID=-1;
    String Trailer1="",Trailer2="",review="";
    TextView title,date,length,rate,overview,reviews;
    ImageView imageView;
    Button btnfavourite,btnTrailer1,btnTrailer2;
    Movie movie;
    RecyclerView recyclerView;
    RecyclerAdapter rAdapter;
    ArrayList<String> reviewList;

    public void play(View view)
    {
        if(view.getId() == R.id.button2)
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Trailer1)));
        }

        if(view.getId() == R.id.button3)
        {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Trailer2)));
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void favourite()
    {
        String URL = "content://com.example.hossam.movies/favourite/"+ID;
        Uri parse = Uri.parse(URL);
        Cursor c = getActivity().managedQuery(parse, null, null, null, null);

        if (c.moveToFirst())
        {
            URL = "content://com.example.hossam.movies/favourite/d/"+ID;
            parse = Uri.parse(URL);
            c = getActivity().managedQuery(parse, null, null, null, null);
            Toast.makeText(getActivity(), "Deleted from favourite list", Toast.LENGTH_LONG).show();
        }
        else
        {
            DBAdapter dbAdapter = new DBAdapter(getActivity());
            BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
            movie.setImage_bmp(bitmapDrawable.getBitmap());
            dbAdapter.insertFavourite(movie);
            btnfavourite.setText("My Favourite Movie !!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.content_detailed,container,false);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        decleration();

        btnfavourite.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                favourite();
                ((MainActivity)getActivity()).showData();
            }
        });

        btnTrailer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(v);
            }
        });

        btnTrailer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(v);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void sendmovie(Movie x)
    {
        this.movie = x;
        ID = movie.getId();
        String URL = "content://com.example.hossam.movies/favourite/"+ID;
        Uri students = Uri.parse(URL);
        Cursor c = getActivity().managedQuery(students, null, null, null, null);
        decleration();
        if (c.moveToFirst())
        {
            btnfavourite.setText("Delete From Favourite");
        }
        else
        {
            btnfavourite.setText("MAKE AS FAVOURITE");
            Log.e("info","Not Found");
        }
        title.setText("   "+movie.getName());
        date.setText(movie.getReleaseDate().substring(0, 4));
        rate.setText(movie.getRate()+" / 10");
        overview.setText(movie.getOverview());
        Trailer1 = movie.getTrailer1();
        Trailer2 = movie.getTrailer2();
        reviewList = movie.getReviews();

        while(reviewList.contains("*_*"))
            reviewList.remove("*_*");

        if(Trailer2.equals(""))
            btnTrailer2.setVisibility(View.INVISIBLE);
        else
            btnTrailer2.setVisibility(View.VISIBLE);

        if(!Utilities.check_wifi(getActivity()))
        {
            btnTrailer1.setVisibility(View.GONE);
            btnTrailer2.setVisibility(View.GONE);
            getActivity().findViewById(R.id.trailer).setVisibility(View.GONE);
            getActivity().findViewById(R.id.seperator).setVisibility(View.GONE);
        }


        if(Utilities.check_wifi(getActivity()))
            Picasso.with(getActivity()).load(movie.getImageLink()).into(imageView);
        else
            imageView.setImageBitmap(movie.getImage_bmp());

        rAdapter = new RecyclerAdapter(reviewList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(rAdapter);

        if(reviewList.size() == 0)
            reviews.setVisibility(View.VISIBLE);
        else
            reviews.setVisibility(View.INVISIBLE);

        ScrollView scroll = (ScrollView) getActivity().findViewById(R.id.scrollView);
        scroll.smoothScrollTo(0, 0);
        scroll.smoothScrollTo(0, 0);
    }

    public void decleration()
    {
        btnfavourite = (Button) getActivity().findViewById(R.id.button);
        btnTrailer1 = (Button) getActivity().findViewById(R.id.button2);
        btnTrailer2 = (Button) getActivity().findViewById(R.id.button3);
        imageView = (ImageView) getActivity().findViewById(R.id.imageView);
        reviews = (TextView) getActivity().findViewById(R.id.reviews);
        title = (TextView) getActivity().findViewById(R.id.title);
        date = (TextView) getActivity().findViewById(R.id.date);
        length = (TextView) getActivity().findViewById(R.id.length);
        rate = (TextView) getActivity().findViewById(R.id.rate);
        overview = (TextView) getActivity().findViewById(R.id.overview);
        btnTrailer2 = (Button) getActivity().findViewById(R.id.button3);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
    }

    class favourite extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... params)
        {
            favourite();
            return null;
        }
    }
}
