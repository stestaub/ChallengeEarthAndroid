package com.challengeearth.cedroid;

import java.io.BufferedReader;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.challengeearth.cedroid.helpers.JSONParser;
import com.challengeearth.cedroid.helpers.NetworkUtilities;
import com.challengeearth.cedroid.model.Challenge;
import com.challengeearth.cedroid.services.TrackingService;

public class CeApplication extends Application {

	private static final String TAG = "CeApplication";
	
	private boolean updaterRunning;
	private ChallengeData challengeData;
	private ActivityData activityData;

	private SharedPreferences prefs;
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.challengeData = new ChallengeData(this);
		this.activityData = new ActivityData(this);
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
	
	/**
	 * This only sets the status of the updater and does not affect the service itself
	 * 
	 * @param updaterRunning
	 */
	public void setUpdaterRunning(boolean updaterRunning) {
		this.updaterRunning = updaterRunning;
	}
	
	/**
	 * Fetches the challenges from ChallengeEarth. Use this in a seperate thread.
	 * 
	 * @return
	 * 		The number of challenges that are fetched.
	 */
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
	
	/**
	 * Synchronizes the Activity Data to the server.
	 */
	public synchronized void syncActivityData() {
		Cursor cursor = activityData.getCachedActivityData();
		while(cursor.moveToNext()) {
			
			JSONObject json = new JSONObject();
			JSONArray challenges = new JSONArray();
			long[] challIds = activityData.getChallengeIdForActivity(cursor.getInt(cursor.getColumnIndex(ActivityData.C_ID)));
			for(long id : challIds) {
				challenges.put(id);
			}
			
			try {
				json.put("challengeId", challenges);
				json.put("latitude", cursor.getDouble(cursor.getColumnIndex(ActivityData.C_LATITUDE)));
				json.put("longitude", cursor.getDouble(cursor.getColumnIndex(ActivityData.C_LONGITUDE)));
				json.put("dateTime", cursor.getInt(cursor.getColumnIndex(ActivityData.C_TIMESTAMP)));
				json.put("accuracy", 50);
				json.put("userId", prefs.getString("userid", "1"));
				BufferedReader response = NetworkUtilities.doPost(json, "/challenge");
				if(response != null) {
					handleResponse(response);
				}
				activityData.removeActivityData(cursor.getInt(cursor.getColumnIndex(ActivityData.C_ID)));
			} catch (Exception e){
				Log.e(TAG, "could not send activity data", e);
			}
		}
		cursor.close();
		activityData.close();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void handleResponse(BufferedReader response) {
		JSONParser parser = new JSONParser(response, Void.class);
		String jsonString = parser.getJSONString();
		try {
			JSONObject object = new JSONObject(jsonString);
			Iterator it = object.keys();
			ContentValues values = new ContentValues();
			while(it.hasNext()) {
				long challengeId = Long.parseLong((String) it.next());
				JSONObject challengeInfo = object.getJSONObject(Long.toString(challengeId));
				Log.d(TAG, challengeInfo.toString());
				int progress = challengeInfo.getInt("progressPercentage");
				values.put(ChallengeData.C_PROGRESS, progress);
				challengeData.updateChallenge(challengeId, values);
				Log.d(TAG, "challenge updated with progress: " + progress);
			}
		} catch (JSONException e) {
			Log.e(TAG, "could not parse string to json object", e);
		}
		
		Log.d(TAG, "recieved content: " + jsonString);
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
		startService(new Intent(this, TrackingService.class));
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
		if(challengeData.activeChallengeCount() == 0) {
			stopService(new Intent(this, TrackingService.class));
		}
	}
}
