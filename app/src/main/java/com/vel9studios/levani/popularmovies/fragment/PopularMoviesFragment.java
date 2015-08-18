package com.vel9studios.levani.popularmovies.fragment;

import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.vel9studios.levani.popularmovies.R;
import com.vel9studios.levani.popularmovies.data.MoviesContract;
import com.vel9studios.levani.popularmovies.sync.MoviesSyncAdapter;
import com.vel9studios.levani.popularmovies.util.Utility;
import com.vel9studios.levani.popularmovies.validation.Validation;
import com.vel9studios.levani.popularmovies.views.MovieAdapter;

public class PopularMoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = PopularMoviesFragment.class.getSimpleName();
    private MovieAdapter mMoviesAdapter;
    private Boolean mShowFavorites = false;
    private int mPosition = ListView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";
    private GridView mGridView;
    private static final int MOVIES_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID,
            //get only the necessary data from local db
            MoviesContract.MoviesEntry.COLUMN_MOVIE_ID,
            MoviesContract.MoviesEntry.COLUMN_IMAGE_PATH,
    };

    public static final int COLUMN_MOVIE_ID = 1;
    public static final int COLUMN_IMAGE_PATH_ID = 2;

    public PopularMoviesFragment() {
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     *
     *  code from Developing Android Apps: Fundamentals course
     */
    public interface Callback {
        void onItemSelected(Uri dataUri);
    }

    // flip favorites flag and restart loader
    public Boolean onFavoritesChanged() {
        mShowFavorites = !mShowFavorites;
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
        return mShowFavorites;
    }

    public void onSortOrderChanged(){

        // if sort changes, update everything
        MoviesSyncAdapter.syncImmediately(getActivity());
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //check for needed elements
        Context context = getActivity();

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        View view = rootView.findViewById(R.id.grid_item_movie_image);

        if (Validation.appContainsAPIKey(context)){

            mMoviesAdapter = new MovieAdapter(context, null, 0);
            mGridView = (GridView) view;

            mGridView.setAdapter(mMoviesAdapter);

            //set click-listener, called when user clicks an image
            //Core code from Udacity's "Developing Android Apps: Fundamentals"
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                    Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                    if (cursor != null) {
                        ((Callback) getActivity())
                                .onItemSelected(MoviesContract.MoviesEntry.buildMovieItemUri(cursor.getInt(COLUMN_MOVIE_ID)));

                        mPosition = position;
                    }
                }

            });

            if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
                mPosition = savedInstanceState.getInt(SELECTED_KEY);
            }

        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        // Core code from Udacity's "Developing Android Apps: Fundamentals"
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // build URI based on what the user is expecting, if favorites, only bring back records flagged as favorite
        Uri moviesUri;
        if (mShowFavorites)
            moviesUri = MoviesContract.MoviesEntry.buildMovieFavoritesUri();
        else
            moviesUri = MoviesContract.MoviesEntry.buildMoviesUri();

        String sortOrder = Utility.getSortOrderQuery(getActivity());

        return new CursorLoader(getActivity(),
                moviesUri,
                MOVIE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (mMoviesAdapter != null) mMoviesAdapter.swapCursor(cursor);

        // Core code from Udacity's "Developing Android Apps: Fundamentals"
        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }

}
