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
package com.vel9studios.levani.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.vel9studios.levani.popularmovies.constants.AppConstants;
import com.vel9studios.levani.popularmovies.data.MoviesContract.MoviesEntry;
import com.vel9studios.levani.popularmovies.data.MoviesContract.VideosEntry;
import com.vel9studios.levani.popularmovies.data.MoviesContract.ReviewsEntry;

public class MoviesProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    private final String LOG_TAG = MoviesProvider.class.getSimpleName();

    //what are the URIs we need for the content provider
    static final int MOVIE = 100;
    static final int MOVIE_DETAILS = 101;
    static final int VIDEOS = 102;
    static final int MOVIE_ITEM_VIDEOS = 103;
    static final int FAVORITE = 104;
    static final int FAVORITES = 105;
    static final int SET_REVIEWS = 106;
    static final int GET_REVIEWS = 107;

    private static final String sMovieIdSelection =
            MoviesEntry.TABLE_NAME+
                    "." + MoviesEntry.COLUMN_MOVIE_ID + " = ? ";

    private static final String sVideosVideoIdSelection =
            VideosEntry.TABLE_NAME+
                    "." + VideosEntry.COLUMN_VIDEO_ID + " = ? ";

    private static final String sVideosMovieIdSelection =
            VideosEntry.TABLE_NAME+
                    "." + MoviesEntry.COLUMN_MOVIE_ID + " = ? ";

    private static final String sReviewsMovieIdSelection =
            ReviewsEntry.TABLE_NAME+
                    "." + MoviesEntry.COLUMN_MOVIE_ID + " = ? ";

    private static final String sFavoritesSelection =
            MoviesEntry.TABLE_NAME+"." + MoviesEntry.COLUMN_FAVORITE_IND + " = ? ";

    private static final String sReviewsReviewIdSelection =
            ReviewsEntry.TABLE_NAME+
                    "." + ReviewsEntry.COLUMN_REVIEW_ID + " = ? ";


    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_DATE,
        and LOCATION integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIE);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/#", MOVIE_DETAILS);

        // videos
        matcher.addURI(authority, MoviesContract.PATH_VIDEOS, VIDEOS);
        matcher.addURI(authority, MoviesContract.PATH_VIDEOS + "/#", MOVIE_ITEM_VIDEOS);

        // favorites
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/" + MoviesContract.PATH_FAVORITE + "/#", FAVORITE);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/" + MoviesContract.PATH_FAVORITE, FAVORITES);

        // reviews
        matcher.addURI(authority, MoviesContract.PATH_REVIEWS, SET_REVIEWS);
        matcher.addURI(authority, MoviesContract.PATH_REVIEWS + "/#", GET_REVIEWS);

        return matcher;
    }

    public static String getMovieIdFromUri(Uri uri) {
        return uri.getPathSegments().get(1);
    }

    public static String getMovieIdFromFavoriteUri(Uri uri) {
        return uri.getPathSegments().get(2);
    }

    public static String getFavoriteActionFromUri(Uri uri) {
        return uri.getQueryParameter("favoriteInd");
    }

    /*
        Students: We've coded this for you.  We just create a new MoviesDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);
        switch (match) {

            // Student: Uncomment and fill out these two cases
            case MOVIE:
                return MoviesContract.MoviesEntry.CONTENT_TYPE;
            case MOVIE_DETAILS:
                return MoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
            case MOVIE_ITEM_VIDEOS:
                return MoviesContract.VideosEntry.CONTENT_TYPE;
            case FAVORITE:
                return MoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
            case FAVORITES:
                return MoviesContract.VideosEntry.CONTENT_TYPE;
            case SET_REVIEWS:
                return MoviesContract.ReviewsEntry.CONTENT_TYPE;
            case GET_REVIEWS:
                return MoviesContract.ReviewsEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            // "movie"
            case MOVIE:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder,
                        AppConstants.GRID_VIEW_ITEM_LIMIT
                );
                break;
            }
            case MOVIE_DETAILS:
            {
                String movieId = getMovieIdFromUri(uri);
                selectionArgs = new String[]{movieId};

                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        sMovieIdSelection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder,
                        AppConstants.DETAIL_VIEW_ITEM_LIMIT
                );
                break;
            }
            case MOVIE_ITEM_VIDEOS:
            {
                String movieId = uri.getLastPathSegment();
                selectionArgs = new String[]{movieId};

                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.VideosEntry.TABLE_NAME,
                        projection,
                        sVideosMovieIdSelection,
                        selectionArgs,
                        null,
                        null,
                        null
                );

                Log.d("GETTING TRAILERS", retCursor.getCount() + "" + movieId);
                break;
            }
            case FAVORITES:
            {
                selectionArgs = new String[]{"Y"};

                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        sFavoritesSelection,
                        selectionArgs,
                        null,
                        null,
                        null
                );

                Log.d(LOG_TAG, "NUMBER OF FAVORITES " + retCursor.getCount());
                break;
            }
            case GET_REVIEWS:
            {
                String movieId = uri.getLastPathSegment();
                selectionArgs = new String[]{movieId};

                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        sReviewsMovieIdSelection,
                        selectionArgs,
                        null,
                        null,
                        null
                );

                Log.d("GETTING REVIEWS", retCursor.getCount() + "" + movieId);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {

                long _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesContract.MoviesEntry.buildMoviesUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MoviesContract.MoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return -1;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                rowsUpdated = db.update(MoviesContract.MoviesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FAVORITE:

                String movieId = getMovieIdFromFavoriteUri(uri);
                String favoriteFlag = getFavoriteActionFromUri(uri);

                selectionArgs = new String[]{movieId};
                ContentValues contentValues = new ContentValues();
                contentValues.put(MoviesEntry.COLUMN_FAVORITE_IND, favoriteFlag);

                rowsUpdated = db.update(MoviesContract.MoviesEntry.TABLE_NAME,
                        contentValues,
                        sMovieIdSelection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:

                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long updateId = db.update(MoviesEntry.TABLE_NAME, value, sMovieIdSelection,
                                new String[]{value.getAsInteger(MoviesEntry.COLUMN_MOVIE_ID).toString()});

                        if (updateId == 0){
                            long _id = db.insert(MoviesEntry.TABLE_NAME, null, value);
                        }

                        returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case VIDEOS:

                db.beginTransaction();
                int returnCountVideos = 0;
                try {
                    for (ContentValues value : values) {

                        long updateId = db.update(VideosEntry.TABLE_NAME, value, sVideosVideoIdSelection,
                                new String[]{value.getAsString(VideosEntry.COLUMN_VIDEO_ID)});

                        if (updateId == 0){
                            long _id = db.insert(VideosEntry.TABLE_NAME, null, value);
                            returnCountVideos++;
                        }

                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCountVideos;

            case SET_REVIEWS:
            {
                db.beginTransaction();
                int returnCountReviews = 0;
                try {
                    for (ContentValues value : values) {

                        long updateId = db.update(ReviewsEntry.TABLE_NAME, value, sReviewsReviewIdSelection,
                                new String[]{value.getAsString(ReviewsEntry.COLUMN_REVIEW_ID)});

                        if (updateId == 0){
                            long _id = db.insert(ReviewsEntry.TABLE_NAME, null, value);
                            returnCountReviews++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCountReviews;
            }

            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}