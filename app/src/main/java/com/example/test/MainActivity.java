package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;


public class MainActivity extends AppCompatActivity {

    private SceneView sceneView;
    private Scene scene;
    private Uri selectedObject;
    private Node node = new Node();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            sceneView.resume();
        }
        catch (CameraNotAvailableException e){}
    }

    @Override
    protected void onPause() {
        super.onPause();
        sceneView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sceneView.destroy();
    }

    private void init() {

        sceneView = findViewById(R.id.scene_view);
        scene = sceneView.getScene();
        setListeners();
        modelPath("LampPost.sfb");
    }

    private void setListeners() {
        ImageView ivLamp = findViewById(R.id.ivLamp);
        ivLamp.setOnClickListener(v -> modelPath("LampPost.sfb"));

        ImageView ivColiseum = findViewById(R.id.ivColiseum);
        ivColiseum.setOnClickListener(v -> modelPath("coliseum.sfb"));

        ImageView ivFlowers = findViewById(R.id.ivflowers);
        ivFlowers.setOnClickListener(v -> modelPath("flowers.sfb"));

        ImageView ivGamingPc = findViewById(R.id.ivGamingPc);
        ivGamingPc.setOnClickListener(v -> modelPath("gamingpc.sfb"));

        Button enterAr = findViewById(R.id.enterAr);
        enterAr.setOnClickListener(v -> openArActivity());
    }

    private void openArActivity(){
        //Creates new intent with source -> destination and starts new activity
        Intent i = new Intent(MainActivity.this, ArActivity.class);
        i.putExtra("fileName", selectedObject.toString());
        startActivity(i);
    }

    private void modelPath(String name){
        selectedObject = Uri.parse(name);
        Toast t = Toast.makeText(this, name, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER_VERTICAL, 0,0);
        t.show();
        updateView();
    }

    private void updateView() {
        ModelRenderable.builder()
                .setSource(sceneView.getContext(), selectedObject)
                .build()
                .thenAccept(this::updateNode)
                .exceptionally(
                        throwable -> {
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                            return null;
                        });
    }

    private void updateNode(ModelRenderable modelRenderable) {
        node.setRenderable(modelRenderable);
        node.setParent(scene);
        node.setWorldPosition(new Vector3(0f,-0.35f,-1f));
        node.setLocalRotation(Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), -160f));
    }
}
