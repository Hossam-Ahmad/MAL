package com.example.hossam.movies;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

class Data
{
    public static ArrayList<Movie> movies = new ArrayList();
    public static String order = "top_rated";
}

public class MainActivity extends AppCompatActivity implements Communicator
{
    final String API_KEY = "7686567ed5a6e3e363a2bb0e268fc737";

    Context context;
    String data = "",data_trailers="",Trailer1="",Trailer2="",data_reviews="";

    GridView gridview;
    ProgressDialog progress;
    Toolbar toolbar;
    int selected_movie=0;
    Intent shareIntent;
    ShareActionProvider mShareActionProvider;

    DBAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Top Movies");
        setSupportActionBar(toolbar);
        gridview = (GridView) findViewById(R.id.gridview);
        context = this;
        adapter = new DBAdapter(this);
        if(Is_Tablet())
        {
            View frag = findViewById(R.id.details_frag);
            frag.setVisibility(View.INVISIBLE);
        }

        if(Data.order == "top_rated")
            toolbar.setTitle("Top Movies");
        else if(Data.order == "popular")
            toolbar.setTitle("Pop Movies");
        else
            toolbar.setTitle("Favourite Movies");

        if(Utilities.check_internet(this))
        {
            if(Data.movies.size() == 0)
            {
                progress = ProgressDialog.show(this, "please wait",
                        "loading movies ..", true);
                new get_movies().execute();
            }
            else
                showData();
        }
        else
        {
            data_offline();
            showData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        if(Is_Tablet())
        {
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            if(Data.movies.size() > 0)
            {
                shareIntent.putExtra(Intent.EXTRA_TEXT, Data.movies.get(selected_movie).getTrailer1());
                mShareActionProvider.setShareIntent(shareIntent);
            }
        }
        else
            item.setVisible(false);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(Data.order != "favourite")
            data_offline();

        showData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.top_rated && Data.order != "top_rated")
        {
            toolbar.setTitle("Top Movies");
            Data.order = "top_rated";
            Data.movies.clear();
            showData();
            if(Utilities.check_internet(this))
            {
                progress = ProgressDialog.show(this, "please wait",
                        "loading movies ..", true);
                new get_movies().execute();
            }
            else
            {
                data_offline();
                showData();
            }
            return true;
        }
        else if(id == R.id.most_popular && Data.order != "popular")
        {
            toolbar.setTitle("Pop Movies");
            Data.order = "popular";
            Data.movies.clear();
            showData();
            if(Utilities.check_internet(this))
            {
                progress = ProgressDialog.show(this, "please wait",
                        "loading movies ..", true);
                new get_movies().execute();
            }
            else
                data_offline();
            showData();
            return true;
        }
        else if(id == R.id.favourite && Data.order != "favourite")
        {
            toolbar.setTitle("Favourite Movies");
            Data.order = "favourite";
            String URL = "content://com.example.hossam.movies/favourite/all";
            data_offline();
            showData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void data_offline()
    {
        try
        {
            String URL = "";
            if(Data.order == "favourite")
                URL = "content://com.example.hossam.movies/favourite/all";
            else if(Data.order == "top_rated")
                URL = "content://com.example.hossam.movies/top";
            else if(Data.order == "popular")
                URL = "content://com.example.hossam.movies/pop";

            Uri movies = Uri.parse(URL);
            Cursor c = managedQuery(movies, null, null, null, null);
            Data.movies.clear();
            while(c.moveToNext())
            {
                c.toString();
                ArrayList reviews = new ArrayList<String>();
                String reviews_String = c.getString(9);
                try
                {
                    reviews = new ArrayList<String>(Arrays.asList(reviews_String.split("%*_*%")));
                }
                catch(Exception e)
                {

                }
                int ID = c.getInt(0);
                String name = c.getString(1);
                String image_link = c.getString(2);
                String overview = c.getString(4);
                double rate = c.getDouble(5);
                String date = c.getString(6);
                String trailer = c.getString(7);
                String trailer2 = c.getString(8);

                Log.e("ID : ",ID+"");
                Log.e("NAME : ",name+"");
                Log.e("IMAGE LINK : ",image_link+"");
                Log.e("OVERVIEW : " , overview);
                Log.e("RATE : " , rate+"");
                Log.e("DATE : " , date);
                Log.e("TRAILER1 : ",trailer);
                Log.e("TRAILER2 : ",trailer2);
                Log.e("IMAGE : ",c.getBlob(3)+"");
                Log.e("","-------------------------------------------------------------------------------------------");

                Movie m =  new  Movie(name,image_link,overview,rate,date,ID,trailer,trailer2,reviews);
                m.setImage_bmp(Utilities.getImage(c.getBlob(3)));
                Data.movies.add(m);
            }
        }
        catch(Exception e)
        {
            Toast.makeText(context,"No Movies Found",Toast.LENGTH_LONG).show();
            Log.e("error",e.getMessage());
        }
    }

    public int get_id(String data,int index) throws JSONException
    {
        JSONObject weather = new JSONObject(data);
        JSONArray movies = weather.getJSONArray("results");
        JSONObject movieInfo = movies.getJSONObject(index);
        return movieInfo.getInt("id");
    }

    public String get_image(String data,int index) throws JSONException
    {
        // http://image.tmdb.org/t/p/w185//6bbZ6XyvgfjhQwbplnUh1LSj1ky.jpg
        JSONObject weather = new JSONObject(data);
        JSONArray movies = weather.getJSONArray("results");
        JSONObject movieInfo = movies.getJSONObject(index);
        return "http://image.tmdb.org/t/p/w185//"+movieInfo.getString("poster_path");
    }

    public String get_title(String data,int index) throws JSONException
    {
        JSONObject weather = new JSONObject(data);
        JSONArray movies = weather.getJSONArray("results");
        JSONObject movieInfo = movies.getJSONObject(index);
        return movieInfo.getString("original_title");
    }

    public String get_overview(String data,int index) throws JSONException
    {
        JSONObject weather = new JSONObject(data);
        JSONArray movies = weather.getJSONArray("results");
        JSONObject movieInfo = movies.getJSONObject(index);
        return movieInfo.getString("overview");
    }

    public String get_trailer(String data,int index) throws JSONException
    {
        JSONObject weather = new JSONObject(data);
        JSONArray movies = weather.getJSONArray("results");
        JSONObject movieInfo = movies.getJSONObject(index);
        return "https://www.youtube.com/watch?v="+movieInfo.getString("key");
    }

    public ArrayList<String> get_reviews(String data) throws JSONException
    {
        //String reviews = "";
        ArrayList<String> reviews_list = new ArrayList<String>();
        try
        {
            JSONObject weather = new JSONObject(data);
            JSONArray movies = weather.getJSONArray("results");
            for (int i = 0; i < movies.length(); i++)
            {
                reviews_list.add(movies.getJSONObject(i).getString("content"));
                //reviews += "- " + movies.getJSONObject(i).getString("content") + '\n' + '\n';
            }
        }
        catch (Exception e)
        {

        }
        //Log.e("review",reviews);
        return reviews_list;
    }

    public String get_releaseDate(String data,int index) throws JSONException
    {
        JSONObject weather = new JSONObject(data);
        JSONArray movies = weather.getJSONArray("results");
        JSONObject movieInfo = movies.getJSONObject(index);
        return movieInfo.getString("release_date");
    }

    public double get_rate(String data,int index) throws JSONException
    {
        JSONObject weather = new JSONObject(data);
        JSONArray movies = weather.getJSONArray("results");
        JSONObject movieInfo = movies.getJSONObject(index);
        return movieInfo.getDouble("vote_average");
    }

    public ArrayList<Movie> sort(ArrayList<Movie> list)
    {
        ArrayList<Movie> final_list = new ArrayList<>();
        ArrayList<Integer> favourite = new ArrayList<>();
        favourite =  adapter.selectFavouriteID();
        for(int i=0;i<favourite.size();i++)
        {
            int id = favourite.get(i);
            for(int j=0;j<list.size();j++)
            {
                if (id == list.get(j).getId())
                {
                    final_list.add(list.get(j));
                    list.remove(j);
                    j--;
                }
            }
        }
        for(int i=0;i<list.size();i++)
            final_list.add(list.get(i));
        return final_list;
    }

    @Override
    public void respond(Movie x)
    {
        FragmentManager fragmentManager = getFragmentManager();
        Details_Fragment details_fragment = (Details_Fragment) fragmentManager.findFragmentById(R.id.details_frag);
        details_fragment.sendmovie(x);
        View frag = findViewById(R.id.details_frag);
        frag.setVisibility(View.VISIBLE);
    }

    public boolean Is_Tablet()
    {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float scaleFactor = metrics.density;
        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;
        float smallestWidth = Math.min(widthDp, heightDp);
        return smallestWidth >= 600;
    }

    public class get_movies extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            try
            {
                URL url = new URL("https://api.themoviedb.org/3/movie/"+Data.order+"?api_key="+API_KEY);
                // Read all the text returned by the server
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String str,result="";
                while ((str = in.readLine()) != null)
                {
                    result += str;
                }
                in.close();
                data = result;
                JSONObject weather = new JSONObject(data);
                JSONArray moviess = weather.getJSONArray("results");
                int movies_count = moviess.length();
                Data.movies.clear();

                for(int i=0;i<movies_count;i++)
                {
                    String overview,title,image,date;
                    double rate;
                    int id;
                    overview = get_overview(data, i);
                    title = get_title(data, i);
                    image = get_image(data, i);
                    date = get_releaseDate(data, i);
                    rate = get_rate(data, i);
                    id = get_id(data,i);


                    try
                    {
                        url = new URL("http://api.themoviedb.org/3/movie/"+id+"/videos?api_key="+API_KEY);
                        // Read all the text returned by the server
                        in = new BufferedReader(new InputStreamReader(url.openStream()));
                        result="";
                        while ((str = in.readLine()) != null)
                        {
                            result += str;
                        }
                        in.close();
                        data_trailers = result;

                        Trailer1 = get_trailer(data_trailers,0);
                        try
                        {
                            Trailer2 = get_trailer(data_trailers, 1);
                        }
                        catch(Exception e)
                        {
                            Trailer2="";
                        }
                    } catch (Exception e) {
                    }

                    try
                    {
                        url = new URL("http://api.themoviedb.org/3/movie/"+id+"/reviews?api_key="+API_KEY);
                        // Read all the text returned by the server
                        in = new BufferedReader(new InputStreamReader(url.openStream()));
                        result="";
                        while ((str = in.readLine()) != null)
                        {
                            result += str;
                        }
                        in.close();
                        data_reviews = result;
                    } catch (Exception e) {
                    }

                    ArrayList<String> reviews_list = get_reviews(data_reviews);

                    Movie movie = new Movie(title,image,overview,rate,date,id,Trailer1,Trailer2,reviews_list);
                    Data.movies.add(movie);
                }


            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            showData();
            if(Is_Tablet())
            {
                shareIntent.putExtra(Intent.EXTRA_TEXT, Data.movies.get(selected_movie).getTrailer1());
                mShareActionProvider.setShareIntent(shareIntent);
            }
            cache();
            if(progress != null)
                progress.dismiss();
        }
    }

