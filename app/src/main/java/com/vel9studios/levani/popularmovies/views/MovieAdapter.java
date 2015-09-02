package com.vel9studios.levani.popularmovies.views;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vel9studios.levani.popularmovies.R;
import com.vel9studios.levani.popularmovies.constants.AppConstants;
import com.vel9studios.levani.popularmovies.fragment.PopularMoviesFragment;

/**
 * {@link MovieAdapter} exposes a grid of movies
 * from a {@link android.database.Cursor} to a {@link android.widget.GridView}.
 */
public class MovieAdapter extends CursorAdapter {

    private final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_movie, parent, false);
        return view;
    }

    private void loadImage(ImageView imageView, String posterPath){

        // retrieve desired image dimension constants
        Resources resources = mContext.getResources();
        int height = resources.getInteger(R.integer.grid_image_height);
        int width = resources.getInteger(R.integer.grid_image_width);

        // some rror-handling, if there's no poster data, load generic image
        String fullPosterPath = AppConstants.QUERY_IMAGE_BASE_URL + AppConstants.QUERY_GRID_IMAGE_QUERY_WIDTH + posterPath;

        Picasso.with(mContext)
                .load(fullPosterPath)
                .resize(width, height)
                .error(R.drawable.unavailable_poster_white)
                .into(imageView);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Read data from cursor
        String posterPath = cursor.getString(PopularMoviesFragment.COLUMN_IMAGE_PATH_ID);
        ImageView imageView = (ImageView) view.findViewById(R.id.grid_item_movie_image);
        loadImage(imageView, posterPath);
    }

}