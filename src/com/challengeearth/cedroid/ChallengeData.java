package com.challengeearth.cedroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
	
	static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE, null, VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG, "creating db: " + DATABASE);
			db.execSQL("create table " + TABLE + " (" + C_ID + " long primary key, " +
					C_TITLE + " text, " +
					C_DESC + " text," +
					C_ACTIVE + " boolean, " +
					C_LATITUDE + " double," +
					C_LONGITUDE + " double," +
					C_PROGRESS + " int," +
					C_IMAGE + " text)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table" + TABLE);
			this.onCreate(db);
		}
	}
	
	private final DbHelper dbHelper;
	
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
}
