package com.challengeearth.cedroid;

import java.io.BufferedReader;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.challengeearth.cedroid.helpers.JSONParser;
import com.challengeearth.cedroid.helpers.NetworkUtilities;

/**
 * This class loads all available Challenges
 * 
 * @author Stefan Staub
 */
public class UpdateService extends Service {
	
	private static final String TAG = "UpdaterService";
	
	static final int UPDATE_DELAY = 60000;
	boolean runflag;
	private Updater updater;
	
	@Override
	public IBinder onBind(Intent arg0) {
		Log.d(TAG, "onBind");
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.updater = new Updater();
		Log.d(TAG, "onCreate");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		this.runflag = false;
		this.updater.interrupt();
		this.updater = null;
		
		Log.d(TAG, "onDestroy");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		
		this.runflag = true;
		this.updater.start();
		
		return START_STICKY;
	}
	
	private class Updater extends Thread {
		public Updater() {
			super("UpdaterService-UpdaterThread");
		}
		
		@Override
		public void run() {
			UpdateService updaterService = UpdateService.this;
			while(updaterService.runflag) {
				Log.d(TAG, "updater is running");
				try {
					// Do updates here
					try {
						BufferedReader reader = NetworkUtilities.getGETReader("challenge");
						JSONParser<Challenge> challengeParser = new JSONParser<Challenge>(reader, Challenge.class);
						List<Challenge> challenges = challengeParser.parseList();
						for(Challenge c:challenges) {
							Log.d(TAG, c.toString());
						}
					} catch (Exception e) {
						Log.e(TAG, "Failed to read available challenges", e);
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
