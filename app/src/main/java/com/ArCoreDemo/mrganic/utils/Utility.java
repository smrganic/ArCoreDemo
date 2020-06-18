package com.ArCoreDemo.mrganic.utils;

import android.content.Context;
import android.os.Handler;

import com.google.ar.core.ArCoreApk;

public abstract class Utility {
    public static boolean ArCompatible(Context context) {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(context);
        if(availability.isTransient()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ArCompatible(context);
                }
            }, 200);
        }
        return availability.isSupported();
    }
}
