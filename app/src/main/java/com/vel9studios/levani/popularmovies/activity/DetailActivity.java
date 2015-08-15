package com.vel9studios.levani.popularmovies.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.vel9studios.levani.popularmovies.R;
import com.vel9studios.levani.popularmovies.data.MoviesContract;
import com.vel9studios.levani.popularmovies.fragment.DetailFragment;
import com.vel9studios.levani.popularmovies.util.Utility;

import java.util.ArrayList;


//Code from "Developing Android Apps: Fundamentals"/default code
public class DetailActivity extends AppCompatActivity {

    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    Uri mUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            mUri = getIntent().getData();
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            if (mUri != null){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_detail_container, fragment)
                        .commit();
            }
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

        //TODO: make sure it works in two pane mode...
        DetailFragment detailFragment = (DetailFragment)getSupportFragmentManager().findFragmentById(R.id.movie_detail_container);
        if ( null != detailFragment) {
            detailFragment.onFavoriteToggle();
        }
    }

    // launch review activity from Detail Activity
    public void launchReviews(View view)
    {
        String movieId = (String) view.getTag();
        Uri reviewsUri = MoviesContract.ReviewsEntry.buildReviewsUri(movieId);
        Intent intent = new Intent(this, ReviewActivity.class).setData(reviewsUri);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
