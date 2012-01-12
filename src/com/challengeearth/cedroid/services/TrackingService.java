package com.challengeearth.cedroid.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.challengeearth.cedroid.ActivityData;
import com.challengeearth.cedroid.CeApplication;
import com.challengeearth.cedroid.OverviewActivity;
import com.challengeearth.cedroid.R;

public class TrackingService extends Service {

	private static final String TAG = "TrackingService";
	private static final int TRACKING_SERVICE = 1;
	private LocationManager locationManager;
	private CeApplication application;
	private ContentValues values;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.application = (CeApplication) getApplication();
		Log.i(TAG, "on Create Tracking Service");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "on Destroy Tracking Service");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "start command received");
		this.startInForeground();
		
		// Register the listener with the Location Manager to receive location
 		// updates
 		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
 		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, locationListener);
		values = new ContentValues(1);
		
		return START_STICKY;
	}
	
	private void startInForeground() {
		Notification notification = 
				new Notification(android.R.drawable.ic_menu_compass, getText(R.string.notificationRunning),System.currentTimeMillis());
		
		Intent notificationIntent = new Intent(this, OverviewActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, getText(R.string.notificationRunning),
		        getText(R.string.notificationRunning), pendingIntent);
		startForeground(TRACKING_SERVICE, notification);
	}
	
	/**
	 * Define a listener that responds to location updates
	 */
	private LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			Log.d(TAG, "new locaion data recieved");
			values.put(ActivityData.C_LATITUDE, location.getLatitude());
			values.put(ActivityData.C_LONGITUDE, location.getLongitude());
			application.getActivityData().insertActivity(values, new int[] {});
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}

		public void onProviderEnabled(String provider) {
			
		}

		public void onProviderDisabled(String provider) {
			
		}
	};
}
