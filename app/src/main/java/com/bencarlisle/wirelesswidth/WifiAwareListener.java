package com.bencarlisle.wirelesswidth;

import android.content.Context;
import android.net.wifi.aware.AttachCallback;
import android.net.wifi.aware.DiscoverySessionCallback;
import android.net.wifi.aware.PeerHandle;
import android.net.wifi.aware.PublishConfig;
import android.net.wifi.aware.SubscribeConfig;
import android.net.wifi.aware.WifiAwareSession;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

import static com.bencarlisle.wirelesswidth.TracerService.MAX_DISTANCE;
import static com.bencarlisle.wirelesswidth.TracerService.pattern;

@RequiresApi(api = Build.VERSION_CODES.P)
class WifiAwareListener extends AttachCallback {

    private final EncounterDB encounterDB;

    WifiAwareListener(Context context) {
        this.encounterDB = new EncounterDB(context);
    }

    @Override
    public void onAttached(WifiAwareSession session) {
        super.onAttached(session);
        String idString = encounterDB.getIdString();
        PublishConfig config = new PublishConfig.Builder()
                .setServiceSpecificInfo(idString.getBytes())
                .setRangingEnabled(true)
                .build();
        session.publish(config, new DiscoverySessionCallback(), null);
        SubscribeConfig subscribeConfig = new SubscribeConfig.Builder()
                .setMaxDistanceMm(MAX_DISTANCE * 1000)
                .build();
        session.subscribe(subscribeConfig, new DiscoverySessionCallback() {
            @Override
            public void onServiceDiscoveredWithinRange (PeerHandle peerHandle, byte[] serviceSpecificInfo, List<byte[]> matchFilter, int distanceMm) {
                Matcher matcher = pattern.matcher(new String(serviceSpecificInfo));
                if (!matcher.matches()) {
                    //not a contact tracer;
                    return;
                }
                long id = Long.parseLong(Objects.requireNonNull(matcher.group(1)));
                Log.e("WifiAwareService", "Adding id: " + id);
                encounterDB.addToDatabase(id);
            }
        }, null);

    }
}
