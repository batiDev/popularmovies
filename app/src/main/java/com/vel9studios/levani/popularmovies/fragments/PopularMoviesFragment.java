package com.vel9studios.levani.popularmovies.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.vel9studios.levani.popularmovies.R;
import com.vel9studios.levani.popularmovies.constants.AppConstants;
import com.vel9studios.levani.popularmovies.constants.AppConstantsPrivate;
import com.vel9studios.levani.popularmovies.data.FetchMovieTask;
import com.vel9studios.levani.popularmovies.data.MoviesContract;
import com.vel9studios.levani.popularmovies.util.Utility;
import com.vel9studios.levani.popularmovies.views.MovieAdapter;

/**
 * Primary fragment, declares primary business methods
 * Contains inner Async FetchMoviesTask
 */
public class PopularMoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = PopularMoviesFragment.class.getSimpleName();
    private MovieAdapter mMoviesAdapter;
    private Boolean applicationRunStatus;
    private Boolean mShowFavorites = false;
    private int mPosition = ListView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";
    private GridView mGridView;
    private static final int MOVIES_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
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
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    public void onFavoritesChanged() {
        mShowFavorites = !mShowFavorites;
        Log.d(LOG_TAG, "Changing Favories");
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
    }

    public void onSortOrderChanged(String sortType){

        FetchMovieTask movieTask = new FetchMovieTask(getActivity());
        movieTask.execute(sortType);
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
    }

    /**
     * Set up method for checking for things which are absolutely needed for app to work.
     * TODO: Move into a validation class
     * @return true if app can go on
     */
    public Boolean appContainsAPIKey(){

        Boolean containsNeededElements = false;

        //check for API key
        if (AppConstantsPrivate.API_KEY.length() == 0){

            Log.e(LOG_TAG, AppConstants.API_KEY_WARNING);
            Toast apiWarning = Toast.makeText(getActivity(), AppConstants.API_KEY_WARNING, Toast.LENGTH_LONG);
            apiWarning.show();

            Toast appStart = Toast.makeText(getActivity(), AppConstants.APP_START_ERROR, Toast.LENGTH_LONG);
            appStart.show();
        } else {
            containsNeededElements = true;
        }

        return containsNeededElements;

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
        applicationRunStatus = appContainsAPIKey();

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        View view = rootView.findViewById(R.id.grid_item_movie_image);

        mMoviesAdapter = new MovieAdapter(context, null, 0);

        mGridView = (GridView) view;

        // The CursorAdapter will take data from our cursor and populate the ListView
        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
        // up with an empty list the first time we run.
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
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

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
        mMoviesAdapter.swapCursor(cursor);

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
