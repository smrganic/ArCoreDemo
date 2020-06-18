package com.ArCoreDemo.mrganic.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.collision.Box;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

//TODO this entire thing
public class SceneHelper {

    private static final String TAG = "SceneHelper";

    private Context context;
    private SceneView sceneView;
    private Scene scene;
    private TransformationSystem transformationSystem;
    private TransformableNode transformableNode;


    public SceneHelper(SceneView viewById, Context context) {
        this.sceneView = viewById;
        scene = sceneView.getScene();
        this.context = context;

        //Todo sceneView transformations
        scene.addOnPeekTouchListener(new Scene.OnPeekTouchListener() {
            @Override
            public void onPeekTouch(HitTestResult hitTestResult, MotionEvent motionEvent) {
                transformationSystem.onTouch(hitTestResult, motionEvent);
            }
        });
        //addTransformations();
    }

    /*public void addTransformations() {
        transformationSystem = new TransformationSystem(context.getResources().getDisplayMetrics(), new FootprintSelectionVisualizer());
        transformableNode = new TransformableNode(transformationSystem);
        transformableNode.setParent(scene);

        transformableNode.getScaleController().setEnabled(true);
        transformableNode.getRotationController().setEnabled(true);
        transformableNode.getTranslationController().setEnabled(false);

        transformableNode.select();
    }*/

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
        transformableNode.setRenderable(modelRenderable);
        limitSize(0.1f, 0.3f);
    }

    private void limitSize(float minSize, float maxSize) {
        Box modelBox = (Box) transformableNode.getCollisionShape();
        Vector3 size = modelBox.getSize();
        float maxDim = Math.max(size.x, Math.max(size.y, size.z));
        float currentScale = transformableNode.getWorldScale().x;

        // Assume all dimensions have the same scale.
        float currentSize = maxDim * currentScale;
        float newScale = currentScale;
        if (currentSize < minSize) {
            newScale = newScale * (minSize/currentSize);
        } else if (currentSize > maxSize) {
            newScale = newScale * (maxSize/currentSize);
        }
        transformableNode.setWorldScale(new Vector3(newScale, newScale, newScale));

    }
}
