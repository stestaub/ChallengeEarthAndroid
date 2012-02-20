package com.challengeearth.cedroid.services;

import java.io.BufferedReader;
import java.util.List;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import com.challengeearth.cedroid.ActivityData;
import com.challengeearth.cedroid.CeApplication;
import com.challengeearth.cedroid.ChallengeData;
import com.challengeearth.cedroid.R;
import com.challengeearth.cedroid.helpers.JSONParser;
import com.challengeearth.cedroid.helpers.NetworkUtilities;
import com.challengeearth.cedroid.model.Challenge;
import com.challengeearth.cedroid.model.ChallengeAttemptHash;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

/**
 * This class loads all available Challenges
 * 
 * @author Stefan Staub
 */
public class UpdateService extends IntentService {
	
	private static final String TAG = "UpdaterService";
	public static final String NEW_CHALLENGES = "com.challengeearth.NEW_CHALLENGES";
	public static final String ACTIVITIES_SYNCHRONIZED = "com.challengeearth.ACTIVITIES_SYNCHRONIZED";
	public static final String CHALLENGE_DONE = "com.challengeearth.cedroid.CHALLENGE_DONE";
	public static final String NEW_CHALLENGE_PROGRESS = "com.challengeearth.cedroid.NEW_CHALLENGE_PROGRESS";
	
	private ChallengeData challengeData;
	private ActivityData activityData;
	private CeApplication application;
	
	public UpdateService() {
		super(TAG);
		Log.d(TAG, "Service created");
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
			
			challengeData.removeUnusedChallenges();
			ContentValues values = new ContentValues();
			
			for(Challenge c:challenges) {
				values.put(ChallengeData.C_ID, c.getId());
				values.put(ChallengeData.C_TITLE, c.getTitle());
				values.put(ChallengeData.C_DESC, c.getDescription());
				values.put(ChallengeData.C_UNUSED, true);
				values.put(ChallengeData.C_IMAGE, c.getImageUrl());
				values.put(ChallengeData.C_LATITUDE, c.getLatitude());
				values.put(ChallengeData.C_LONGITUDE, c.getLongitude());
				count = challengeData.insertOrIgnore(values)?count+1:count;
			}
			
		} catch (Exception e) {
			Log.e(TAG, "Failed to read available challenges", e);
		} finally {
			challengeData.close();
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
			ChallengeAttemptHash[] challIds = activityData.getChallengeIdHashForActivity(cursor.getInt(cursor.getColumnIndex(ActivityData.C_ID)));
			
			try {
				for(ChallengeAttemptHash challenge : challIds) {
					JSONObject idHashPair = new JSONObject();
					idHashPair.put("challengeId", challenge.id);
					idHashPair.put("attemptHash", challenge.hash);
					challenges.put(idHashPair);
				}
				json.put("challengeId", challenges);
				json.put("latitude", cursor.getDouble(cursor.getColumnIndex(ActivityData.C_LATITUDE)));
				json.put("longitude", cursor.getDouble(cursor.getColumnIndex(ActivityData.C_LONGITUDE)));
				json.put("dateTime", cursor.getLong(cursor.getColumnIndex(ActivityData.C_TIMESTAMP)));
				json.put("accuracy", 50);
				json.put("userId", application.getPreferences().getString("userid", "1"));
				HttpResponse response = NetworkUtilities.doPost(json, "/challenge");
				if(response != null && response.getStatusLine().getStatusCode() >= 200 
						&& response.getStatusLine().getStatusCode() < 300) {
					activityData.removeActivityData(cursor.getInt(cursor.getColumnIndex(ActivityData.C_ID)));
				}
			} catch (Exception e){
				Log.e(TAG, "could not send activity data", e);
			}
		}
		int newProgress = fetchChallengeProgresses();
		cursor.close();
		activityData.close();
		if(newProgress > 0) {
			Intent intent = new Intent(NEW_CHALLENGE_PROGRESS);
			sendBroadcast(intent);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public synchronized int fetchChallengeProgresses() {
		ChallengeAttemptHash[] active = challengeData.getActiveChallenges();
		ContentValues values = new ContentValues();
		int newProgress = 0;
		for(ChallengeAttemptHash challenge : active) {
			try {
				BufferedReader reader = NetworkUtilities.getGETReader("progress/"+challenge.id+
						"/"+application.getPreferences().getString("userid", "1")+"/"+
						challenge.hash);
				JSONParser parser = new JSONParser(reader, Void.class);
				String jsonString = parser.getJSONString();
				JSONObject progressObject = new JSONObject(jsonString);
				int progress = progressObject.getInt("progress");
				values.put(ChallengeData.C_PROGRESS, progress);
				newProgress += challengeData.updateChallengeProgress(challenge.id, values);
				Log.d(TAG, "challenge updated with progress: " + progress);
				if(progress == 100) {
					challengeFinishedEvent(challenge.id);
				}
			} catch (Exception e) {
				Log.e(TAG, "Error while fetching Challenge Progress", e);
			}
		}
		challengeData.close();
		return newProgress;
	}
	
	
	private void challengeFinishedEvent(long challengeId) {
		application.stopChallenge(challengeId);
		sendBroadcast(new Intent(CHALLENGE_DONE));
		Toast.makeText(this, R.string.infoChallengeDone, Toast.LENGTH_LONG);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		this.application = (CeApplication)getApplication();
		if(application == null) {
			throw new Error(TAG + ": Application is null");
		}
		
		challengeData = new ChallengeData(this);
		activityData = new ActivityData(this);
		
		int updates = fetchAvailableChallenges();
		if(updates > 0){
			intent = new Intent(NEW_CHALLENGES);
			sendBroadcast(intent);
		} 
		syncActivityData();
		Log.d(TAG, "update done");
	}
}
