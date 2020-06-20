package com.ArCoreDemo.mrganic.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;

import com.ArCoreDemo.mrganic.R;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
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
    private SnackBarHelper snackBarHelper;
    private CustomArFragment fragment;
    private Scene scene;
    private Camera camera;
    private ModelRenderable modelRenderable;
    private int numberOfAnchorNodes = 0;


    public SceneHelper(CustomArFragment fragment) {
        this.fragment = fragment;
        this.scene = fragment.getArSceneView().getScene();
        this.camera = scene.getCamera();
        snackBarHelper = new SnackBarHelper();
        snackBarHelper.showMessage(fragment.getActivity(), fragment.getString(R.string.searching));
        setupFragment();
    }

    private void setupFragment() {

        //This runs on every frame
        scene.addOnUpdateListener(this::onUpdate);

        //This handles taps on ArPlane
        fragment.setOnTapArPlaneListener(this::onTapPlane);
    }

    private void onUpdate(FrameTime frameTime) {
        warnIfInsideObject(checkForCollision());
        showInstructions();
    }

    private boolean checkForCollision() {
        //Checks if phone is closer than 1 millimeter to any node
        Ray ray = new Ray(camera.getWorldPosition(), camera.getForward());
        HitTestResult hitTestResult = scene.hitTest(ray);
        return hitTestResult.getNode() != null && hitTestResult.getDistance() < 0.001;
    }

    //Is there a better way to do this?
    //Explore adding blur
    private void warnIfInsideObject(boolean collision) {
        if (collision) {
            Vibrator vibrator = (Vibrator) fragment.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            //Check the installed android version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(100);
            }
        }
    }

    private void showInstructions() {
        if (snackBarHelper.getMessage().equals(fragment.getString(R.string.searching))) {
            for (Plane plane : fragment.getArSceneView().getSession().getAllTrackables(Plane.class)) {
                if (plane.getTrackingState() == TrackingState.TRACKING && plane.getType().equals(Plane.Type.HORIZONTAL_UPWARD_FACING)) {
                    snackBarHelper.showDismissibleMessage(fragment.getActivity(), fragment.getString(R.string.tapInstruction));
                    break;
                }
            }
        }
    }

    private void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {

        if (numberOfAnchorNodes == 0) {
            snackBarHelper.showTimedMessage(fragment.getActivity(), fragment.getString(R.string.nodeInstruction));
        }

        //Creating anchor and a node for the anchor
        Anchor anchor = hitResult.createAnchor();
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(fragment.getArSceneView().getScene());

        //Create transformable object and add it to anchor from above
        TransformableNode object = new TransformableNode(fragment.getTransformationSystem());
        object.setRenderable(modelRenderable);
        object.getScaleController().setMinScale(0.3f);
        object.getScaleController().setMaxScale(0.65f);
        object.setParent(anchorNode);
        object.select();
        numberOfAnchorNodes++;

        //This releases nodes so that the renderer doesn't get overloaded
        if (numberOfAnchorNodes > 5) {
            for (int i = 0; i < scene.getChildren().size(); i++) {
                if (scene.getChildren().get(i) instanceof AnchorNode) {
                    scene.getChildren().get(i).setParent(null);
                    numberOfAnchorNodes--;
                    break;
                }
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
