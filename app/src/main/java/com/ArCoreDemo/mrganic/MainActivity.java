package com.ArCoreDemo.mrganic;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ArCoreDemo.mrganic.interfaces.CompletionListener;
import com.ArCoreDemo.mrganic.network.Parser;
import com.ArCoreDemo.mrganic.network.PolyAPI;
import com.ArCoreDemo.mrganic.recycler.item;
import com.ArCoreDemo.mrganic.recycler.itemAdapter;
import com.ArCoreDemo.mrganic.retrofit.IAPICallPoly;
import com.ArCoreDemo.mrganic.retrofit.PolyObject;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity {

    private Button button3D;
    private Button buttonSearch;
    private RecyclerView recyclerView;

    private static final String TAG = "MainActivityTAG";
    private SceneView sceneView;
    private Scene scene;

    private Uri selectedObject = null;

    private Node node = new Node();

    private String APIKey;
    private Call<PolyObject> PolyAPI;

    private static final String ID = "9C-MLNfxaor";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupButtons();
        setupRecycler();
        setupScene();
    }


    @Override
    protected void onResume() {
        super.onResume();
        try { sceneView.resume(); }
        catch (CameraNotAvailableException e){
            Log.e(TAG, "Something went wrong on resume " + e);
            e.printStackTrace();
        }
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


    private void setupButtons() {
        button3D = findViewById(R.id.btnEnter3D);
        buttonSearch = findViewById(R.id.btnSearchPoly);
        checkIfPhoneIsArCompatible();
        buttonSearch.setOnClickListener(this::onSearch);
    }


    private void checkIfPhoneIsArCompatible() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if(availability.isTransient()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkIfPhoneIsArCompatible();
                }
            }, 200);
        }
        if(availability.isSupported()){
            setARListeners();
        }
        else {
            setNonArListeners();
        }
    }


    private void setARListeners() {
        button3D.setOnClickListener(v -> {
            //Creates new intent with source -> destination and starts new activity
            if(selectedObject != null){
                Intent i = new Intent(MainActivity.this, ArActivity.class);
                i.putExtra("fileName", selectedObject.toString());
                startActivity(i);
            }
            else {
                Toast t = Toast.makeText(this, R.string.noModels, Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();

            }
        });
    }


    private void setNonArListeners() {
        button3D.setOnClickListener(v -> {
            if(selectedObject != null){
                Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
                sceneViewerIntent.setData(selectedObject);
                sceneViewerIntent.setPackage("com.google.android.googlequicksearchbox");
                startActivity(sceneViewerIntent);
            }
            else {
                Toast t = Toast.makeText(this, R.string.noModels, Toast.LENGTH_SHORT);
                t.setGravity(Gravity.CENTER, 0, 0);
                t.show();

            }
        });
    }

    //Creates a new search dialog and calls Poly API using keyword
    private void onSearch(View view) {
        View search = this
                .getLayoutInflater()
                .inflate(R.layout.searchpopup, (ViewGroup) view.getParent(), false);

        EditText editText = search.findViewById(R.id.keyword);

        new AlertDialog.Builder(this)
                .setTitle("Search 3D Models")
                .setView(search)
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String keyword = editText.getText().toString();
                        callAPIWithKeyword(keyword);
                    }
                })
                .setCancelable(true)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void callAPIWithKeyword(String keyword) {

        APIKey = getString(R.string.apiKey);

        /*Uri.Builder urlBuilder = new Uri.Builder()
                .scheme("https")
                .authority("poly.googleapis.com")
                .appendPath("v1")
                .appendPath("assets")
                .appendQueryParameter("key", APIKey)
                .appendQueryParameter("curated", Boolean.toString(true))
                .appendQueryParameter("format", "GLTF2")
                .appendQueryParameter("pageSize", "10");*/

        String url = new Uri.Builder()
                .scheme("https")
                .authority("poly.googleapis.com")
                .appendPath("v1")
                .appendPath("assets")
                .appendPath(ID)
                .appendQueryParameter("key", APIKey)
                .build().toString();

        Log.d(TAG, "Url for data: " + url);

        PolyAPI = com.ArCoreDemo.mrganic.retrofit.PolyAPI.getApiInterface(APIKey).getAsset(url);

        APICallPolyResponse();
    }

    private void APICallPolyResponse() {
        PolyAPI.enqueue(new Callback<PolyObject>() {
            @Override
            public void onResponse(Call<PolyObject> call, Response<PolyObject> response) {
                if(response.isSuccessful()){
                    renderObject(Parser.parseAsset(response.body()));
                }
            }

            @Override
            public void onFailure(Call<PolyObject> call, Throwable t) {
                Log.d(TAG, "Retrofit failed");
            }
        });
    }


    private void setupRecycler() {
        recyclerView = findViewById(R.id.recycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

    }


    private void setupScene() {
        // Setup scene needed to display models
        sceneView = findViewById(R.id.scene_view);
        scene = sceneView.getScene();
        sceneView.setOnClickListener(this::onSceneTouch);
    }

    private void onSceneTouch(View view) {
        if(recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() != 0) {
            String modelUrl = ((itemAdapter) recyclerView.getAdapter()).getSelected().getModelUrl();
            selectedObject = Uri.parse(modelUrl);
            renderObject(modelUrl);
        }
    }

    private void renderObject(String modelUrl) {

        Log.d(TAG, "URL for object render: " + modelUrl);

        RenderableSource source = RenderableSource
                .builder()
                .setSource(sceneView.getContext(), Uri.parse(modelUrl), RenderableSource.SourceType.GLTF2)
                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                .setScale(0.25f)
                .build();

        ModelRenderable
                .builder()
                .setSource(sceneView.getContext(), source)
                .build()
                .thenAccept(MainActivity.this::updateNode);

        selectedObject = Uri.parse(modelUrl);
        Log.d(TAG, "selectedObjectURI " + modelUrl);
    }


    private void updateNode(ModelRenderable modelRenderable) {
        node.setRenderable(modelRenderable);
        node.setParent(scene);
        node.setWorldPosition(new Vector3(0f,-0.35f,-1f));
    }
}
