package com.ArCoreDemo.mrganic.utils;

import android.content.Context;

import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

public class SceneHelper {

    private SceneView sceneView;
    private Scene scene;
    private TransformationSystem transformationSystem;
    private TransformableNode transformableNode;


    public SceneHelper(SceneView viewById, Context context) {
        this.sceneView = viewById;
        scene = sceneView.getScene();
        transformationSystem = new TransformationSystem(context.getResources().getDisplayMetrics(), new FootprintSelectionVisualizer());
        transformableNode = new TransformableNode(transformationSystem);
        transformableNode.setParent(scene);
    }

    public Scene getScene() {
        return scene;
    }

    public SceneView getSceneView() {
        return sceneView;
    }

    public TransformableNode getTransformableNode() {
        return transformableNode;
    }
}
