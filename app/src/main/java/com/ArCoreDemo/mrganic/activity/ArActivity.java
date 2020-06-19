package com.ArCoreDemo.mrganic.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.ArCoreDemo.mrganic.R;
import com.ArCoreDemo.mrganic.CustomArFragment;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class ArActivity extends AppCompatActivity {

    private static final String TAG = "ArActivity";
    private ModelRenderable modelRenderable;
    private CustomArFragment fragment;
    private Button button;
    private Plane plane;
    private Anchor anchor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fragment.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fragment.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fragment.onPause();
    }

    private void init() {

        button = findViewById(R.id.btnSearchPoly);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent arIntent = new Intent(ArActivity.this, MainActivity.class);
                startActivity(arIntent);
            }
        });

        //Get selected object from intent
        Intent intent = getIntent();
        Uri selectedObject = Uri.parse(intent.getStringExtra("fileName"));

        //Setting up SceneForm with ArFragment using ArCore
        fragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        RenderableSource source = RenderableSource.builder()
                .setSource(
                        fragment.getArSceneView().getContext(),
                        selectedObject,
                        RenderableSource.SourceType.GLTF2)
                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                .build();

        ModelRenderable.builder()
                .setSource(this, source)
                .build()
                .thenAccept(object3D -> modelRenderable = object3D)
                .exceptionally(
                        throwable -> {
                            Log.d(TAG, "Failed to load ModelRenderable");
                            return null;
                        });

        fragment.getArSceneView().getScene().addOnUpdateListener(new Scene.OnUpdateListener() {
            @Override
            public void onUpdate(FrameTime frameTime) {
                if(anchor == null){
                    for(Plane plane : fragment.getArSceneView().getSession().getAllTrackables(Plane.class)){
                        if(plane.getTrackingState() == TrackingState.TRACKING){
                            anchor = plane.createAnchor(plane.getCenterPose());
                            AnchorNode anchorNode = new AnchorNode(anchor);
                            anchorNode.setParent(fragment.getArSceneView().getScene());

                            //Create transformable object and add it to anchor from above
                            TransformableNode object = new TransformableNode(fragment.getTransformationSystem());
                            object.setRenderable(modelRenderable);
                            object.getScaleController().setMinScale(0.3f);
                            object.getScaleController().setMaxScale(0.7f);
                            object.setParent(anchorNode);
                            object.select();
                        }
                    }
                }
            }
        });


        fragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            if(modelRenderable == null) return;

            //Creating anchor
            anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(fragment.getArSceneView().getScene());

            //Create transformable object and add it to anchor from above
            TransformableNode object = new TransformableNode(fragment.getTransformationSystem());
            object.setRenderable(modelRenderable);
            object.getScaleController().setMinScale(0.3f);
            object.getScaleController().setMaxScale(0.7f);
            object.setParent(anchorNode);
            object.select();
    });
    }
}
