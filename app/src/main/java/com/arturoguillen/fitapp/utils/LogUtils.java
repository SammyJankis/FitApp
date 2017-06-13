package com.arturoguillen.fitapp.utils;

import android.support.compat.BuildConfig;
import android.util.Log;

/**
 * Created by arturo.guillen on 13/06/2017.
 */

public class LogUtils {

    public static void DEBUG(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }
}
