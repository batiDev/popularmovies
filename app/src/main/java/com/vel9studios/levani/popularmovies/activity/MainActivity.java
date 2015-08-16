package com.vel9studios.levani.popularmovies.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.vel9studios.levani.popularmovies.R;
import com.vel9studios.levani.popularmovies.constants.AppConstants;
import com.vel9studios.levani.popularmovies.data.MoviesContract;
import com.vel9studios.levani.popularmovies.fragment.DetailFragment;
import com.vel9studios.levani.popularmovies.fragment.PopularMoviesFragment;
import com.vel9studios.levani.popularmovies.fragment.ReviewsFragment;
import com.vel9studios.levani.popularmovies.sync.MoviesSyncAdapter;
import com.vel9studios.levani.popularmovies.util.Utility;

import java.util.ArrayList;

//Code from "Developing Android Apps: Fundamentals"/default code
public class MainActivity extends AppCompatActivity implements PopularMoviesFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private String mActiveSortType;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActiveSortType = Utility.getPreferredSortOrder(this);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailFragment(), AppConstants.DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        MoviesSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {

        super.onResume();
        String sortType = Utility.getPreferredSortOrder(this);

        if (!mActiveSortType.equals(sortType)){

            PopularMoviesFragment popularMoviesFragment = (PopularMoviesFragment)getSupportFragmentManager().findFragmentById(R.id.popular_movies_fragment);
            if ( null != popularMoviesFragment ) {

                popularMoviesFragment.onSortOrderChanged(sortType);
            }

            DetailFragment detailFragment = (DetailFragment)getSupportFragmentManager().findFragmentByTag(AppConstants.DETAILFRAGMENT_TAG);
            if ( null != detailFragment) {

                detailFragment.onSortOrderChanged(sortType);
            }

            mActiveSortType = sortType;
        }
    }

    // launch Reviews activity
    public void launchReviews(View view)
    {
        if (mTwoPane)
        {
            String movieId = (String) view.getTag();
            Uri reviewsUri = MoviesContract.ReviewsEntry.buildReviewsUri(movieId);

            Bundle args = new Bundle();
            args.putParcelable(ReviewsFragment.REVIEWS_URI, reviewsUri);

            ReviewsFragment fragment = new ReviewsFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, AppConstants.REVIEWFRAGMENT_TAG)
                    // add to backstrack so user can use the back button
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void setFavorite(View view)
    {
        ArrayList<String> favoriteValues = (ArrayList<String>) view.getTag();
        String movieId = favoriteValues.get(0);
        String favoriteInd = favoriteValues.get(1);
        String movieTitle = favoriteValues.get(2);

        String favoriteFlag = Utility.getFavoriteFlag(favoriteInd);
        Uri favoriteUri = MoviesContract.MoviesEntry.buildFavoriteUri(movieId, favoriteFlag);
        int updated = this.getContentResolver().update(favoriteUri, null, null, null);

        if (updated == 1){
            Utility.displayFavoritesMessage(favoriteFlag, movieTitle, this);
        }

        DetailFragment detailFragment = (DetailFragment)getSupportFragmentManager().findFragmentById(R.id.movie_detail_container);
        if ( null != detailFragment) {
            detailFragment.onFavoriteToggle();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            //Settings Intent
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_favorites) {

            PopularMoviesFragment popularMoviesFragment = (PopularMoviesFragment)getSupportFragmentManager().findFragmentById(R.id.popular_movies_fragment);
            if ( null != popularMoviesFragment ) {
                Boolean showFavorites = popularMoviesFragment.onFavoritesChanged();
                Resources resources = getResources();
                if (showFavorites) item.setTitle(resources.getString(R.string.action_favorites_hide));
                    else if (!showFavorites) item.setTitle(resources.getString(R.string.action_favorites_show));
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri contentUri) {

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, AppConstants.DETAILFRAGMENT_TAG)
                    .commit();
        } else {

            Intent intent = new Intent(this, DetailActivity.class).setData(contentUri);
            startActivity(intent);
        }
    }


}
