package com.vel9studios.levani.popularmovies.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.vel9studios.levani.popularmovies.R;
import com.vel9studios.levani.popularmovies.fragments.PopularMoviesFragment;
import com.vel9studios.levani.popularmovies.util.Utility;

//Code from "Developing Android Apps: Fundamentals"/default code
public class MainActivity extends AppCompatActivity implements PopularMoviesFragment.Callback {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private String mActiveSortType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActiveSortType = Utility.getPreferredSortOrder(this);
        Log.d(LOG_TAG, mActiveSortType);
        setContentView(R.layout.activity_main);
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

                Log.d(LOG_TAG, "calling on sort type changed");
                popularMoviesFragment.onSortOrderChanged(sortType);
            }
            mActiveSortType = sortType;
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri contentUri) {

        Intent intent = new Intent(this, DetailActivity.class).setData(contentUri);
        Log.d(LOG_TAG, contentUri.toString());
        startActivity(intent);
    }
}
