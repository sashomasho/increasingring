package org.apelikecoder.increasingring;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

abstract public class VolumeControlService extends Service {
    
    private final static String TAG = "VolumeControlService";
    
    private AudioManager mAudioManager;
    private MyHandler mHandler;
    private int mMaxVolume;
    private int mIncreaseVolumeDelay;
    private int mStreamType = -1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    abstract protected void reloadSettings();
    abstract protected boolean shouldStop();

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new MyHandler();
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        reloadSettings();
        android.util.Log.d(TAG, getCurrentVolume() + " " + getMaxVolume() + " " + getDelay());
        if (mStreamType == -1)
            throw new RuntimeException("Provide valid stream type");
    }
    
    @Override
    public void onStart(Intent intent, int startId) {
        startVolumeIncrease();
        super.onStart(intent, startId);
    }
    
    protected void stop() {
        stopSelf();
    }
    
    protected void startVolumeIncrease() {
        mHandler.sendEmptyMessageDelayed(0, getDelay());
    }

    protected void increaseVolume() {
        if (isRoomVolumeAvailable()) {
            setCurrentVolume(getCurrentVolume() + 1);
            android.util.Log.d(TAG, "INCREASE VOLUME " + getCurrentVolume());
        }
    }

    protected boolean isRoomVolumeAvailable() {
        return getCurrentVolume() < getMaxVolume();        
    }
    
    protected void setDelay(int delay) {
        if (delay == 0)
            delay = RingSettingsBase.DEFAULT_DELAY_INTERVAL;
        mIncreaseVolumeDelay = delay * 1000;
    }
    
    protected long getDelay() {
        return mIncreaseVolumeDelay;
    }
    
    protected void setMaxVolume(int maxVolume) {
        this.mMaxVolume = maxVolume;
    }
    
    protected int getMaxVolume() {
        return mMaxVolume;
    }

    protected int getStreamMaxVolume() {
        return mAudioManager.getStreamMaxVolume(mStreamType);
    }

    protected void setStreamType(int mStreamType) {
        this.mStreamType = mStreamType;
    }

    protected int getStreamType() {
        return mStreamType;
    }
    
    protected void setCurrentVolume(int volumeIndex) {
        mAudioManager.setStreamVolume(mStreamType, volumeIndex, 0);
    }

    protected int getCurrentVolume() {
        return mAudioManager.getStreamVolume(mStreamType);
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (shouldStop()) {
                stop();
                return;
            }
            increaseVolume();
            sendEmptyMessageDelayed(0, getDelay());
        }
    }
}
