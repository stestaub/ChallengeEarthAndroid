package com.challengeearth.cedroid.receivers;

import com.challengeearth.cedroid.services.UpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * This class is registered as BootReceiver and starts the updater Servcie
 * as soon as the Android System has started
 *  
 * @author Stefan Staub
 */
public class BootReceiver extends BroadcastReceiver {

	private static final String TAG = "BootReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, UpdateService.class));
		Log.d(TAG, "onReceived");
	}

}
