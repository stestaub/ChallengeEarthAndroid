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
        startManagingCursor(cursor);
        
        // Set contents
        title.setText(this.cursor.getString(this.cursor.getColumnIndex(ChallengeData.C_TITLE)));
        description.setText(this.cursor.getString(this.cursor.getColumnIndex(ChallengeData.C_DESC)));
        progress.setProgress(this.cursor.getInt(this.cursor.getColumnIndex(ChallengeData.C_PROGRESS)));
        
        button.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Log.d(TAG, "button clicked");
		application.startChallenge(challengeId);
	}

	
	
}
