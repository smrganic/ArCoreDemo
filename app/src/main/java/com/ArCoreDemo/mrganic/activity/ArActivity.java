package com.ArCoreDemo.mrganic.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.ArCoreDemo.mrganic.R;
import com.ArCoreDemo.mrganic.CustomArFragment;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;

public class ArActivity extends AppCompatActivity {

    private static final String TAG = "ArActivity";
    private ModelRenderable modelRenderable;
    private CustomArFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void init() {

        //Get selected object from intent
        Intent intent = getIntent();
        Uri selectedObject = Uri.parse(intent.getStringExtra("fileName"));

        //Setting up SceneForm with ArFragment using ArCore
        fragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        RenderableSource source = RenderableSource.builder().setSource(fragment.getArSceneView().getContext(),
                selectedObject, RenderableSource.SourceType.GLTF2)
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

        fragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            if(modelRenderable == null) return;

            //Creating anchor
            Anchor anchor = hitResult.createAnchor();
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(fragment.getArSceneView().getScene());

            //Create transformable object and add it to anchor from above
            TransformableNode object = new TransformableNode(fragment.getTransformationSystem());
            object.setParent(anchorNode);
            object.setRenderable(modelRenderable);
            object.select();
        });
    }
}
