package org.apelikecoder.turnup;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

public class About extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        TextView tv = (TextView) findViewById(R.id.webbage);
        tv.setText(getString(R.string.about_text).replace("\\n","\n").replace("${VERSION}", getVersion(this)));
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
