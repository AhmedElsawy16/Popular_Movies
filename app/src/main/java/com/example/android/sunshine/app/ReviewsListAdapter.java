package com.example.android.sunshine.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ahmed on 1/26/2016.
 */
public class ReviewsListAdapter extends ArrayAdapter<Reviews> {

    private List<Videos> myData;

    public ReviewsListAdapter(Context context){
        super(context, 0);
        this.myData = myData;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.reviews_list_item, null);
        }

        final Reviews reviews = getItem(position);

        TextView Author = (TextView) view.findViewById(R.id.author_textview);
        TextView Content = (TextView) view.findViewById(R.id.content_textview);

        if (Author != null){
            Author.setText(reviews.get_author());
        }

        if (Content != null){
            Content.setText(reviews.get_content());
        }

        return view;
    }
}
