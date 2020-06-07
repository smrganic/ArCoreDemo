package com.ArCoreDemo.mrganic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ArCoreDemo.mrganic.interfaces.CompletionListener;
import com.ArCoreDemo.mrganic.network.PolyAPI;
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

import java.net.URI;
import java.nio.charset.Charset;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityTAG";
    private SceneView sceneView;
    private Scene scene;

    private Uri selectedObject;

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
        setListeners();
        setAPICall(polyAPI, backGroundThreadHandler);
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
                        } catch (JSONException jsonException) {
                            Log.e(TAG, "JSON parsing error while processing response: " + jsonException);
                            jsonException.printStackTrace();
                        }
                    });
                    renderObject(format.getJSONObject("root").getString("url"));
                    break;
                }
            }
        } catch (JSONException jsonException) {
            Log.e(TAG, "JSON parsing error while processing response: " + jsonException);
            jsonException.printStackTrace();
        }
    }

    private void renderObject(String modelUrl) {
        RenderableSource source = RenderableSource.builder().setSource(sceneView.getContext(),
                Uri.parse(modelUrl), RenderableSource.SourceType.GLTF2)
                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                .build();
        ModelRenderable.builder().setSource(sceneView.getContext(), source).build()
                .thenAccept(this::updateNode);
    }

    private void handleFailure(int status, String message, Exception ex) {
        Log.e(TAG, "Request failed. Status code " + status + ", message: " + message +
                ((ex != null) ? ", exception: " + ex : ""));
        if (ex != null) ex.printStackTrace();
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
