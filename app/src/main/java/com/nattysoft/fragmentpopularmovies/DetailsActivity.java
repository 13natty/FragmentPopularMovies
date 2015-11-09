package com.nattysoft.fragmentpopularmovies;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by F3838284 on 2015/09/03.
 */

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG_RESULTS = "results";

    private static final String TAG = "DetailsActivity";
    private ProgressDialog pDialog;
    private Target loadTarget;
    private String movie_id;
    private MovieItem item;
    private String trailersURL = null;
    private ArrayList<TrailerObject> trailerList;
    private ListView trailerListview;
    private TrailerViewAdapter trailerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getIntent().getStringExtra("title"));

        TextView year = (TextView) findViewById(R.id.year);
        String yearText = getIntent().getStringExtra("release_date");
        if(!yearText.startsWith("Year"))
        year.setText(yearText);

        trailerList = new ArrayList<TrailerObject>();

        Bundle b = getIntent().getExtras();
        item = b.getParcelable("movie_item");
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggle);
        toggle.setChecked(item.getFavorite());

        movie_id = getIntent().getStringExtra("id");

        TextView overviewTextView = (TextView) findViewById(R.id.overview);

        String overviewText = getIntent().getStringExtra("overview");
        overviewTextView.setText(overviewText);

        trailerListview = (ListView) findViewById(R.id.trailer_listview);

        trailerListview.setClickable(true);
        trailerListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                TrailerObject trailerObect = trailerAdapter.getItem(position);
                trailersURL = "http://api.themoviedb.org/3/movie/" + trailerObect.trailerID + "/videos?api_key=" + MainActivity.KEY;
                Log.d(TAG, "trailerObect.trailerID -------------------------------> "+trailerObect.trailerID);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v="+trailerObect.trailerKey)));
            }
        });

        try {
            trailersURL = "http://api.themoviedb.org/3/movie/" + movie_id + "/videos?api_key=" + MainActivity.KEY;
            new GetTrailers().execute();
        }catch (Exception e){
            Log.d("Exception ","Exception >>>>>>>>>>>>>>>>>>>>.. "+e.getMessage());
        }

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            loadBitmap("http://image.tmdb.org/t/p/w500/" + getIntent().getStringExtra("backdrop_path"));
        }else {
            loadBitmap("http://image.tmdb.org/t/p/w780/" + getIntent().getStringExtra("backdrop_path"));
        }
        ((AppCompatActivity) this).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            loadBitmap("http://image.tmdb.org/t/p/original/" + getIntent().getStringExtra("backdrop_path"));
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            loadBitmap("http://image.tmdb.org/t/p/w780/" + getIntent().getStringExtra("backdrop_path"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadBitmap(String url) {

        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setBackgroundResource(R.drawable.no_image);

        if (loadTarget == null) loadTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // do something with the Bitmap
                setBackgroundImage(bitmap);
                if(pDialog != null)
                    pDialog.dismiss();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d(TAG, "onBitmapFailed");
                if(pDialog != null)
                    pDialog.dismiss();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.d(TAG,"onPrepareLoad");
                pDialog = new ProgressDialog(DetailsActivity.this);
                pDialog.setMessage("Please wait loading image...");
                pDialog.setCancelable(true);
                pDialog.show();
            }
        };

        Picasso.with(this).load(url).into(loadTarget);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setBackgroundImage(Bitmap bitmap) {
        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setBackground(new BitmapDrawable(getResources(), bitmap));
    }

    public void onToggleClicked(View view) {
        // Is the toggle on?
        item.setFavorite(((ToggleButton) view).isChecked());
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetTrailers extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            if (pDialog != null) {
                pDialog.setMessage("Please wait getting trailers...");
                pDialog.show();
            }
        }

        @Override
        protected String doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(trailersURL);

            Log.d("Response: ", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + jsonStr);

            if (pDialog != null) {
                pDialog.dismiss();
                pDialog.cancel();
            }

            return jsonStr;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute >>> " );
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog != null) {
                pDialog.dismiss();
                pDialog.cancel();
            }
            /**
             * Updating parsed JSON data into ListView
             * */

            if (result != null) {
                try {
                    JSONObject jsonObj = new JSONObject(result);

                   Log.d(TAG, "jsonObj --------------------- >"+jsonObj);

                    // Getting JSON Array node
                    JSONArray trailers = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < trailers.length(); i++) {
                        JSONObject c = trailers.getJSONObject(i);

                        String site = c.getString("site");
                        String trailerID = c.getString("id");
                        String iso_639_1 = c.getString("iso_639_1");
                        String trailerType = c.getString("type");
                        String trailerKey = c.getString("key");
                        String trailerSize = c.getString("size");
                        String trailerName = c.getString("name");

                        TrailerObject trailer = new TrailerObject(site, trailerID, iso_639_1, trailerType, trailerKey, trailerSize, trailerName);
                        // adding a movie to movie list
                        trailerList.add(trailer);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            displayTrailers();
        }
    }

    private void displayTrailers() {
        Log.d(TAG, "displayTrailers >>> " );
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog.cancel();
        }
        trailerAdapter = new TrailerViewAdapter(this, R.layout.trailer_item_layout, trailerList);

        trailerListview.setAdapter(trailerAdapter);
        setListViewHeightBasedOnChildren(trailerListview);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public class TrailerObject {
        String site = null;
        String trailerID = null;
        String iso_639_1 = null;
        String trailerType = null;
        String trailerKey = null;
        String trailerSize = null;
        String trailerName = null;

        public TrailerObject(String site, String trailerID, String iso_639_1, String trailerType, String trailerKey, String trailerSize, String trailerName){
            this.site = site;
            this.trailerID = trailerID;

            this.iso_639_1 = iso_639_1;

            this.trailerType = trailerType;

            this.trailerKey = trailerKey;

            this.trailerSize = trailerSize;

            this.trailerName = trailerName;

        }
    }

}