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
            //it makes sense to return null here, since onPostExecute checks for null,
            //but need to figure out what's possible with Exceptions within framework overall
            //TODO: Exception handling
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

        int inserted = 0;
        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(MoviesContract.VideosEntry.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "FetchVideosTask Complete. " + inserted + " Inserted");

        return null;
    }
}
