package com.vel9studios.levani.popularmovies.views;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vel9studios.levani.popularmovies.R;
import com.vel9studios.levani.popularmovies.constants.DetailFragmentConstants;

/**
 * {@link TrailerAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class TrailerAdapter extends CursorAdapter {

    private final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    public TrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_trailer, parent, false);


        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Read date from cursor
        TextView trailerName = (TextView) view.findViewById(R.id.list_item_trailer_name);
        trailerName.setText(cursor.getString(DetailFragmentConstants.COLUMN_VIDEO_NAME));
    }

}