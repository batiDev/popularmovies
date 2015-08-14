package com.vel9studios.levani.popularmovies.validation;

import android.content.Context;
import android.widget.Toast;

import com.vel9studios.levani.popularmovies.constants.AppConstants;
import com.vel9studios.levani.popularmovies.constants.AppConstantsPrivate;

/**
 * Created by levani on 8/13/15.
 */
public class Validation {

    /**
     * Set up method for checking for things which are absolutely needed for app to work.
     * @return true if app can go on
     */
    public static Boolean appContainsAPIKey(Context context){

        Boolean containsNeededElements = false;

        //check for API key
        if (AppConstantsPrivate.API_KEY.length() == 0){

            Toast apiWarning = Toast.makeText(context, AppConstants.API_KEY_WARNING, Toast.LENGTH_LONG);
            apiWarning.show();

            Toast appStart = Toast.makeText(context, AppConstants.APP_START_ERROR, Toast.LENGTH_LONG);
            appStart.show();
        } else {
            containsNeededElements = true;
        }

        return containsNeededElements;

    }
}
