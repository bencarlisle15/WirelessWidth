package com.bencarlisle.wirelesswidth;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

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
        Log.e("HotspotService", "Adding id: " + id);
        EncounterDB encounterDB = new EncounterDB(this);
        encounterDB.addToDatabase(id);
    }

    void findContacts() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        assert wifiManager != null;
        List<ScanResult> scanResults = wifiManager.getScanResults();
        Log.e("HotspotService", "Found " + scanResults.size() + " results");
        for (ScanResult scanResult: scanResults) {
            parseResult(scanResult);
        }
    }

    void omit() {
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifiManager != null;
        HotspotListener hotspotListener = new HotspotListener();
        wifiManager.startLocalOnlyHotspot(hotspotListener, null);
    }
}
