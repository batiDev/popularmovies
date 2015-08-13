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
    public static final String API_BASE_DISCOVER_URL = "https://api.themoviedb.org/3/discover/movie?";
    public static final String API_BASE_ITEM_URL = "https://api.themoviedb.org/3/movie/";
    public static final String API_VIDEOS_PATH = "videos";
    public static final String API_REVIEWS_PATH = "reviews";
    public static final String SORT_PARAM = "sort_by";
    public static final String API_KEY_PARAM = "api_key";

    //JSON keys
    public static final String RESULTS = "results";
    public static final String TITLE = "original_title";
    public static final String OVERVIEW = "overview";
    public static final String RELEASE_DATE = "release_date";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String POSTER_PATH = "poster_path";

    //save state
    public static final String MOVIE_VALUES = "movieValues";

    //General Messages
    public static final String API_KEY_WARNING = "Please set API Key";
    public static final String APP_START_ERROR = "Could not start application";
    public static final String CONNECTION_ERROR = "Connection Error";

    //sort orders
    public static final String SORT_TYPE_POPULARITY = "popularity.desc";
    public static final String SORT_TYPE_VOTE = "vote_average.desc";

    //Grid View Item Limit
    public static final String GRID_VIEW_ITEM_LIMIT = "20";
    public static final String DETAIL_VIEW_ITEM_LIMIT = "1";

    public static final String DETAILFRAGMENT_TAG = "DFTAG";
    public static final String REVIEWFRAGMENT_TAG = "RFTAG";
    public static final String Y_FLAG = "Y";
    public static final String N_FLAG = "N";

    //youTube values
    public static final String YOUTUBE_VIDEO_ID = "VIDEO_ID";
    public static final String YOUTUBE_URI = "vnd.youtube:";

    public static final String FAVORITE_IND = "favoriteInd";
}
