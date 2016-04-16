package com.example.android.sunshine.app;

/**
 * Created by Ahmed on 1/6/2016.
 */
public class Reviews {

    private String ID;
    private String AUTHOR;
    private String CONTENT;
    private String URL;

    public Reviews(){}

    public Reviews(String id, String author, String content, String url){
        this.ID = id;
        this.AUTHOR = author;
        this.CONTENT = content;
        this.URL = url;
    }

    public String get_id(){
        return ID;
    }
    public String get_author(){
        return AUTHOR;
    }
    public String get_content(){
        return CONTENT;
    }
    public String get_url(){
        return URL;
    }
}
