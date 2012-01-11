package com.challengeearth.cedroid;

import java.io.BufferedReader;
import java.util.List;

import com.challengeearth.cedroid.helpers.JSONParser;
import com.challengeearth.cedroid.helpers.NetworkUtilities;

import android.app.Application;
import android.content.ContentValues;
import android.util.Log;

public class CeApplication extends Application {

	private static final String TAG = "CeApplication";
	
	private boolean updaterRunning;
	private ChallengeData challengeData;
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.challengeData = new ChallengeData(this);
	}

	public boolean isUpdaterRunning() {
		return updaterRunning;
	}
	
	public ChallengeData getChallengeData() {
		return this.challengeData;
	}
	
	/**
	 * This only sets the status of the updater and does not affect the service itself
	 * 
	 * @param updaterRunning
	 */
	public void setUpdaterRunning(boolean updaterRunning) {
		this.updaterRunning = updaterRunning;
	}
	
	public synchronized int fetchAvailableChallenges() {
		int count = 0;
		try {
			BufferedReader reader = NetworkUtilities.getGETReader("challenge");
			JSONParser<Challenge> challengeParser = new JSONParser<Challenge>(reader, Challenge.class);
			List<Challenge> challenges = challengeParser.parseList();
			
			ContentValues values = new ContentValues();
			
			for(Challenge c:challenges) {
				values.put(ChallengeData.C_ID, c.getId());
				values.put(ChallengeData.C_TITLE, c.getTitle());
				values.put(ChallengeData.C_DESC, c.getDescription());
				values.put(ChallengeData.C_IMAGE, c.getImageUrl());
				values.put(ChallengeData.C_LATITUDE, c.getLatitude());
				values.put(ChallengeData.C_LONGITUDE, c.getLongitude());
				
				count = challengeData.insertOrIgnore(values)?count+1:count;
			}
			
		} catch (Exception e) {
			Log.e(TAG, "Failed to read available challenges", e);
		}
		return count;
	}
	
}
