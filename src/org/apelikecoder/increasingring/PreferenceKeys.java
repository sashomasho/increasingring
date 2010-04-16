package org.apelikecoder.increasingring;

public class PreferenceKeys {
    static class Alarm {
        public final static String ENABLE_SERVICE = "prefs_listen_for_alarms";
        public final static String MAX_VOLUME = "prefs_alarm_max_volume";
        public final static String DELAY_INTREVAL = "prefs_alarm_delay_interval";
    }
    static class Ringer {
        public final static String ENABLE_PICKUP_ATT = "prefs_enable_pickup_attenuation";
        public final static String ENABLE_SERVICE = "prefs_listen_for_incoming_calls";
        public final static String RESPECT_SILENCE = "prefs_key_respect_silence";
        public final static String MAX_VOLUME = "prefs_ringer_max_volume";
        public final static String DELAY_INTREVAL = "prefs_ringer_delay_interval";
        public final static String ENABLE_VIBRATOR = "prefs_ringer_enable_vibrator";
        public final static String VIBRATOR_STARTUP_LEVEL = "prefs_ringer_vibrator_startup_level";
    }
}
