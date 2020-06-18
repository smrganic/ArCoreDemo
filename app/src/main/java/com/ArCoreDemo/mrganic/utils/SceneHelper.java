package com.ArCoreDemo.mrganic.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

public class SceneHelper {

    private static final String TAG = "SceneHelper";

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

    public void renderObject(String modelUrl) {

        Log.d(TAG, "URL for object render: " + modelUrl);

        RenderableSource source = RenderableSource
                .builder()
                .setSource(sceneView.getContext(), Uri.parse(modelUrl), RenderableSource.SourceType.GLTF2)
                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                .setScale(0.25f)
                .build();

        ModelRenderable
                .builder()
                .setSource(sceneView.getContext(), source)
                .build()
                .thenAccept(this::updateNode);
    }


    private void updateNode(ModelRenderable modelRenderable) {
        transformableNode.getScaleController().setEnabled(true);
        transformableNode.getTranslationController().setEnabled(true);
        transformableNode.setRenderable(modelRenderable);
        transformableNode.setLocalPosition(new Vector3(0f,0f,-1f));
    }
}
