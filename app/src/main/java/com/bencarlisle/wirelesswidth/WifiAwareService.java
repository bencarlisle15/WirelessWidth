package com.bencarlisle.wirelesswidth;

import android.content.Context;
import android.net.wifi.aware.WifiAwareManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class WifiAwareService extends TracerService {


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    void init() {
        WifiAwareManager wifiAwareManager = (WifiAwareManager) getSystemService(Context.WIFI_AWARE_SERVICE);
        if (wifiAwareManager == null) {
            Log.e("WifiAwareService", "No Wifi Aware Manager found");
            return;
        }
        wifiAwareManager.attach(new WifiAwareListener(this), null);
    }
}
