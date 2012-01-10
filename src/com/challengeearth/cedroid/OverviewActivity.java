package com.challengeearth.cedroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class OverviewActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview);
        startService(new Intent(this, UpdateService.class));
    }
}