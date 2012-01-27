package com.challengeearth.cedroid;

import com.challengeearth.cedroid.model.ChallengeAttemptHash;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ActivityData {
	private static final String TAG ="ActivityData";
	
	public static final String TABLE = "activity";
	
	public static final String C_ID = "_id";
	public static final String C_TIMESTAMP = "timestamp";
	public static final String C_LONGITUDE = "longitude";
	public static final String C_LATITUDE = "latitude";
	
	public static final String TABLE_CHALLENGE = "act_chall";
	
	public static final String C_CHALL_ID = "_cid";
	public static final String C_ACT_ID = "_aid";
	public static final String C_ATTEMPT_HASH = "hash";
	
	
	private final DbHelper dbHelper;
	
	public ActivityData(Context context) {
		this.dbHelper = new DbHelper(context);
		Log.i(TAG, "Database initialized");
	}
	
	/**
	 * Close all db connections
	 * TODO: move in superclass? same code as in ChallengeData
	 */
	public void close() {
		dbHelper.close();
	}
	
	/**
	 * Insert values in the activity table for the challenges given in the array.
	 * if the challenge array is empty, the value will be overwritten the next time.
	 *  
	 * @param values
	 * 		The activity values that should be stored
	 * 
	 * @param challenges
	 * 		the challenges this activity belongs to.
	 */
	public void insertActivity(ContentValues values, ChallengeAttemptHash[] challenges) {
		Log.d(TAG, "Insert Activitydata: " + values);
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		long activityId = db.insert(TABLE, null, values);
		ContentValues act_chal_ref = new ContentValues(2);
		for(ChallengeAttemptHash challenge:challenges) {
			act_chal_ref.put(C_ACT_ID, activityId);
			act_chal_ref.put(C_CHALL_ID, challenge.id);
			act_chal_ref.put(C_ATTEMPT_HASH, challenge.hash);
			db.insert(TABLE_CHALLENGE, null, act_chal_ref);
		}
		db.close();
	}
	
	public Cursor getCachedActivityData() {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE, null, null, null, null, null, C_TIMESTAMP + " ASC");
		return cursor;
	}
	
	public ChallengeAttemptHash[] getChallengeIdHashForActivity(int activityId) {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CHALLENGE, new String[] {C_CHALL_ID, C_ATTEMPT_HASH},
				C_ACT_ID + " = ?", new String[] {Integer.toString(activityId)}, null, null, null);
		ChallengeAttemptHash[] challenges = new ChallengeAttemptHash[cursor.getCount()];
		while(cursor.moveToNext()) {
			ChallengeAttemptHash challenge = new ChallengeAttemptHash();
			challenge.id = cursor.getLong(cursor.getColumnIndex(C_CHALL_ID));
			challenge.hash = cursor.getString(cursor.getColumnIndex(C_ATTEMPT_HASH));
			
			challenges[cursor.getPosition()] = challenge;
		}
		cursor.close();
		db.close();
		return challenges;
	}
	
	public void removeActivityData(int id) {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		String[] args =new String [] {Integer.toString(id)};
		db.delete(TABLE, C_ID + " = ?", args);
		db.delete(TABLE_CHALLENGE, C_ACT_ID + " = ?", args);
		db.close();
	}
	
	public void removeActivityDataForChallenge(long challengeId) {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		String[] args =new String [] {Long.toString(challengeId)};
		db.delete(TABLE_CHALLENGE, C_CHALL_ID + " = ?", args);
		db.close();
	}
}
