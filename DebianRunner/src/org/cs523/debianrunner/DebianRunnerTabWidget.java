package org.cs523.debianrunner;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class DebianRunnerTabWidget extends TabActivity {


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		TabHost tabs = getTabHost();

		Intent intent = new Intent();

		intent.setClass(this,SelectApplicationActivity.class);
		TabSpec selectTabSpec = tabs.newTabSpec("selectTab");
		selectTabSpec.setIndicator("Select/Install");
		selectTabSpec.setContent(intent);
		tabs.addTab(selectTabSpec);

        intent.setClass(this,ShowVNCActivity.class);
        TabSpec vncTabSpec = tabs.newTabSpec("vncTab");
        vncTabSpec.setIndicator("View");
        vncTabSpec.setContent(intent);
        tabs.addTab(vncTabSpec);

		tabs.setCurrentTab(0);
	}

}
