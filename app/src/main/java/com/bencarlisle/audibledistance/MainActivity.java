package com.bencarlisle.audibledistance;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            RelativeLayout buttons = findViewById(R.id.buttons);

            String hotspotString = "Please copy the below text into Settings->Wi-Fi & internet->Hotspot & tethering->Wi-Fi Hotspot->Hotspot name";
            String tracerId = "contact_tracer_3133731337";

            TextView hotspotName = new TextView(this);
            Button copyButton = new Button(this);

            hotspotName.setId(R.id.hotspot_text);
            copyButton.setId(R.id.hotspot_copy);

            RelativeLayout.LayoutParams belowElement = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            belowElement.addRule(RelativeLayout.BELOW, R.id.wifidirect);
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
        startActivityAndService(HotspotService.class);
    }

    public void wifiDirectMode(View view) {
        startActivityAndService(WifiDirectService.class);
    }


    private void startActivityAndService(Class newClass) {
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("finderService", newClass);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Hotspot name", ((Button) v).getText());
        clipboard.setPrimaryClip(clip);
    }
}

