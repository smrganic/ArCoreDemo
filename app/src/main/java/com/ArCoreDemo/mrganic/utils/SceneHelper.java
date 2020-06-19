package com.ArCoreDemo.mrganic.utils;

import android.net.Uri;
import android.util.Log;
import com.ArCoreDemo.mrganic.CustomArFragment;
import com.google.ar.core.Anchor;
import com.google.ar.core.Plane;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.List;

public class SceneHelper {

    private static final String TAG = "SceneHelper";

    private CustomArFragment fragment;
    private Anchor anchor;
    private ModelRenderable modelRenderable;
    private Scene scene;


    public SceneHelper(CustomArFragment fragment) {
        this.fragment = fragment;
        this.scene = fragment.getArSceneView().getScene();
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

            int count = 0;
            for(int i=0;i<scene.getChildren().size();i++){
                if(scene.getChildren().get(i) instanceof AnchorNode) count++;
            }

            if(count > 5){
                for(int i = 0; i < scene.getChildren().size(); i++) {
                    if(scene.getChildren().get(i) instanceof AnchorNode) {
                        ((AnchorNode) scene.getChildren().get(i)).setParent(null);
                        break;
                    }
                }
            }
        });

        //This runs on every frame
        fragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            //
            if(anchor == null){
                for(Plane plane : fragment.getArSceneView().getSession().getAllTrackables(Plane.class)){

                    //Second if because sometimes two planes would exist at the same time

                    if(anchor == null){
                        if(plane.getTrackingState() == TrackingState.TRACKING && plane.getType().equals(Plane.Type.HORIZONTAL_UPWARD_FACING)){
                            anchor = plane.createAnchor(plane.getCenterPose());
                            AnchorNode anchorNode = new AnchorNode(anchor);
                            anchorNode.setParent(fragment.getArSceneView().getScene());

                            TransformableNode object = new TransformableNode(fragment.getTransformationSystem());
                            object.setRenderable(modelRenderable);
                            object.getScaleController().setMinScale(0.3f);
                            object.getScaleController().setMaxScale(0.7f);
                            object.setParent(anchorNode);
                            object.select();
                        }
                    }
                    else break;

                }
            }
        });
    }

    //Getting GLTF2 object from the internet using URI and constructing modelRenderable
    public void renderObject(Uri selectedObject) {

        Log.d(TAG, "URL for object render: " + selectedObject.toString());

        RenderableSource source = RenderableSource.builder()
                .setSource(
                        fragment.getArSceneView().getContext(),
                        selectedObject,
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
