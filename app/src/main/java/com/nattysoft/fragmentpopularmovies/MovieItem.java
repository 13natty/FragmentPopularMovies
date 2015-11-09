package com.nattysoft.fragmentpopularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by F3838284 on 2015/09/03.
 */

public class MovieItem implements Parcelable {
    private String mov_id;
    private String imageURL;
    private String title;
    private boolean favorite = false;

    public MovieItem(String image, String title, String mov_id) {
        super();
        this.imageURL = image;
        this.title = title;
        this.mov_id = mov_id;
    }

    public MovieItem(Parcel in) {
        readFromParcel(in);
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
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
        imageURL = in.readString();
        title = in.readString();
        favorite = "true".equalsIgnoreCase(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageURL);
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
