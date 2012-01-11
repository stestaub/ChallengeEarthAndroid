package com.challengeearth.cedroid;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DetailsActivity extends BaseActivity {

	private Cursor cursor;
	private long challengeId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);
	}

	@Override
	protected void onResume() {
		super.onResume();
        
		// Get challenge Id
		this.challengeId = getIntent().getExtras().getLong(ChallengeData.C_ID);
        this.cursor = challengeData.getChallengeById(this.challengeId);
        this.cursor.moveToFirst();
        startManagingCursor(cursor);
        
        // Finding views
        TextView title = (TextView) findViewById(R.id.challengeTitle);
        TextView description = (TextView) findViewById(R.id.challengeDescription);
        ProgressBar progress = (ProgressBar) findViewById(R.id.progress);
        
        title.setText(this.cursor.getString(this.cursor.getColumnIndex(ChallengeData.C_TITLE)));
        description.setText(this.cursor.getString(this.cursor.getColumnIndex(ChallengeData.C_DESC)));
        progress.setProgress(this.cursor.getInt(this.cursor.getColumnIndex(ChallengeData.C_PROGRESS)));

	}

	@Override
	protected void onPause() {
		super.onPause();
		
	}

	
	
}
