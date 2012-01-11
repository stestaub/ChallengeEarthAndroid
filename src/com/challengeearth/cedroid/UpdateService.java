package com.challengeearth.cedroid;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * This class loads all available Challenges
 * 
 * @author Stefan Staub
 */
public class UpdateService extends Service {
	
	private static final String TAG = "UpdaterService";
	private static final String NEW_CHALLENGES = "com.challengeearth.NEW_CHALLENGES";
	
	static final int UPDATE_DELAY = 60000;
	boolean runflag;
	private Updater updater;
	private CeApplication application;
	
	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(TAG, "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.updater = new Updater();
		this.application = (CeApplication) getApplication();
		Log.d(TAG, "onCreate");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		this.runflag = false;
		this.updater.interrupt();
		this.updater = null;
		this.application.setUpdaterRunning(false);
		this.application = null;
		
		Log.d(TAG, "onDestroy");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		
		if(!runflag) {
			this.runflag = true;
			this.updater.start();
			this.application.setUpdaterRunning(true);
		}
		
		return START_STICKY;
	}
	
	/**
	 * This Updater is a backround Thread that loads periodically the new data from the server
	 * 
	 * @author Stefan Staub
	 *
	 */
	private class Updater extends Thread {
		Intent intent;
		
		public Updater() {
			super("UpdaterService-UpdaterThread");
		}
		
		@Override
		public void run() {
			UpdateService updaterService = UpdateService.this;
			while(updaterService.runflag) {
				Log.d(TAG, "updater is running");
				try {
					CeApplication application = (CeApplication) updaterService.getApplication();
					int updates = application.fetchAvailableChallenges();
					if(updates >0) {
						intent = new Intent(NEW_CHALLENGES);
						updaterService.sendBroadcast(intent);
					}
					Log.d(TAG, "update done");
					Thread.sleep(UPDATE_DELAY);
				} catch (InterruptedException e) {
					updaterService.runflag = false;
				}
			}
		}
	}
}
