package org.cs523.debianrunner;

import android.androidVNC.VncConstants;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ShowVNCActivity extends Activity {
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textview = new TextView(this);
        textview.setText("This is the VNC tab");
        setContentView(textview);
        canvasStart();
    }
    
	private void canvasStart() {
		
		// String ip = ipText.getText().toString();
		// int port = Integer.parseInt(portText.getText().toString());
		// String password = passwordText.getText().toString();
		// COLORMODEL model = (COLORMODEL) colorSpinner.getSelectedItem();
		
		String ip = "192.168.0.199";
		int port = 5902;
		String password = "android";
		ColorModelEnum model = ColorModelEnum.C24bit;
		
		vnc(this, ip, port, password, null, model);
	}
	
	private void vnc(final Context _context, final String host, final int port, final String password, final String repeaterID, final ColorModelEnum model) {
		/*
		MemoryInfo info = Utils.getMemoryInfo(_context);
		if (info.lowMemory) {
			// Low Memory situation.  Prompt.
			Utils.showYesNoPrompt(_context, "Continue?", "Android reports low system memory.\nContinue with VNC connection?", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					vnc_(_context, host, port, password, repeaterID, model);
				}
			}, null);
		} else*/
			vnc_(_context, host, port, password, repeaterID, model);
	}
		
	private void vnc_(Context _context, String host, int port, String password, String repeaterID, final ColorModelEnum colorModel) {
		 Intent intent = new Intent(Intent.ACTION_VIEW);
		 intent.setClassName("android.androidVNC", "android.androidVNC.VncCanvasActivity");
		 intent.putExtra(VncConstants.HOST, host);
		 intent.putExtra(VncConstants.PORT, port);
		 intent.putExtra(VncConstants.PASSWORD, password);
		 intent.putExtra(VncConstants.ID, repeaterID);
		 intent.putExtra(VncConstants.COLORMODEL, 0);
		 _context.startActivity(intent);
	}
}
