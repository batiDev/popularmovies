package com.vel9studios.levani.popularmovies.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.vel9studios.levani.popularmovies.R;
import com.vel9studios.levani.popularmovies.activities.DetailActivity;
import com.vel9studios.levani.popularmovies.beans.Movie;
import com.vel9studios.levani.popularmovies.views.MovieAdapter;
import com.vel9studios.levani.popularmovies.constants.AppConstants;
import com.vel9studios.levani.popularmovies.constants.AppConstantsPrivate;
import com.vel9studios.levani.popularmovies.data.MoviesDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Primary fragment, declares primary business methods
 * Contains inner Async FetchMoviesTask
 */
public class PopularMoviesFragment extends Fragment {

    private final String LOG_TAG = PopularMoviesFragment.class.getSimpleName();
    private MovieAdapter moviesAdapter;
    private Boolean applicationRunStatus;

    public PopularMoviesFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();

        //get movies on view
        if (applicationRunStatus) updateMovies();
    }

    /**
     * Fetches latest state of values in User Preferences
     * Uses preference values for fetching fresh data each time view is displayed
     *
     */
    public void updateMovies(){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Resources resources = getResources();
        String sortOrder = preferences.getString(resources.getString(R.string.pref_sort_key), "");

        //use default if there's no sort key
        if (sortOrder == null || sortOrder.length() == 0)
            sortOrder = resources.getString(R.string.pref_sort_default);

        //updated view with new movies data
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute(sortOrder);
    }

    /**
     * Set up method for checking for things which are absolutely needed for app to work.
     * TODO: Move into a validation class
     * @return true if app can go on
     */
    public Boolean appContainsAPIKey(){

        Boolean containsNeededElements = false;

        //check for API key
        if (AppConstantsPrivate.API_KEY.length() == 0){

            Log.e(LOG_TAG, AppConstants.API_KEY_WARNING);
            Toast apiWarning = Toast.makeText(getActivity(), AppConstants.API_KEY_WARNING, Toast.LENGTH_LONG);
            apiWarning.show();

            Toast appStart = Toast.makeText(getActivity(), AppConstants.APP_START_ERROR, Toast.LENGTH_LONG);
            appStart.show();

        }else{
            containsNeededElements = true;
        }

        return containsNeededElements;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //check for needed elements
        applicationRunStatus = appContainsAPIKey();

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        View view = rootView.findViewById(R.id.grid_item_movie_image);

        if (view instanceof GridView && applicationRunStatus){

            GridView gw = (GridView) view;

            //create adapter handling display of poster images
            moviesAdapter = new MovieAdapter(getActivity(), R.layout.grid_item_movie,
                    new ArrayList<Movie>());

            gw.setAdapter(moviesAdapter);

            //set click-listener, called when user clicks an image
            //Core code from Udacity's "Developing Android Apps: Fundamentals"
            gw.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    DetailActivity detailActivity = new DetailActivity();

                    //create explicit intent
                    Intent seeMovieDetail = new Intent(getActivity(), DetailActivity.class);

                    //get data for the movie user clicked on, data is contained within the clicked object
                    Movie movie = (Movie) adapterView.getItemAtPosition(i);

                    //pass data to activity
                    seeMovieDetail.putExtra(AppConstants.MOVIE_OBJECT_EXTRA, movie);
                    startActivity(seeMovieDetail);
                }
            });

        } else {

            Log.e(LOG_TAG, AppConstants.APP_START_ERROR);
        }

        return rootView;
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        @Override
        protected void onPostExecute(Movie[] movieList) {

            if (movieList != null){

                //after task completes and new data is returned, clear old data and update
                moviesAdapter.clear();
                moviesAdapter.addAll(movieList);
            }
        }

        protected Movie[] doInBackground(String... params){

            try{

                MoviesDAO moviesDAO = new MoviesDAO();

                //fetch data from server
                String movieListJsonStr = moviesDAO.getMovieData(params);

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

            //Date string is in "YYYY-MM-DD" format, get just the year
            if (!releaseDate.equals(AppConstants.STRING_NO_DATA) && releaseDate.length() > 4)
                releaseDate = releaseDate.substring(0,4);

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
            for(int i = 0; i < moviesListLength; i++) {

                JSONObject movieObj = moviesList.getJSONObject(i);
                movies[i] = populateMovie(movieObj);
            }

            return movies;
        }
    }
}
