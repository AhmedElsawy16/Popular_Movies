package com.example.android.sunshine.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed on 1/28/2016.
 */

public class Database extends SQLiteOpenHelper {

    public static final String DB_NAME = "favorites.db";
    public static final String TABLE_NAME = "favorites_t";

    public static final String COL_1 = "id";                    // 0
    public static final String POSTER = "poster_url";           //
    public static final String OVERVIEW = "overview";           //
    public static final String RELEASE_DATE = "release_date";   //
    public static final String VOTE_AVERAGE = "vote_average";   //
    public static final String BACKGROUND_PATH = "background_path"; //
    public static final String TITLE = "title";                 //
    public static final String FAVORITE_OR_NOT = "favorite_or_not";// 7
    public static final String ID = "movie_id";                 //
    public static final String POPULARITY = "popularity";       //

    public Database(Context context) {
        super(context, DB_NAME, null, 2);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + POSTER + " TEXT , "
                + OVERVIEW + " TEXT , "
                + RELEASE_DATE + " TEXT , "
                + VOTE_AVERAGE + " REAL , "
                + BACKGROUND_PATH + " TEXT , "
                + TITLE + " TEXT , "
                + FAVORITE_OR_NOT + " INTEGER DEFAULT 0 , "
                + ID + " TEXT UNIQE , "
                + POPULARITY + " TEXT "
                + ");";

        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(Movies movies){
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(POSTER, movies.get_poster_path());
        contentValues.put(OVERVIEW, movies.get_overview());
        contentValues.put(RELEASE_DATE, movies.get_release_date());
        contentValues.put(VOTE_AVERAGE, movies.get_vote_average());
        contentValues.put(BACKGROUND_PATH, movies.get_background_path());
        contentValues.put(TITLE, movies.get_tittel());
        contentValues.put(FAVORITE_OR_NOT, 1);
        contentValues.put(ID, movies.get_id());
        contentValues.put(POPULARITY, movies.get_popularity());

        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1){
            return false;
        }
        else
            return true;
    }

    public List<Movies> selectAll(){

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        Log.i("count = ", res.getColumnCount() + "");
        List<Movies> favorites = new ArrayList<>();

        while (res.moveToNext()){
            Movies movies = new Movies(
                    res.getInt(8),
                    res.getString(1),
                    res.getString(2),
                    res.getString(6),
                    res.getString(5),
                    res.getString(9),
                    res.getString(4),
                    res.getString(3)
            );

            favorites.add(movies);
        }
        return favorites;
    }

    public ArrayList getMoviesIds(){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor movieId = db.rawQuery("SELECT movie_id FROM " + TABLE_NAME, null);

        ArrayList allMoviesId = new ArrayList();

        while (movieId.moveToNext()){
            allMoviesId.add(movieId.getInt(0));
        }
        return allMoviesId;
    }

    public int deletMovie(String movieID){
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_NAME, ID + " = ?", new String[] {movieID});
        //db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + ID + " = " + movieID);
    }
}
