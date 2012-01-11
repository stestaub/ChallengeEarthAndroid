package com.challengeearth.cedroid;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.challengeearth.cedroid.ChallengeData.DbHelper;

public class BaseActivity extends Activity {
	
	protected DbHelper dbHelper;
	protected SQLiteDatabase db;
	protected CeApplication application;
	protected ChallengeData challengeData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Prepare Database
	    this.dbHelper = new DbHelper(this);
	    this.application = (CeApplication) getApplication();
	    this.challengeData = application.getChallengeData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.challengeData.close();
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
		}
		return true;
	}

}
