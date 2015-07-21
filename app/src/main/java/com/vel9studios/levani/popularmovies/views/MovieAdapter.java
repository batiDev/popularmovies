package com.vel9studios.levani.popularmovies.views;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vel9studios.levani.popularmovies.R;
import com.vel9studios.levani.popularmovies.beans.Movie;
import com.vel9studios.levani.popularmovies.constants.AppConstants;

import java.util.ArrayList;

/**
 * Reference Code: https://devtut.wordpress.com/2011/06/09/custom-arrayadapter-for-a-listview-android/
 * Updated comments and getView method
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private final String LOG_TAG = MovieAdapter.class.getSimpleName();

    // declaring our ArrayList of items
    private ArrayList<Movie> movies;

    // Override the constructor for ArrayAdapter, set ArrayList<Movie>
    public MovieAdapter(Context context, int textViewResourceId, ArrayList<Movie> movies) {
        super(context, textViewResourceId, movies);
        this.movies = movies;
    }

    public ArrayList<Movie> getValues(){
        return movies;
    }

    /**
     * Prepare image, retrieve constants and allow for basic error handlng
     * @param imageView imageView that needs to be populated
     * @param posterPath partial path for retrieving poster URL
     */
    private void loadImage(ImageView imageView, String posterPath){

        //retrieve image dimension constants
        Context context = getContext();
        Resources resources = getContext().getResources();
        int height = resources.getInteger(R.integer.grid_image_height);
        int width = resources.getInteger(R.integer.grid_image_width);

        //basic error-handling, if there's no poster data, load generic image
        //Picasso has a .error() function but there seems to be a known issue with
        //sizing? https://github.com/square/picasso/issues/427
        if (posterPath.equals(AppConstants.STRING_NO_DATA)){

            Picasso.with(context)
                    .load(R.drawable.unavailable_poster_white)
                    .resize(width, height)
                    .into(imageView);
        } else {

            //generate full poster path
            String fullPosterPath = AppConstants.IMAGE_BASE_URL + AppConstants.GRID_IMAGE_QUERY_WIDTH + posterPath;
            Picasso.with(context)
                    .load(fullPosterPath)
                    .resize(width, height)
                    .into(imageView);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.grid_item_movie, null);
        }

        // The position refers to the position of the current object in the list. (The ArrayAdapter
        Movie movie = movies.get(position);

        if (movie != null) {

            String posterPath = movie.getImagePath();
            ImageView imageView = (ImageView) v.findViewById(R.id.grid_item_movie_image);
            loadImage(imageView, posterPath);
        }

        // the view must be returned to our activity
        return v;
    }

}

