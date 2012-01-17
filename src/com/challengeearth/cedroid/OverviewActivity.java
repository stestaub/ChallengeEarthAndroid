package com.challengeearth.cedroid;

import java.net.URL;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;

import com.challengeearth.cedroid.helpers.AdapterImageLoader;
import com.challengeearth.cedroid.services.UpdateService;

/**
 * This is the Activity that shows an overview over all Available challenges.
 * 
 * @author Stefan Staub
 *
 */
public class OverviewActivity extends BaseActivity {
	
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
	
	/**
	 * Broadcast Receiver receives broadcasts when new Challenges are available
	 * and updates the list
	 * 
	 * @author Stefan Staub
	 */
	class ChallengeUpdatesReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			adapter.getCursor().requery();
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
	    
	    // Prepare Listener for updates
	    this.receiver = new ChallengeUpdatesReceiver();
	    this.filter = new IntentFilter("com.challengeearth.NEW_CHALLENGES");
    }

	@Override
	protected void onResume() {
		super.onResume();
		
		// prepare the data
		this.cursor = challengeData.getAvailableChallenges();
		Log.d(TAG, "start managing cursor");
		
		// set the adapter
		this.adapter = new SimpleCursorAdapter(this, R.layout.row, this.cursor, MAP_FROM, MAP_TO);
		this.adapter.setViewBinder(VIEW_BINDER);
		this.challengeList.setAdapter(this.adapter);
		this.challengeList.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				cursor.moveToPosition(position);
				long c_id = cursor.getInt(cursor.getColumnIndex(ChallengeData.C_ID));
				Intent intent = new Intent(OverviewActivity.this, DetailsActivity.class);
				intent.putExtra(ChallengeData.C_ID, c_id);
				startActivity(intent);
			}
		});
		registerReceiver(receiver, filter);
		Log.d(TAG, "receiver registered");
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
		Log.d(TAG, "receiver unregistered");
		cursor.close();
		cursor = null;
		this.challengeData.close();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "on Destroy");
	}
}