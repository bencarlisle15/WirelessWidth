package com.bencarlisle.audibledistance;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

public class HotspotService extends TracerService {


    private void parseResult(ScanResult scanResult) {
        String ssid = scanResult.SSID;
        Matcher matcher = pattern.matcher(ssid);
        if (!matcher.matches()) {
            //not a contact tracer;
            return;
        }
        double distance = getDistance(scanResult.level, scanResult.frequency);
        if (distance > MAX_DISTANCE) {
            //over 6ft away
            return;
        }
        long id = Long.parseLong(Objects.requireNonNull(matcher.group(1)));
        Log.e("HOTSPOT SERVICE", "Adding id: " + id);
        EncounterDB encounterDB = new EncounterDB(this);
        encounterDB.addToDatabase(id);
    }

    void findContacts() {
//        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//        assert wifiManager != null;
//        List<ScanResult> scanResults = wifiManager.getScanResults();
//        Log.e("HOTSPOT SERVICE", "Found " + scanResults.size() + " results");
//        for (ScanResult scanResult: scanResults) {
//            parseResult(scanResult);
//        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void omit() {
//        Log.e("START", "OMIT");
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    wifiManager.start
        //        wifiManager.ap
//        assert wifiManager != null;
//        HotspotListener hotspotListener = new HotspotListener();
//        wifiManager.startLocalOnlyHotspot(hotspotListener, new Handler());
////        turnOnHotspot();
    }
}
