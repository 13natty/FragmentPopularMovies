package com.nattysoft.fragmentpopularmovies;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    private static int clickedItemPos = 0;
    private ProgressDialog pDialog;

    private static final String MOVIE_KEY = "movieList";

    // JSON Node names
    private static final String TAG_RESULTS = "results";
    private static final String TAG_ADULT = "adult";
    private static final String TAG_BACKDROP_PATH = "backdrop_path";
    private static final String TAG_GENRE_IDS = "genre_ids";
    private static final String TAG_ID = "id";
    private static final String TAG_ORIGINAL_LANGUAGE = "original_language";
    private static final String TAG_ORIGINAL_TITLE = "original_title";
    private static final String TAG_OVERVIEW = "overview";
    private static final String TAG_RELEASE_DATE = "release_date";
    private static final String TAG_POSTER_PATH = "poster_path";
    private static final String TAG_POPULARITY = "popularity";
    private static final String TAG_TITLE = "title";
    private static final String TAG_VIDEO = "video";
    private static final String TAG_VOTE_AVERAGE = "vote_average";
    private static final String TAG_VOTE_COUNT = "vote_count";

    // movies JSONArray
    JSONArray movies = null;

    ArrayList<MovieItem> moviesList;
    private GridView gridView;
    private GridViewAdapter gridAdapter;

    private String poster_path;
    private String title;
    private String adult;
    private String backdrop_path;
    private String genre_ids;
    private String mov_id;
    private String original_language;
    private String original_title;
    private String overview;
    private String release_date;
    private String popularity;
    private String video;
    private String vote_average;
    private String vote_count;

    public static String KEY;

    // URL to get themoviedb JSON
    private static String url;
    private static int sort = 0;

    private static final String TAG = MainActivity.class.getName();
    private Configuration config;
    private Target loadTarget;
    private ArrayList<TrailerObject> trailerList;
    private String trailersURL = null;
    private LinearLayout seriesMainListItemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        KEY = getString(R.string.movie_api_key);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sort = settings.getInt("sort", 0);

        switch(sort){
            case 0:
                url = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key="+ KEY;
                break;
            case 1:
                url = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key="+ KEY;
                break;
            default:
                url = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key="+ KEY;
        }

        Log.d(TAG, "befor inflate >>> ");

        //View view = inflater.inflate(R.layout.activity_main, container, false);

        Log.d(TAG, "inflated >>> ");
        moviesList = new ArrayList<MovieItem>();

        if(savedInstanceState == null || !savedInstanceState.containsKey(MOVIE_KEY)) {

            if(isNetworkAvailable()) {
                // Calling async task to get json
                new GetMovies().execute();
            }else{

                new AlertDialog.Builder(this)
                        .setTitle("No connection.")
                        .setMessage("You have no internet connection.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        } else {
            moviesList = (ArrayList<MovieItem>) savedInstanceState.get(MOVIE_KEY);

            displayGrid();
        }

        config = getResources().getConfiguration();


        /**
         * Check the device orientation and act accordingly
         */
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            /**
             * Landscape mode of the device
             */
            setContentView(R.layout.activity_main2);
        }else{
            /**
             * Portrait mode of the device
             */
            setContentView(R.layout.activity_main1);
        }


    }

    @Override
    public boolean  onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_sort:
                final CharSequence[] items = {
                        "Sort order by Most Popular", "Sort order by Highest-Rated"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Change Sort order");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch(item){
                            case 0:
                                if(sort!=0) {
                                    url = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key="+ KEY;
                                    sort = 0;
                                    moviesList = new ArrayList<MovieItem>();
                                    new GetMovies().execute();
                                }else{
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Most Popular sorted")
                                            .setMessage("Your movies are already sorted according to most popular")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).create().show();
                                }
                                break;
                            case 1:
                                if(sort!=1) {
                                    url = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key="+ KEY;
                                    sort = 1;
                                    moviesList = new ArrayList<MovieItem>();
                                    new GetMovies().execute();
                                }else{
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Highest-Rated sorted")
                                            .setMessage("Your movies are already sorted according to highest-rated")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).create().show();
                                }
                                break;
                        }
                        //save sort
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this.getApplicationContext());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt("sort", sort);
                        editor.commit();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isNetworkAvailable() {
        Log.d(TAG, "isNetworkAvailable >>>>>>>>>>>>>>>>>>>>");
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetMovies extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(true);
            if(!pDialog.isShowing())
                pDialog.show();

        }

        @Override
        protected String doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.d("Response: ", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + jsonStr);


            return jsonStr;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG, "onPostExecute >>> " );
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */

            if (result != null) {
                try {
                    JSONObject jsonObj = new JSONObject(result);

                    // Getting JSON Array node
                    movies = jsonObj.getJSONArray(TAG_RESULTS);

                    // looping through All Movies
                    for (int i = 0; i < movies.length(); i++) {
                        JSONObject c = movies.getJSONObject(i);

                        poster_path = c.getString(TAG_POSTER_PATH);
                        title = c.getString(TAG_TITLE);
                        adult = c.getString(TAG_ADULT);
                        backdrop_path = c.getString(TAG_BACKDROP_PATH);
                        genre_ids = c.getString(TAG_GENRE_IDS);
                        mov_id = c.getString(TAG_ID);
                        original_language = c.getString(TAG_ORIGINAL_LANGUAGE);
                        original_title = c.getString(TAG_ORIGINAL_TITLE);
                        overview = c.getString(TAG_OVERVIEW);
                        if(overview == "null")
                            overview = "No overview specified";

                        release_date = c.getString(TAG_RELEASE_DATE);
                        if(release_date == "null")
                            release_date = "Year not specified";

                        popularity = c.getString(TAG_POPULARITY);
                        video = c.getString(TAG_VIDEO);
                        vote_average = c.getString(TAG_VOTE_AVERAGE);
                        vote_count = c.getString(TAG_VOTE_COUNT);

                        MovieItem movie = new MovieItem(poster_path, title, mov_id);


                        // adding a movie to movie list
                        moviesList.add(movie);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            displayGrid();
            trailersURL = "http://api.themoviedb.org/3/movie/" + moviesList.get(0).getID() + "/videos?api_key=" + MainActivity.KEY;
            new GetTrailers().execute();
        }
    }

    private void displayGrid() {

        Log.d(TAG, "displayGrid >>> " );
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, moviesList);
        gridView = (GridView) this.findViewById(R.id.gridView);
        gridView.setAdapter(gridAdapter);

        //landscape mode
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            //get details of the clicked item
            JSONObject c = null;
            try {
                c = movies.getJSONObject(0);

                poster_path = c.getString(TAG_POSTER_PATH);
                title = c.getString(TAG_TITLE);
                adult = c.getString(TAG_ADULT);
                backdrop_path = c.getString(TAG_BACKDROP_PATH);
                genre_ids = c.getString(TAG_GENRE_IDS);
                mov_id = c.getString(TAG_ID);
                original_language = c.getString(TAG_ORIGINAL_LANGUAGE);
                original_title = c.getString(TAG_ORIGINAL_TITLE);
                overview = c.getString(TAG_OVERVIEW);
                if(overview == "null")
                    overview = "No overview specified";

                release_date = c.getString(TAG_RELEASE_DATE);
                if(release_date == "null")
                    release_date = "Year not specified";

                popularity = c.getString(TAG_POPULARITY);
                video = c.getString(TAG_VIDEO);
                vote_average = c.getString(TAG_VOTE_AVERAGE);
                vote_count = c.getString(TAG_VOTE_COUNT);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            MovieItem item = (MovieItem) gridAdapter.getItem(0);//temAtPosition(0);



            TextView title = (TextView) findViewById(R.id.title);
            title.setText(item.getTitle());

            TextView year = (TextView) findViewById(R.id.year);
            if(release_date == "null")
                year.setText("Year not specified");
            else
                year.setText(release_date.substring(0, 4));

            trailerList = new ArrayList<TrailerObject>();

            ToggleButton toggle = (ToggleButton) findViewById(R.id.toggle);
            toggle.setChecked(item.getFavorite());

            String movie_id = mov_id;

            TextView overviewTextView = (TextView) findViewById(R.id.overview);
            if(overview == "null")
                overviewTextView.setText("No overiew specified");
            else
                overviewTextView.setText(overview);

            seriesMainListItemView = (LinearLayout) findViewById(R.id.trailers_container);

            try {
                ArrayList<Object> passing = new ArrayList<Object>();
                passing.add("http://api.themoviedb.org/3/movie/"+movie_id+"/videos?api_key="+MainActivity.KEY);
                passing.add("Loading trailers ...");
                //passing.add(DetailsActivity.this);
                AsyncTask task = new httpGet().execute(passing);

                Object taskResults = task.get();
                Log.d("taskResults ","taskResults >>>>>>>>>>>>>>>>>>>>.. "+taskResults);
            }catch (Exception e){
                Log.d("Exception ","Exception >>>>>>>>>>>>>>>>>>>>.. "+e.getMessage());
            }

            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                loadBitmap("http://image.tmdb.org/t/p/w500/" + backdrop_path);
            }else {
                loadBitmap("http://image.tmdb.org/t/p/w780/" + backdrop_path);
            }
        }

        Log.d(TAG, "moviesList" + moviesList);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                MainActivity.clickedItemPos = position;
                //get details of the clicked item
                JSONObject c = null;
                try {
                    c = movies.getJSONObject(position);

                    Log.d(TAG, "c ------------> " + c);

                    poster_path = c.getString(TAG_POSTER_PATH);
                    title = c.getString(TAG_TITLE);
                    adult = c.getString(TAG_ADULT);
                    backdrop_path = c.getString(TAG_BACKDROP_PATH);
                    genre_ids = c.getString(TAG_GENRE_IDS);
                    mov_id = c.getString(TAG_ID);
                    original_language = c.getString(TAG_ORIGINAL_LANGUAGE);
                    original_title = c.getString(TAG_ORIGINAL_TITLE);

                    overview = c.getString(TAG_OVERVIEW);
                    if (overview == "null")
                        overview = "No overview specified";

                    release_date = c.getString(TAG_RELEASE_DATE);
                    if (release_date == "null")
                        release_date = "Year not specified";

                    popularity = c.getString(TAG_POPULARITY);
                    video = c.getString(TAG_VIDEO);
                    vote_average = c.getString(TAG_VOTE_AVERAGE);
                    vote_count = c.getString(TAG_VOTE_COUNT);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {

                    MovieItem item = (MovieItem) parent.getItemAtPosition(position);
                    TextView title = (TextView) findViewById(R.id.title);
                    title.setText(item.getTitle());

                    TextView year = (TextView) findViewById(R.id.year);
                    if (release_date == "null")
                        year.setText("Year not specified");
                    else
                        year.setText(release_date.substring(0, 4));

                    ToggleButton toggle = (ToggleButton) findViewById(R.id.toggle);
                    toggle.setChecked(item.getFavorite());

                    String movie_id = mov_id;

                    TextView overviewTextView = (TextView) findViewById(R.id.overview);
                    if (overview == "null")
                        year.setText("No overiew specified");
                    else
                        overviewTextView.setText("" + overview);

                    try {
                        ArrayList<Object> passing = new ArrayList<Object>();
                        passing.add("http://api.themoviedb.org/3/movie/" + movie_id + "/videos?api_key=" + MainActivity.KEY);
                        passing.add("Loading trailers ...");
                        //passing.add(DetailsActivity.this);
                        AsyncTask task = new httpGet().execute(passing);

                        Object taskResults = task.get();
                        Log.d("taskResults ", "taskResults >>>>>>>>>>>>>>>>>>>>.. " + taskResults);
                    } catch (Exception e) {
                        Log.d("Exception ", "Exception >>>>>>>>>>>>>>>>>>>>.. " + e.getMessage());
                    }

                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        loadBitmap("http://image.tmdb.org/t/p/w500/" + backdrop_path);
                    } else {
                        loadBitmap("http://image.tmdb.org/t/p/w780/" + backdrop_path);
                    }

                    try {
                        trailersURL = "http://api.themoviedb.org/3/movie/" + movie_id + "/videos?api_key=" + MainActivity.KEY;
                        new GetTrailers().execute();
                    }catch (Exception e){
                        Log.d("Exception ","Exception >>>>>>>>>>>>>>>>>>>>.. "+e.getMessage());
                    }

                } else {


                    MovieItem item = (MovieItem) parent.getItemAtPosition(position);
                    //Create intent
                    Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                    intent.putExtra("title", item.getTitle());
                    intent.putExtra("imageURL", item.getImageURL());
                    intent.putExtra("adult", adult);
                    intent.putExtra("backdrop_path", backdrop_path);
                    intent.putExtra("genre_ids", genre_ids);
                    intent.putExtra("id", mov_id);
                    intent.putExtra("original_language", original_language);
                    intent.putExtra("original_title", original_title);
                    intent.putExtra("overview", overview);
                    intent.putExtra("release_date", release_date);
                    intent.putExtra("popularity", popularity);
                    intent.putExtra("video", video);
                    intent.putExtra("vote_average", vote_average);
                    intent.putExtra("vote_count", vote_count);
                    intent.putExtra("movie_item", item);
                    intent.putExtra("movie_item", item);

                    //Start details activity
                    startActivity(intent);
                }
            }
        });
    }

    public void loadBitmap(String url) {
        Log.d(TAG, "loadBitmap ---------------------------------> url "+url);
        ImageView imageView = (ImageView) findViewById(R.id.lm_image);
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
                Log.d(TAG, "onPrepareLoad");
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(true);
                if(!pDialog.isShowing())
                    pDialog.show();
            }
        };

        Picasso.with(this).load(url).into(loadTarget);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setBackgroundImage(Bitmap bitmap) {
        ImageView imageView = (ImageView) findViewById(R.id.lm_image);
        imageView.setBackground(new BitmapDrawable(getResources(), bitmap));
    }

    public void onToggleClicked(View view) {
        // Is the toggle on?
        ((MovieItem) gridAdapter.getItem(clickedItemPos)).setFavorite(((ToggleButton) view).isChecked());
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
                if(!pDialog.isShowing())
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
            }
            /**
             * Updating parsed JSON data into ListView
             * */

            if (result != null && seriesMainListItemView != null) {
                try {
                    JSONObject jsonObj = new JSONObject(result);

                    Log.d(TAG, "jsonObj --------------------- >"+jsonObj);

                    // Getting JSON Array node
                    JSONArray trailers = jsonObj.getJSONArray(TAG_RESULTS);
                    trailerList.clear();
                    trailerList = new ArrayList<TrailerObject>();
                    seriesMainListItemView.removeAllViews();
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

                        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        View inflatedView = mInflater.inflate(R.layout.trailer_item_layout, null);
                        TextView label = (TextView) inflatedView.findViewById(R.id.trailer_label);
                        label.setText(trailerName);
                        inflatedView.setOnClickListener(new View.OnClickListener() {

                            public void onClick(View v)
                            {
                                TrailerObject trailerObect = trailerList.get(seriesMainListItemView.indexOfChild(v));
                                trailersURL = "http://api.themoviedb.org/3/movie/" + trailerObect.trailerID + "/videos?api_key=" + MainActivity.KEY;
                                Log.d(TAG, "trailerObect.trailerID -------------------------------> " + trailerObect.trailerID);
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + trailerObect.trailerKey)));

                            }
                        });
                        seriesMainListItemView.addView(inflatedView);

                        // adding a movie to movie list
                        trailerList.add(trailer);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
        }
    }
}
