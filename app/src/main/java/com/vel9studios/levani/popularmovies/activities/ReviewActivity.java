package com.vel9studios.levani.popularmovies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.vel9studios.levani.popularmovies.R;
import com.vel9studios.levani.popularmovies.fragments.ReviewsFragment;


//Code from "Developing Android Apps: Fundamentals"/default code
public class ReviewActivity extends AppCompatActivity {

    private final String LOG_TAG = ReviewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reviews);
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(ReviewsFragment.REVIEW_URI, getIntent().getData());

            ReviewsFragment fragment = new ReviewsFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
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
