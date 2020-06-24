package com.ArCoreDemo.mrganic.utils;

import android.content.Context;
import android.os.Handler;

import com.google.ar.core.ArCoreApk;

public final class Utility {

    private Utility() {}

    public static boolean ArCompatible(Context context) {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(context);
        if (availability.isTransient()) {
            new Handler().postDelayed(() -> ArCompatible(context), 200);
        }
        return availability.isSupported();
    }
}
