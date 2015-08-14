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

import com.vel9studios.levani.popularmovies.constants.AppConstants;
import com.vel9studios.levani.popularmovies.data.MoviesContract.MoviesEntry;
import com.vel9studios.levani.popularmovies.data.MoviesContract.ReviewsEntry;
import com.vel9studios.levani.popularmovies.data.MoviesContract.VideosEntry;

public class MoviesProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    private final String LOG_TAG = MoviesProvider.class.getSimpleName();

    // see URI matcher method for matching details
    static final int MOVIES = 100;
    static final int MOVIE_ITEM_DETAILS = 101;
    static final int VIDEOS = 102;
    static final int MOVIE_ITEM_VIDEOS = 103;
    static final int MOVIE_ITEM_FAVORITE = 104;
    static final int FAVORITES = 105;
    static final int REVIEWS = 106;
    static final int MOVIE_ITEM_REVIEWS = 107;

    static final int FIRST_MOVIE = 108;

    // WHERE clauses
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


    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        // movies
        matcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIES);

        //
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/#", MOVIE_ITEM_DETAILS);

        // videos
        matcher.addURI(authority, MoviesContract.PATH_VIDEOS, VIDEOS);
        matcher.addURI(authority, MoviesContract.PATH_VIDEOS + "/#", MOVIE_ITEM_VIDEOS);

        // view favorites
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/" + MoviesContract.PATH_FAVORITE, FAVORITES);

        // set movie as favorite or remove movie from favorites
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/" + MoviesContract.PATH_FAVORITE + "/#", MOVIE_ITEM_FAVORITE);

        // reviews
        matcher.addURI(authority, MoviesContract.PATH_REVIEWS, REVIEWS);

        // view reviews for movie
        matcher.addURI(authority, MoviesContract.PATH_REVIEWS + "/#", MOVIE_ITEM_REVIEWS);

        // first movie
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/" + MoviesContract.PATH_FIRST, FIRST_MOVIE);

        return matcher;
    }

    /** URI PARSE HELPER METHODS **/
    public static String getMovieIdFromUri(Uri uri) {
        return uri.getPathSegments().get(1);
    }

    public static String getMovieIdFromFavoriteUri(Uri uri) {
        return uri.getPathSegments().get(2);
    }

    public static String getFavoriteActionFromUri(Uri uri) {
        return uri.getQueryParameter(AppConstants.FAVORITE_IND);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);
        switch (match) {

            case MOVIES:
                return MoviesContract.MoviesEntry.CONTENT_TYPE;
            case MOVIE_ITEM_DETAILS:
                return MoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
            case MOVIE_ITEM_VIDEOS:
                return MoviesContract.VideosEntry.CONTENT_TYPE;
            case MOVIE_ITEM_FAVORITE:
                return MoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
            case FAVORITES:
                return MoviesContract.VideosEntry.CONTENT_TYPE;
            case REVIEWS:
                return MoviesContract.ReviewsEntry.CONTENT_TYPE;
            case MOVIE_ITEM_REVIEWS:
                return MoviesContract.ReviewsEntry.CONTENT_TYPE;
            case FIRST_MOVIE:
                return MoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            // get movies, with appropriate sort order, used in main view
            case MOVIES:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder,
                        // set limit of results
                        AppConstants.GRID_VIEW_ITEM_LIMIT
                );
                break;
            }
            // get movie details by movie ID
            case MOVIE_ITEM_DETAILS:
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
            // get videos for given movie by using its movie ID to selected videos from the videos table
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

                break;
            }
            //returns all movies marked as favorite
            case FAVORITES:
            {
                selectionArgs = new String[]{AppConstants.Y_FLAG};

                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        sFavoritesSelection,
                        selectionArgs,
                        null,
                        null,
                        null
                );

                break;
            }
            // returns reviews for given movie Id
            case MOVIE_ITEM_REVIEWS:
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

                break;
            }
            case FIRST_MOVIE:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder,
                        // set limit of results
                        AppConstants.DETAIL_VIEW_ITEM_LIMIT
                );
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
            /** not currently in use **/
            case MOVIES: {

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
            case MOVIES:
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
            /** not in use **/
            case MOVIES:
                rowsUpdated = db.update(MoviesContract.MoviesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            /** set movie as favorite **/
            case MOVIE_ITEM_FAVORITE:

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

    /** this is a key function for the provider, modified to work more as an "UPSERT"
     * if movie exists, update it with new data, if not, insert it**/
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case MOVIES:

                db.beginTransaction();
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
                try {
                    for (ContentValues value : values) {

                        long updateId = db.update(VideosEntry.TABLE_NAME, value, sVideosVideoIdSelection,
                                new String[]{value.getAsString(VideosEntry.COLUMN_VIDEO_ID)});

                        if (updateId == 0){
                            long _id = db.insert(VideosEntry.TABLE_NAME, null, value);
                        }

                        returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case REVIEWS:
            {
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {

                        long updateId = db.update(ReviewsEntry.TABLE_NAME, value, sReviewsReviewIdSelection,
                                new String[]{value.getAsString(ReviewsEntry.COLUMN_REVIEW_ID)});

                        if (updateId == 0){
                            long _id = db.insert(ReviewsEntry.TABLE_NAME, null, value);
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            default:
                return super.bulkInsert(uri, values);
        }
    }

    /**** CONTENT PROVIDER METHODS ****/

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