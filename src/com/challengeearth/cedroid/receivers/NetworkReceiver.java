package com.challengeearth.cedroid.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.challengeearth.cedroid.CeApplication;
import com.challengeearth.cedroid.services.UpdateService;

public class NetworkReceiver extends BroadcastReceiver {

	private static final String TAG = "NetworkReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		boolean isNetworkDown = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		CeApplication application = (CeApplication) context.getApplicationContext();
		if(isNetworkDown) {
			Log.d(TAG, "No internet connection. Stoping Updater Service");
			context.stopService(new Intent(context, UpdateService.class));
			application.requestStopUpdates(true);
		}
		else {
			Log.d(TAG, "internet connection available, starting updater service");
			context.startService(new Intent(context, UpdateService.class));
			if(application.isTrackingActive()) {
				application.startPeriodicUpdates();
			}
		}
	}

}
