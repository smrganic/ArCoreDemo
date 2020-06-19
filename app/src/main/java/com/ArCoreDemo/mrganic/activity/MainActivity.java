package com.ArCoreDemo.mrganic.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ArCoreDemo.mrganic.R;
import com.ArCoreDemo.mrganic.interfaces.CallBackListener;
import com.ArCoreDemo.mrganic.recycler.Item;
import com.ArCoreDemo.mrganic.recycler.ItemAdapter;
import com.ArCoreDemo.mrganic.retrofit.PolyAPI;
import com.ArCoreDemo.mrganic.utils.Parser;
import com.ArCoreDemo.mrganic.retrofit.PolyResponse;
import com.ArCoreDemo.mrganic.utils.Utility;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    private static final String LAYOUT_MANAGER_STATE = "LAYOUT_MANAGER_STATE";

    private Button button3D;
    private Button buttonSearch;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private GridLayoutManager layoutManager;
    private String selectedObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupAPI();
        setupRecycler();
        setupButtons();
    }

    private void setupAPI() {
        PolyAPI.setAPIKey(getString(R.string.apiKey));

        PolyAPI.callAPIWithKeyword("");

        PolyAPI.setCallBackListener(new CallBackListener() {
            @Override
            public void successfulResponse(PolyResponse response) {
                if(response.isEmpty()){
                    Log.d(TAG, "Nothing on poly for that keyword");
                    Toast toast = Toast.makeText(MainActivity.this, getString(R.string.nothingForKeyword), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                else {
                    List<Item> items = Parser.parseListAssets(response);
                    adapter = new ItemAdapter(items);
                    adapter.setSelected(items.get(0));
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void failedResponse(Exception ex) {
                Log.d(TAG, "Retrofit failed");
                if(ex != null) ex.printStackTrace();

                Toast toast = Toast.makeText(MainActivity.this, "Network request failed. Please try again.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }


    private void setupRecycler() {
        layoutManager = new GridLayoutManager(this, 2);
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
                    PolyAPI.callAPIWithKeyword(keyword);
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
}
