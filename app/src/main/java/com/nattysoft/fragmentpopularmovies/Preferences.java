package com.nattysoft.fragmentpopularmovies;

/**
 * Created by F3838284 on 2015/11/10.
 */
import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    @SuppressWarnings("unused")
    private static final String LOG_TAG = Preferences.class.getSimpleName();
    private static final String NAME = "PopularMovies";

    public static void savePreference(final Context context, final String key, final String value) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getPreference(final Context context, final String key) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }
}
