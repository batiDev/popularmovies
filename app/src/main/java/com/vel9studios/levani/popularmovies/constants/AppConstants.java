package com.vel9studios.levani.popularmovies.constants;

/**
 * Class for holding constants used in code
 */
public class AppConstants {





    // Query params - general
    public static final String QUERY_BASE_DISCOVER_URL = "https://api.themoviedb.org/3/discover/movie?";
    public static final String QUERY_BASE_ITEM_URL = "https://api.themoviedb.org/3/movie/";
    public static final String QUERY_VIDEOS_PATH = "videos";
    public static final String QUERY_REVIEWS_PATH = "reviews";
    public static final String QUERY_SORT_PARAM = "sort_by";
    public static final String QUERY_API_KEY_PARAM = "api_key";
    // Query params - image
    public static final String QUERY_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w";
    public static final String QUERY_GRID_IMAGE_QUERY_WIDTH = "185/";
    public static final String QUERY_DETAIL_IMAGE_QUERY_WIDTH = "185/";
    //sort orders
    public static final String QUERY_SORT_TYPE_POPULARITY = "popularity.desc";
    public static final String QUERY_SORT_TYPE_VOTE = "vote_average.desc";

    // JSON "keys"/"fields"
    public static final String JSON_PARSE_RESULTS = "results";
    public static final String JSON_PARSE_TITLE = "original_title";
    public static final String JSON_PARSE_OVERVIEW = "overview";
    public static final String JSON_RELEASE_DATE = "release_date";
    public static final String JSON_VOTE_AVERAGE = "vote_average";
    public static final String JSON_PARSE_POSTER_PATH = "poster_path";
    public static final String JSON_PARSE_POPULARITY = "popularity";
    public static final String JSON_PARSE_MOVIE_ID = "id";
    // JSON Parse Possible messages for empty/null
    public static final String JSON_PARSE_NULL = "null";
    public static final String JSON_PARSE_STRING_NO_DATA = "No data";

    // YouTube constants
    public static final String YOUTUBE_VIDEO_ID = "VIDEO_ID";
    public static final String YOUTUBE_URI = "vnd.youtube:";
    public static final String YOUTUBE_URL_SHARE = "http://youtube.com/watch?v=";

    //Grid View Item Limit
    public static final String GRID_VIEW_ITEM_LIMIT = "20";
    public static final String DETAIL_VIEW_ITEM_LIMIT = "1";

    // fragment tags
    public static final String DETAIL_FRAGMENT_TAG = "DFTAG";
    public static final String REVIEW_FRAGMENT_TAG = "RFTAG";

    public static final String Y_FLAG = "Y";
    public static final String N_FLAG = "N";

    public static final String FAVORITE_IND = "favoriteInd";
    public static final String FAVORITE_PREF_KEY = "favoriteInd";

    public static final String MOVIE_ID_KEY = "movieId";

    public static final String SELECTED_ITEM_POSITION = "selected_position";

    //General Application Messages
    public static final String MESSAGE_API_KEY_WARNING = "Please set API Key";
    public static final String MESSAGE_APP_START_ERROR = "Could not start application";
    public static final String MESSAGE_CONNECTION_ERROR = "Could not fetch updated movie data";
    public static final String MESSAGE_FAVORITE_ADDED = " successfully added to favorites";
    public static final String MESSAGE_FAVORITE_REMOVED = " removed from favorites";

    public static final Integer FAVORITE_VALUES_MOVIE_ID_POSITION = 0;
    public static final Integer FAVORITE_VALUES_FAVORITE_IND_POSITION = 1;
    public static final Integer FAVORITE_VALUES_MOVIE_TITLE_POSITION = 2;

    public static final String REVIEWS_URI_KEY = "ReviewsURI";
}
