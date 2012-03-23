package com.challengeearth.cedroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.challengeearth.cedroid.services.UpdateService;

public class DetailsActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "DetailsActivity";
	
	private Cursor cursor;
	private long challengeId;
	private boolean challengeActive;
	
	private NewProgressListener receiver;
    private IntentFilter filter;
	
	TextView challengeTitle;
	TextView description;
	ProgressBar progress;
	ImageButton button;
	ImageView image;
	
	class NewProgressListener extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			cursor.requery();
			cursor.moveToFirst();
			progress.setProgress(cursor.getInt(cursor.getColumnIndex(ChallengeData.C_PROGRESS)));
			Log.d(TAG, "Update broadcast received and done. Progress is now on: " + cursor.getColumnIndex(ChallengeData.C_PROGRESS));
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);
		
		this.title.setText(R.string.titleChallenge);
		
        // Finding views
        challengeTitle = (TextView) findViewById(R.id.challengeTitle);
        description = (TextView) findViewById(R.id.challengeDescription);
        progress = (ProgressBar) findViewById(R.id.progress);
        button = (ImageButton) findViewById(R.id.toggelTracking);
        image = (ImageView) findViewById(R.id.detailImage);
        
        this.receiver = new NewProgressListener();
	    this.filter = new IntentFilter(UpdateService.NEW_CHALLENGE_PROGRESS);
	}

	@Override
	protected void onResume() {
		super.onResume();
        
		// Get challenge Id
		this.challengeId = getIntent().getExtras().getLong(ChallengeData.C_ID);
        this.cursor = challengeData.getChallengeById(this.challengeId);
        //startManagingCursor(cursor);
        this.cursor.moveToFirst();
        
        // Set contents
        challengeActive = this.cursor.getInt(this.cursor.getColumnIndex(ChallengeData.C_ACTIVE))>0;
        challengeTitle.setText(this.cursor.getString(this.cursor.getColumnIndex(ChallengeData.C_TITLE)));
        description.setText(this.cursor.getString(this.cursor.getColumnIndex(ChallengeData.C_DESC)));
        progress.setProgress(this.cursor.getInt(this.cursor.getColumnIndex(ChallengeData.C_PROGRESS)));

		CeApplication.getImageLoader().fetchDrawableOnThread(this.cursor.getString(this.cursor.getColumnIndex(ChallengeData.C_IMAGE)), image);

        
        button.setOnClickListener(this);
        setButtonState();
        
        registerReceiver(receiver, filter);

	}

	@Override
	public void onClick(View v) {
		Log.d(TAG, "button clicked");
		if(challengeActive) {
			application.stopChallenge(challengeId);
			challengeActive = false;
		} else {
			application.startChallenge(challengeId);
			challengeActive = true;
		}
		setButtonState();
	}

	private void setButtonState() {
		if(challengeActive) {
        	button.setImageResource(android.R.drawable.ic_media_pause);
        }
        else {
        	button.setImageResource(android.R.drawable.ic_media_play);
        }
		button.refreshDrawableState();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		super.onPause();
		cursor.close();
		cursor = null;
		this.challengeData.close();
		System.gc();
	}
	
	@Override
	protected void onDestroy() {
		challengeTitle = null;
		description = null;
		progress = null;
		button = null;
		image = null;
		
		receiver = null;
		filter = null;
		System.gc();
		super.onDestroy();
		Log.d(TAG, "on Destroy");
	}
	
}
