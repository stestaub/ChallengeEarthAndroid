package com.challengeearth.cedroid.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.challengeearth.cedroid.helpers.IJSONParsable;

public class Activity implements IJSONParsable {

	private static final String TAG = "Activity";
	
	private long[] challengeId;
	private double latitude;
    private double longitude;
    private int velocity;
    private int accuracy;
    private int altitude;
    private long timestamp;
    private long userId;
	
	
	public long[] getChallengeId() {
		return challengeId;
	}

	public void setChallengeId(long[] challengeId) {
		this.challengeId = challengeId;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getVelocity() {
		return velocity;
	}

	public void setVelocity(int velocity) {
		this.velocity = velocity;
	}

	public int getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	@Override
	public void parse(JSONObject json) throws JSONException {
		Log.e(TAG, "parsing of an Activity is not supported");
	}

	@Override
	public JSONObject getJson() throws JSONException {
		JSONObject json = new JSONObject();
		
		JSONArray challenges = new JSONArray();
		for(long id : challengeId) {
			challenges.put(id);
		}
		
		json.put("challengeId", challenges);
		json.put("latitude", latitude);
		json.put("longitude", longitude);
		json.put("accuracy", accuracy);
		json.put("dateTime", timestamp);
		json.put("altitude", altitude);
		json.put("userId", userId);
		return json;
	}

}
