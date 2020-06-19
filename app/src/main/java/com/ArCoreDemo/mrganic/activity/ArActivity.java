package com.ArCoreDemo.mrganic.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import com.ArCoreDemo.mrganic.R;
import com.ArCoreDemo.mrganic.CustomArFragment;
import com.ArCoreDemo.mrganic.utils.SceneHelper;

public class ArActivity extends AppCompatActivity {

    private static final String TAG = "ArActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        init();
    }

    private void init() {

        //Initialising back button
        Button button = findViewById(R.id.btnSearchPoly);
        button.setOnClickListener(v -> finish());

        //Get selected object from intent
        Intent intent = getIntent();
        Uri selectedObject = Uri.parse(intent.getStringExtra("fileName"));

        //Setting up SceneForm with ArFragment using ArCore
        CustomArFragment fragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        //Delegating rendering away from parent activity
        SceneHelper sceneHelper = new SceneHelper(fragment);
        sceneHelper.renderObject(selectedObject);
    }
}
