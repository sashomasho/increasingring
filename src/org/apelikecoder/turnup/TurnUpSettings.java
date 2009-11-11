package org.apelikecoder.turnup;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

abstract public class TurnUpSettings extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    AudioManager mAudioManager;

    public final static int DEFAULT_DELAY_INTERVAL = 3;
    private final static int ID_MENU_ABOUT = 1;

    abstract protected void initView();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        initView();
    }

    protected void setMaxVolumeSummary(SharedPreferences sp, String prefKey, int type, int summary) {
        Preference pref = getPreferenceScreen().findPreference(prefKey);
        int currentValue = sp.getInt(prefKey, 0);
        if (currentValue == 0)
            currentValue = getMaxVolume(type);
        pref.setSummary(getString(summary, currentValue));
    }

    protected void setDelaySummary(SharedPreferences sp, String prefKey) {
        Preference pref = getPreferenceScreen().findPreference(prefKey);
        String currentValue = sp.getString(prefKey, String.valueOf(DEFAULT_DELAY_INTERVAL));
        pref.setSummary(getString(R.string.prefs_increasing_interval_current_value, currentValue));
    }

    protected int getMaxVolume(int type) {
        return mAudioManager.getStreamMaxVolume(type);
    }
    
    protected boolean isVolumeControlServiceEnabled(String key) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getBoolean(key, false);
    }

    static class SeekBarPreference extends DialogPreference {

        private Context context;
        private SeekBar mSeekBar;
        private int mMaxValue;
        private int mDefaultValue;

        public SeekBarPreference(Context context, AttributeSet attrs, int maxValue,
                int defaultValue) {
            super(context, attrs);
            this.context = context;
            mMaxValue = maxValue;
            mDefaultValue = defaultValue;
        }

        protected void onPrepareDialogBuilder(Builder builder) {
            LinearLayout layout = new LinearLayout(context);
            layout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            layout.setMinimumWidth(400);
            layout.setPadding(20, 20, 20, 20);

            mSeekBar = new SeekBar(context);
            mSeekBar.setMax(mMaxValue);
            mSeekBar.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mSeekBar.setProgress(getPersistedInt(mDefaultValue));
            layout.addView(mSeekBar);
            builder.setView(layout);
            builder.setTitle(getTitle());
            super.onPrepareDialogBuilder(builder);
        }

        protected void onDialogClosed(boolean positiveResult) {
            if (positiveResult) {
                persistInt(mSeekBar.getProgress());
            }
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        MenuItem m = menu.add(0, ID_MENU_ABOUT, 0, R.string.prefs_about_title);
        m.setIcon(android.R.drawable.ic_menu_info_details);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = super.onOptionsItemSelected(item);
        if (item.getItemId() == ID_MENU_ABOUT) {
            showAboutDialog();
        }
        return result;
    }

    
    private void showAboutDialog() {
        View view = View.inflate(this, R.layout.dialog, null);
        ((TextView ) view).setText(getString(R.string.about_text).replace("\\n","\n").replace("${VERSION}", getVersion(this)));
        new AlertDialog.Builder(this)
            .setTitle(R.string.app_name)
            .setView(view)
            .setIcon(R.drawable.turnup48)
            .setPositiveButton(android.R.string.ok, null)
            .create()
            .show();
    }

    public static String getVersion(Context context) {
        final String unknown = "Unknown";
        try {
            return context.getPackageManager()
                   .getPackageInfo(context.getPackageName(), 0)
                   .versionName;
        } catch(NameNotFoundException ex) {
        }
        return unknown;
    }

}
