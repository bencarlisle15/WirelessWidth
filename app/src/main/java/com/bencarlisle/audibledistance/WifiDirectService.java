package com.bencarlisle.audibledistance;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.regex.Matcher;

public class WifiDirectService extends TracerService implements WifiP2pManager.PeerListListener {

    private void parseResult(WifiP2pDevice peer) {
        String name = peer.deviceName;
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            //not a contact tracer;
            return;
        }
        //center freq 2.4 GHz
        double distance = getDistance(getLevel(peer), 2420);
        if (distance > MAX_DISTANCE) {
            //over 6ft away
            return;
        }
        long id = Long.parseLong(Objects.requireNonNull(matcher.group(1)));
        Log.e("HOTSPOT SERVICE", "Adding id: " + id);
        EncounterDB encounterDB = new EncounterDB(this);
        encounterDB.addToDatabase(id);
    }

    private int getLevel(WifiP2pDevice peer) {
        //Implemented by android
        //https://android.googlesource.com/platform/packages/apps/Settings/+/master/src/com/android/settings/wifi/p2p/WifiP2pPeer.java#44
        return -60;
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        for (WifiP2pDevice peer: peers.getDeviceList()) {
            parseResult(peer);
        }
    }

    void findContacts() {
        WifiP2pManager manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        assert manager != null;
        WifiP2pManager.Channel channel = manager.initialize(this, getMainLooper(), null);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        WifiDirectService self = this;
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                manager.requestPeers(channel, self);
            }

            @Override
            public void onFailure(int reasonCode) {
            }
        });
    }

    void omit() {
        try {
            WifiP2pManager manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
            assert manager != null;
            WifiP2pManager.Channel channel = manager.initialize(this, getMainLooper(), null);
            Method m = manager.getClass().getMethod("setDeviceName", channel.getClass(), String.class, WifiP2pManager.ActionListener.class);
            m.invoke(manager, channel, "contact_tracer_0123456789", new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure(int reason) {
                }
            });
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
