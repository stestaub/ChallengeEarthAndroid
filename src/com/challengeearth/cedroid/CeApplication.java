package com.challengeearth.cedroid;

import java.net.ResponseCache;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.challengeearth.cedroid.caching.CEResponseCache;
import com.challengeearth.cedroid.helpers.DrawableManager;
import com.challengeearth.cedroid.services.TrackingService;
import com.challengeearth.cedroid.services.UpdateService;

public class CeApplication extends Application {
	
	private static final String TAG = "CeApplication";
	private static final long DEFAULT_UPDATE_INTERVAL = 50000; 
	
	private boolean updaterRunning;
	private boolean trackingActive;
	private ChallengeData challengeData;
	private ActivityData activityData;
	
	private Intent updaterServiceIntent;
	private PendingIntent updaterPendingIntent;
	
	private static DrawableManager imageLoader = new DrawableManager();

	private SharedPreferences prefs;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		// Setting up caching
		ResponseCache.setDefault(new CEResponseCache());
		
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.challengeData = new ChallengeData(this);
		this.activityData = new ActivityData(this);
		
		// Prepare Intent for UpdaterService
		updaterServiceIntent = new Intent(this, UpdateService.class);
		updaterPendingIntent = PendingIntent.getService(this, -1, updaterServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	public SharedPreferences getPreferences() {
		return this.prefs;
	}
	
	public boolean isTrackingActive() {
		return this.trackingActive;
	}
	
	public boolean isUpdaterRunning() {
		return updaterRunning;
	}
	
	public ChallengeData getChallengeData() {
		return this.challengeData;
	}
	
	public ActivityData getActivityData() {
		return this.activityData;
	}
	
	public long getUpdateInterval() {
		return DEFAULT_UPDATE_INTERVAL;
	}
	
	/**
	 * This only sets the status of the updater and does not affect the service itself
	 * 
	 * @param updaterRunning
	 */
	public void setUpdaterRunning(boolean updaterRunning) {
		this.updaterRunning = updaterRunning;
	}


	/**
	 * Starts the challenge with the given id;
	 * 
	 * @param id
	 * 		The id of the challenge for that tracking will be started
	 */
	public void startChallenge(long id) {
		Log.d(TAG, "Start tracking for challenge: " + id);
		challengeData.setChallengeStatus(id, true);
		this.trackingActive = true;
		this.startTracking();
	}
		
	/**
	 * Stops the challenge with the given id;
	 * 
	 * @param id
	 * 		The id of the challenge for that tracking will be stoped
	 */
	public void stopChallenge(long id) {
		Log.d(TAG, "Stop tracking for challenge: " + id);
		challengeData.setChallengeStatus(id, false);
	}
	
	public void startPeriodicUpdates() {
		if(updaterPendingIntent == null) {
			throw new Error(TAG + ": updaterPendingIntent is null");
		}
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 
				System.currentTimeMillis() + 15000, this.getUpdateInterval(), updaterPendingIntent);
		Log.d(TAG, "starting periodic updates now");
	}
	
	/**
	 * This method will stop the periodic updates, but only stops the updates if no Challenges
	 * are active. This method can be called, when the app is closed.
	 * 
	 * To force a stop of the Updates, set the flag forceStop true. This can be used when no network
	 * access is available.
	 * 
	 * @param forceStop
	 * 		Stops the periodic updates, even if tracking is active.
	 * 
	 */
	public void requestStopUpdates(boolean forceStop) {
		if(forceStop || (challengeData.activeChallengeCount() == 0)) {
			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarmManager.cancel(updaterPendingIntent);
			Log.d(TAG, "stoping periodic updates now");
		}
	}
	
	/**
	 * Starts the tracking service
	 */
	public void startTracking() {
		this.trackingActive = true;
		startService(new Intent(this, TrackingService.class));
	}
	
	/**
	 * This method will stop the tracking, but only stops the tracking if no Challenges
	 * are active. This method can be called, when the app is closed.
	 * 
	 * To force a stop of the tracking, set the flag forceStop true
	 * 
	 * @param forceStop
	 * 		Stops the periodic tracking, even if tracking is active.
	 * 
	 */
	public void requestStopTracking(boolean forceStop) {
		if(forceStop || (challengeData.activeChallengeCount() == 0)) {
			this.trackingActive = false;
			stopService(new Intent(this, TrackingService.class));
		}
	}

	public static DrawableManager getImageLoader() {
		return imageLoader;
	}
}
