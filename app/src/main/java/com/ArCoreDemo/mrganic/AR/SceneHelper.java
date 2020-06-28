package com.ArCoreDemo.mrganic.AR;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;

import com.ArCoreDemo.mrganic.R;
import com.ArCoreDemo.mrganic.utils.SnackBarHelper;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.collision.Ray;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.PlaneRenderer;
import com.google.ar.sceneform.ux.TransformableNode;

public class SceneHelper {

    private static final String TAG = "SceneHelper";
    private SnackBarHelper snackBarHelper;
    private CustomArFragment fragment;
    private ArSceneView sceneView;
    private PlaneRenderer planeRenderer;
    private Scene scene;
    private Camera camera;
    private ModelRenderable modelRenderable;
    private int numberOfAnchorNodes = 0;


    public SceneHelper(CustomArFragment fragment) {
        this.fragment = fragment;

        this.sceneView = fragment.getArSceneView();
        planeRenderer = sceneView.getPlaneRenderer();

        this.scene = sceneView.getScene();
        this.camera = scene.getCamera();

        snackBarHelper = new SnackBarHelper();
        snackBarHelper
                .showMessage(
                        fragment.getActivity(),
                        fragment.getString(R.string.searchForSurface));
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
        renderDetectedPlane();
    }

    private void renderDetectedPlane() {
        if(!planeRenderer.isVisible()) return;
        else if(numberOfAnchorNodes == 0)
            planeRenderer
                    .getMaterial()
                    .thenAccept(
                            material -> {
                                material
                                    .setFloat(
                                            PlaneRenderer.MATERIAL_SPOTLIGHT_RADIUS, 100f);
                            });
        else planeRenderer.setVisible(false);

        //Used for changing render matrix matrix color
        //planeRenderer.getMaterial().thenAccept(material -> {material.setFloat3(PlaneRenderer.MATERIAL_COLOR, new Color(1f,0f,0f));});
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
            if(!snackBarHelper.isShown()) {
                snackBarHelper.showTimedMessage(fragment.getActivity(), fragment.getActivity().getString(R.string.insideModel));
                ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
            }
        }
    }

    private void showInstructions() {
        if (snackBarHelper.getMessage().equals(fragment.getString(R.string.searchForSurface))) {
            for (Plane plane : sceneView.getSession().getAllTrackables(Plane.class)) {
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

        //Limit object placement on horizontal planes
        if(plane.getTrackingState() == TrackingState.TRACKING && plane.getType() == Plane.Type.HORIZONTAL_UPWARD_FACING) {
            //Creating anchor and a node for the anchor
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(scene);

            //Create transformable object and add it to anchor from above
            TransformableNode object = new TransformableNode(fragment.getTransformationSystem());
            object.setRenderable(modelRenderable);
            object.getScaleController().setMinScale(0.3f);
            object.getScaleController().setMaxScale(0.65f);
            object.setLocalScale(new Vector3(0.2f,0.2f,0.2f));
            object.setParent(anchorNode);
            object.select();
            numberOfAnchorNodes++;
        }


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
    public void setObject(String selectedObject) {

        Log.d(TAG, "URL for object render: " + selectedObject);

        Uri modelURI = Uri.parse(selectedObject);

        RenderableSource source = RenderableSource.builder()
                .setSource(
                        sceneView.getContext(),
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
                            snackBarHelper.showErrorMessage(fragment.getActivity(), "Model not compatible. Try another.");
                            return null;
                        });
    }
}
