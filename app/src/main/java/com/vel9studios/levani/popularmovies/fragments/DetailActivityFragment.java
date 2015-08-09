package com.vel9studios.levani.popularmovies.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vel9studios.levani.popularmovies.R;
import com.vel9studios.levani.popularmovies.beans.Movie;
import com.vel9studios.levani.popularmovies.constants.AppConstants;
import com.vel9studios.levani.popularmovies.data.MoviesContract;


public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private Movie movie;
    public DetailActivityFragment() {
    }

    TextView mTitle;
    TextView mReleaseDate;
    TextView mVoteAverage;
    TextView mMovieOverview;
    ImageView mPoster;
    Uri mUri;
    private static final int DETAIL_LOADER = 0;

    private static final String[] MOVIE_DETAIL_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_ID,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE,
            MoviesContract.MoviesEntry.COLUMN_IMAGE_PATH,
            MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MoviesEntry.COLUMN_OVERVIEW,
            MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE
    };

    public static final int COLUMN_MOVIE_ID = 1;
    public static final int COLUMN_MOVIE_TITLE_ID = 2;
    public static final int COLUMN_IMAGE_PATH_ID = 3;
    public static final int COLUMN_RELEASE_DATE_ID = 4;
    public static final int COLUMN_OVERVIEW_ID = 5;
    public static final int COLUMN_VOTE_AVERAGE_ID = 6;

    //Build detail view of the movie
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();

        if (intent != null){

            mUri = intent.getData();

            //set text elements
            mTitle = (TextView) rootView.findViewById(R.id.detail_movie_title);
            mReleaseDate = (TextView) rootView.findViewById(R.id.detail_movie_release_date);
            mVoteAverage = (TextView) rootView.findViewById(R.id.detail_movie_vote_average);
            mMovieOverview = (TextView) rootView.findViewById(R.id.detail_movie_overview);


            mPoster = (ImageView) rootView.findViewById(R.id.detail_movie_image);

        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    MOVIE_DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {

            mTitle.setText(cursor.getString(COLUMN_MOVIE_TITLE_ID));
            mReleaseDate.setText(cursor.getString(COLUMN_RELEASE_DATE_ID));
            mVoteAverage.setText(String.valueOf(cursor.getDouble(COLUMN_VOTE_AVERAGE_ID)));
            mMovieOverview.setText(cursor.getString(COLUMN_OVERVIEW_ID));

            String posterPath = cursor.getString(COLUMN_IMAGE_PATH_ID);
            String fullPosterPath = AppConstants.IMAGE_BASE_URL + AppConstants.DETAIL_IMAGE_QUERY_WIDTH + posterPath;
            Resources resources = getResources();
            int height = resources.getInteger(R.integer.grid_image_height);
            int width = resources.getInteger(R.integer.grid_image_width);

            Picasso.with(getActivity())
                    .load(fullPosterPath)
                    .resize(width, height)
                    .error(R.drawable.unavailable_poster_black)
                    .into(mPoster);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
