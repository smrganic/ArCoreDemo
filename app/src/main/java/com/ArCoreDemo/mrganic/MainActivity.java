package com.ArCoreDemo.mrganic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Button;

import com.ArCoreDemo.mrganic.interfaces.CompletionListener;
import com.ArCoreDemo.mrganic.network.PolyAPI;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;


public class MainActivity extends AppCompatActivity {

    private Button button;

    private static final String TAG = "MainActivityTAG";
    private SceneView sceneView;
    private Scene scene;

    private Uri selectedObject = null;

    private Node node = new Node();

    private PolyAPI polyAPI;
    private String APIKey;

    private Handler backGroundThreadHandler;

    private static final String ID = "9C-MLNfxaor";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
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


    private void init() {

        button = findViewById(R.id.enter3D);

        checkIfPhoneIsArCompatible();

        // Setup scene needed to display models
        sceneView = findViewById(R.id.scene_view);
        scene = sceneView.getScene();

        HandlerThread backgroundThread = new HandlerThread("loaderThread");
        backgroundThread.start();
        backGroundThreadHandler = new Handler(backgroundThread.getLooper());

        // Init the api with key
        APIKey = getString(R.string.apiKey);
        polyAPI = new PolyAPI(APIKey);

        // Call the api
        setAPICall(polyAPI, backGroundThreadHandler);
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
        button.setOnClickListener(v -> {
            //Creates new intent with source -> destination and starts new activity
            if(selectedObject != null){
                Intent i = new Intent(MainActivity.this, ArActivity.class);
                i.putExtra("fileName", selectedObject.toString());
                startActivity(i);
            }
        });
    }


    private void setNonArListeners() {
        button.setOnClickListener(v -> {
            if(selectedObject != null){
                Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
                sceneViewerIntent.setData(selectedObject);
                sceneViewerIntent.setPackage("com.google.android.googlequicksearchbox");
                startActivity(sceneViewerIntent);
            }
        });
    }


    private void setAPICall(PolyAPI polyAPI, Handler handler) {
        polyAPI.GetAsset(ID, handler, new CompletionListener() {
            @Override
            public void onHttpRequestFailure(int status, String message, Exception ex) {
               handleFailure(status, message, ex);
            }

            @Override
            public void onHttpRequestSuccess(byte[] responseBody) {
                parseAsset(responseBody);
            }
        });
    }


    private void handleFailure(int status, String message, Exception ex) {
        Log.e(TAG, "Request failed. Status code " + status + ", message: " + message +
                ((ex != null) ? ", exception: " + ex : ""));
        if (ex != null) ex.printStackTrace();
    }


    private void parseAsset(byte[] responseBody) {

        String assetBody = new String(responseBody, Charset.forName("UTF-8"));

        try {

            JSONObject response = new JSONObject(assetBody);

            String displayName = response.getString("displayName");
            String authorName = response.getString("authorName");
            Log.d(TAG, "Display name: " + displayName);
            Log.d(TAG, "Author name: " + authorName);

            JSONArray formats = response.getJSONArray("formats");
            for (int i = 0; i < formats.length(); i++) {
                JSONObject format = formats.getJSONObject(i);

                if (format.getString("formatType").equals("GLTF2")) {
                    runOnUiThread(() -> {
                        try {
                            renderObject(format.getJSONObject("root").getString("url"));
                        }
                        catch (JSONException jsonException) {
                            Log.e(TAG, "JSON parsing error while processing response: " + jsonException);
                            jsonException.printStackTrace();
                        }
                    });
                }
            }
        }
        catch (JSONException jsonException) {
            Log.e(TAG, "JSON parsing error while processing response: " + jsonException);
            jsonException.printStackTrace();
        }
    }


    private void renderObject(String modelUrl) {

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
                .thenAccept(this::updateNode);

        selectedObject = Uri.parse(modelUrl);
        Log.d(TAG, "selectedObjectURI" + modelUrl);
    }


    private void updateNode(ModelRenderable modelRenderable) {
        node.setRenderable(modelRenderable);
        node.setParent(scene);
        node.setWorldPosition(new Vector3(0f,-0.35f,-1f));
    }
}
