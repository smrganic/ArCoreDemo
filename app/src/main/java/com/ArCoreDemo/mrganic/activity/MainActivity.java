package com.ArCoreDemo.mrganic.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ArCoreDemo.mrganic.R;
import com.ArCoreDemo.mrganic.recycler.Item;
import com.ArCoreDemo.mrganic.recycler.ItemAdapter;
import com.ArCoreDemo.mrganic.retrofit.PolyAPI;
import com.ArCoreDemo.mrganic.utils.Parser;
import com.ArCoreDemo.mrganic.retrofit.PolyResponse;
import com.ArCoreDemo.mrganic.utils.Utility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button button3D;
    private Button buttonSearch;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private String selectedObject;

    private String APIKey;
    private Call<PolyResponse> PolyAPICall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        APIKey = getString(R.string.apiKey);
        callAPIWithKeyword("");
        setupRecycler();
        setupButtons();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void setupRecycler() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(layoutManager);
    }


    private void setupButtons() {
        button3D = findViewById(R.id.btnEnter3D);
        buttonSearch = findViewById(R.id.btnSearchPoly);
        if(Utility.ArCompatible(this)) { setARListeners(); }
        else { setNonArListeners(); }
        buttonSearch.setOnClickListener(this::onSearch);
    }


    private void setARListeners() {
        button3D.setOnClickListener(v -> {
            //Creates new intent with source -> destination and starts new activity
            if(adapter != null){
                selectedObject = adapter.getSelected().getModelUrl();
                Intent arIntent = new Intent(MainActivity.this, ArActivity.class);
                arIntent.putExtra("fileName", selectedObject);
                startActivity(arIntent);
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
            if(adapter != null){
                selectedObject = adapter.getSelected().getModelUrl();
                Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
                sceneViewerIntent.setData(Uri.parse(selectedObject));
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
                .inflate(R.layout.search_popup, (ViewGroup) view.getParent(), false);

        EditText editText = search.findViewById(R.id.keyword);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Search 3D Models")
                .setView(search)
                .setPositiveButton("Search", (dialog, which) -> {
                    String keyword = editText.getText().toString();
                    callAPIWithKeyword(keyword);
                })
                .setCancelable(true)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();

        //Spent way too much time on this
        //This auto selects the edit text
        //and shows keyboard
        editText.requestFocus();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    private void callAPIWithKeyword(String keyword) {

        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme("https")
                .authority("poly.googleapis.com")
                .appendPath("v1")
                .appendPath("assets")
                .appendQueryParameter("key", APIKey)
                .appendQueryParameter("curated", Boolean.toString(true))
                .appendQueryParameter("format", "GLTF2")
                .appendQueryParameter("pageSize", "40");

        if(keyword != null && !keyword.isEmpty()){
            uriBuilder.appendQueryParameter("keywords", keyword);
        }

        String url = uriBuilder.build().toString();

        Log.d(TAG, "Url for data: " + url);

        PolyAPICall = PolyAPI.getApiInterface().getListAssets(url);
        APICallPolyResponse();
    }

    private void APICallPolyResponse() {
        PolyAPICall.enqueue(new Callback<PolyResponse>() {
            @Override
            public void onResponse(Call<PolyResponse> call, Response<PolyResponse> response) {
                if(response.isSuccessful()) {
                    if(response.body().isEmpty()){
                        Log.d(TAG, "Nothing on poly for that keyword");
                        Toast toast = Toast.makeText(MainActivity.this, getString(R.string.nothingForKeyword), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    else {
                        List<Item> items = Parser.parseListAssets(response.body());
                        adapter = new ItemAdapter(items);
                        adapter.setSelected(items.get(0));
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<PolyResponse> call, Throwable t) {
                Log.d(TAG, "Retrofit failed");
                t.printStackTrace();

                Toast toast = Toast.makeText(MainActivity.this, "Network request failed. Please try again.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }
}
