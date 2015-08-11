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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vel9studios.levani.popularmovies.R;
import com.vel9studios.levani.popularmovies.beans.Movie;
import com.vel9studios.levani.popularmovies.constants.AppConstants;
import com.vel9studios.levani.popularmovies.constants.DetailFragmentConstants;
import com.vel9studios.levani.popularmovies.data.FetchVideosTask;
import com.vel9studios.levani.popularmovies.data.MoviesContract;
import com.vel9studios.levani.popularmovies.views.ReviewsAdapter;
import com.vel9studios.levani.popularmovies.views.TrailerAdapter;

import java.util.ArrayList;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    public static final String DETAIL_URI = "URI";

    TextView mTitle;
    TextView mReleaseDate;
    TextView mVoteAverage;
    TextView mMovieOverview;
    String mMovieId;
    String mFavoriteInd;
    TextView mFavorite;
    TextView mReviews;
    TrailerAdapter mTrailerAdapter;
    ReviewsAdapter mReviewsAdapter;
    ImageView mPoster;
    ListView mTrailerListView;
    ListView mReviewsListView;

    //Uris
    Uri mUri;
    Uri mVideosUri;
    Uri mReviewsUri;

    private static final int DETAIL_LOADER = 0;
    private static final int VIDEO_LOADER = 1;
    private static final int REVIEWS_LOADER = 2;

    private Movie movie;
    public DetailFragment() {

    }

    //Build detail view of the movie
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();

        if (savedInstanceState == null) {

            if (arguments != null) {
                mUri = arguments.getParcelable(DETAIL_URI);
                getVideoData();
            }
        } else {

            mUri = (Uri) savedInstanceState.getParcelable(DETAIL_URI);
            getVideoData();
        }


        //set text elements
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mTrailerAdapter = new TrailerAdapter(getActivity(), null, 0);
        mTrailerListView = (ListView) rootView.findViewById(R.id.listview_trailers);
        mTrailerListView.setAdapter(mTrailerAdapter);
        mTrailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {

                    //http://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
                    String youtubeKey = cursor.getString(DetailFragmentConstants.COLUMN_VIDEO_KEY);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtubeKey));
                    intent.putExtra("VIDEO_ID", youtubeKey);
                    startActivity(intent);
                }
            }
        });

        mTitle = (TextView) rootView.findViewById(R.id.detail_movie_title);
        mReleaseDate = (TextView) rootView.findViewById(R.id.detail_movie_release_date);
        mVoteAverage = (TextView) rootView.findViewById(R.id.detail_movie_vote_average);
        mMovieOverview = (TextView) rootView.findViewById(R.id.detail_movie_overview);
        mPoster = (ImageView) rootView.findViewById(R.id.detail_movie_image);
        mFavorite = (TextView) rootView.findViewById(R.id.detail_favorite);
        mReviews = (TextView) rootView.findViewById(R.id.detail_reviews);

        return rootView;
    }

    private void getVideoData(){

        String movieId = mUri.getLastPathSegment();

        FetchVideosTask fetchVideosTask = new FetchVideosTask(getActivity());
        fetchVideosTask.execute(movieId);

        mVideosUri = MoviesContract.VideosEntry.buildVideosUri(movieId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(VIDEO_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void updateFavorite(){

        String favoriteInd = "";
        if (mFavoriteInd != null && mFavoriteInd.equals("Y"))
            favoriteInd = "N";
        else
            favoriteInd = "Y";

        Uri favoriteUri = MoviesContract.MoviesEntry.buildFavoriteUri(mMovieId, favoriteInd);
        int updated = getActivity().getContentResolver().update(favoriteUri, null, null, null);
    }

    public void onSortOrderChanged(String sortType){

        //TODO: figure out what makes sense here
        //getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == DETAIL_LOADER && mUri != null){

            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DetailFragmentConstants.MOVIE_DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        if (id == VIDEO_LOADER && mVideosUri != null){

            return new CursorLoader(
                    getActivity(),
                    mVideosUri,
                    DetailFragmentConstants.VIDEO_DETAIL_COLUMNS,
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

            int currentLoader = loader.getId();
            if (currentLoader == DETAIL_LOADER){

                mTitle.setText(cursor.getString(DetailFragmentConstants.COLUMN_MOVIE_TITLE_ID));
                mReleaseDate.setText(cursor.getString(DetailFragmentConstants.COLUMN_RELEASE_DATE_ID));
                mVoteAverage.setText(String.valueOf(cursor.getDouble(DetailFragmentConstants.COLUMN_VOTE_AVERAGE_ID)));
                mMovieOverview.setText(cursor.getString(DetailFragmentConstants.COLUMN_OVERVIEW_ID));
                mMovieId = cursor.getString(DetailFragmentConstants.COLUMN_MOVIE_ID);
                mFavoriteInd = cursor.getString(DetailFragmentConstants.COLUMN_FAVORITE_IND_ID);

                ArrayList<String> favoriteValues = new ArrayList<>();
                favoriteValues.add(mMovieId);
                favoriteValues.add(mFavoriteInd);
                mFavorite.setTag(favoriteValues);

                mReviews.setTag(mMovieId);

                String posterPath = cursor.getString(DetailFragmentConstants.COLUMN_IMAGE_PATH_ID);
                String fullPosterPath = AppConstants.IMAGE_BASE_URL + AppConstants.DETAIL_IMAGE_QUERY_WIDTH + posterPath;
                Resources resources = getResources();
                int height = resources.getInteger(R.integer.grid_image_height);
                int width = resources.getInteger(R.integer.grid_image_width);

                Picasso.with(getActivity())
                        .load(fullPosterPath)
                        .resize(width, height)
                        .error(R.drawable.unavailable_poster_black)
                        .into(mPoster);

            } else if (currentLoader == VIDEO_LOADER) {
                Log.d(LOG_TAG, "COUNT IN UI " + cursor.getCount());
                mTrailerAdapter.swapCursor(cursor);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == VIDEO_LOADER)
            mTrailerAdapter.swapCursor(null);

    }

}
