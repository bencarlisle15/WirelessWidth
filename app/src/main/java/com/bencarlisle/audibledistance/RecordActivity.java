package com.bencarlisle.audibledistance;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class RecordActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
    }

    public void togglePlaying(View view) {
        AudioRecord recorder = findAudioRecord();
        for (int i = 0; i < 8; i++) {
            float amp = (float) (MAX_REPORTABLE_DB + (20 * Math.log10(getRawAmplitude() / MAX_REPORTABLE_AMP)));

        }
        recorder.release();
    }

    public AudioRecord findAudioRecord() {
        int rate = 44100;
        for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
            for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
                try {
                    Log.e("RECORD", "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
                            + channelConfig);
                    int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                    if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                        // check if we can instantiate and have a success
                        AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

                        if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                            return recorder;
                    }
                } catch (Exception e) {
                    Log.e("RECORD", rate + "Exception, keep trying.",e);
                }
            }
        }
        return null;
    }
}