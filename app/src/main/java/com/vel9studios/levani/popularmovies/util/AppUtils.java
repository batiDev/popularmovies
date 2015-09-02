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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.vel9studios.levani.popularmovies.R;
import com.vel9studios.levani.popularmovies.constants.AppConstants;
import com.vel9studios.levani.popularmovies.data.MoviesContract;

import org.json.JSONException;
import org.json.JSONObject;

public class AppUtils {

    public static String getPreferredSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));
    }

    // query our local db with the same criteria/sort order the user is querying the API
    public static String getSortOrderQuery(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortOrder = prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));

        if (sortOrder.equals(AppConstants.QUERY_SORT_TYPE_POPULARITY))
            return MoviesContract.MoviesEntry.COLUMN_POPULARITY + " DESC";
        else if (sortOrder.equals(AppConstants.QUERY_SORT_TYPE_VOTE))
            return MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " DESC";

        return null;
    }

    // returns the toggle state of the favorite flag, if it's current true, return false etc
    public static String getFavoriteFlag(String favoriteInd){
        return (favoriteInd != null && favoriteInd.equals(AppConstants.Y_FLAG))? AppConstants.N_FLAG : AppConstants.Y_FLAG;
    }

    /**
     * movie objects may contain fields which contain "something" but are actually null:
     * e.g. response will read: "overview":null
     *
     * The presence of null means that get(key) won't actually return a null value.
     * Instead the get(key) method returns a JSONObject with the value of null. The toString() then
     * returns "null" string.
     *
     * This rather confusing API characteristic (if something is null.. maybe don't include it in the response?)
     * still allows us to:
     *
     * a. check for null
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
        if (subMovieObj != null && !subMovieObj.toString().equalsIgnoreCase(AppConstants.JSON_PARSE_NULL))
            content = subMovieObj.toString();
        else
            content = AppConstants.JSON_PARSE_STRING_NO_DATA;

        return content;
    }

    // displays favorited/unfavorited toast based on record's current state
    public static void displayFavoritesMessage(String favoriteFlag, String movieTitle, Context context){

        String favoriteMessage = "";
        if (favoriteFlag.equals(AppConstants.Y_FLAG))
            favoriteMessage = movieTitle + AppConstants.MESSAGE_FAVORITE_ADDED;
        else if (favoriteFlag.equals(AppConstants.N_FLAG))
            favoriteMessage = movieTitle + AppConstants.MESSAGE_FAVORITE_REMOVED;

        Toast appStart = Toast.makeText(context, favoriteMessage, Toast.LENGTH_SHORT);
        appStart.show();
    }

    // check network availability
    public static Boolean isNetworkAvailable(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting())
            return true;

        return false;
    }

}