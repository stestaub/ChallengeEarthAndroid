package com.challengeearth.cedroid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.challengeearth.cedroid.services.UpdateService;

public class BaseActivity extends Activity {
	
	private static final String TAG = "BaseActivity";
	
	protected CeApplication application;
	protected ChallengeData challengeData;
	protected SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		// Prepare Database
	    this.application = (CeApplication) getApplication();
	    this.challengeData = application.getChallengeData();
	}



	/// Menu handling --------------------------------------------------------------------------------------
	/**
	 * Creating the menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	/**
	 * Handle menu item clicked event
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.itemRefresh:
			startService(new Intent(this, UpdateService.class));
			break;
		case R.id.prefs:
			startActivity(new Intent(this, PrefsActivity.class));
			break;
		}
		return true;
	}

}
