package com.bencarlisle.audibledistance;

import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HotspotListener extends WifiManager.LocalOnlyHotspotCallback {

    private final static int OMIT_LENGTH = 5;
    private WifiManager.LocalOnlyHotspotReservation reservation;
    public HotspotListener() {
        Log.e("CReated", "HOTSTPO");
    }

    @Override
    public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
//        super.onStarted(reservation);
        Log.e("DDSD", " " + (5 / 0));
        Log.e("HOTSPOT LISTENER", "Wifi Hotspot is now on");
        reservation.close();
        this.reservation = reservation;
//        new Thread(this::timerKill).start();
    }

    WifiManager.LocalOnlyHotspotReservation getReservation() {
        return reservation;
    }

    private void timerKill() {
        Log.e("DDSD", " " + (5 / 0));
        Log.e("AM", "START KILLING TIMER");
        try {
            Thread.sleep(OMIT_LENGTH *  1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.e("AM", "KILLING");
        reservation.close();
        reservation = null;
    }

    @Override
    public void onStopped() {
//        super.onStopped();
        Log.e("DDSD", " " + (5 / 0));
        Log.e("HOTSPOT LISTENER", "Stopped");
    }

    @Override
    public void onFailed(int reason) {
//        super.onFailed(reason);
        Log.e("DDSD", " " + (5 / 0));
        Log.e("HOTSPOT LISTENER", "Failed");
    }
}
