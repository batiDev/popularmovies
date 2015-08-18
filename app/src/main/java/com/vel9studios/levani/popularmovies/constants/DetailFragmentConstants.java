package com.vel9studios.levani.popularmovies.constants;

import com.vel9studios.levani.popularmovies.data.MoviesContract;

public class DetailFragmentConstants {

    public static final String[] MOVIE_DETAIL_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_ID,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_TITLE,
            MoviesContract.MoviesEntry.COLUMN_IMAGE_PATH,
            MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MoviesEntry.COLUMN_OVERVIEW,
            MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE,
            MoviesContract.MoviesEntry.COLUMN_FAVORITE_IND
    };

    public static final String[] VIDEO_DETAIL_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MoviesContract.VideosEntry.TABLE_NAME + "." + MoviesContract.VideosEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_ID,
            MoviesContract.VideosEntry.COLUMN_VIDEO_ID,
            MoviesContract.VideosEntry.COLUMN_VIDEO_KEY,
            MoviesContract.VideosEntry.COLUMN_VIDEO_NAME,
            MoviesContract.VideosEntry.COLUMN_VIDEO_SITE,
            MoviesContract.VideosEntry.COLUMN_VIDEO_SIZE,
            MoviesContract.VideosEntry.COLUMN_TYPE,
    };

    public static final String[] REVIEWS_DETAIL_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MoviesContract.ReviewsEntry.TABLE_NAME + "." + MoviesContract.VideosEntry._ID,
            MoviesContract.MoviesEntry.COLUMN_MOVIE_ID,
            MoviesContract.ReviewsEntry.COLUMN_REVIEW_ID,
            MoviesContract.ReviewsEntry.COLUMN_REVIEW_CONTENT,
            MoviesContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR
    };

    //global to all three tables movies, videos, and reviews
    public static final int COLUMN_MOVIE_ID = 1;

    public static final int COLUMN_MOVIE_TITLE_ID = 2;
    public static final int COLUMN_IMAGE_PATH_ID = 3;
    public static final int COLUMN_RELEASE_DATE_ID = 4;
    public static final int COLUMN_OVERVIEW_ID = 5;
    public static final int COLUMN_VOTE_AVERAGE_ID = 6;
    public static final int COLUMN_FAVORITE_IND_ID = 7;

    public static final int COLUMN_VIDEO_ID = 2;
    public static final int COLUMN_VIDEO_KEY = 3;
    public static final int COLUMN_VIDEO_NAME = 4;
    public static final int COLUMN_VIDEO_SITE = 5;
    public static final int COLUMN_VIDEO_TYPE = 6;

    public static final int COLUMN_REVIEW_ID = 2;
    public static final int COLUMN_REVIEW_CONTENT_ID = 3;
    public static final int COLUMN_REVIEWS_AUTHOR_ID = 4;

}
