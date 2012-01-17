package com.challengeearth.cedroid;

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
	public void insertActivity(ContentValues values, long[] challenges) {
		Log.d(TAG, "Insert Activitydata: " + values);
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		long activityId = db.insert(TABLE, null, values);
		ContentValues act_chal_ref = new ContentValues(2);
		for(long id:challenges) {
			act_chal_ref.put(C_ACT_ID, activityId);
			act_chal_ref.put(C_CHALL_ID, id);
			db.insert(TABLE_CHALLENGE, null, act_chal_ref);
		}
		db.close();
	}
	
	public Cursor getCachedActivityData() {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE, null, null, null, null, null, C_TIMESTAMP + " ASC");
		return cursor;
	}
	
	public long[] getChallengeIdForActivity(int activityId) {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CHALLENGE, new String[] {C_CHALL_ID},
				C_ACT_ID + " = ?", new String[] {Integer.toString(activityId)}, null, null, null);
		long[] challenges = new long[cursor.getCount()];
		while(cursor.moveToNext()) {
			challenges[cursor.getPosition()] = cursor.getLong(cursor.getColumnIndex(C_CHALL_ID));
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
}
