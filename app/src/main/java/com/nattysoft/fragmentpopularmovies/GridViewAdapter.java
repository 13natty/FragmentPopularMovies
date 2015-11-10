package com.nattysoft.fragmentpopularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by F3838284 on 2015/09/03.
 */

public class GridViewAdapter extends ArrayAdapter<MovieItem> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<MovieItem> data = new ArrayList<MovieItem>();

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<MovieItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.text);
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }


        MovieItem item = data.get(position);
        holder.imageTitle.setText(item.getTitle());
        if(this.context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            Picasso.with(this.context).load("http://image.tmdb.org/t/p/w185/" + item.getImageURL()).placeholder(R.drawable.android_loading).error(R.drawable.no_image).into(holder.image);
        }else {
            Picasso.with(this.context).load("http://image.tmdb.org/t/p/w500/" + item.getImageURL()).placeholder(R.drawable.android_loading).error(R.drawable.no_image).into(holder.image);
        }
        return row;
    }

    static class ViewHolder {
        TextView imageTitle;
        ImageView image;
    }
}