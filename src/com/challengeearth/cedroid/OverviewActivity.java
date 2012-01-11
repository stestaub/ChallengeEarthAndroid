package com.challengeearth.cedroid;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.challengeearth.cedroid.ChallengeData.DbHelper;

public class OverviewActivity extends Activity {
    DbHelper dbHelper;
    SQLiteDatabase db;
    ListView challengeList;
    Cursor cursor;
    SimpleCursorAdapter adapter;
    
    static final String[] MAP_FROM = {ChallengeData.C_TITLE, ChallengeData.C_DESC};
    static final int[] MAP_TO = {R.id.title, R.id.description};
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview);
        startService(new Intent(this, UpdateService.class));
        
        challengeList = (ListView) findViewById(R.id.listView1);
        
        dbHelper = new DbHelper(this);
        db = dbHelper.getReadableDatabase();
        
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		cursor = db.query(ChallengeData.TABLE, null, null, null, null, null, null);
		startManagingCursor(cursor);
		adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, MAP_FROM, MAP_TO);
		challengeList.setAdapter(adapter);
	}
    
    
}