package com.bencarlisle.audibledistance;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.View;

public class OmitActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omit);
    }

    public void startSound(View view) {
        new Thread(this::playSound).start();
    }

    byte[] genTone(int duration, int sampleRate) {
        int numSamples = duration * sampleRate;
        byte[] generatedSnd = new byte[2 * numSamples];
        double[] sample = new double[numSamples];
        for (int i = 0; i < numSamples; ++i) {
            double freqOfTone = 15500;
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
        }
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
        return generatedSnd;
    }

    void playSound() {
        int frequency = 15 * 1000;
        int sampleRate = 2 * frequency;
        byte[] generatedSnd = genTone(1, sampleRate);
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
    }
}
