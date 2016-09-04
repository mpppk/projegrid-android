package com.projegrid.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, BadgeService.class));
        Toast.makeText(getApplicationContext(), "Welcome to projegrid!", Toast.LENGTH_LONG).show();
        finish();
    }

}
