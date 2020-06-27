package com.ArCoreDemo.mrganic.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ArCoreDemo.mrganic.R;
import com.ArCoreDemo.mrganic.retrofit.interfaces.CallBackListener;
import com.ArCoreDemo.mrganic.recycler.Item;
import com.ArCoreDemo.mrganic.recycler.ItemAdapter;
import com.ArCoreDemo.mrganic.retrofit.PolyAPI;
import com.ArCoreDemo.mrganic.retrofit.POJO.PolyResponse;
import com.ArCoreDemo.mrganic.retrofit.PolyParser;
import com.ArCoreDemo.mrganic.utils.SnackBarHelper;
import com.ArCoreDemo.mrganic.utils.Utility;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    private static final String LAYOUT_MANAGER_STATE = "LAYOUT_MANAGER_STATE";

    private Button buttonAR;
    private Button buttonSearch;

    private RecyclerView recyclerView;
    private ItemAdapter adapter;

    private SnackBarHelper snackBarHelper;

    private String selectedObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        snackBarHelper = new SnackBarHelper();
        setupAPI();
        setupRecycler();
        setupButtons();
    }

    private void setupAPI() {

        snackBarHelper.showTimedMessage(MainActivity.this, getString(R.string.searchingModels));

        CallBackListener onCallBack = new CallBackListener() {
            @Override
            public void successfulResponse(PolyResponse polyResponse) {
                if (polyResponse.isEmpty()) {
                    Log.d(TAG, "Nothing on poly for that keyword");
                    snackBarHelper.showTimedMessage(
                            MainActivity.this,
                            getString(R.string.nothingForKeyword));
                } else {
                    List<Item> items = PolyParser.parseListAssets(polyResponse);
                    adapter = new ItemAdapter(items);
                    adapter.setSelected(items.get(0));
                    recyclerView.setAdapter(adapter);
                }
            }
            @Override
            public void failedResponse(Exception ex) {
                Log.d(TAG, "Retrofit failed");
                if (ex != null) ex.printStackTrace();

                snackBarHelper.showMessage(
                                MainActivity.this,
                                getString(R.string.networkError));
            }
        };
        PolyAPI.setCallBackListener(onCallBack);
        PolyAPI.callAPI();
    }


    private void setupRecycler() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(layoutManager);
    }


    private void setupButtons() {
        buttonAR = findViewById(R.id.btnEnterAR);
        buttonSearch = findViewById(R.id.btnBack);
        if (Utility.ArCompatible(MainActivity.this)) {
            setARListeners();
        } else {
            setNonARListeners();
        }
        buttonSearch.setOnClickListener(this::onSearch);
    }


    private void setARListeners() {
        buttonAR.setOnClickListener(v -> {
            //Creates new intent with source -> destination and starts new activity
            if (adapter != null) {
                selectedObject = adapter.getSelected().getModelUrl();
                Intent arIntent = new Intent(MainActivity.this, ArActivity.class);
                arIntent.putExtra("fileName", selectedObject);
                startActivity(arIntent);
            } else {
                snackBarHelper.showTimedMessage(this, getString(R.string.noModels));
            }
        });
    }


    private void setNonARListeners() {
        buttonAR.setOnClickListener(v -> {
            if (adapter != null) {
                selectedObject = adapter.getSelected().getModelUrl();
                Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
                sceneViewerIntent.setData(Uri.parse(selectedObject));
                sceneViewerIntent.setPackage("com.google.android.googlequicksearchbox");
                startActivity(sceneViewerIntent);
            } else {
                snackBarHelper.showTimedMessage(this, getString(R.string.noModels));
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
                    snackBarHelper.showTimedMessage(
                            MainActivity.this,
                            getString(R.string.searchingModels));
                    PolyAPI.callAPI(keyword, 40);
                })
                .setCancelable(true)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();
        //This shows the keyboard on dialog popup
        alertDialog.getWindow()
                .setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


        /*
        Spent way too much time on this but it auto selects the edit text
        Then notifies the user and runs a search when enter is pressed
        on the keyboard
        */
        editText.requestFocus();
        editText.setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                snackBarHelper.showTimedMessage(
                        MainActivity.this,
                        getString(R.string.searchingModels));
                PolyAPI.callAPI(editText.getText().toString());
                alertDialog.dismiss();
                return true;
            }
            return false;
        });
        alertDialog.show();
    }
}
