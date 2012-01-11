package com.challengeearth.cedroid;

import java.net.URL;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

/**
 * This is the Activity that shows an overview over all Available challenges.
 * 
 * @author Stefan Staub
 *
 */
public class OverviewActivity extends Activity {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private ListView challengeList;
    private Cursor cursor;
    private SimpleCursorAdapter adapter;
    
    // Challenge Updates
    private ChallengeUpdatesReceiver receiver;
    private IntentFilter filter;
    
    private static final String TAG = "OverviewActivity";
    
    static final String[] MAP_FROM = {ChallengeData.C_TITLE, ChallengeData.C_DESC, ChallengeData.C_IMAGE};
    static final int[] MAP_TO = {R.id.title, R.id.description, R.id.image};
	
    /**
	 * View Binder to load the images from the internet
	 */
    static final ViewBinder VIEW_BINDER = new ViewBinder() {
		
		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if(view.getId() != R.id.image) {
				return false;
			}
			AdapterImageLoader imageLoader = new AdapterImageLoader(null);
			try {
				imageLoader.addImage(new URL(cursor.getString(columnIndex)), (ImageView)view);
			}
			catch (Exception e) {
				Log.e(TAG, "could not load image", e);
			}
			return true;
		}
	};
	
	class ChallengeUpdatesReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			cursor.requery();
			adapter.notifyDataSetChanged();
			Log.d(TAG, "ChallengeUpdate Received");
		}
		
	}
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    // Prepare Layout
	    setContentView(R.layout.overview);
	    this.challengeList = (ListView) findViewById(R.id.listView1);
	    
	    startService(new Intent(this, UpdateService.class));
	    
	    // Prepare Database
	    this.dbHelper = new DbHelper(this);
	    this.db = this.dbHelper.getReadableDatabase();
	    
	    // Prepare Listener for updates
	    this.receiver = new ChallengeUpdatesReceiver();
	    this.filter = new IntentFilter("com.challengeearth.NEW_CHALLENGES");
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.db.close();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// prepare the data
		this.cursor = this.db.query(ChallengeData.TABLE, null, null, null, null, null, null);
		startManagingCursor(cursor);
		
		// set the adapter
		this.adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, MAP_FROM, MAP_TO);
		this.adapter.setViewBinder(VIEW_BINDER);
		this.challengeList.setAdapter(this.adapter);
		
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
	}
	
}