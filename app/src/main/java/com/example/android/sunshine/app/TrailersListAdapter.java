package com.example.android.sunshine.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Ahmed on 1/7/2016.
 */
public class TrailersListAdapter extends ArrayAdapter<Videos> {


    public static class ViewHolder {
        // define  ImageView and name it
        public final ImageView imageView;

        public ViewHolder(View view) {
            // inflate this imageView into its xml File
            imageView = (ImageView) view.findViewById(R.id.image_arrow_play);
        }
    }

    private List<Videos> myData;

    public TrailersListAdapter(Context context){
        super(context, 0);
        this.myData = myData;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.trailers_list_items, null);

            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        final Videos videos = getItem(position);
        viewHolder = (ViewHolder) view.getTag();

        String yt_thumbnail_url = "http://img.youtube.com/vi/" + videos.get_key() + "/0.jpg";
        Picasso.with(getContext())
                .load(yt_thumbnail_url)
                .into(viewHolder.imageView);

        TextView tailerName = (TextView) view.findViewById(R.id.list_item_trailer_name);

        if (tailerName != null){
            tailerName.setText(videos.get_name());
        }

        return view;
    }

}
