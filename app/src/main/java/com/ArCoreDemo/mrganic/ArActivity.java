package com.ArCoreDemo.mrganic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;

public class ArActivity extends AppCompatActivity {

    private ModelRenderable modelRenderable;
    private customArFragment fragment;

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
        fragment = (customArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        ModelRenderable.builder()
                .setSource(this, selectedObject)
                .build()
                .thenAccept(object3D -> modelRenderable = object3D)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
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
