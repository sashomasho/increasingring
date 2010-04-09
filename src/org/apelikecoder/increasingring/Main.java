package org.apelikecoder.increasingring;

import org.apelikecoder.increasingring.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class Main extends TabActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabview_main);
        //mTabHost = (TabHost)findViewById(android.R.id.tabhost);
        //mTabHost.setup();
        //mTabHost.setup(getLocalActivityManager()); //XXX
        TabHost tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("tab1")
                    .setIndicator("Ringer", getResources().getDrawable(R.drawable.ic_tab_ringer))
                    .setContent(new Intent(this, RingerSettings.class)));
        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator("Alarm", getResources().getDrawable(R.drawable.ic_tab_alarm))
                .setContent(new Intent(this, AlarmSettings.class)));
        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator("About", getResources().getDrawable(android.R.drawable.ic_dialog_info))
                .setContent(new Intent(this, About.class)));
        
    }

}
