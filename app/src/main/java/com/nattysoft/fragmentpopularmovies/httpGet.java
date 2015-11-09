package com.nattysoft.fragmentpopularmovies;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by F3838284 on 2015/10/06.
 */
public class httpGet extends AsyncTask<ArrayList<Object>, Void, String> {
    private ProgressDialog pDialog;
    private String url;
    public String loadingMessage;
    public Activity activity;
    ArrayList<Object> passed;

    @Override
    protected String doInBackground(ArrayList<Object>... params) {

        Log.d("params: ", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + params);

        passed =  params[0];
        this.url = passed.get(0).toString();
        this.loadingMessage = passed.get(1).toString();
        this.activity = (Activity) passed.get(2);

        // Creating service handler class instance
        ServiceHandler sh = new ServiceHandler();

        // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(url);

        Log.d("Response: ", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + jsonStr);

        if (jsonStr == null) {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }

        return jsonStr;
    }

    @Override
    protected void onPreExecute() {
        Log.d("activity: ", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + activity);
        Log.d("loadingMessage: ", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + loadingMessage);
        super.onPreExecute();
        // Showing progress dialog
        pDialog = new ProgressDialog(activity);
        pDialog.setMessage(loadingMessage);
        pDialog.setCancelable(true);
        pDialog.show();

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Dismiss the progress dialog
        if (pDialog.isShowing())
            pDialog.dismiss();
        /**
         * Updating parsed JSON data into ListView
         * */
    }
}
