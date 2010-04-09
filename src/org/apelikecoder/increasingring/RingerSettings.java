package org.apelikecoder.increasingring;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

public class RingerSettings extends RingSettingsBase {

    @Override
    protected void initView() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
        PreferenceScreen prefScreen = getPreferenceManager().createPreferenceScreen(this);
        setPreferenceScreen(prefScreen);
        /*
        PreferenceCategory prefCategory = new PreferenceCategory(this);
        prefCategory.setTitle(R.string.prefs_category_ringer_title);
        prefScreen.addPreference(prefCategory);
        PreferenceGroup containerPref = prefCategory;
        */
        PreferenceGroup containerPref = prefScreen;
        CheckBoxPreference chkPref = new CheckBoxPreference(this);
        chkPref.setKey(PreferenceKeys.Ringer.ENABLE_SERVICE);
        chkPref.setTitle(R.string.prefs_ringer_service_enabled);
        chkPref.setSummary(R.string.prefs_ringer_service_enabled_summary);
        chkPref.setChecked(isVolumeControlServiceEnabled(PreferenceKeys.Ringer.ENABLE_SERVICE));
        containerPref.addPreference(chkPref);
 
        int maxVolume = getMaxVolume(AudioManager.STREAM_RING);
        SeekBarPreference sb = new SeekBarPreference(this, null, maxVolume, maxVolume);
        sb.setTitle(R.string.prefs_max_volume);
        sb.setKey(PreferenceKeys.Ringer.MAX_VOLUME);
        containerPref.addPreference(sb);
        sb.setDependency(PreferenceKeys.Ringer.ENABLE_SERVICE);
        setMaxVolumeSummary(sharedPrefs, PreferenceKeys.Ringer.MAX_VOLUME,
                AudioManager.STREAM_RING, R.string.prefs_ringer_max_volume_summary_current_value);
        
        ListPreference listPref = new ListPreference(this);
        listPref.setKey(PreferenceKeys.Ringer.DELAY_INTREVAL);
        listPref.setTitle(R.string.prefs_delay_interval_title);
        listPref.setDialogTitle(R.string.prefs_delay_interval_title);
        listPref.setEntries(R.array.delay_display_values);
        listPref.setEntryValues(R.array.delay_values);
        listPref.setDefaultValue(getResources().getStringArray(R.array.delay_values)[1]);
        containerPref.addPreference(listPref);
        listPref.setDependency(PreferenceKeys.Ringer.ENABLE_SERVICE);
        setDelaySummary(sharedPrefs, PreferenceKeys.Ringer.DELAY_INTREVAL);

        chkPref = new CheckBoxPreference(this);
        chkPref.setKey(PreferenceKeys.Ringer.RESPECT_SILENCE);
        chkPref.setTitle(R.string.prefs_ringer_respect_silence);
        chkPref.setSummary(R.string.prefs_ringer_respect_silence_summary);
        containerPref.addPreference(chkPref);
        chkPref.setDependency(PreferenceKeys.Ringer.ENABLE_SERVICE);
        
        chkPref = new CheckBoxPreference(this);
        chkPref.setKey(PreferenceKeys.Ringer.ENABLE_VIBRATOR);
        chkPref.setTitle(R.string.prefs_ringer_vibration_enable);
        chkPref.setSummary(R.string.prefs_ringer_vibration_enable_summary);
        containerPref.addPreference(chkPref);
        chkPref.setDependency(PreferenceKeys.Ringer.ENABLE_SERVICE);

        sb = new SeekBarPreference(this, null, maxVolume, maxVolume);
        sb.setTitle(R.string.prefs_ringer_vibration_startup_level);
        sb.setKey(PreferenceKeys.Ringer.VIBRATOR_STARTUP_LEVEL);
        containerPref.addPreference(sb);
        sb.setDependency(PreferenceKeys.Ringer.ENABLE_VIBRATOR);
        setMaxVolumeSummary(sharedPrefs, PreferenceKeys.Ringer.VIBRATOR_STARTUP_LEVEL,
                AudioManager.STREAM_RING, R.string.prefs_ringer_vibration_startup_level_summary);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferenceKeys.Ringer.MAX_VOLUME)) {
                setMaxVolumeSummary(sharedPreferences, key, AudioManager.STREAM_RING,
                        R.string.prefs_ringer_max_volume_summary_current_value);
                checkRingVibrateLevels();
        } else if (key.equals(PreferenceKeys.Ringer.VIBRATOR_STARTUP_LEVEL)) {
            setMaxVolumeSummary(sharedPreferences, key, AudioManager.STREAM_RING,
                    R.string.prefs_startup_vibration_summary_current_value);
            checkRingVibrateLevels();
        } else if (key.equals(PreferenceKeys.Ringer.ENABLE_VIBRATOR)) {
            checkRingVibrateLevels();
        } else if (key.equals(PreferenceKeys.Ringer.DELAY_INTREVAL)) {
            setDelaySummary(sharedPreferences, key);
        }
    }
    
    private void checkRingVibrateLevels() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPrefs.getBoolean(PreferenceKeys.Ringer.ENABLE_VIBRATOR, false)) {
            int maxVolume = getMaxVolume(AudioManager.STREAM_RING);
            int limitVolume = sharedPrefs.getInt(PreferenceKeys.Ringer.MAX_VOLUME, maxVolume);
            int vibrationStartupVolume = sharedPrefs.getInt(PreferenceKeys.Ringer.VIBRATOR_STARTUP_LEVEL, maxVolume);
            if (vibrationStartupVolume > limitVolume)
                Toast.makeText(this, R.string.msg_wrong_vibration_settings, Toast.LENGTH_LONG).show();
        }
    }
}
