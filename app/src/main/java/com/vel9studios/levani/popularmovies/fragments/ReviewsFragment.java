package com.vel9studios.levani.popularmovies.fragments;

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
import android.widget.ListView;
import android.widget.TextView;

import com.vel9studios.levani.popularmovies.R;
import com.vel9studios.levani.popularmovies.beans.Movie;
import com.vel9studios.levani.popularmovies.constants.DetailFragmentConstants;
import com.vel9studios.levani.popularmovies.data.FetchReviewsTask;
import com.vel9studios.levani.popularmovies.views.ReviewsAdapter;


public class ReviewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = ReviewsFragment.class.getSimpleName();
    public static final String REVIEWS_URI = "URI";

    TextView mReviews;
    ReviewsAdapter mReviewsAdapter;
    ListView mReviewsListView;

    //Uris
    Uri mReviewsUri;

    private static final int REVIEWS_LOADER = 2;

    private Movie movie;
    public ReviewsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {

            mReviewsUri = arguments.getParcelable(REVIEWS_URI);

            // get movieId we want to view reviews for
            String movieId = mReviewsUri.getLastPathSegment();

            // get the reviews
            //TODO: this is probably called on screen rotation
            FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(getActivity());
            fetchReviewsTask.execute(movieId);
        }

        //set text elements
        View rootView = inflater.inflate(R.layout.fragment_reviews, container, false);

        mReviewsListView = (ListView) rootView.findViewById(R.id.listview_reviews);
        mReviewsAdapter = new ReviewsAdapter(getActivity(), null, 0);
        mReviewsListView.setAdapter(mReviewsAdapter);

        mReviews = (TextView) rootView.findViewById(R.id.detail_reviews);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == REVIEWS_LOADER && mReviewsUri != null){

            return new CursorLoader(
                    getActivity(),
                    mReviewsUri,
                    DetailFragmentConstants.REVIEWS_DETAIL_COLUMNS,
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

            if (loader.getId() == REVIEWS_LOADER){
                mReviewsAdapter.swapCursor(cursor);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == REVIEWS_LOADER)
            mReviewsAdapter.swapCursor(null);
    }
}
