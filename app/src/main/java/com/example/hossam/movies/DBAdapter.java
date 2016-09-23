package com.example.hossam.movies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class DBAdapter
{
    DbHelper helper;

    String DB_NAME = "MoviesDB";

    String TABLE_FAVOURITE = "Favourite_Movies";
    String CREATE_TABLE_FAVOURITE = "CREATE TABLE "+ TABLE_FAVOURITE + " ( ID INTEGER PRIMARY KEY , NAME VARCHAR , IMAGE_LINK VARCHAR , IMAGE BLOB , OVERVIEW VARCHAR , RATE DECIMAL , DATE INTEGER , TRAILER1 VARCHAR , TRAILER2 VARCHAR , REVIEWS VARCHAR )";

    String TABLE_TOP = "Top_Movies";
    String CREATE_TABLE_TOP = "CREATE TABLE "+ TABLE_TOP + " ( ID INTEGER PRIMARY KEY , NAME VARCHAR , IMAGE_LINK VARCHAR , IMAGE BLOB , OVERVIEW VARCHAR , RATE DECIMAL , DATE INTEGER , TRAILER1 VARCHAR , TRAILER2 VARCHAR , REVIEWS VARCHAR )";

    String TABLE_POP = "Pop_Movies";
    String CREATE_TABLE_POP = "CREATE TABLE "+ TABLE_POP + " ( ID INTEGER PRIMARY KEY , NAME VARCHAR , IMAGE_LINK VARCHAR , IMAGE BLOB , OVERVIEW VARCHAR , RATE DECIMAL , DATE INTEGER , TRAILER1 VARCHAR , TRAILER2 VARCHAR , REVIEWS VARCHAR )";

    int VERSION_DB = 1;

    public DBAdapter(Context context)
    {
        helper = new DbHelper(context, TABLE_FAVOURITE,null,VERSION_DB);
    }

    public ArrayList<Integer> selectFavouriteID()
    {
        ArrayList<Integer> x = new ArrayList<>();
        SQLiteDatabase db = helper.getWritableDatabase();
        try
        {
            Cursor cursor = db.rawQuery("select ID from " + TABLE_FAVOURITE,null);
            while(cursor.moveToNext())
                x.add(cursor.getInt(0));
        }
        catch (Exception e)
        {

        }
        return x;
    }

    public boolean selectFavouriteID(int id)
    {
        ArrayList<Integer> x = new ArrayList<>();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_FAVOURITE + " where ID = " + id, null);
        return(cursor.moveToNext());
    }

    public long insertData(int id)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        long num = db.insert(TABLE_FAVOURITE, null, contentValues);
        return num;
    }

    public long insertFavourite(Movie movie)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", movie.getId());
        contentValues.put("NAME", movie.getName());
        contentValues.put("IMAGE_LINK", movie.getImageLink());
        contentValues.put("IMAGE", Utilities.getBytes(movie.getImage_bmp()));
        contentValues.put("OVERVIEW", movie.getOverview());
        contentValues.put("RATE", movie.getRate());
        contentValues.put("DATE", movie.getReleaseDate());
        contentValues.put("TRAILER1", movie.getTrailer1());
        contentValues.put("TRAILER2", movie.getTrailer2());
        String reviews;
        try
        {
            reviews = movie.getReviews().get(0);
            for(int i=1;i<movie.getReviews().size();i++)
                reviews += "%*_*%" + movie.getReviews().get(i);
        }
        catch(Exception e)
        {
            reviews = "No Reviews Found";
        }
        contentValues.put("REVIEWS", reviews);
        long num = db.insert(TABLE_FAVOURITE, null, contentValues);
        return num;
    }

    public long insertTop(Movie movie)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", movie.getId());
        contentValues.put("NAME", movie.getName());
        contentValues.put("IMAGE_LINK", movie.getImageLink());
        contentValues.put("IMAGE", Utilities.getBytes(movie.getImage()));
        contentValues.put("OVERVIEW", movie.getOverview());
        contentValues.put("RATE", movie.getRate());
        contentValues.put("DATE", movie.getReleaseDate());
        contentValues.put("TRAILER1", movie.getTrailer1());
        contentValues.put("TRAILER2", movie.getTrailer2());
        String reviews;
        try
        {
            reviews = movie.getReviews().get(0);
            for(int i=1;i<movie.getReviews().size();i++)
                reviews += "%*_*%" + movie.getReviews().get(i);
        }
        catch(Exception e)
        {
            reviews = "No Reviews Found";
        }
        contentValues.put("REVIEWS", reviews);
        long num = db.insert(TABLE_TOP, null, contentValues);
        return num;
    }

    public long insertPop(Movie movie)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", movie.getId());
        contentValues.put("NAME", movie.getName());
        contentValues.put("IMAGE_LINK", movie.getImageLink());
        contentValues.put("IMAGE", Utilities.getBytes(movie.getImage()));
        contentValues.put("OVERVIEW", movie.getOverview());
        contentValues.put("RATE", movie.getRate());
        contentValues.put("DATE", movie.getReleaseDate());
        contentValues.put("TRAILER1", movie.getTrailer1());
        contentValues.put("TRAILER2", movie.getTrailer2());
        String reviews;
        try
        {
            reviews = movie.getReviews().get(0);
            for(int i=1;i<movie.getReviews().size();i++)
                reviews += "%*_*%" + movie.getReviews().get(i);
        }
        catch(Exception e)
        {
            reviews = "No Reviews Found";
        }
        contentValues.put("REVIEWS", reviews);
        long num = db.insert(TABLE_POP, null, contentValues);
        return num;
    }

    public long deleteData(int id)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        long num = db.delete(TABLE_FAVOURITE, "id =" + id, null);
        return num;
    }

    class DbHelper extends SQLiteOpenHelper
    {
        public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_TABLE_FAVOURITE);
            Log.e("info", "favourite created");
            db.execSQL(CREATE_TABLE_TOP);
            Log.e("info", "top created");
            db.execSQL(CREATE_TABLE_POP);
            Log.e("info", "pop created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {

        }
    }
}
