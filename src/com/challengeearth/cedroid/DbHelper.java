package com.challengeearth.cedroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Class to handle the creation and Access of the db
 * 
 * @author Stefan Staub
 *
 */
public class DbHelper extends SQLiteOpenHelper {

	private static final String TAG = "DbHelper";
	static final int VERSION = 1;
	static final String DATABASE = "challenge.db";
	
	public DbHelper(Context context) {
		super(context, DATABASE, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "creating db: " + DATABASE);
		db.execSQL("create table " + ChallengeData.TABLE + " (" + ChallengeData.C_ID + " long primary key, " +
				ChallengeData.C_TITLE + " text, " +
				ChallengeData.C_DESC + " text," +
				ChallengeData.C_ACTIVE + " boolean, " +
				ChallengeData.C_LATITUDE + " double," +
				ChallengeData.C_LONGITUDE + " double," +
				ChallengeData.C_PROGRESS + " int," +
				ChallengeData.C_IMAGE + " text)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table" + ChallengeData.TABLE);
		this.onCreate(db);
	}
}
