package com.challengeearth.cedroid.services;

import com.challengeearth.cedroid.CeApplication;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * This class loads all available Challenges
 * 
 * @author Stefan Staub
 */
public class UpdateService extends IntentService {
	
	private static final String TAG = "UpdaterService";
	public static final String NEW_CHALLENGES = "com.challengeearth.NEW_CHALLENGES";
	public static final String ACTIVITIES_SYNCHRONIZED = "com.challengeearth.ACTIVITIES_SYNCHRONIZED";
	
	public UpdateService() {
		super(TAG);
		Log.d(TAG, "Service created");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		CeApplication application = (CeApplication) getApplication();
		int updates = application.fetchAvailableChallenges();
		if(updates > 0){
			intent = new Intent(NEW_CHALLENGES);
			sendBroadcast(intent);
		} 
		application.syncActivityData();
		intent = new Intent(ACTIVITIES_SYNCHRONIZED);
		sendBroadcast(intent);
		Log.d(TAG, "update done");
	}
}
