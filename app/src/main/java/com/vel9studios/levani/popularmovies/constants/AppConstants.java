package com.vel9studios.levani.popularmovies.constants;

/**
 * Class for holding constants used in code
 */
public class AppConstants {
    public static final String MOVIE_OBJECT_EXTRA = "movieExtra";

    //Query params - image
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w";
    public static final String GRID_IMAGE_QUERY_WIDTH = "185/";
    public static final String DETAIL_IMAGE_QUERY_WIDTH = "185/";

    //JSON Parse
    public static final String STRING_MOVIEDB_NULL = "null";
    public static final String STRING_NO_DATA = "No data";

    //Query params - general
    public static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
    public static final String SORT_PARAM = "sort_by";
    public static final String API_KEY_PARAM = "api_key";

    //JSON keys
    public static final String RESULTS = "results";
    public static final String TITLE = "original_title";
    public static final String OVERVIEW = "overview";
    public static final String RELEASE_DATE = "release_date";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String POSTER_PATH = "poster_path";

    //General Messages
    public static final String API_KEY_WARNING = "Please set API Key";
    public static final String APP_START_ERROR = "Could not start application";

}
