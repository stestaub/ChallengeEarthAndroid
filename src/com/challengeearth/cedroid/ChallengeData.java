package com.challengeearth.cedroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ChallengeData {

	private static final String TAG = "ChallengeData";
	
	static final int VERSION = 1;
	static final String DATABASE = "challenge.db";
	static final String TABLE = "challenge";
	
	public static final String C_ID = "_id";
	public static final String C_TITLE = "title";
	public static final String C_DESC = "description";
	public static final String C_ACTIVE = "active";
	public static final String C_LATITUDE = "lat";
	public static final String C_LONGITUDE = "lon";
	public static final String C_PROGRESS = "progress";
	public static final String C_IMAGE = "image_url";
	
	private final DbHelper dbHelper;
	private int activeCount = -1;
	
	public ChallengeData(Context context) {
		this.dbHelper = new DbHelper(context);
		Log.i(TAG, "Data initialized");
	}
	
	public void close() {
		this.dbHelper.close();
	}
	
	public boolean insertOrIgnore(ContentValues values) {
		Log.d(TAG, "insertOrIgnre on " + values);
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		boolean inserted = false;
		try {
			db.insertOrThrow(TABLE, null, values);
			inserted = true;
		} catch(Exception e) {
			Log.w(TAG, "could not insert data in db: ", e);
		} finally {
			db.close();
		}
		return inserted;
	}
	
	public Cursor getAvailableChallenges() {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		return db.query(TABLE, null, null, null, null, null, null);
	}
	
	public Cursor getChallengeById(long id) {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		String[] selectArgs = {Long.toString(id)};
		return db.query(TABLE, null, C_ID + " = ?", selectArgs, null, null, null);
	}
	
	public int activeChallengeCount() {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE, new String [] {C_ID}, C_ACTIVE + "= 1", null, null, null, null);
		activeCount = cursor.getCount();
		cursor.close();
		db.close();
		return activeCount;
	}
	
	public void updateChallenge(long id, ContentValues values) {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		String[] selectArgs = {Long.toString(id)};
		db.update(TABLE, values, C_ID + " = ?", selectArgs);
		db.close();
	}
	
	public void setChallengeStatus(long id, boolean status) {
		ContentValues values = new ContentValues(1);
		values.put(ChallengeData.C_ACTIVE, status);
		updateChallenge(id, values);
	}
}
