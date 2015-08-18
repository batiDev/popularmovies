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
            String reviewsJSONStr = moviesDAO.getReviews(mMovieId);

            //get serialized data
            getVideosDataFromJson(reviewsJSONStr);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return null;
    }

    private Void getVideosDataFromJson(String reviewsJsonStr)
            throws JSONException {

        if (reviewsJsonStr == null){
            return null;
        }

        JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
        JSONArray reviewsList = reviewsJson.getJSONArray(AppConstants.RESULTS);

        int videoListLength = reviewsList.length();

        Vector<ContentValues> cVVector = new Vector<>(videoListLength);

        for(int i = 0; i < videoListLength; i++) {

            JSONObject reviewObj = reviewsList.getJSONObject(i);

            Log.d(LOG_TAG, reviewObj.toString());

            String reviewId = Utility.parseMovieContents(reviewObj, MoviesContract.ReviewsEntry.COLUMN_REVIEW_ID);
            String author = Utility.parseMovieContents(reviewObj, MoviesContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR);
            String content = Utility.parseMovieContents(reviewObj, MoviesContract.ReviewsEntry.COLUMN_REVIEW_CONTENT);

            ContentValues reviewValues = new ContentValues();

            reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_REVIEW_ID, reviewId);
            reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR, author);
            reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_REVIEW_CONTENT, content);
            reviewValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, mMovieId);

            cVVector.add(reviewValues);
        }

        //Insert content values code from "Developing Android Apps: Fundamentals"
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(MoviesContract.ReviewsEntry.CONTENT_URI, cvArray);
        }

        return null;
    }
}
