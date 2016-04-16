package com.example.android.sunshine.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class Adapter extends ArrayAdapter<Movies> {

    public static class ViewHolder {
        // define  ImageView and name it
        public final ImageView imageView;

        public ViewHolder(View view) {
            // inflate this imageView into its xml File
            imageView = (ImageView) view.findViewById(R.id.grid_item_image);
        }
    }

    public Adapter(Context context) {
        super(context, 0);
    }

    // to make the Adapter give its Data to the layout (xml file).
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        final Movies movie = getItem(position);
        String image_url = "http://image.tmdb.org/t/p/w185" + movie.get_poster_path();

        viewHolder = (ViewHolder) view.getTag();

        Picasso.with(getContext())
                .load(image_url)
                .into(viewHolder.imageView);

        return view;
    }
}
