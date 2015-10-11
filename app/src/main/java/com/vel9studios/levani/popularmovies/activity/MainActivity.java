package com.vel9studios.levani.popularmovies.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.vel9studios.levani.popularmovies.util.AppUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PopularMoviesFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private String mActiveSortType;
    private boolean mTwoPane;
    private boolean mShowFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActiveSortType = AppUtils.getPreferredSortOrder(this);
        setContentView(R.layout.activity_main);

        // two-pane code reference: Developing Android Apps: Fundamentals course
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
                        .replace(R.id.movie_detail_container, new DetailFragment(), AppConstants.DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

        // start sync
        MoviesSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //maintain favorite setting state/text
        MenuItem item = menu.findItem(R.id.action_favorites);
        setFavoritesMenuText(item);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        String sortType = AppUtils.getPreferredSortOrder(this);

        // if user has selected a new sort type, update the app accordingly
        if (!mActiveSortType.equals(sortType)){

            PopularMoviesFragment popularMoviesFragment = (PopularMoviesFragment)getSupportFragmentManager().findFragmentById(R.id.popular_movies_fragment);
            if (popularMoviesFragment != null)
                popularMoviesFragment.onSortOrderChanged();

            DetailFragment detailFragment = (DetailFragment)getSupportFragmentManager().findFragmentByTag(AppConstants.DETAIL_FRAGMENT_TAG);
            if (detailFragment != null)
                detailFragment.onSortOrderChanged();

            mActiveSortType = sortType;
        }

        // set current favorites state
        mShowFavorites = AppUtils.getPreferredFavoritesState(this);
    }

    // launch Reviews activity
    public void launchReviewsActivity(View view){

        if (mTwoPane){
            String movieId = (String) view.getTag();
            Uri reviewsUri = MoviesContract.ReviewsEntry.buildReviewsUri(movieId);

            Bundle args = new Bundle();
            args.putParcelable(AppConstants.REVIEWS_URI_KEY, reviewsUri);

            ReviewsFragment reviewsFragment;
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentByTag(AppConstants.REVIEW_FRAGMENT_TAG);
            // Reference: http://stackoverflow.com/questions/9033019/removing-a-fragment-from-the-back-stack
            if (fragment != null){
                reviewsFragment = (ReviewsFragment) fragment;
                fm.beginTransaction().remove(reviewsFragment).commit();
                fm.popBackStack();
            } else {
                // if ReviewsFragment does not already exist, create it
                reviewsFragment = new ReviewsFragment();
                reviewsFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, reviewsFragment, AppConstants.REVIEW_FRAGMENT_TAG)
                        // add to backstack so user can use the back button
                        .addToBackStack(AppConstants.REVIEW_FRAGMENT_TAG)
                        .commit();
            }
        }
    }

    public void setFavorite(View view) {
        // toggle record favorite state in db
        ArrayList<String> favoriteValues = (ArrayList<String>) view.getTag();
        String movieId = favoriteValues.get(AppConstants.FAVORITE_VALUES_MOVIE_ID_POSITION);
        String favoriteInd = favoriteValues.get(AppConstants.FAVORITE_VALUES_FAVORITE_IND_POSITION);
        String movieTitle = favoriteValues.get(AppConstants.FAVORITE_VALUES_MOVIE_TITLE_POSITION);

        String favoriteFlag = AppUtils.getFavoriteFlag(favoriteInd);
        Uri favoriteUri = MoviesContract.MoviesEntry.buildFavoriteUri(movieId, favoriteFlag);
        int numRecordsUpdated = this.getContentResolver().update(favoriteUri, null, null, null);

        if (numRecordsUpdated == 1) {
            AppUtils.displayFavoritesMessage(favoriteFlag, movieTitle, this);

            DetailFragment detailFragment = (DetailFragment)getSupportFragmentManager().findFragmentById(R.id.movie_detail_container);
            if ( null != detailFragment) {
                detailFragment.onFavoriteToggle();
            }
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
            if (popularMoviesFragment != null) {
                // toggle state
                mShowFavorites = !mShowFavorites;
                // notify fragment of change with new value
                popularMoviesFragment.onFavoritesChanged(mShowFavorites);
                /*
                    We set the favorites to shared preferences instead of as an instance variable.
                    I'd like to keep the previously set favorite state even when user closes or moves away from the application.
                    Expectation is, I say "show favorites,"  then I leave application, do something else, and when I come back,
                    I expect to still see the favorites.
                 */
                AppUtils.setPreferredFavoritesState(this, mShowFavorites);
                // update menu option text
                setFavoritesMenuText(item);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void setFavoritesMenuText(MenuItem item){

        Resources resources = getResources();

        String favoritesTitle;
        if (mShowFavorites)
            favoritesTitle = resources.getString(R.string.action_favorites_hide);
        else
            favoritesTitle = resources.getString(R.string.action_favorites_show);

        item.setTitle(favoritesTitle);
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

            // Reference: http://stackoverflow.com/questions/9033019/removing-a-fragment-from-the-back-stack
            FragmentManager fm = getSupportFragmentManager();
            if (fm.findFragmentByTag(AppConstants.REVIEW_FRAGMENT_TAG) != null){
                //if fragment already exists, remove it
                ReviewsFragment reviewsFragment = (ReviewsFragment) fm.findFragmentByTag(AppConstants.REVIEW_FRAGMENT_TAG);
                fm.beginTransaction().remove(reviewsFragment).commit();
                fm.popBackStack();
            }

            fm.beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, AppConstants.DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {

            Intent intent = new Intent(this, DetailActivity.class).setData(contentUri);
            startActivity(intent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

}
