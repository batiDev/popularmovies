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

public class FetchVideosTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchVideosTask.class.getSimpleName();
    private final Context mContext;
    private String mMovieId;

    public FetchVideosTask(Context context) {
        mContext = context;
    }

    protected Void doInBackground(String... params){

        try{

            mMovieId = params[0];
            MoviesDAO moviesDAO = new MoviesDAO();
            //fetch data from server
            String videosJSONStr = moviesDAO.getVideos(mMovieId);

            //get serialized data
            getVideosDataFromJson(videosJSONStr);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return null;
    }

    private Void getVideosDataFromJson(String videosJsonStr)
            throws JSONException {

        if (videosJsonStr== null){
            return null;
        }

        JSONObject moviesJson = new JSONObject(videosJsonStr);
        JSONArray videoList = moviesJson.getJSONArray(AppConstants.RESULTS);

        int videoListLength = videoList.length();

        Vector<ContentValues> cVVector = new Vector<>(videoListLength);

        for(int i = 0; i < videoListLength; i++) {

            JSONObject movieObj = videoList.getJSONObject(i);

            String videoId = Utility.parseMovieContents(movieObj, MoviesContract.VideosEntry.COLUMN_VIDEO_ID);
            String videoKey = Utility.parseMovieContents(movieObj, MoviesContract.VideosEntry.COLUMN_VIDEO_KEY);
            String name = Utility.parseMovieContents(movieObj, MoviesContract.VideosEntry.COLUMN_VIDEO_NAME);
            String site = Utility.parseMovieContents(movieObj, MoviesContract.VideosEntry.COLUMN_VIDEO_SITE);
            Integer size = movieObj.getInt(MoviesContract.VideosEntry.COLUMN_VIDEO_SIZE);
            String type = Utility.parseMovieContents(movieObj, MoviesContract.VideosEntry.COLUMN_TYPE);

            ContentValues videoValues = new ContentValues();

            videoValues.put(MoviesContract.VideosEntry.COLUMN_VIDEO_ID,videoId);
            videoValues.put(MoviesContract.VideosEntry.COLUMN_VIDEO_KEY, videoKey);
            videoValues.put(MoviesContract.VideosEntry.COLUMN_VIDEO_NAME, name);
            videoValues.put(MoviesContract.VideosEntry.COLUMN_VIDEO_SITE, site);
            videoValues.put(MoviesContract.VideosEntry.COLUMN_VIDEO_SIZE, size);
            videoValues.put(MoviesContract.VideosEntry.COLUMN_TYPE, type);
            videoValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, mMovieId);

            cVVector.add(videoValues);
        }

        //Insert content values code from "Developing Android Apps: Fundamentals"
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(MoviesContract.VideosEntry.CONTENT_URI, cvArray);
        }

        return null;
    }
}
