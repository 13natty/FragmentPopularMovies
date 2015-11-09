package com.nattysoft.fragmentpopularmovies;

/**
 * Created by F3838284 on 2015/09/01.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

public class ServiceHandler {
    static String response = null;

    public ServiceHandler() {
    }

    /*
     * Making service call
     * @url - url to make request
     * */
    public String makeServiceCall(String url) {

        try {
            URL requestUrl = new URL(url);

            URLConnection con = requestUrl.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();
            int cp;
            try {
                while ((cp = in.read()) != -1) {
                    sb.append((char) cp);
                }
            } catch (Exception e) {
            }
            response = sb.toString();


        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        return response;


    }

}