    public void showData()
    {
        try
        {
            Data.movies = sort(Data.movies);
            MyAdapter adapter2 = new MyAdapter(getBaseContext(),Data.movies);
            gridview.setAdapter(adapter2);

            if(Is_Tablet())
            {
                respond(Data.movies.get(0));
                FragmentManager fragmentManager = getFragmentManager();
                Details_Fragment details_fragment = (Details_Fragment) fragmentManager.findFragmentById(R.id.details_frag);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.show(details_fragment);
            }


            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    selected_movie = position;
                    if(shareIntent != null)
                        shareIntent.putExtra(Intent.EXTRA_TEXT, Data.movies.get(selected_movie).getTrailer1());
                    if(Is_Tablet())
                    {
                        respond(Data.movies.get(position));
                    }
                    else
                    {
                        Intent i = new Intent(MainActivity.this,Detailed.class);
                        i.putExtra("title",Data.movies.get(position).getName());
                        i.putExtra("rate",Data.movies.get(position).getRate());
                        i.putExtra("id",Data.movies.get(position).getId());
                        i.putExtra("image",Data.movies.get(position).getImageLink());
                        i.putExtra("overview",Data.movies.get(position).getOverview());
                        i.putExtra("date",Data.movies.get(position).getReleaseDate());
                        i.putExtra("trailer1",Data.movies.get(position).getTrailer1());
                        i.putExtra("trailer2",Data.movies.get(position).getTrailer2());
                        i.putExtra("reviews", Data.movies.get(position).getReviews());
                        if(!Utilities.check_internet(getApplicationContext()))
                            i.putExtra("image_bmp", Utilities.getBytes(Data.movies.get(position).getImage_bmp()));
                        startActivity(i);
                    }
                }
            });
        }
        catch(Exception e)
        {
            Log.e("error",e.getMessage());
        }
    }

    public void cache()
    {
        Log.e("info","caching");

        if(Data.order == "top_rated")
        {
            String URL = "content://com.example.hossam.movies/top";
            Uri parse = Uri.parse(URL);
            Cursor c = managedQuery(parse, null, null, null, null);
            if(!c.moveToNext())
            {
                Log.e("info","start cashing");
                for(int i=0;i<Data.movies.size();i++)
                {
                    adapter.insertTop(Data.movies.get(i));
                    Log.e("cashed",i+"");
                }
            }
        }

        else if(Data.order == "popular")
        {
            String URL = "content://com.example.hossam.movies/pop";
            Uri parse = Uri.parse(URL);
            Cursor c = managedQuery(parse, null, null, null, null);
            if(!c.moveToNext())
            {
                for(int i=0;i<Data.movies.size();i++)
                {
                    adapter.insertPop(Data.movies.get(i));
                    Log.e("cashed", i + "");
                }
            }
        }
        Log.e("info","cached");
    }

}
