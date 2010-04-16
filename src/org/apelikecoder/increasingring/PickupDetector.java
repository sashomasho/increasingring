package org.apelikecoder.increasingring;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class PickupDetector {

    public interface Listener {
        public void onPickup();
    }

    public static final int POSITION_PHONE_UNKNOWN = 0;
    public static final int POSITION_PHONE_LAYING_DOWN = 0;
    public static final int POSITION_PHONE_PICKED_UP = 1;

    private int currentState = POSITION_PHONE_UNKNOWN;

    private SensorManager sensorMan;

    private Listener listener;

    public PickupDetector(Context context, Listener listener) {
        sensorMan = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.listener = listener;
    }

    public void startListening() {
        if (!sensorMan.getSensorList(Sensor.TYPE_ORIENTATION).isEmpty())
            sensorMan.registerListener(sensorListener, sensorMan
                .getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
    }

    public void stopListening() {
        sensorMan.unregisterListener(sensorListener);
    }

    private SensorEventListener sensorListener = new SensorEventListener() {

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            float vals[] = event.values;
            if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
                int pitch = Math.abs((int) vals[1]);
                int roll = Math.abs((int) vals[2]);
                int state = (pitch < 5 && roll < 5) 
                        ? POSITION_PHONE_LAYING_DOWN
                        : POSITION_PHONE_PICKED_UP;
                if (state == POSITION_PHONE_PICKED_UP
                        && currentState == POSITION_PHONE_LAYING_DOWN)
                    listener.onPickup();

                if (currentState != POSITION_PHONE_PICKED_UP)
                    currentState = state;
            }
        }
    };
    
    public boolean isLying() {
        return currentState == POSITION_PHONE_LAYING_DOWN;
    }
}