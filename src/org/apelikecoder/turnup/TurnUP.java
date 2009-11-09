package org.apelikecoder.turnup;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class TurnUP extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    AudioManager mAudioManager;
    
    public final static String PREFS_KEY_ENABLE_RINGER_SERVICE = "prefs_listen_for_incoming_calls";
    public final static String PREFS_KEY_ENABLE_ALARM_SERVICE = "prefs_listen_for_alarms";
    public final static String PREFS_KEY_RESPECT_SILENCE = "prefs_key_respect_silence";
    public final static String PREFS_KEY_MAX_RINGER_VOLUME = "prefs_ringer_max_volume";
    public final static String PREFS_KEY_MAX_ALARM_VOLUME = "prefs_alarm_max_volume";
    public final static String PREFS_KEY_RINGER_DELAY_INTREVAL = "prefs_ringer_delay_interval";
    public final static String PREFS_KEY_ALARM_DELAY_INTREVAL = "prefs_alarm_delay_interval";
    public final static int DEFAULT_DELAY_INTERVAL = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);

        PreferenceScreen prefScreen = getPreferenceManager().createPreferenceScreen(this);
        setPreferenceScreen(prefScreen);

        PreferenceCategory prefCategory = new PreferenceCategory(this);
        prefCategory.setTitle(R.string.prefs_category_ringer_title);
        prefScreen.addPreference(prefCategory);

        //RINGER SETTINGS
        CheckBoxPreference chkPref = new CheckBoxPreference(this);
        chkPref.setKey(PREFS_KEY_ENABLE_RINGER_SERVICE);
        chkPref.setTitle(R.string.prefs_ringer_service_enabled);
        chkPref.setSummary(R.string.prefs_ringer_service_enabled_summary);
        chkPref.setChecked(isRingerServiceRunning(PREFS_KEY_ENABLE_RINGER_SERVICE));
        prefCategory.addPreference(chkPref);
 
        int maxVolume = getMaxVolume(AudioManager.STREAM_RING);
        SeekBarPreference sb = new SeekBarPreference(this, null, maxVolume, maxVolume,
                R.string.prefs_max_volume);
        sb.setTitle(R.string.prefs_max_volume);
        sb.setKey(PREFS_KEY_MAX_RINGER_VOLUME);
        prefCategory.addPreference(sb);
        sb.setDependency(PREFS_KEY_ENABLE_RINGER_SERVICE);
        setMaxVolumeSummary(sharedPrefs, PREFS_KEY_MAX_RINGER_VOLUME, AudioManager.STREAM_RING);
        
        ListPreference listPref = new ListPreference(this);
        listPref.setKey(PREFS_KEY_RINGER_DELAY_INTREVAL);
        listPref.setTitle(R.string.prefs_delay_interval_title);
        listPref.setDialogTitle(R.string.prefs_delay_interval_title);
        listPref.setEntries(R.array.delay_display_values);
        listPref.setEntryValues(R.array.delay_values);
        listPref.setDefaultValue(getResources().getStringArray(R.array.delay_values)[1]);
        prefCategory.addPreference(listPref);
        listPref.setDependency(PREFS_KEY_ENABLE_RINGER_SERVICE);
        setDelaySummary(sharedPrefs, PREFS_KEY_RINGER_DELAY_INTREVAL);

        chkPref = new CheckBoxPreference(this);
        chkPref.setKey(PREFS_KEY_RESPECT_SILENCE);
        chkPref.setTitle(R.string.prefs_ringer_respect_silence);
        chkPref.setSummary(R.string.prefs_ringer_respect_silence_summary);
        prefCategory.addPreference(chkPref);
        chkPref.setDependency(PREFS_KEY_ENABLE_RINGER_SERVICE);
        
        //ALARM SETTINGS
        prefCategory = new PreferenceCategory(this);
        prefCategory.setTitle(R.string.prefs_category_alarm_title);
        prefScreen.addPreference(prefCategory);

        chkPref = new CheckBoxPreference(this);
        chkPref.setKey(PREFS_KEY_ENABLE_ALARM_SERVICE);
        chkPref.setTitle(R.string.prefs_alarm_service_enabled);
        chkPref.setSummary(R.string.prefs_alarm_service_enabled_summary);
        chkPref.setChecked(isRingerServiceRunning(PREFS_KEY_ENABLE_ALARM_SERVICE));
        prefCategory.addPreference(chkPref);
        
        maxVolume = getMaxVolume(AudioManager.STREAM_ALARM);
        sb = new SeekBarPreference(this, null, maxVolume, maxVolume,
                R.string.prefs_max_volume);
        sb.setTitle(R.string.prefs_max_volume);
        sb.setKey(PREFS_KEY_MAX_ALARM_VOLUME);
        prefCategory.addPreference(sb);
        sb.setDependency(PREFS_KEY_ENABLE_ALARM_SERVICE);
        setMaxVolumeSummary(sharedPrefs, PREFS_KEY_MAX_ALARM_VOLUME, AudioManager.STREAM_ALARM);
        
        listPref = new ListPreference(this);
        listPref.setKey(PREFS_KEY_ALARM_DELAY_INTREVAL);
        listPref.setTitle(R.string.prefs_delay_interval_title);
        listPref.setDialogTitle(R.string.prefs_delay_interval_title);
        listPref.setEntries(R.array.delay_display_values);
        listPref.setEntryValues(R.array.delay_values);
        listPref.setDefaultValue(getResources().getStringArray(R.array.delay_values)[1]);
        prefCategory.addPreference(listPref);
        listPref.setDependency(PREFS_KEY_ENABLE_ALARM_SERVICE);
        setDelaySummary(sharedPrefs, PREFS_KEY_ALARM_DELAY_INTREVAL);
    }

    private void setMaxVolumeSummary(SharedPreferences sp, String prefKey, int type) {
        Preference pref = getPreferenceScreen().findPreference(prefKey);
        int currentValue = sp.getInt(prefKey, 0);
        if (currentValue == 0)
            currentValue = getMaxVolume(type);
        pref.setSummary(getString(R.string.current_value, currentValue));
    }

    private void setDelaySummary(SharedPreferences sp, String prefKey) {
        Preference pref = getPreferenceScreen().findPreference(prefKey);
        String currentValue = sp.getString(prefKey, String.valueOf(DEFAULT_DELAY_INTERVAL));
        pref.setSummary(getString(R.string.current_value, currentValue));
    }

    private int getMaxVolume(int type) {
        return mAudioManager.getStreamMaxVolume(type);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PREFS_KEY_MAX_RINGER_VOLUME)) {
            setMaxVolumeSummary(sharedPreferences, key, AudioManager.STREAM_RING);
        } else if (key.equals(PREFS_KEY_MAX_ALARM_VOLUME)) {
            setMaxVolumeSummary(sharedPreferences, key, AudioManager.STREAM_ALARM);
        } else if (key.equals(PREFS_KEY_ALARM_DELAY_INTREVAL)
                || key.equals(PREFS_KEY_RINGER_DELAY_INTREVAL)) {
            setDelaySummary(sharedPreferences, key);
        }
    }

    private boolean isRingerServiceRunning(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getBoolean(key, false);
    }

    static class SeekBarPreference extends DialogPreference {

        private Context context;
        private SeekBar volumeLevel;
        private int mMaxValue;
        private int mDefaultValue;
        private int mTitle;

        public SeekBarPreference(Context context, AttributeSet attrs, int maxValue,
                int defaultValue, int title) {
            super(context, attrs);
            this.context = context;
            mMaxValue = maxValue;
            mDefaultValue = defaultValue;
            mTitle = title;
        }

        protected void onPrepareDialogBuilder(Builder builder) {
            LinearLayout layout = new LinearLayout(context);
            layout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.setMinimumWidth(400);
            layout.setPadding(20, 20, 20, 20);

            volumeLevel = new SeekBar(context);
            volumeLevel.setMax(mMaxValue);
            volumeLevel.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            volumeLevel.setProgress(getPersistedInt(mDefaultValue));
            layout.addView(volumeLevel);
            builder.setView(layout);
            builder.setTitle(mTitle);// get it from outside
            super.onPrepareDialogBuilder(builder);
        }

        protected void onDialogClosed(boolean positiveResult) {
            if (positiveResult) {
                persistInt(volumeLevel.getProgress());
            }
        }
    }
}
