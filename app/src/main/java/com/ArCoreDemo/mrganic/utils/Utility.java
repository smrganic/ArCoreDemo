package com.ArCoreDemo.mrganic.utils;

import android.app.Activity;
import android.os.Handler;

import com.google.ar.core.ArCoreApk;

public final class Utility {

    private Utility() {}

    public static boolean ArCompatible(Activity activity) {
        ArCoreApk.Availability availability =
                ArCoreApk
                        .getInstance()
                        .checkAvailability(activity);
        if (availability.isTransient()) {
            new Handler().postDelayed(() -> ArCompatible(activity), 200);
        }
        return availability.isSupported();
    }
}
