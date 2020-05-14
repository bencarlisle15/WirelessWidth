package com.bencarlisle.wirelesswidth;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            RelativeLayout buttons = findViewById(R.id.buttons);
            EncounterDB encounterDB = new EncounterDB(this);
            String hotspotString = "Please copy the below text into Settings->Wi-Fi & internet->Hotspot & tethering->Wi-Fi Hotspot->Hotspot name";
            String tracerId = encounterDB.getIdString();

            TextView hotspotName = new TextView(this);
            Button copyButton = new Button(this);

            hotspotName.setId(R.id.hotspot_text);
            copyButton.setId(R.id.hotspot_copy);

            RelativeLayout.LayoutParams belowElement = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            belowElement.addRule(RelativeLayout.BELOW, R.id.wifiaware);
            belowElement.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            hotspotName.setLayoutParams(belowElement);

            belowElement = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            belowElement.addRule(RelativeLayout.BELOW, R.id.hotspot_text);
            belowElement.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            copyButton.setLayoutParams(belowElement);

            hotspotName.setText(hotspotString);
            copyButton.setText(tracerId);
            copyButton.setOnClickListener(this);

            buttons.addView(hotspotName);
            buttons.addView(copyButton);

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    public void hotspotMode(View view) {
        startActivityAndService(0);
    }

    public void wifiDirectMode(View view) {
        startActivityAndService(1);
    }

    public void wifiAwareMode(View view) {
        startActivityAndService(2);
    }

    private void startActivityAndService(int code) {
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("finderCode", code);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Hotspot name", ((Button) v).getText());
        Objects.requireNonNull(clipboard).setPrimaryClip(clip);
    }
}

