package com.challengeearth.cedroid;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DetailsActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "DetailsActivity";
	
	private Cursor cursor;
	private long challengeId;
	private boolean challengeActive;
	
	TextView title;
	TextView description;
	ProgressBar progress;
	ImageButton button;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);
		
        // Finding views
        title = (TextView) findViewById(R.id.challengeTitle);
        description = (TextView) findViewById(R.id.challengeDescription);
        progress = (ProgressBar) findViewById(R.id.progress);
        button = (ImageButton) findViewById(R.id.toggelTracking);
	}

	@Override
	protected void onResume() {
		super.onResume();
        
		// Get challenge Id
		this.challengeId = getIntent().getExtras().getLong(ChallengeData.C_ID);
        this.cursor = challengeData.getChallengeById(this.challengeId);
        this.cursor.moveToFirst();
        
        // Set contents
        challengeActive = this.cursor.getInt(this.cursor.getColumnIndex(ChallengeData.C_ACTIVE))>0;
        title.setText(this.cursor.getString(this.cursor.getColumnIndex(ChallengeData.C_TITLE)));
        description.setText(this.cursor.getString(this.cursor.getColumnIndex(ChallengeData.C_DESC)));
        progress.setProgress(this.cursor.getInt(this.cursor.getColumnIndex(ChallengeData.C_PROGRESS)));
        
        button.setOnClickListener(this);
        setButtonState();

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
		super.onPause();
		cursor.close();
	}
	
	
}
