package org.apelikecoder.turnup;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

public class RingVolumeControlService extends VolumeControlService {

    private TelephonyManager mTelephonyManager;
    private int mStartupVolume;
    private boolean mRespectSilence;
    private final static String TAG = "TurnUP/RingVolumeControlService";

    @Override
    protected void startVolumeIncrease() {
        mStartupVolume = getCurrentVolume();
        android.util.Log.d(TAG, "Current volume: " + mStartupVolume);
        if (mStartupVolume < 1 && mRespectSilence) {
            stopSelf();
            return;
        }
        super.startVolumeIncrease();
    }

    @Override
    protected void stop() {
        setCurrentVolume(mStartupVolume);
        super.stop();
    }

    @Override
    protected void reloadSettings() {
        setStreamType(AudioManager.STREAM_RING);
        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mRespectSilence = sp.getBoolean(TurnUP.PREFS_KEY_RESPECT_SILENCE, true);
        setMaxVolume(sp.getInt(TurnUP.PREFS_KEY_MAX_RINGER_VOLUME, getStreamMaxVolume()));
        setDelay(Integer.valueOf(sp.getString(TurnUP.PREFS_KEY_RINGER_DELAY_INTREVAL, "0")));
    }

    private boolean isRinging() {
        return mTelephonyManager.getCallState() == TelephonyManager.CALL_STATE_RINGING;
    }
    
    @Override
    protected boolean shouldStop() {
        return !isRinging();
    }
}
