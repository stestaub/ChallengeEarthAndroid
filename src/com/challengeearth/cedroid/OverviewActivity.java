package com.challengeearth.cedroid;

import java.util.ArrayList;

import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.CloudmadeUtil;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.MapView.LayoutParams;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.MyLocationOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.TilesOverlay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;

import com.challengeearth.cedroid.helpers.ResourceProxyImpl;
import com.challengeearth.cedroid.map.Map;
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
    
    static final String[] MAP_FROM = {ChallengeData.C_TITLE, ChallengeData.C_IMAGE, ChallengeData.C_ACTIVE};
    static final int[] MAP_TO = {R.id.title, R.id.image, R.id.iconRunning};
	
    protected static final int EDIT_ENTRY_DIALOG_ID = 0;
    
    private LinearLayout mapContainer;
	private ArrayList<OverlayItem> chalLocOverlayArray;
	
	private Map map;
	
    /**
	 * View Binder to load the images from the internet
	 */
    static final ViewBinder VIEW_BINDER = new ViewBinder() {
		
		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			switch(view.getId()) {
			case R.id.iconRunning:
				setChallengeStatusIcon(cursor, (ImageView) view);
				return true;
			case R.id.image:
				((ImageView)view).setImageResource(android.R.drawable.gallery_thumb);
				// Load challenge image
				CeApplication.getImageLoader().fetchDrawableOnThread(cursor.getString(columnIndex), (ImageView)view);

				return true;
			default:
				return false;
			}
		}
		
		private void setChallengeStatusIcon(Cursor cursor, ImageView image) {
			
			if(cursor.getInt(cursor.getColumnIndex(ChallengeData.C_PROGRESS)) == 100) {
				image.setImageResource(R.drawable.done);
				Log.d(TAG, "setting challenge icon to done");
			} 
			else if(cursor.getInt(cursor.getColumnIndex(ChallengeData.C_ACTIVE)) > 0) {
				image.setImageResource(R.drawable.running);
				Log.d(TAG, "setting challenge icon to running");
			}
			else if(cursor.getInt(cursor.getColumnIndex(ChallengeData.C_UNUSED)) == 0) {
				image.setImageResource(R.drawable.pause);
				Log.d(TAG, "setting challenge icon to pause");
			}
			else {
				image.setImageDrawable(null);
			}
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
			fillMapWithChallenges();
			Log.d(TAG, "ChallengeUpdate Received");
		}
	}
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    // Prepare Layout
	    setContentView(R.layout.overview);
	    this.title.setText(R.string.titleChallenges);
	    this.challengeList = (ListView) findViewById(R.id.listView1);
	    
	    startService(new Intent(this, UpdateService.class));
	    
	    // Prepare Listener for updates
	    this.receiver = new ChallengeUpdatesReceiver();
	    this.filter = new IntentFilter(UpdateService.NEW_CHALLENGES);

	    // Map ----------------------
	    mapContainer = (LinearLayout)findViewById(R.id.mapContainer);
	    map = new Map(this, mapContainer);
	    this.chalLocOverlayArray = new ArrayList<OverlayItem>();

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
		
		fillMapWithChallenges();
		
		// Set the onclick listener for a click on a Challenge
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
		registerForContextMenu(challengeList);
		
		registerReceiver(receiver, filter);
		Log.d(TAG, "receiver registered");
	}

	private void fillMapWithChallenges() {
		// Fill the map with the challenges
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			OverlayItem chal = new OverlayItem(
					cursor.getString(cursor.getColumnIndex(ChallengeData.C_ID)),
					cursor.getString(cursor.getColumnIndex(ChallengeData.C_DESC)), 
					new GeoPoint(
							cursor.getDouble(cursor.getColumnIndex(ChallengeData.C_LATITUDE)), 
							cursor.getDouble(cursor.getColumnIndex(ChallengeData.C_LONGITUDE))));
			this.chalLocOverlayArray.add(chal);
			cursor.moveToNext();
		}
		this.map.addChallengeOverlays(chalLocOverlayArray);
	}
	
	/**
	 * Create the context menu
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
			
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		cursor.moveToPosition(info.position);
		String title = cursor.getString(cursor.getColumnIndex(ChallengeData.C_TITLE));
		menu.setHeaderTitle(title);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	  AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	  int menuItemIndex = item.getItemId();
	  cursor.moveToPosition(info.position);
	  long c_id = cursor.getInt(cursor.getColumnIndex(ChallengeData.C_ID));
	  
	  switch (menuItemIndex) {
	  case R.id.deleteChallenge:
		  application.getChallengeData().removeChallenge(c_id);
		  application.getActivityData().removeActivityDataForChallenge(c_id);
		  break;
	  }
	  adapter.getCursor().requery();
	  adapter.notifyDataSetChanged();
	  return true;
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
		map.destroy();
		challengeList = null;
	    adapter = null;
	    receiver = null;
	    filter = null;
		
		Log.d(TAG, "on Destroy");
	}
}