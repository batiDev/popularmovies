package com.vel9studios.levani.popularmovies.views;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vel9studios.levani.popularmovies.R;
import com.vel9studios.levani.popularmovies.constants.DetailFragmentConstants;

/**
 * {@link ReviewsAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class ReviewsAdapter extends CursorAdapter {

    private final String LOG_TAG = ReviewsAdapter.class.getSimpleName();

    public ReviewsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_review, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Read date from cursor
        TextView reviewContent = (TextView) view.findViewById(R.id.list_item_review_content);
        reviewContent.setText(cursor.getString(DetailFragmentConstants.COLUMN_REVIEW_CONTENT_ID));
    }

}