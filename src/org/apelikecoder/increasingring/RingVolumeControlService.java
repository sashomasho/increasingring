package org.apelikecoder.increasingring;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

public class RingVolumeControlService extends VolumeControlService {

    private TelephonyManager mTelephonyManager;
    private int mStartupVolume;
    private boolean mRespectSilence;
    private final static String TAG = "RingVolumeControlService";
    private boolean mShouldVibrate;
    private static final int UNKNOWN_VIBRATION_SETTINGS = -123;//something meaningful here
    private int mVibrationStartupSettings = UNKNOWN_VIBRATION_SETTINGS ;
    private int mVibrationStartupLevel;
    
    private static final int ATTENUATED_VOLUME_LEVEL = 2;
    
    private PickupDetector pickup;
    private boolean isAttenuated;
    
    private PickupDetector.Listener listener = new PickupDetector.Listener() {
        public void onPickup() {
            attenuate();
        }
    };
    
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
        if (mVibrationStartupSettings != UNKNOWN_VIBRATION_SETTINGS) {
            android.util.Log.d(TAG, "RESTORE VIBRATION");
            AudioManager audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
            audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, mVibrationStartupSettings);
        }
        if (pickup != null)
            pickup.stopListening();
        super.stop();
    }
    
    @Override
    protected void increaseVolume() {
        if (isAttenuated)
            return;
        super.increaseVolume();
        if (mShouldVibrate && getCurrentVolume() >= mVibrationStartupLevel) {
            setVibration(true);
        }
    }

    @Override
    protected void reloadSettings() {
        setStreamType(AudioManager.STREAM_RING);
        mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean(PreferenceKeys.Ringer.ENABLE_PICKUP_ATT, false)) {
            pickup = new PickupDetector(this, listener);
            pickup.startListening();
        }
        mRespectSilence = sp.getBoolean(PreferenceKeys.Ringer.RESPECT_SILENCE, true);
        int maxVolume = getStreamMaxVolume(); 
        setMaxVolume(sp.getInt(PreferenceKeys.Ringer.MAX_VOLUME, maxVolume));
        setDelay(Integer.valueOf(sp.getString(PreferenceKeys.Ringer.DELAY_INTREVAL, "0")));
        boolean vibratorOn = isGlobalVibrateOn();
        if (!vibratorOn) {
            mShouldVibrate = sp.getBoolean(PreferenceKeys.Ringer.ENABLE_VIBRATOR, false);
            android.util.Log.d(TAG, "mShouldVibrate " + mShouldVibrate);
            mVibrationStartupLevel = sp.getInt(PreferenceKeys.Ringer.VIBRATOR_STARTUP_LEVEL, maxVolume);;
        }
    }

    private boolean isRinging() {
        return mTelephonyManager.getCallState() == TelephonyManager.CALL_STATE_RINGING;
    }
    
    @Override
    protected boolean shouldStop() {
        return !isRinging();
    }
    
    private boolean isGlobalVibrateOn() {
        AudioManager audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        return audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER) == AudioManager.VIBRATE_SETTING_ON;
        //return audioManager.shouldVibrate(AudioManager.VIBRATE_TYPE_RINGER);
    }
    
    private void setVibration(boolean on) {
        AudioManager audioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        if (mVibrationStartupSettings == UNKNOWN_VIBRATION_SETTINGS) {
            mVibrationStartupSettings = audioManager.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER);
        }
        android.util.Log.d(TAG, "ENABLE VIBRATION " + on);
        audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, on ? AudioManager.VIBRATE_SETTING_ON : AudioManager.VIBRATE_SETTING_OFF);
    }

    private void attenuate() {
        isAttenuated = true;
        setCurrentVolume(ATTENUATED_VOLUME_LEVEL);
        setVibration(false);
    }
}
