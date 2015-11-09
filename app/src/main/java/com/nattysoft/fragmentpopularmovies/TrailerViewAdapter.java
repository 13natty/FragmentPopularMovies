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

import java.util.ArrayList;
import com.nattysoft.fragmentpopularmovies.DetailsActivity.TrailerObject;
import com.squareup.picasso.Picasso;

/**
 * Created by F3838284 on 2015/11/06.
 */

public class TrailerViewAdapter extends ArrayAdapter<TrailerObject> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<TrailerObject> data = new ArrayList<TrailerObject>();

    public TrailerViewAdapter(Context context, int layoutResourceId, ArrayList<TrailerObject> data) {
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
            holder.tralerLabel = (TextView) row.findViewById(R.id.trailer_label);
            holder.image = (ImageView) row.findViewById(R.id.trailer_play);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }


        TrailerObject item = data.get(position);
        holder.tralerLabel.setText(item.trailerName);

        return row;
    }

    static class ViewHolder {
        TextView tralerLabel;
        ImageView image;
    }
}
