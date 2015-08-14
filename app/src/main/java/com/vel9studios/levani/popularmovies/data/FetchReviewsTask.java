package com.vel9studios.levani.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.vel9studios.levani.popularmovies.constants.AppConstants;
import com.vel9studios.levani.popularmovies.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

/**
 * Created by levani on 8/6/15.
 */
public class FetchReviewsTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();
    private final Context mContext;
    private String mMovieId;

    public FetchReviewsTask(Context context) {
        mContext = context;
    }

    protected Void doInBackground(String... params){

        try{

            mMovieId = params[0];
            MoviesDAO moviesDAO = new MoviesDAO();
            //fetch data from server
            String videosJSONStr = moviesDAO.getReviews(mMovieId);

            //get serialized data
            getVideosDataFromJson(videosJSONStr);

        } catch (JSONException e) {
            //it makes sense to return null here, since onPostExecute checks for null,
            //but need to figure out what's possible with Exceptions within framework overall
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return null;
    }

    private Void getVideosDataFromJson(String videosJsonStr)
            throws JSONException {

        JSONObject moviesJson = new JSONObject(videosJsonStr);
        JSONArray videoList = moviesJson.getJSONArray(AppConstants.RESULTS);

        int videoListLength = videoList.length();

        Vector<ContentValues> cVVector = new Vector<ContentValues>(videoListLength);

        for(int i = 0; i < videoListLength; i++) {

            JSONObject movieObj = videoList.getJSONObject(i);

            Log.d(LOG_TAG, movieObj.toString());

            String reviewId = Utility.parseMovieContents(movieObj, MoviesContract.ReviewsEntry.COLUMN_REVIEW_ID);
            String author = Utility.parseMovieContents(movieObj, MoviesContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR);
            String content = Utility.parseMovieContents(movieObj, MoviesContract.ReviewsEntry.COLUMN_REVIEW_CONTENT);

            ContentValues reviewValues = new ContentValues();

            reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_REVIEW_ID, reviewId);
            reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR, author);
            reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_REVIEW_CONTENT, content);
            reviewValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, mMovieId);

            cVVector.add(reviewValues);
        }

        int inserted = 0;
        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(MoviesContract.ReviewsEntry.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "FetchReviewsTask Complete. " + inserted + " Inserted");

        return null;
    }
}
