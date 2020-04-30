package com.bencarlisle.audibledistance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startOmit(View view) {
        Intent intent = new Intent(this, OmitActivity.class);
        startActivity(intent);
    }

    public void startRecord(View view) {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);    }
}
