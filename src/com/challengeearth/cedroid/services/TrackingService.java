package com.challengeearth.cedroid.services;

import com.challengeearth.cedroid.OverviewActivity;
import com.challengeearth.cedroid.R;
import com.challengeearth.cedroid.R.string;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TrackingService extends Service {

	private static final String TAG = "TrackingService";
	private static final int TRACKING_SERVICE = 1;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
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
}
