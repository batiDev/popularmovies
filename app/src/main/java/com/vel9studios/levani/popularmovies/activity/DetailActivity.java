package com.vel9studios.levani.popularmovies.activity;

import android.content.Intent;
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
import com.vel9studios.levani.popularmovies.util.AppUtils;

import java.util.ArrayList;


public class DetailActivity extends AppCompatActivity {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            //get movie uri from incoming intent
            Uri movieDetailUri = getIntent().getData();
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            if (movieDetailUri != null){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_detail_container, fragment)
                        .commit();
            }
        }
    }

    public void setFavorite(View view)
    {
        //retrieve values needed for setting favorites
        ArrayList<String> favoriteValues = (ArrayList<String>) view.getTag();
        String movieId = favoriteValues.get(AppConstants.FAVORITE_VALUES_MOVIE_ID_POSITION);
        String favoriteInd = favoriteValues.get(AppConstants.FAVORITE_VALUES_FAVORITE_IND_POSITION);
        String movieTitle = favoriteValues.get(AppConstants.FAVORITE_VALUES_MOVIE_TITLE_POSITION);

        String favoriteFlag = AppUtils.getFavoriteFlag(favoriteInd);

        //get Uri for updating record with appropriate favorite flag
        Uri favoriteUri = MoviesContract.MoviesEntry.buildFavoriteUri(movieId, favoriteFlag);

        // DB call should be minor enough where creating an Async task could be unncessary -- do some research
        int updated = this.getContentResolver().update(favoriteUri, null, null, null);

        // if record was successfully updated, show message
        if (updated == 1){
            AppUtils.displayFavoritesMessage(favoriteFlag, movieTitle, this);
        }

        DetailFragment detailFragment = (DetailFragment)getSupportFragmentManager().findFragmentById(R.id.movie_detail_container);
        if (detailFragment != null) {
            // notify detail fragment
            detailFragment.onFavoriteToggle();
        }
    }

    // launch review activity from Detail Activity
    public void launchReviewsActivity(View view){

        String movieId = (String) view.getTag();
        // use movieId for fetching reviews
        Uri reviewsUri = MoviesContract.ReviewsEntry.buildReviewsUri(movieId);
        Intent intent = new Intent(this, ReviewsActivity.class).setData(reviewsUri);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
