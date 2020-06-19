package com.ArCoreDemo.mrganic;

import android.view.MotionEvent;

import com.google.ar.core.Config;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;

public class CustomArFragment extends ArFragment {

    private Config config;

    @Override
    protected Config getSessionConfiguration(Session session) {
        Config.LightEstimationMode lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR;
        config = new Config(session);
        config.setLightEstimationMode(lightEstimationMode);
        return config;
    }
}
