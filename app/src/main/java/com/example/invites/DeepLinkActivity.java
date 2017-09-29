package com.example.invites;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

public class DeepLinkActivity extends AppCompatActivity {
	private TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deep_link);
		mTextView = findViewById(R.id.deep_link_text);

		FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
				.addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
					@Override
					public void onSuccess(PendingDynamicLinkData data) {
						if (data == null) {
							mTextView.append("getInvitation: no data");
							return;
						}

						mTextView.append("DeepLink: " + data.getLink() + "\n\n");

						FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(data);
						if (invite != null) {
							String invitationId = invite.getInvitationId();
							mTextView.append("invitationId: " + invitationId);
						}
					}
				})
				.addOnFailureListener(this, new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						mTextView.append("onFailure: " + e.getMessage());
					}
				});
	}

	@Override
	protected void onStart() {
		super.onStart();
		Uri data = getIntent().getData();
		if (getIntent() != null && data != null) {
			if (!data.toString().contains("invitation_id")) {
				mTextView.setText(getString(R.string.invitation_no_id, data.toString()));
			}
		}
	}
}