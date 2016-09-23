package com.example.hossam.movies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class Movies_Provider extends ContentProvider
{

    static final String PROVIDER_NAME = "com.example.hossam.movies";
    static final String URL = "content://" + PROVIDER_NAME + "/favourite";
    static final Uri CONTENT_URI = Uri.parse(URL);

    static final String TABLE_FAVOURITE = "Favourite_Movies";
    static final String TABLE_POP = "Pop_Movies";
    static final String TABLE_TOP = "Top_Movies";

    static final int MOVIES = 1;
    static final int MOVIE_ID = 2;
    static final int MOVIE_DEL = 3;
    static final int MOVIE_DETAILS = 4;

    static final int MOVIE_TOP = 5;
    static final int MOVIE_TOP_DEL = 6;

    static final int MOVIE_POP = 7;
    static final int MOVIE_POP_DEL = 8;

    DBAdapter adapter;

    static final UriMatcher uriMatcher;
    static
    {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "favourite", MOVIES);
        uriMatcher.addURI(PROVIDER_NAME, "favourite/all", MOVIE_DETAILS);
        uriMatcher.addURI(PROVIDER_NAME, "favourite/#", MOVIE_ID);
        uriMatcher.addURI(PROVIDER_NAME, "favourite/d/#", MOVIE_DEL);

        uriMatcher.addURI(PROVIDER_NAME, "top", MOVIE_TOP);
        uriMatcher.addURI(PROVIDER_NAME, "top/d", MOVIE_TOP_DEL);

        uriMatcher.addURI(PROVIDER_NAME, "pop", MOVIE_POP);
        uriMatcher.addURI(PROVIDER_NAME, "pop/d", MOVIE_POP_DEL);
    }


    @Override
    public boolean onCreate()
    {
        adapter =  new DBAdapter(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        Cursor c;
        switch (uriMatcher.match(uri))
        {
            case MOVIES :
                c = adapter.helper.getWritableDatabase().rawQuery("select ID from " + TABLE_FAVOURITE,null);
                break;
            case MOVIE_ID :
                c = adapter.helper.getWritableDatabase().rawQuery("select ID from "+TABLE_FAVOURITE+" where ID = "+uri.getLastPathSegment(),null);
                break;
            case MOVIE_DEL :
                adapter.helper.getWritableDatabase().delete(TABLE_FAVOURITE , "id = "+uri.getLastPathSegment(),null);
                c = null;
                break;
            case MOVIE_DETAILS:
                c = adapter.helper.getWritableDatabase().rawQuery("select * from "+TABLE_FAVOURITE , null);
                break;
            case MOVIE_TOP:
                c = adapter.helper.getWritableDatabase().rawQuery("select * from "+TABLE_TOP, null);
                break;
            case MOVIE_TOP_DEL:
                adapter.helper.getWritableDatabase().delete(TABLE_TOP , null,null);
                c = null;
                break;
            case MOVIE_POP:
                c = adapter.helper.getWritableDatabase().rawQuery("select * from "+TABLE_POP , null);
                break;
            case MOVIE_POP_DEL:
                adapter.helper.getWritableDatabase().delete(TABLE_POP , null,null);
                c = null;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri)
    {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        Log.e("id", values.getAsInteger("ID") + "");
        long rowID = adapter.insertData(values.getAsInteger("ID"));

        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        return 0;
    }
}
