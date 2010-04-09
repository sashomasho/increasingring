package org.apelikecoder.increasingring;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

public class AlarmSettings extends RingSettingsBase {

    @Override
    protected void initView() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
        PreferenceScreen prefScreen = getPreferenceManager().createPreferenceScreen(this);
        setPreferenceScreen(prefScreen);

        CheckBoxPreference chkPref = new CheckBoxPreference(this);
        chkPref.setKey(PreferenceKeys.Alarm.ENABLE_SERVICE);
        chkPref.setTitle(R.string.prefs_alarm_service_enabled);
        chkPref.setSummary(R.string.prefs_alarm_service_enabled_summary);
        chkPref.setChecked(isVolumeControlServiceEnabled(PreferenceKeys.Alarm.ENABLE_SERVICE));
        prefScreen.addPreference(chkPref);
        
        int maxVolume = getMaxVolume(AudioManager.STREAM_ALARM);
        SeekBarPreference sb = new SeekBarPreference(this, null, maxVolume, maxVolume);
        sb.setTitle(R.string.prefs_max_volume);
        sb.setKey(PreferenceKeys.Alarm.MAX_VOLUME);
        prefScreen.addPreference(sb);
        sb.setDependency(PreferenceKeys.Alarm.ENABLE_SERVICE);
        setMaxVolumeSummary(sharedPrefs, PreferenceKeys.Alarm.MAX_VOLUME,
                AudioManager.STREAM_ALARM, R.string.prefs_alarm_max_volume_summary_current_value);
        
        ListPreference listPref = new ListPreference(this);
        listPref.setKey(PreferenceKeys.Alarm.DELAY_INTREVAL);
        listPref.setTitle(R.string.prefs_delay_interval_title);
        listPref.setDialogTitle(R.string.prefs_delay_interval_title);
        listPref.setEntries(R.array.delay_display_values);
        listPref.setEntryValues(R.array.delay_values);
        listPref.setDefaultValue(getResources().getStringArray(R.array.delay_values)[1]);
        prefScreen.addPreference(listPref);
        listPref.setDependency(PreferenceKeys.Alarm.ENABLE_SERVICE);
        setDelaySummary(sharedPrefs, PreferenceKeys.Alarm.DELAY_INTREVAL);
    }
    
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferenceKeys.Alarm.MAX_VOLUME)) {
            setMaxVolumeSummary(sharedPreferences, key, AudioManager.STREAM_ALARM,
                    R.string.prefs_alarm_max_volume_summary_current_value);
        } else if (key.equals(PreferenceKeys.Alarm.DELAY_INTREVAL)) {
            setDelaySummary(sharedPreferences, key);
        }
    }

}
