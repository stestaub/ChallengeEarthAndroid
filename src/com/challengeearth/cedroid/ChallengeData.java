package com.challengeearth.cedroid;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Class to access the challenge Data that is stored in the
 * database
 * 
 * @author Stefan Staub
 *
 */
public class ChallengeData {

	private static final String TAG = "ChallengeData";
	
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
	private List<SQLiteDatabase> openDbs;
	
	public ChallengeData(Context context) {
		this.dbHelper = DbHelper.getInstance(context);
		openDbs = new LinkedList<SQLiteDatabase>();
		Log.i(TAG, "Data initialized");
	}
	
	/**
	 * Close all db connections
	 */
	public void close() {
		for(SQLiteDatabase db:openDbs) {
			db.close();
		}
	}
	
	/**
	 * Inserts the content values. Any exceptions while inserting
	 * will be ignored.
	 * 
	 * @param values
	 * 		The values that should be inserted in the challenge table
	 * 
	 * @return
	 * 		Returns if the values are inserted or not
	 */
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
	
	/**
	 * Returns a cursor on all available challenges
	 * 
	 * @return
	 * 		Cursor
	 */
	public Cursor getAvailableChallenges() {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		openDbs.add(db);
		return db.query(TABLE, null, null, null, null, null, null);
	}
	
	/**
	 * Returns a cursor to the challenge with the given id.
	 * The cursor is pointing to -1 at this point so you must
	 * move the cursor to the first entry.
	 * 
	 * @param id
	 * 		Id of the challenge you want
	 * 
	 * @return
	 * 		Cursor
	 */
	public Cursor getChallengeById(long id) {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		String[] selectArgs = {Long.toString(id)};
		openDbs.add(db);
		return db.query(TABLE, null, C_ID + " = ?", selectArgs, null, null, null);
	}
	
	/**
	 * Returns the amount of challenges where the active flag is set
	 * 
	 * @return
	 */
	public int activeChallengeCount() {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE, new String [] {C_ID}, C_ACTIVE + "= 1", null, null, null, null);
		activeCount = cursor.getCount();
		cursor.close();
		db.close();
		return activeCount;
	}
	
	/**
	 * 
	 * @param id
	 * @param values
	 */
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
