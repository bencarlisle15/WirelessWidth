package com.bencarlisle.wirelesswidth;

import android.net.wifi.WifiManager;
import android.util.Log;

class HotspotListener extends WifiManager.LocalOnlyHotspotCallback {

    private final static int OMIT_LENGTH = 5;
    private WifiManager.LocalOnlyHotspotReservation reservation;

    @Override
    public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
        super.onStarted(reservation);
        Log.e("HOTSPOT LISTENER", "Wifi Hotspot is now on");
        reservation.close();
        this.reservation = reservation;
        new Thread(this::timerKill).start();
    }

    private void timerKill() {
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
        super.onStopped();
        Log.e("HOTSPOT LISTENER", "Stopped");
    }

    @Override
    public void onFailed(int reason) {
        super.onFailed(reason);
        Log.e("HOTSPOT LISTENER", "Failed");
    }
}
