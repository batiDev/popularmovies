/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vel9studios.levani.popularmovies.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.vel9studios.levani.popularmovies.R;
import com.vel9studios.levani.popularmovies.constants.AppConstants;
import com.vel9studios.levani.popularmovies.data.MoviesContract;

import org.json.JSONException;
import org.json.JSONObject;

public class Utility {

    public static String getPreferredSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));
    }

    public static String getSortOrderQuery(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortOrder = prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));


        if (sortOrder.equals(AppConstants.SORT_TYPE_POPULARITY))
            return MoviesContract.MoviesEntry.COLUMN_POPULARITY + " DESC";
        else if (sortOrder.equals(AppConstants.SORT_TYPE_VOTE))
            return MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " DESC";

        return null;
    }

    /**
     * movie objects may contain fields which contain "something" but are actually null:
     * e.g. response will read: "overview":null
     * The presence of null means that get(key) won't actually return null,
     * instead it returns a JSONObject with the value of null. The toString() then
     * returns "null" string. This allows us to:
     *
     * a. actually check for null
     * b. handle gracefully
     *
     * @param movieObj current JSONObject containing movie data
     * @param key key with which to retrieve a portion of the movie JSONObject
     * @return String value
     * @throws JSONException
     */

    public static String parseMovieContents(JSONObject movieObj, String key) throws JSONException {

        String content;
        Object subMovieObj = movieObj.get(key);
        if (subMovieObj != null && !subMovieObj.toString().equalsIgnoreCase(AppConstants.STRING_MOVIEDB_NULL))
            content = subMovieObj.toString();
        else
            content = AppConstants.STRING_NO_DATA;

        return content;
    }

}