package com.example.android.sunshine.app;

import android.os.Parcel;
import android.os.Parcelable;

public class Movies {

    private int id;
//    private String RESULT ;
    private String POSTER_PATH ;
    private String OVERVIEW ;
    private String TITLE ;
    private String BACKGROUND_PATH;
    private String POPULARITY ;
    private String VOTE_AVERAGE ;
    private String RELEASE_DATE ;

    public Movies(){}

    public Movies(int id, String poster_path, String overview, String title, String background_bath, String popularity, String vote_average, String release_date){
        this.id = id;
        this.POSTER_PATH = poster_path;
        this.OVERVIEW = overview;
        this.TITLE = title;
        this.BACKGROUND_PATH = background_bath;
        this.POPULARITY = popularity;
        this.VOTE_AVERAGE = vote_average;
        this.RELEASE_DATE = release_date;
    }

    public int get_id(){
        return id;
    }
    public String get_poster_path(){
        return POSTER_PATH;
    }
    public String get_overview(){
        return OVERVIEW;
    }
    public String get_tittel(){
        return TITLE;
    }
    public String get_background_path(){
        return BACKGROUND_PATH;
    }
    public String get_popularity(){
        return POPULARITY;
    }
    public String get_vote_average(){
        return VOTE_AVERAGE;
    }
    public String get_release_date(){return RELEASE_DATE; }
}
