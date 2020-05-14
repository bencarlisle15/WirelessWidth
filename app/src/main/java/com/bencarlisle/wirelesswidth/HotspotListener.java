package com.bencarlisle.wirelesswidth;

import android.net.wifi.WifiManager;

class HotspotListener extends WifiManager.LocalOnlyHotspotCallback {

    private final int EMIT_LENGTH;
    private WifiManager.LocalOnlyHotspotReservation reservation;

    HotspotListener(int EMIT_LENGTH) {
        this.EMIT_LENGTH = EMIT_LENGTH;
    }

    @Override
    public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
        super.onStarted(reservation);
        reservation.close();
        this.reservation = reservation;
    }

    void timerKill() {
        try {
            Thread.sleep(EMIT_LENGTH *  1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        reservation.close();
        reservation = null;
    }

    @Override
    public void onStopped() {
        super.onStopped();
    }

    @Override
    public void onFailed(int reason) {
        super.onFailed(reason);
    }
}
