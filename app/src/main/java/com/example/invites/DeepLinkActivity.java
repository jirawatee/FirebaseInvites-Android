package com.example.invites;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DeepLinkActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deep_link);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (getIntent() != null && getIntent().getData() != null) {
			Uri data = getIntent().getData();

			((TextView) findViewById(R.id.deep_link_text)).setText(getString(R.string.deep_link_fmt, data.toString()));
		}
	}
}