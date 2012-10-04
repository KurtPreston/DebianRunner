package org.cs523.debianrunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cs523.debianrunner.R;
import org.cs523.system.NativeTask;

import android.androidVNC.VncConstants;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class SelectApplicationActivity extends Activity {

	// Connection constants
	String host = "localhost";
	int port = 5900;
	String password = "password";
	
	// Layout elements
	ListView installedAppList;
	Button button;
	EditText newAppTextField;
	Button launchVncBtn;
	Button bootDebBtn;

	// Installed program data
	ArrayAdapter<String> installedAppAdapter;
	protected static final int REMOVE_APP_NO_ID = 0;
	protected static final int REMOVE_APP_YES_ID = 1;

	// Settings
	SharedPreferences settings;
	public static final String PREFS_NAME = "InstalledDebianAppsListFile";
	public static final String INSTALLED_APPS_PREF = "installedApps";

	// Methods
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set layout
		setContentView(R.layout.selectapp);
		installedAppList = (ListView) findViewById(R.id.ListView01);
		button = (Button) findViewById(R.id.Button01);
		newAppTextField = (EditText) findViewById(R.id.EditText01);
		bootDebBtn = (Button) findViewById(R.id.Button02);
		launchVncBtn = (Button) findViewById(R.id.Button03);

		// Setup adapter
		List<String> blankList = new ArrayList<String>();
		installedAppAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,blankList);
		installedAppList.setAdapter(installedAppAdapter);

		// Get and restore list of installed apps
		settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		String installedAppsBlock = settings.getString(INSTALLED_APPS_PREF,"");
		List<String> installedAppsList = Arrays.asList(installedAppsBlock.split(";"));
		for(String thisApp : installedAppsList)
		{
			if(thisApp.length() > 0) {
				installedAppAdapter.add(thisApp);
			}
		}
		installedAppList.setSelectionAfterHeaderView();

		// Add button listener
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Perform action on clicks
				String newAppText = newAppTextField.getText().toString();
				installApplication(newAppText);
			}
		});
		

		// Add boot deb activity
		bootDebBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Perform action on clicks
				runSysCall("/sdcard/debian/bootdeb");
			}
		});
		
		// Add launch vnc activity
		launchVncBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Perform action on clicks
				runSysCall("/sdcard/debian/vncstart_320x480");
			}
		});

		// Add text entry listener
		newAppTextField.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				String newAppText = newAppTextField.getText().toString();

				if(keyCode == KeyEvent.KEYCODE_ENTER)
				{
					// On enter, install
					newAppTextField.setText("");
					installedAppAdapter.getFilter().filter("");
					installApplication(newAppText);
					return true;
				}
				else if (keyCode == KeyEvent.KEYCODE_SPACE || 
						keyCode == KeyEvent.KEYCODE_SEMICOLON)
				{
					return true;
				}
				else
				{
					// On other key presses, filter
					installedAppAdapter.getFilter().filter(newAppText);
					return false;
				}
			}
		});


		// Add list click listener
		installedAppList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				// Perform action on clicks
				String runAppText = installedAppList.getItemAtPosition(pos).toString();
				runApplication(runAppText);
			}
		});

		// Add list context menu
		installedAppList.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				AdapterView.AdapterContextMenuInfo info =
					(AdapterView.AdapterContextMenuInfo) menuInfo;
				String selectedWord = ((TextView) info.targetView).getText().toString();

				menu.setHeaderTitle("Remove " + selectedWord + " ?");
				menu.add(0,REMOVE_APP_NO_ID,0,"No");
				menu.add(0,REMOVE_APP_YES_ID,0,"Yes");
			}
		});
		
		// Add launch VNC function
	}

	@Override
	protected void onStop(){
		super.onStop();
		syncPreferences();
	}


	private void installApplication(String appName)
	{
		if(appName.length() > 0)
		{
			int appPos = installedAppAdapter.getPosition(appName);
			if(appPos == -1)
			{
				// Install if app is not in list
				Toast.makeText(SelectApplicationActivity.this, "Installing " + appName, Toast.LENGTH_SHORT).show();
				installedAppAdapter.add(appName);
				installApp(appName);
			}
			else
			{
				// Otherwise, run installed app
				runApplication(appName);
			}

		}
	}

	private void runApplication(String appName)
	{
		Toast.makeText(SelectApplicationActivity.this, "Running " + appName, Toast.LENGTH_SHORT).show();
		runApp(appName);
		launchVNC();
	}

	private void uninstallApplication(String appName)
	{
		Toast.makeText(SelectApplicationActivity.this, "Uninstalling " + appName, Toast.LENGTH_SHORT).show();
		installedAppAdapter.remove(appName);
		unInstallApp(appName);
	}


	@Override
	public boolean onContextItemSelected(MenuItem menuItem) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) menuItem.getMenuInfo();

		// Switch on the ID of the item, to get what the user selected.
		switch (menuItem.getItemId()) {
		case REMOVE_APP_YES_ID:
			String removedAppName = installedAppAdapter.getItem(menuInfo.position).toString();
			uninstallApplication(removedAppName);
			return true;
		}
		return false;
	}

	private void launchVNC() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setClassName("android.androidVNC", "android.androidVNC.VncCanvasActivity");
		intent.putExtra(VncConstants.HOST, host);
		intent.putExtra(VncConstants.PORT, port);
		intent.putExtra(VncConstants.PASSWORD, password);
		intent.putExtra(VncConstants.ID, "");
		intent.putExtra(VncConstants.COLORMODEL, 0);
		this.startActivity(intent);
	}
	
	private boolean installApp(String appName) {
		
		boolean didSucceed = runSysCall("export INSTALL_APP=" + appName);
		
		if(!didSucceed) {
			return false;
		}
		
		didSucceed = runSysCall("su <./sdcard/debian/install_app");
		syncPreferences();
		return didSucceed;
	}

	private boolean unInstallApp(String appName) {
		
		boolean didSucceed = runSysCall("export UNINSTALL_APP=" + appName);
		syncPreferences();
		if(!didSucceed) {
			return false;
		}
		
		didSucceed = runSysCall("su <./sdcard/debian/uninstall_app");
		
		return didSucceed;
	}
	
	private boolean runApp(String appName) {
		boolean didSucceed = runSysCall("export RUN_APP=" + appName);
		
		if(!didSucceed) {
			return false;
		}
		
		didSucceed = runSysCall("su <./sdcard/debian/run_app");
		return didSucceed;
	}
	
	private boolean runSysCall(String command)
	{	
    	if (NativeTask.runCommand(command) == 0) {
    		Toast.makeText(SelectApplicationActivity.this, command + "\nSUCCEEDED", Toast.LENGTH_SHORT).show();
            return true;
    	}
    	else {
    		Toast.makeText(SelectApplicationActivity.this, command + "\nFAILED", Toast.LENGTH_SHORT).show();
            return false;
    	}
	}
	
	private void syncPreferences() {

		// Save user preferences. We need an Editor object to
		// make changes. All objects are from android.context.Context
		StringBuilder marshalledAppListBuilder = new StringBuilder();
		for(int i=0 ; i<installedAppAdapter.getCount() ; i++)
		{
			marshalledAppListBuilder.append(installedAppAdapter.getItem(i).toString());
			marshalledAppListBuilder.append(";");
		}
		String marshalledAppList = marshalledAppListBuilder.toString();
		if(marshalledAppList.length() > 1)
			marshalledAppList = marshalledAppList.substring(0,marshalledAppList.length() - 1);

		SharedPreferences.Editor editor = settings.edit();
		editor.putString(INSTALLED_APPS_PREF,marshalledAppList);

		// Don't forget to commit your edits!!!
		editor.commit();		
	}

}
