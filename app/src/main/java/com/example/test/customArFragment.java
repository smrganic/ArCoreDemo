package com.example.test;

import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

public class customArFragment extends ArFragment {
    @Override
    protected Config getSessionConfiguration(Session session) {
        Config.LightEstimationMode lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR;
        Config config = new Config(session);
        config.setLightEstimationMode(lightEstimationMode);
        return config;
    }
}
