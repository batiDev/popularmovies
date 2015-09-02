package com.vel9studios.levani.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.vel9studios.levani.popularmovies.constants.AppConstants;
import com.vel9studios.levani.popularmovies.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    private Void getVideosDataFromJson(String reviewsResponseStr)
            throws JSONException {

        // need to handle this
        if (reviewsResponseStr == null)
            return null;

        JSONObject reviewsJson = new JSONObject(reviewsResponseStr);
        JSONArray reviewsJSONArray = reviewsJson.getJSONArray(AppConstants.JSON_PARSE_RESULTS);

        List<ContentValues> reviewContentValuesList = new ArrayList<>();

        for(int i = 0; i < reviewsJSONArray.length(); i++) {

            JSONObject reviewObj = reviewsJSONArray.getJSONObject(i);

            String reviewId = AppUtils.parseMovieContents(reviewObj, MoviesContract.ReviewsEntry.COLUMN_REVIEW_ID);
            String author = AppUtils.parseMovieContents(reviewObj, MoviesContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR);
            String content = AppUtils.parseMovieContents(reviewObj, MoviesContract.ReviewsEntry.COLUMN_REVIEW_CONTENT);

            ContentValues reviewValues = new ContentValues();

            reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_REVIEW_ID, reviewId);
            reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR, author);
            reviewValues.put(MoviesContract.ReviewsEntry.COLUMN_REVIEW_CONTENT, content);
            reviewValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, mMovieId);

            reviewContentValuesList.add(reviewValues);
        }

        //Insert content values code from "Developing Android Apps: Fundamentals"
        Integer contentValuesListSize = reviewContentValuesList.size();
        if ( contentValuesListSize> 0 ) {
            ContentValues[] cvArray = new ContentValues[contentValuesListSize];
            reviewContentValuesList.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(MoviesContract.ReviewsEntry.CONTENT_URI, cvArray);
        }

        return null;
    }
}
