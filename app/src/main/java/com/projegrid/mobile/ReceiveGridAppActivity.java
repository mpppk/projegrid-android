package com.projegrid.mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.projegrid.mobile.gridapp.model.GridAppModel;
import com.projegrid.mobile.gridapp.parser.GridAppParser;
import com.projegrid.mobile.gridapp.parser.YTrainGridAppParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReceiveGridAppActivity extends AppCompatActivity {
    private String TAG = "ReceiveGridAppActivity";

    private List<GridAppParser> gridAppParsers;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    SharedPreferences prefer;
    private String screenIdQueryKey = "screenId";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefer = getSharedPreferences("screen", MODE_PRIVATE);

        gridAppParsers = createParsers();

        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_SEND.equals(action)) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                CharSequence ext = extras.getCharSequence(Intent.EXTRA_TEXT);
                if (ext != null) {
                    // 文字列 or どのアプリから送られてきたかによって処理を分ける
                    try {
                        GridAppParser gridAppParser =  GridAppParser.chooseParser(gridAppParsers, (String) ext);
                        Log.d(TAG, gridAppParser.parse());
                        GridAppModel model = gridAppParser.createModel();
                        // firebaseにjsonを格納
                        // screens/screenID/grid1
                        String screenId = prefer.getString(screenIdQueryKey, "not found");
                        if(screenId.equals("not found")){
                            Log.d(TAG, "screen id not found(key: " + screenIdQueryKey + ")");
                        }
                        Log.d(TAG, "grid path: " + "screens/" + screenId + "/grid1");
                        DatabaseReference screensRef = database.getReference("screens/" + screenId + "/grid1");
                        screensRef.setValue(model);
                        Toast.makeText(getApplicationContext(), "Send to projegrid", Toast.LENGTH_LONG).show();
                        finish();
                    } catch (IOException e) {
                        Log.w(TAG, "Grid App用文字列のparseに失敗しました。");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private List<GridAppParser> createParsers(){
        List<GridAppParser> parsers = new ArrayList<>();
        parsers.add(new YTrainGridAppParser());
        return parsers;
    }
}
