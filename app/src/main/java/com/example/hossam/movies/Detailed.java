package com.example.hossam.movies;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Detailed extends AppCompatActivity {

    int ID=-1;
    Movie movie;
    String Trailer1="",Trailer2="";
    TextView title,date,length,rate,overview,reviews;
    ImageView imageView;
    Button btnfavourite,btnTrailer1,btnTrailer2;
    ArrayList reviewList;
    RecyclerView recyclerView;
    RecyclerAdapter rAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.share, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        Intent shareIntent;
        ShareActionProvider mShareActionProvider;
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,movie.getTrailer1());
        mShareActionProvider.setShareIntent(shareIntent);
        return true;
    }

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
        Cursor c = managedQuery(parse, null, null, null, null);

        if (c.moveToFirst())
        {
            URL = "content://com.example.hossam.movies/favourite/d/"+ID;
            parse = Uri.parse(URL);
            c = managedQuery(parse, null, null, null, null);
            Toast.makeText(getBaseContext(), "Deleted from favourite list", Toast.LENGTH_LONG).show();
        }
        else
        {
            btnfavourite.setText("My Favourite Movie !!");
            BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
            movie.setImage_bmp(bitmapDrawable.getBitmap());
            DBAdapter dbAdapter = new DBAdapter(this);
            dbAdapter.insertFavourite(movie);
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        btnfavourite = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.imageView);
        reviews = (TextView) findViewById(R.id.reviews);
        title = (TextView) findViewById(R.id.title);
        date = (TextView) findViewById(R.id.date);
        length = (TextView) findViewById(R.id.length);
        rate = (TextView) findViewById(R.id.rate);
        overview = (TextView) findViewById(R.id.overview);
        btnTrailer1 = (Button) findViewById(R.id.button2);
        btnTrailer2 = (Button) findViewById(R.id.button3);
        btnfavourite.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                favourite();
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

        Intent i = getIntent();
        ID = i.getIntExtra("id",-1);
        title.setText("   "+i.getStringExtra("title"));
        date.setText(i.getStringExtra("date").substring(0,4));
        rate.setText(i.getDoubleExtra("rate", 0) + " / 10");
        overview.setText(i.getStringExtra("overview"));
        Trailer1 = i.getStringExtra("trailer1");
        Trailer2 = i.getStringExtra("trailer2");
        reviewList = i.getStringArrayListExtra("reviews");

        while(reviewList.contains("*_*"))
            reviewList.remove("*_*");

        movie = new Movie(i.getStringExtra("title"),i.getStringExtra("image"),i.getStringExtra("overview"),i.getDoubleExtra("rate",0),i.getStringExtra("date").substring(0,4),i.getIntExtra("id",-1),i.getStringExtra("trailer1"),i.getStringExtra("trailer2"),i.getStringArrayListExtra("reviews"));

        if(Trailer2.equals(""))
            btnTrailer2.setVisibility(View.INVISIBLE);
        else
            btnTrailer2.setVisibility(View.VISIBLE);

        if(!Utilities.check_wifi(this))
        {
            btnTrailer1.setVisibility(View.GONE);
            btnTrailer2.setVisibility(View.GONE);
            findViewById(R.id.trailer).setVisibility(View.GONE);
            findViewById(R.id.seperator).setVisibility(View.GONE);
        }

        String URL = "content://com.example.hossam.movies/favourite/"+ID;
        Uri students = Uri.parse(URL);
        Cursor c = this.managedQuery(students, null, null, null, null);
        if (c.moveToFirst())
            btnfavourite.setText("Delete From Favourite");
        else
            btnfavourite.setText("MAKE AS FAVOURITE");

        if(Utilities.check_wifi(this))
            Picasso.with(getBaseContext()).load(movie.getImageLink()).into(imageView);
        else
            imageView.setImageBitmap( Utilities.getImage(i.getByteArrayExtra("image_bmp")));



        rAdapter = new RecyclerAdapter(reviewList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(rAdapter);

        if(reviewList.size() == 0)
            reviews.setVisibility(View.VISIBLE);

        ScrollView scroll = (ScrollView) findViewById(R.id.scrollView);
        scroll.setFocusableInTouchMode(true);
        scroll.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        if(!Utilities.check_wifi(this))
        {
            btnTrailer1.setVisibility(View.GONE);
            btnTrailer2.setVisibility(View.GONE);
            findViewById(R.id.trailer).setVisibility(View.GONE);
            findViewById(R.id.seperator).setVisibility(View.GONE);
        }
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
