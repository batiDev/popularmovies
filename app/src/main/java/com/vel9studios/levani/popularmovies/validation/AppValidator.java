package com.vel9studios.levani.popularmovies.validation;

import android.content.Context;
import android.widget.Toast;

import com.vel9studios.levani.popularmovies.constants.AppConstants;
import com.vel9studios.levani.popularmovies.constants.AppConstantsPrivate;

public class AppValidator {

    /**
     * Set up method for checking for things which are absolutely needed for app to work.
     * @return true if app can go on
     */
    public static Boolean appContainsAPIKey(Context context){

        Boolean containsNeededElements = false;

        // check for API key
        // ideally we use StringUtils.isBlank(AppConstantsPrivate.API_KEY) here
        if (AppConstantsPrivate.API_KEY.length() == 0){

            Toast apiWarning = Toast.makeText(context, AppConstants.MESSAGE_API_KEY_WARNING, Toast.LENGTH_LONG);
            apiWarning.show();

            Toast appStart = Toast.makeText(context, AppConstants.MESSAGE_APP_START_ERROR, Toast.LENGTH_LONG);
            appStart.show();
        } else {
            containsNeededElements = true;
        }

        return containsNeededElements;

    }
}
