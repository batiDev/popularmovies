package com.vel9studios.levani.popularmovies.control;

import android.app.Application;

import com.vel9studios.levani.popularmovies.data.FetchMovieTask;
import com.vel9studios.levani.popularmovies.util.Utility;

/**
 * Created by levani on 8/13/15.
 */
public class AppStartUp extends Application {

    public AppStartUp() {
        // this method fires only once per application start.
        // getApplicationContext returns null here
    }

    @Override
    public void onCreate() {
        super.onCreate();

        String sortOrder = Utility.getPreferredSortOrder(this);
        FetchMovieTask movieTask = new FetchMovieTask(this);
        movieTask.execute(sortOrder);
    }
}
