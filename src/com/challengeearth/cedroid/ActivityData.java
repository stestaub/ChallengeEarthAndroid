package com.challengeearth.cedroid;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
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
	private List<SQLiteDatabase> openDbs;
	
	public ActivityData(Context context) {
		this.dbHelper = DbHelper.getInstance(context);
		openDbs = new LinkedList<SQLiteDatabase>();
		Log.i(TAG, "Database initialized");
	}
	
	/**
	 * Close all db connections
	 * TODO: move in superclass? same code as in ChallengeData
	 */
	public void close() {
		for(SQLiteDatabase db:openDbs) {
			db.close();
		}
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
}
