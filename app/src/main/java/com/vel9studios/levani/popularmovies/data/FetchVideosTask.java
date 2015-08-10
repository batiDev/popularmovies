package com.vel9studios.levani.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.vel9studios.levani.popularmovies.beans.Movie;
import com.vel9studios.levani.popularmovies.constants.AppConstants;

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
    private String parseMovieContents(JSONObject movieObj, String key) throws JSONException {

        String content;
        Object subMovieObj = movieObj.get(key);
        if (subMovieObj != null && !subMovieObj.toString().equalsIgnoreCase(AppConstants.STRING_MOVIEDB_NULL))
            content = subMovieObj.toString();
        else
            content = AppConstants.STRING_NO_DATA;

        return content;
    }

    /**
     * Takes data from JSONObject and creates new Parcable movie bean.
     *
     * @param movieObj JSON object containing movie details
     * @return populated Movie bean
     * @throws JSONException
     */
    private Movie populateMovie(JSONObject movieObj) throws JSONException {

        Movie movie = new Movie();

        String title = parseMovieContents(movieObj, AppConstants.TITLE);
        String partialPath = parseMovieContents(movieObj, AppConstants.POSTER_PATH);
        String overview = parseMovieContents(movieObj, AppConstants.OVERVIEW);

        String releaseDate = parseMovieContents(movieObj, AppConstants.RELEASE_DATE);

        //Date string is (usually) in "YYYY-MM-DD" format, get just the year
        if (!releaseDate.equals(AppConstants.STRING_NO_DATA) && releaseDate.length() > 4){
            releaseDate = releaseDate.substring(0,4);
            //year data may also just be "empty" check for it here
        } else if (releaseDate.length() == 0) {
            releaseDate = AppConstants.STRING_NO_DATA;
        }

        String voteAverage = parseMovieContents(movieObj, AppConstants.VOTE_AVERAGE);

        //populate bean
        movie.setTitle(title);
        movie.setImagePath(partialPath);
        movie.setOverview(overview);
        movie.setReleaseDate(releaseDate);
        movie.setVoteAverage(voteAverage);

        return movie;
    }

    /**
     * Core code taken from Udacity's "Developing Android Apps: Fundamentals" course
     * Updated method return an array of Movie objects for use with Adapter
     *
     * @return an array of populated Movie objects
     * @throws JSONException
     */
    private Void getVideosDataFromJson(String videosJsonStr)
            throws JSONException {

        JSONObject moviesJson = new JSONObject(videosJsonStr);
        JSONArray videoList = moviesJson.getJSONArray(AppConstants.RESULTS);

        int videoListLength = videoList.length();

        Vector<ContentValues> cVVector = new Vector<ContentValues>(videoListLength);

        for(int i = 0; i < videoListLength; i++) {

            JSONObject movieObj = videoList.getJSONObject(i);

            Log.d(LOG_TAG, movieObj.toString());

            String videoId = parseMovieContents(movieObj, MoviesContract.VideosEntry.COLUMN_VIDEO_ID);
            String videoKey = parseMovieContents(movieObj, MoviesContract.VideosEntry.COLUMN_VIDEO_KEY);
            String name = parseMovieContents(movieObj, MoviesContract.VideosEntry.COLUMN_VIDEO_NAME);
            String site = parseMovieContents(movieObj, MoviesContract.VideosEntry.COLUMN_VIDEO_SITE);
            Integer size = movieObj.getInt(MoviesContract.VideosEntry.COLUMN_VIDEO_SIZE);
            String type = parseMovieContents(movieObj, MoviesContract.VideosEntry.COLUMN_TYPE);

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
