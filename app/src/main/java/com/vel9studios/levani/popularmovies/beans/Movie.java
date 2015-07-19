package com.vel9studios.levani.popularmovies.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by levani on 7/15/15.
 *
 * Parcelable code from http://www.parcelabler.com/
 */
public class Movie implements Parcelable {

    private String title;
    private String imagePath;
    private String releaseDate;
    private String overview;
    private String voteAverage;

    public Movie(){
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    protected Movie(Parcel in) {
        title = in.readString();
        imagePath = in.readString();
        releaseDate = in.readString();
        overview = in.readString();
        voteAverage = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(imagePath);
        dest.writeString(releaseDate);
        dest.writeString(overview);
        dest.writeString(voteAverage);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
