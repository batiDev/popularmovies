package com.vel9studios.levani.popularmovies.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.vel9studios.levani.popularmovies.R;
import com.vel9studios.levani.popularmovies.data.MoviesContract;
import com.vel9studios.levani.popularmovies.fragments.DetailFragment;

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
            Log.d(LOG_TAG, "savedInstance NULL");
            Bundle arguments = new Bundle();
            mUri = getIntent().getData();
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        } else {
            Log.d(LOG_TAG, "savedInstance NOT null");
            mUri = (Uri) savedInstanceState.get(DetailFragment.DETAIL_URI);
        }
    }

    public void setFavorite(View view)
    {
        ArrayList<String> favoriteValues = (ArrayList<String>) view.getTag();
        String movieId = favoriteValues.get(0);
        String favoriteInd = favoriteValues.get(1);

        String newFavoriteInd = "";
        if (favoriteInd != null && favoriteInd.equals("Y"))
            newFavoriteInd = "N";
        else
            newFavoriteInd = "Y";

        Uri favoriteUri = MoviesContract.MoviesEntry.buildFavoriteUri(movieId, newFavoriteInd);
        int updated = this.getContentResolver().update(favoriteUri, null, null, null);
        Log.d("UPDATED", updated + " " + movieId + " " + newFavoriteInd);
    }

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

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);


        Log.d(LOG_TAG, "RESTORING?");
        // Restore state members from saved instance
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(LOG_TAG, "SAVING STATE");
        outState.putParcelable(DetailFragment.DETAIL_URI, mUri);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(LOG_TAG, "DESTROYING");
    }

}
