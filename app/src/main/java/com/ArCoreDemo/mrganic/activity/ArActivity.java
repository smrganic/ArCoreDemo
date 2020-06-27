package com.ArCoreDemo.mrganic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.ArCoreDemo.mrganic.R;
import com.ArCoreDemo.mrganic.AR.CustomArFragment;
import com.ArCoreDemo.mrganic.AR.SceneHelper;

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
        Button button = findViewById(R.id.btnBack);
        button.setOnClickListener(v -> finish());

        //Get selected object from intent
        Intent intent = getIntent();
        String selectedObject = intent.getStringExtra("fileName");

        //Setting up SceneForm with ArFragment using ArCore
        CustomArFragment fragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        //Delegating rendering away from parent activity
        SceneHelper sceneHelper = new SceneHelper(fragment);
        sceneHelper.renderObject(selectedObject);
    }
}
