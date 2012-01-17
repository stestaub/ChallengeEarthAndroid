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
	static final int VERSION = 3	;
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
		
		db.execSQL("create table " + ActivityData.TABLE + " (" + ActivityData.C_ID + " integer primary key autoincrement, " +
				ActivityData.C_LATITUDE + " double, " +
				ActivityData.C_LONGITUDE + " double," +
				ActivityData.C_TIMESTAMP + " long)");
		
		db.execSQL("create table " + ActivityData.TABLE_CHALLENGE + " (" + ActivityData.C_ACT_ID + " integer, " +
				ActivityData.C_CHALL_ID + " long)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table " + ChallengeData.TABLE);
		db.execSQL("drop table " + ActivityData.TABLE);
		db.execSQL("drop table " + ActivityData.TABLE_CHALLENGE);
		this.onCreate(db);
	}
}
