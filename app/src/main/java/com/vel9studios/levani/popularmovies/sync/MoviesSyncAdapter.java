package com.vel9studios.levani.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.vel9studios.levani.popularmovies.R;
import com.vel9studios.levani.popularmovies.constants.AppConstants;
import com.vel9studios.levani.popularmovies.data.MoviesContract;
import com.vel9studios.levani.popularmovies.data.MoviesDAO;
import com.vel9studios.levani.popularmovies.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MoviesSyncAdapter extends AbstractThreadedSyncAdapter {
    private final String LOG_TAG = MoviesSyncAdapter.class.getSimpleName();

    // Interval at which to sync with the weather, in milliseconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    private static final int SYNC_INTERVAL = 60 * 180;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public MoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        String sortType = AppUtils.getPreferredSortOrder(getContext());
        Log.d(LOG_TAG, "performing sync with " + sortType);

        try{

            MoviesDAO moviesDAO = new MoviesDAO();
            //fetch data from server
            String movieListJsonStr = moviesDAO.getMovieData(sortType);

            //get serialized data
            updateDatabaseWithMovies(movieListJsonStr);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {


        // cancel any pending syncs
        // http://stackoverflow.com/questions/13132865/cant-perform-sync-onperformsync-is-not-called
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (ContentResolver.isSyncPending(account, authority) || ContentResolver.isSyncActive(account, authority)) {
            ContentResolver.cancelSync(account, authority);
        }

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account,authority, bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    private static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private Void updateDatabaseWithMovies(String moviesJsonStr)
            throws JSONException {

        // need to handle this
        if (moviesJsonStr == null)
            return null;

        JSONObject moviesJSON = new JSONObject(moviesJsonStr);
        JSONArray moviesJSONArray = moviesJSON.getJSONArray(AppConstants.JSON_PARSE_RESULTS);

        List<ContentValues> moviesContentValuesList = new ArrayList<>();

        for(int i = 0; i < moviesJSONArray.length(); i++) {

            JSONObject movieObj = moviesJSONArray.getJSONObject(i);

            String title = AppUtils.parseMovieContents(movieObj, AppConstants.JSON_PARSE_TITLE);
            String partialPath = AppUtils.parseMovieContents(movieObj, AppConstants.JSON_PARSE_POSTER_PATH);
            String overview = AppUtils.parseMovieContents(movieObj, AppConstants.JSON_PARSE_OVERVIEW);

            String releaseDate = AppUtils.parseMovieContents(movieObj, AppConstants.JSON_RELEASE_DATE);

            //Date string is (usually) in "YYYY-MM-DD" format, get just the year
            if (!releaseDate.equals(AppConstants.JSON_PARSE_STRING_NO_DATA) && releaseDate.length() > 4){
                releaseDate = releaseDate.substring(0,4);
                //year data may also just be "empty" check for it here
            } else if (releaseDate.length() == 0) {
                releaseDate = AppConstants.JSON_PARSE_STRING_NO_DATA;
            }

            Integer movieId = movieObj.getInt(AppConstants.JSON_PARSE_MOVIE_ID);
            Double voteAverage = movieObj.getDouble(AppConstants.JSON_VOTE_AVERAGE);
            Double popularity = movieObj.getDouble(AppConstants.JSON_PARSE_POPULARITY);

            ContentValues movieValues = new ContentValues();

            movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movieId);
            movieValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE, title);
            movieValues.put(MoviesContract.MoviesEntry.COLUMN_IMAGE_PATH, partialPath);
            movieValues.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, overview);
            movieValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, voteAverage);
            movieValues.put(MoviesContract.MoviesEntry.COLUMN_POPULARITY, popularity);

            moviesContentValuesList.add(movieValues);
        }

        int inserted = 0;
        // add to database
        Integer moviesContentValuesListSize = moviesContentValuesList.size();
        if (moviesContentValuesListSize > 0) {
            ContentValues[] cvArray = new ContentValues[moviesContentValuesListSize];
            moviesContentValuesList.toArray(cvArray);
            getContext().getContentResolver().bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI, cvArray);
        }

        return null;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    private static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

}