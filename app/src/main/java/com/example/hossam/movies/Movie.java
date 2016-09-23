package com.example.hossam.movies;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Movie
{
    private String name,image,overview,releaseDate,trailer1,trailer2;
    private ArrayList<String> reviews;
    private double rate;
    private int id;
    private Bitmap image_bmp;

    public Movie(String name,String image,String overview,double rate,String releaseDate,int id,String trailer1,String trailer2,ArrayList<String> reviews)
    {
        this.id = id;
        this.name = name;
        this.image = image;
        this.overview = overview;
        this.rate = rate;
        this.releaseDate = releaseDate;
        this.trailer1 = trailer1;
        this.trailer2 = trailer2;
        this.reviews = reviews;
    }

    public Bitmap getImage_bmp() {
        return image_bmp;
    }

    public void setImage_bmp(Bitmap image_bmp) {
        this.image_bmp = image_bmp;
    }

    public void setReviews(ArrayList<String> reviews) {
        this.reviews = reviews;
    }

    public void setTrailer1(String trailer1) {
        this.trailer1 = trailer1;
    }

    public void setTrailer2(String trailer2) {
        this.trailer2 = trailer2;
    }

    public ArrayList<String> getReviews() {
        return reviews;
    }

    public String getTrailer1() {
        return trailer1;
    }

    public String getTrailer2() {
        return trailer2;
    }

    public int getId() {
        return id;
    }

    public double getRate() {
        return rate;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getImageLink() {
        return image;
    }

    public Bitmap getImage()
    {
        Bitmap bmImg = null;
        class download_Image extends AsyncTask<Bitmap,Bitmap,Bitmap>
        {
            @Override
            protected void onPostExecute(Bitmap o)
            {
                super.onPostExecute(o);
                Log.e("Done", "Done");
            }

            @Override
            protected Bitmap doInBackground(Bitmap... params)
            {
                try
                {
                    URL ImageUrl = new URL(getImageLink());
                    HttpURLConnection conn = (HttpURLConnection) ImageUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    Bitmap bmImg = BitmapFactory.decodeStream(is, null, options);
                    return bmImg;
                }
                catch(Exception e)
                {
                    Log.e("error", e.getMessage());
                }
                return null;
            }
        }
        try
        {
            bmImg = new download_Image().execute().get();
        }
        catch (Exception e)
        {
            Log.e("error : ",e.getMessage());
        }
        return bmImg;
    }

    public String getName() {
        return name;
    }

    public String getOverview() {
        return overview;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setId(int id) {
        this.id = id;
    }
}
