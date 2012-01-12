package com.challengeearth.cedroid.receivers;

import com.challengeearth.cedroid.services.UpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver {

	private static final String TAG = "NetworkReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		boolean isNetworkDown = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		
		if(isNetworkDown) {
			Log.d(TAG, "No internet connection. Stoping Updater Service");
			context.stopService(new Intent(context, UpdateService.class));
		}
		else {
			Log.d(TAG, "internet connection available, starting updater service");
			context.startService(new Intent(context, UpdateService.class));
		}
	}

}
