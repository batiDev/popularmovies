package com.vel9studios.levani.popularmovies.activity;

import android.app.Activity;
import android.os.Bundle;

import com.vel9studios.levani.popularmovies.fragment.SettingsFragment;

/**
 * http://developer.android.com/guide/topics/ui/settings.html#Fragment
 *
 * Updated code from "Developing Android Apps: Fundamentals" to the "non-deprecated" format
 *
 */
public class SettingsActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}