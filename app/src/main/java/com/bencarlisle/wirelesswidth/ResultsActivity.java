package com.bencarlisle.wirelesswidth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Objects;

public class ResultsActivity extends Activity {

    private static final int SCAN_FREQUENCY = 10;
    private Class finderService;
    private static final int UPDATE_FREQUENCY = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encounters);
        TextView mainText = findViewById(R.id.main_text);
        Intent intent = getIntent();
        int finderCode = Objects.requireNonNull(intent.getExtras()).getInt("finderCode");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String resultsText = "Location Permission is required";
            mainText.setText(resultsText);
            return;
        }
        switch (finderCode) {
            case 0:
                if (getSystemService(Context.WIFI_SERVICE) == null) {
                    mainText.setText(R.string.no_support);
                    return;
                }
                mainText.setText(R.string.hotspot_mode);
                this.finderService = HotspotService.class;
                break;
            case 1:
                if (getSystemService(Context.WIFI_P2P_SERVICE) == null) {
                    mainText.setText(R.string.no_support);
                    return;
                }
                mainText.setText(R.string.wifidirect_mode);
                this.finderService = WifiDirectService.class;
                break;
            default:
                if (getSystemService(Context.WIFI_AWARE_SERVICE) == null || android.os.Build.VERSION.SDK_INT < 28 ){
                    mainText.setText(R.string.no_support);
                    return;
                }
                mainText.setText(R.string.wifiaware_mode);
                this.finderService = WifiAwareService.class;
                break;
        }
        new EncounterDB(this).deleteAllEntries();
        new Thread(this::startServicePeriodically).start();
        new Thread(this::addEncounterResultsPeriodically).start();
    }

    private void startServicePeriodically() {
        while (true) {
            startService(0);
            startService(1);
            try {
                Thread.sleep(SCAN_FREQUENCY * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void startService(int methodType) {
        Intent intent = new Intent(this, finderService);
        intent.putExtra("methodCode", methodType);
        startForegroundService(intent);
    }


    private void addEncounterResultsPeriodically() {
        while (true) {
            runOnUiThread(this::addEncounterResults);
            try {
                Thread.sleep(UPDATE_FREQUENCY * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("InflateParams")
    private void addEncounterResults() {
        EncounterDB encounterDB = new EncounterDB(this);
        ArrayList<Encounter> encounters = encounterDB.getAllEncounters();
        Log.e("ResultsActivity", "Updating encounter results " + encounters.size());
        LinearLayout encountersLayout = findViewById(R.id.encounters);
        encountersLayout.removeAllViews();
        for (Encounter encounter: encounters) {
            View newView = LayoutInflater.from(this).inflate(R.layout.encounter, null);
            TextView text = newView.findViewById(R.id.encounter_id);
            text.setText(encounter.getId());
            TextView timestamp = newView.findViewById(R.id.timestamp);
            timestamp.setText(encounter.getTimestamp());
            encountersLayout.addView(newView);
        }
    }
}
