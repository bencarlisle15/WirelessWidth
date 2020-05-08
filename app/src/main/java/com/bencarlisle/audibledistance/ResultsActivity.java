package com.bencarlisle.audibledistance;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class ResultsActivity extends Activity {

    private static final int SCAN_FREQUENCY = 1000;
    private Class finderService;
    private static final int UPDATE_FREQUENCY = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encounters);
        Intent intent = getIntent();
        new EncounterDB(this).deleteAllEntries();
        this.finderService = (Class) Objects.requireNonNull(intent.getExtras()).get("finderService");
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
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
        Log.e("RESULTS ACTIVITY", "Updating encounter results " + encounters.size());
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
