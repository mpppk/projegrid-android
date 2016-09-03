package com.projegrid.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.projegrid.mobile.gridapp.parser.GridAppParser;
import com.projegrid.mobile.gridapp.parser.YTrainGridAppParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReceiveGridAppActivity extends AppCompatActivity {
    private String TAG = "ReceiveGridAppActivity";

    private List<GridAppParser> gridAppParsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_grid_app);

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
