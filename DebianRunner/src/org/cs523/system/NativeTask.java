package org.cs523.system;

import android.util.Log;

public class NativeTask {

	public static final String MSG_TAG = "NativeTask";

	static {
		try {
			Log.i(MSG_TAG, "Trying to load libNativeTask.so");
			System.load("/data/data/org.cs523.finalproject/lib/libNativeTask.so");
		} catch (UnsatisfiedLinkError ule) {
			Log.e(MSG_TAG, "Could not load libNativeTask.so");
		}
	}

	public static native int runCommand(String command);
}
