/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vel9studios.levani.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.vel9studios.levani.popularmovies.constants.AppConstants;

/**
 * Defines table and column names for the movie database.
 * Core code from Udacity's "Developing Android Apps: Fundamentals"
 */
public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.vel9studios.levani.popularmovies";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";
    public static final String PATH_VIDEOS = "videos";
    public static final String PATH_REVIEWS= "reviews";
    public static final String PATH_FAVORITE = "favorite";

    // used in combination with movies to get id for "first" movie
    public static final String PATH_FIRST = "first";

    // next three inner classes define our three main tables: movies, videos and trailers
    public static final class MoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        // Table name
        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_IMAGE_PATH = "imagePath";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_FAVORITE_IND = "favoriteInd";

        public static Uri buildMoviesUri() {
            return BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES)
                    .build();
        }

        public static Uri buildMovieFavoritesUri() {
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_MOVIES)
                    .appendPath(PATH_FAVORITE)
                    .build();
        }

        public static Uri buildMovieItemUri(int id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildFavoriteUri(String movieId, String addOrRemove){
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_MOVIES)
                    .appendPath(PATH_FAVORITE)
                    .appendPath(movieId)
                    .appendQueryParameter(AppConstants.FAVORITE_IND, addOrRemove)
                    .build();
        }

        public static Uri buildFirstMovieUri(){
            return BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_MOVIES)
                    .appendPath(PATH_FIRST)
                    .build();
        }

    }

    public static final class VideosEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEOS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VIDEOS;

        // Table name
        public static final String TABLE_NAME = "videos";

        public static final String COLUMN_VIDEO_ID = "id";
        public static final String COLUMN_VIDEO_KEY = "key";
        public static final String COLUMN_VIDEO_NAME = "name";
        public static final String COLUMN_VIDEO_SITE = "site";
        public static final String COLUMN_VIDEO_SIZE = "size";
        public static final String COLUMN_TYPE = "type";

        public static Uri buildVideosUri(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

    }

    public static final class ReviewsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

        // Table name
        public static final String TABLE_NAME = "reviews";

        public static final String COLUMN_REVIEW_ID = "id";
        public static final String COLUMN_REVIEW_AUTHOR = "author";
        public static final String COLUMN_REVIEW_CONTENT = "content";

        public static Uri buildReviewsUri(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

    }
}
