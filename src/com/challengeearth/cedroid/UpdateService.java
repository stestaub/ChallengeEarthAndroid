package com.challengeearth.cedroid;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * This class loads all available Challenges
 * 
 * @author Stefan Staub
 */
public class UpdateService extends IntentService {
	
	private static final String TAG = "UpdaterService";
	private static final String NEW_CHALLENGES = "com.challengeearth.NEW_CHALLENGES";
	
	public UpdateService() {
		super(TAG);
		Log.d(TAG, "Service created");
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(TAG, "onBind");
		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		CeApplication application = (CeApplication) getApplication();
		int updates = application.fetchAvailableChallenges();
		if(updates >0) {
			intent = new Intent(NEW_CHALLENGES);
			sendBroadcast(intent);
		}
		Log.d(TAG, "update done");
	}
}
