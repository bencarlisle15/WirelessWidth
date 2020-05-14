package com.bencarlisle.wirelesswidth;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

public class HotspotService extends TracerService {

    private final static int EMIT_LENGTH = 5;

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

    boolean isHotspotOn() {
        try {
            WifiManager wifimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            assert wifimanager != null;
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (boolean) method.invoke(wifimanager);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return false;
        }
    }

    void changeHotspotState(boolean newState) {
        WifiManager wifimanager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        assert wifimanager != null;
        try {
            if(isHotspotOn()) {
                wifimanager.setWifiEnabled(false);
            }
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, null, newState);
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
        }
    }

    void emit() {
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            assert wifiManager != null;
            HotspotListener hotspotListener = new HotspotListener(EMIT_LENGTH);
            wifiManager.startLocalOnlyHotspot(hotspotListener, null);
            new Thread(hotspotListener::timerKill).start();
        } else {
            changeHotspotState(true);
            try {
                Thread.sleep(EMIT_LENGTH *  1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            changeHotspotState(false);
        }
    }
}
