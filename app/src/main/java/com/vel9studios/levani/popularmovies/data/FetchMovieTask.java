package com.vel9studios.levani.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.vel9studios.levani.popularmovies.beans.Movie;
import com.vel9studios.levani.popularmovies.constants.AppConstants;
import com.vel9studios.levani.popularmovies.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

public class FetchMovieTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final Context mContext;

    public FetchMovieTask(Context context) {
        mContext = context;
    }

    protected Void doInBackground(String... params){

        try{

            MoviesDAO moviesDAO = new MoviesDAO();
            //fetch data from server
            String movieListJsonStr = moviesDAO.getMovieData(params);

            //get serialized data
            updateDatabaseWithMovies(movieListJsonStr);

        } catch (JSONException e) {
            //it makes sense to return null here, since onPostExecute checks for null,
            //but need to figure out what's possible with Exceptions within framework overall
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return null;
    }

    private Void updateDatabaseWithMovies(String moviesJsonStr)
            throws JSONException {

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesList = moviesJson.getJSONArray(AppConstants.RESULTS);

        int moviesListLength = moviesList.length();
        Movie[] movies = new Movie[moviesListLength];

        Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesListLength);

        for(int i = 0; i < moviesListLength; i++) {

            JSONObject movieObj = moviesList.getJSONObject(i);

            String title = Utility.parseMovieContents(movieObj, AppConstants.TITLE);
            String partialPath = Utility.parseMovieContents(movieObj, AppConstants.POSTER_PATH);
            String overview = Utility.parseMovieContents(movieObj, AppConstants.OVERVIEW);

            String releaseDate = Utility.parseMovieContents(movieObj, AppConstants.RELEASE_DATE);

            //Date string is (usually) in "YYYY-MM-DD" format, get just the year
            if (!releaseDate.equals(AppConstants.STRING_NO_DATA) && releaseDate.length() > 4){
                releaseDate = releaseDate.substring(0,4);
                //year data may also just be "empty" check for it here
            } else if (releaseDate.length() == 0) {
                releaseDate = AppConstants.STRING_NO_DATA;
            }

            //TODO: AppConstants
            Integer movieId = movieObj.getInt("id");
            Double voteAverage = movieObj.getDouble(AppConstants.VOTE_AVERAGE);
            Double popularity = movieObj.getDouble("popularity");

            ContentValues moviesValues = new ContentValues();

            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movieId);
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE, title);
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_IMAGE_PATH, partialPath);
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, overview);
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE, releaseDate);
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE, voteAverage);
            moviesValues.put(MoviesContract.MoviesEntry.COLUMN_POPULARITY, popularity);

            cVVector.add(moviesValues);
        }

        int inserted = 0;
        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            inserted = mContext.getContentResolver().bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI, cvArray);
        }

        Log.d(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted/Updated");

        return null;
    }
}
