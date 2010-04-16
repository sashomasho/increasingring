package org.apelikecoder.increasingring;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

public class IncomingCallListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            if (sp.getBoolean(PreferenceKeys.Ringer.ENABLE_SERVICE, false)
                    ||  sp.getBoolean(PreferenceKeys.Ringer.ENABLE_PICKUP_ATT, false)
                )
                context.startService(new Intent(context, RingVolumeControlService.class));
            else
                android.util.Log.d("IncominCallListener", "SERVICE NOT ENABLED");
        }
    }
}