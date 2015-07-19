package com.vel9studios.levani.popularmovies.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vel9studios.levani.popularmovies.R;
import com.vel9studios.levani.popularmovies.beans.Movie;
import com.vel9studios.levani.popularmovies.constants.AppConstants;


public class DetailActivityFragment extends Fragment {

    private final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private Movie movie;
    public DetailActivityFragment() {
    }

    //Build detail view of the movie
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra(AppConstants.MOVIE_OBJECT_EXTRA)){
            Bundle extras = intent.getExtras();
            movie = (Movie) extras.get(AppConstants.MOVIE_OBJECT_EXTRA);

            //set text elements
            ((TextView) rootView.findViewById(R.id.detail_movie_title)).setText(movie.getTitle());
            ((TextView) rootView.findViewById(R.id.detail_movie_release_date)).setText(movie.getReleaseDate());
            ((TextView) rootView.findViewById(R.id.detail_movie_overview)).setText(movie.getOverview());
            ((TextView) rootView.findViewById(R.id.detail_movie_vote_average)).setText(movie.getVoteAverage());

            //set image
            String posterPath = movie.getImagePath();
            String fullPosterPath = AppConstants.IMAGE_BASE_URL + AppConstants.DETAIL_IMAGE_QUERY_WIDTH + posterPath;
            ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_movie_image);

            Resources resources = getResources();
            int height = resources.getInteger(R.integer.grid_image_height);
            int width = resources.getInteger(R.integer.grid_image_width);

            Picasso.with(getActivity())
                    .load(fullPosterPath)
                    .resize(width, height)
                    .error(R.drawable.unavailable_poster_black)
                    .into(imageView);
        }

        //TODO: in what situation would I need an else statement here

        return rootView;
    }
}
