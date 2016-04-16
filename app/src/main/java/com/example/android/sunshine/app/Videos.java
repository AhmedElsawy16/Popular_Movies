package com.example.android.sunshine.app;

/**
 * Created by Ahmed on 1/6/2016.
 */
public class Videos {

    private String ID;
    private String KEY;
    private String NAME;

    public Videos(){}

    public Videos(String id, String key, String name){
        this.ID = id;
        this.KEY = key;
        this.NAME = name;
    }

    public String get_id(){
        return ID;
    }

    public String get_key(){
        return KEY;
    }

    public String get_name(){
        return NAME;
    }

}
