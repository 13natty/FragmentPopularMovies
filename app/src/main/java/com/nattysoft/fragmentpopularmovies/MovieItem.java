package com.nattysoft.fragmentpopularmovies;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by F3838284 on 2015/09/03.
 */

public class MovieItem implements Parcelable {
    public String mov_id;
    public String title;
    public String movId;
    public String adult;
    public String backdrop_path;
    public String genre_ids;
    public String poster_path;
    public String original_language;
    public String original_title;
    public String overview;
    public String release_date;
    public String popularity;
    public String video;
    public String vote_average;
    public String vote_count;
    public boolean favorite = false;

    public MovieItem(Context context, String poster_path, String title, String movId, String adult, String backdrop_path, String genre_ids, String original_language,
                     String original_title, String overview, String release_date, String popularity, String video, String vote_average, String vote_count) {
        super();
        this.title = title;
        this.mov_id = movId;
        this.poster_path = poster_path;
        this.adult = adult;
        this.backdrop_path = backdrop_path;
        this.genre_ids = genre_ids;
        this.original_language = original_language;
        this.original_title = original_title;
        this.overview = overview;
        this.release_date = release_date;
        this.popularity = popularity;
        this.video = video;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
        this.favorite = Preferences.getPreference(context, mov_id)!=null? Preferences.getPreference(context, mov_id).equalsIgnoreCase("true"):false;
    }

    public MovieItem(Parcel in) {
        readFromParcel(in);
    }

    public String getImageURL() {
        return poster_path;
    }

    public void setImageURL(String imageURL) {
        this.poster_path = imageURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getID() {
        return mov_id;
    }

    public void setID(String mov_id) {
        this.mov_id = mov_id;
    }

    private void readFromParcel(Parcel in) {

        // We just need to read back each
        // field in the order that it was
        // written to the parcel
        poster_path = in.readString();
        title = in.readString();
        favorite = "true".equalsIgnoreCase(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster_path);
        dest.writeString(title);
        dest.writeString(favorite+"");
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean getFavorite() {
        return this.favorite;
    }

    public static final Parcelable.Creator CREATOR =
    new Parcelable.Creator() {
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        public MovieItem[] newArray(int size) {
            return new MovieItem[size];
        }
    };
}
