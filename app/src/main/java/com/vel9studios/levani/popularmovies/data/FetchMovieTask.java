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
public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final Context mContext;

    public FetchMovieTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPostExecute(Movie[] movieList) {

        if (movieList != null){
            //after task completes and new data is returned, clear old data and update
            //moviesAdapter.clear();
            //moviesAdapter.addAll(movieList);
        } else {
            //Toast appStart = Toast.makeText(getActivity(), AppConstants.CONNECTION_ERROR, Toast.LENGTH_LONG);
            //appStart.show();
        }
    }

    protected Movie[] doInBackground(String... params){

        try{

            MoviesDAO moviesDAO = new MoviesDAO();
            //fetch data from server
            String movieListJsonStr = moviesDAO.getMovieData(params);
            if (movieListJsonStr == null)
                return null;

            //get serialized data
            return getMovieDataFromJson(movieListJsonStr);

        } catch (JSONException e) {
            //it makes sense to return null here, since onPostExecute checks for null,
            //but need to figure out what's possible with Exceptions within framework overall
            //TODO: Exception handling
            Log.e(LOG_TAG, e.getMessage(), e);
            return null;
        }
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
     * @param moviesJsonStr response from server as String
     * @return an array of populated Movie objects
     * @throws JSONException
     */
    private Movie[] getMovieDataFromJson(String moviesJsonStr)
            throws JSONException {

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesList = moviesJson.getJSONArray(AppConstants.RESULTS);



        int moviesListLength = moviesList.length();
        Movie[] movies = new Movie[moviesListLength];

        Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesListLength);

        for(int i = 0; i < moviesListLength; i++) {

            JSONObject movieObj = moviesList.getJSONObject(i);
            movies[i] = populateMovie(movieObj);

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

            ContentValues moviesValues = new ContentValues();

            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE, title);
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_IMAGE_PATH, partialPath);
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, overview);
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, releaseDate);
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, voteAverage);
            cVVector.add(moviesValues);
        }

        int inserted = 0;
        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");

        return movies;
    }
}
