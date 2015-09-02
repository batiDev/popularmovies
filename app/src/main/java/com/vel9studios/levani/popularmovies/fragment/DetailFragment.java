package com.vel9studios.levani.popularmovies.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vel9studios.levani.popularmovies.R;
import com.vel9studios.levani.popularmovies.constants.AppConstants;
import com.vel9studios.levani.popularmovies.constants.ProjectionConstants;
import com.vel9studios.levani.popularmovies.data.FetchVideosTask;
import com.vel9studios.levani.popularmovies.data.MoviesContract;
import com.vel9studios.levani.popularmovies.util.AppUtils;
import com.vel9studios.levani.popularmovies.views.TrailerAdapter;

import java.util.ArrayList;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    public static final String DETAIL_URI = "URI";

    private ShareActionProvider mShareActionProvider;
    private String mFirstVideoYouTubeKey;

    // id for the movie in detail view, and the id used to fetch videos and reviews
    private String mMovieId;

    //views
    private TextView mTitle;
    private TextView mReleaseDate;
    private TextView mVoteAverage;
    private TextView mMovieOverview;
    private ImageButton mFavorite;
    private TextView mReviews;
    private ImageView mPoster;
    private ListView mTrailerListView;
    private TextView mNoTrailersView;

    // video/trailer values
    private TrailerAdapter mTrailerAdapter;

    //Uris
    private Uri mUri;
    private Uri mVideosUri;

    private static final int DETAIL_LOADER = 0;
    private static final int VIDEO_LOADER = 1;

    public DetailFragment() { setHasOptionsMenu(true);}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        // share intent code from Developing Android Apps: Fundamentals course
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mFirstVideoYouTubeKey != null){
            mShareActionProvider.setShareIntent(createShareVideoIntent(mFirstVideoYouTubeKey));
        }

    }

    private Intent createShareVideoIntent(String youTubeVideoKey) {

        // code from Developing Android Apps: Fundamentals course
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, AppConstants.YOUTUBE_URL_SHARE + youTubeVideoKey);
        return shareIntent;
    }

    //Build detail view of the movie
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null)
            mUri = arguments.getParcelable(DETAIL_URI);

        if (savedInstanceState != null)
            mMovieId = savedInstanceState.getString(AppConstants.MOVIE_ID_KEY);

        //set text elements
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mTrailerAdapter = new TrailerAdapter(getActivity(), null, 0);

        /* Add overview into the list view to take advantage of the built-in scrolling.
         * Avoids the "ListView in ScrollView" problem
         * http://stackoverflow.com/questions/7978359/using-listview-how-to-add-a-header-view
         *
         * Note: Should there be a better way of doing this. When implementing
         * RecyclerView into future projects, look into this type of scrolling handling.
         */
        mTrailerListView = (ListView) rootView.findViewById(R.id.listview_trailers);

        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.movie_details, mTrailerListView, false);
        mTrailerListView.addHeaderView(header, null, false);

        mTrailerListView.setAdapter(mTrailerAdapter);

        mTrailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    //http://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
                    String youTubeKey = cursor.getString(ProjectionConstants.COLUMN_VIDEO_KEY);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConstants.YOUTUBE_URI + youTubeKey));
                    intent.putExtra(AppConstants.YOUTUBE_VIDEO_ID, youTubeKey);
                    startActivity(intent);
                }
            }
        });

        setViews(rootView);

        return rootView;
    }

    private void setViews(View rootView)
    {
        // find views
        mTitle = (TextView) rootView.findViewById(R.id.detail_movie_title);
        mReleaseDate = (TextView) rootView.findViewById(R.id.detail_movie_release_date);
        mVoteAverage = (TextView) rootView.findViewById(R.id.detail_movie_vote_average);
        mMovieOverview = (TextView) rootView.findViewById(R.id.detail_movie_overview);
        mPoster = (ImageView) rootView.findViewById(R.id.detail_movie_image);
        mFavorite = (ImageButton) rootView.findViewById(R.id.detail_favorite);
        mReviews = (TextView) rootView.findViewById(R.id.detail_reviews);
        mNoTrailersView = (TextView) rootView.findViewById(R.id.listview_trailers_empty);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void onSortOrderChanged(){

        mUri = null;
        mVideosUri = null;
        // restart the loader to reflect the updated sort order change
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }

    public void onFavoriteToggle() {
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == DETAIL_LOADER){

            Uri moveDetailUri;
            /*
                if mUri is available, use it, else display details for first movie in db
                a. if mUri is available, user has explicitly selected a movie
                b. mUri will be unavalable if fragment is started in two pane mode and no movie has been explicitly selected.
             */
            String sortOrder = null;
            if (mUri != null)
                moveDetailUri =  mUri;
            else {
                sortOrder = AppUtils.getSortOrderQuery(getActivity());
                moveDetailUri = MoviesContract.MoviesEntry.buildFirstMovieUri();
            }

            return new CursorLoader(
                    getActivity(),
                    moveDetailUri,
                    ProjectionConstants.MOVIE_DETAIL_COLUMNS,
                    null,
                    null,
                    sortOrder
            );
        }

        if (id == VIDEO_LOADER && mVideosUri != null){

            return new CursorLoader(
                    getActivity(),
                    mVideosUri,
                    ProjectionConstants.VIDEO_DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        int currentLoader = loader.getId();
        if (currentLoader == DETAIL_LOADER && cursor.moveToFirst()){
            updateDetailsView(cursor);
        } else if (currentLoader == VIDEO_LOADER) {

            if (cursor.moveToFirst()){
                // get youtube video key for first trailer and create share video intent
                mFirstVideoYouTubeKey = cursor.getString(ProjectionConstants.COLUMN_VIDEO_KEY);
                if (mShareActionProvider != null)
                    mShareActionProvider.setShareIntent(createShareVideoIntent(mFirstVideoYouTubeKey));

                // hide the "no trailers" message. Ideally this would be possible with setEmptyView
                // but due to the way I am adding layouts into trailer list view, that methods ends up
                // overwriting the movie details layout. Revisit.
                mNoTrailersView.setVisibility(View.GONE);
            } else {
                mNoTrailersView.setVisibility(View.VISIBLE);
            }

            mTrailerAdapter.swapCursor(cursor);
        }

    }

    /**
     * Cursor will contain all of the data needed for controlling, painting the detail view, as well
     * as getting the values needed for retrieving trailers, and launching the reviews fragment
     *
     * @param cursor
     */
    private void updateDetailsView(Cursor cursor)
    {
        // update details view
        String movieTitle = cursor.getString(ProjectionConstants.COLUMN_MOVIE_TITLE_ID);
        mTitle.setText(movieTitle);
        mReleaseDate.setText(cursor.getString(ProjectionConstants.COLUMN_RELEASE_DATE_ID));
        mVoteAverage.setText(String.valueOf(cursor.getDouble(ProjectionConstants.COLUMN_VOTE_AVERAGE_ID)));
        mMovieOverview.setText(cursor.getString(ProjectionConstants.COLUMN_OVERVIEW_ID));

        String currentMovieId = cursor.getString(ProjectionConstants.COLUMN_MOVIE_ID);
        getVideos(currentMovieId);
        mMovieId = currentMovieId;

        //http://stackoverflow.com/questions/21114025/how-to-change-only-the-image-inside-an-imagebutton-and-not-the-whole-imagebutton
        String favoriteInd = cursor.getString(ProjectionConstants.COLUMN_FAVORITE_IND_ID);
        setFavoriteState(favoriteInd);

        // load image
        String posterPath = cursor.getString(ProjectionConstants.COLUMN_IMAGE_PATH_ID);
        String fullPosterPath = AppConstants.QUERY_IMAGE_BASE_URL + AppConstants.QUERY_DETAIL_IMAGE_QUERY_WIDTH + posterPath;
        loadImage(fullPosterPath);

        // gather values for saving movie to favorites
        ArrayList<String> favoriteValues = new ArrayList<>();
        favoriteValues.add(mMovieId);
        favoriteValues.add(favoriteInd);
        favoriteValues.add(movieTitle);
        mFavorite.setTag(favoriteValues);

        // set movieId value for launching ReviewsActivity
        mReviews.setTag(mMovieId);
    }

    private void setFavoriteState(String favoriteInd){

        if (favoriteInd != null && favoriteInd.equals(AppConstants.Y_FLAG))
            mFavorite.setImageResource(android.R.drawable.btn_star_big_on);
        else
            mFavorite.setImageResource(android.R.drawable.btn_star_big_off);
    }

    private void loadImage(String fullPosterPath){

        Resources resources = getResources();
        int height = resources.getInteger(R.integer.grid_image_height);
        int width = resources.getInteger(R.integer.grid_image_width);

        Picasso.with(getActivity())
                .load(fullPosterPath)
                .resize(width, height)
                .error(R.drawable.unavailable_poster_black)
                .into(mPoster);
    }

    private void getVideos(String currentMovieId) {

        LoaderManager loaderManager = getLoaderManager();
        /*
            if displaying details for a NEW movie, fetch videos.
            This method will be called on screen rotate as the fragment is recreated,
            In such a case, the movieId will be the same as it was before screen rotate -- no need to get videos again.
        */
        if (mMovieId == null || !mMovieId.equals(currentMovieId) ){

            mVideosUri = MoviesContract.VideosEntry.buildVideosUri(currentMovieId);
            FetchVideosTask fetchVideosTask = new FetchVideosTask(getActivity());
            fetchVideosTask.execute(currentMovieId);

            if (loaderManager.getLoader(VIDEO_LOADER) != null){
                loaderManager.restartLoader(VIDEO_LOADER, null, this);
                return;
            }

        }

        loaderManager.initLoader(VIDEO_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(AppConstants.MOVIE_ID_KEY, mMovieId);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == VIDEO_LOADER)
            mTrailerAdapter.swapCursor(null);
    }

}
