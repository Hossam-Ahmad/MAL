package com.example.hossam.movies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MyAdapter extends BaseAdapter
{
    private final LayoutInflater mInflater;
    ArrayList<Movie> movies = new ArrayList<>();
    Context context;
    public MyAdapter(Context context,ArrayList<Movie> movies)
    {
        mInflater = LayoutInflater.from(context);
        this.movies = movies;
        this.context = context;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public String getItem(int i) {
        return movies.get(i).getImageLink();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView picture;
        ImageView star;
        if (v == null) {
            v = mInflater.inflate(R.layout.grid_item, viewGroup, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.star, v.findViewById(R.id.star));
        }

        picture = (ImageView) v.getTag(R.id.picture);
        star = (ImageView) v.getTag(R.id.star);

        String URL = "content://com.example.hossam.movies/favourite/"+movies.get(i).getId();

        Uri students = Uri.parse(URL);
        Cursor c = context.getContentResolver().query(students, null, null, null, null);

        if (c.moveToFirst())
        {
            star.setVisibility(View.VISIBLE);
        }
        else
        {
            star.setVisibility(View.INVISIBLE);
        }

        if(Utilities.check_wifi(context))
            Picasso.with(context).load(movies.get(i).getImageLink()).into(picture);
        else
            picture.setImageBitmap(movies.get(i).getImage_bmp());
        c.close();
        return v;
    }

}
