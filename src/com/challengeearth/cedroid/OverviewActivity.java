package com.challengeearth.cedroid;

import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;

import com.challengeearth.cedroid.ChallengeData.DbHelper;
import com.challengeearth.cedroid.helpers.AdapterImageLoader;

public class OverviewActivity extends Activity {
    DbHelper dbHelper;
    SQLiteDatabase db;
    ListView challengeList;
    Cursor cursor;
    SimpleCursorAdapter adapter;
    
    private static final String TAG = "OverviewActivity";
    
    static final String[] MAP_FROM = {ChallengeData.C_TITLE, ChallengeData.C_DESC, ChallengeData.C_IMAGE};
    static final int[] MAP_TO = {R.id.title, R.id.description, R.id.image};
	
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
		adapter.setViewBinder(VIEW_BINDER);
		challengeList.setAdapter(adapter);
	}
    
    static final ViewBinder VIEW_BINDER = new ViewBinder() {
		
		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if(view.getId() != R.id.image) {
				return false;
			}
			ImageView image = (ImageView)view;
			AdapterImageLoader imageLoader = new AdapterImageLoader(null);
			try {
				imageLoader.addImage(new URL(cursor.getString(columnIndex)), image);
			}
			catch (Exception e) {
				Log.e(TAG, "could not load image", e);
			}
			return true;
		}
	};
}