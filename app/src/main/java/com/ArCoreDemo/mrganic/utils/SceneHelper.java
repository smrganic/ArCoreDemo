package com.ArCoreDemo.mrganic.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.google.ar.core.Anchor;
import com.google.ar.core.Plane;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.collision.Ray;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;

public class SceneHelper {

    private static final String TAG = "SceneHelper";

    private CustomArFragment fragment;
    private Scene scene;
    private Camera camera;
    private Anchor anchor;
    private ModelRenderable modelRenderable;
    private int numberOfAnchorNodes = 0;
    private boolean flag = false;


    public SceneHelper(CustomArFragment fragment) {
        this.fragment = fragment;
        this.scene = fragment.getArSceneView().getScene();
        this.camera = scene.getCamera();
        setupFragment();
    }

    private void setupFragment() {

        //This handles taps on ArPlane
        fragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

            //Creating anchor
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(fragment.getArSceneView().getScene());

            //Create transformable object and add it to anchor from above
            TransformableNode object = new TransformableNode(fragment.getTransformationSystem());
            object.setRenderable(modelRenderable);
            object.getScaleController().setMinScale(0.3f);
            object.getScaleController().setMaxScale(0.7f);
            object.setParent(anchorNode);
            object.select();
            numberOfAnchorNodes++;

            if (numberOfAnchorNodes > 5) {
                for (int i = 0; i < scene.getChildren().size(); i++) {
                    if (scene.getChildren().get(i) instanceof AnchorNode) {
                        scene.getChildren().get(i).setParent(null);
                        numberOfAnchorNodes--;
                        break;
                    }
                }
            }
        });

        //This runs on every frame
        fragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdate);
    }

    private void onUpdate(FrameTime frameTime) {
        //This ensure that this part runs only once
        if (anchor == null) {
            for (Plane plane : fragment.getArSceneView().getSession().getAllTrackables(Plane.class)) {
                //Second if because sometimes two planes would exist at the same time
                if (anchor == null) {
                    //Ensures that the plane is horizontal and tracked
                    if (plane.getTrackingState() == TrackingState.TRACKING && plane.getType().equals(Plane.Type.HORIZONTAL_UPWARD_FACING)) {
                        anchor = plane.createAnchor(plane.getCenterPose());
                        AnchorNode anchorNode = new AnchorNode(anchor);
                        anchorNode.setParent(fragment.getArSceneView().getScene());

                        TransformableNode object = new TransformableNode(fragment.getTransformationSystem());
                        object.setRenderable(modelRenderable);
                        object.getScaleController().setMinScale(0.3f);
                        object.getScaleController().setMaxScale(0.7f);
                        object.setParent(anchorNode);
                        object.select();
                        numberOfAnchorNodes++;
                    }
                } else break;
            }
        }

        Ray ray = new Ray(camera.getWorldPosition(), camera.getForward());
        HitTestResult hitTestResult = scene.hitTest(ray);
        if (hitTestResult.getNode() != null && hitTestResult.getDistance() < 0.001) {
            Vibrator vibrator = (Vibrator) fragment.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(500);
            }
        }
    }

    //Getting GLTF2 object from the internet using URI and constructing modelRenderable
    public void renderObject(String selectedObject) {

        Log.d(TAG, "URL for object render: " + selectedObject);

        Uri modelURI = Uri.parse(selectedObject);

        RenderableSource source = RenderableSource.builder()
                .setSource(
                        fragment.getArSceneView().getContext(),
                        modelURI,
                        RenderableSource.SourceType.GLTF2)
                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                .build();

        ModelRenderable.builder()
                .setSource(fragment.getContext(), source)
                .build()
                .thenAccept(object3D -> modelRenderable = object3D)
                .exceptionally(
                        throwable -> {
                            Log.d(TAG, "Failed to load ModelRenderable");
                            return null;
                        });
    }
}
