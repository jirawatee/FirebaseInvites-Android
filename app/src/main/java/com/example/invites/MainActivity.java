package com.example.invites;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
	private static final String TAG = MainActivity.class.getSimpleName();
	private static final int REQUEST_INVITE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.invite_button).setOnClickListener(this);

		// Check for App Invite invitations and launch deep-link activity if possible.
		// Requires that an Activity is registered in AndroidManifest.xml to handle
		// deep-link URLs.
		FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
				.addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
					@Override
					public void onSuccess(PendingDynamicLinkData data) {
						if (data == null) {
							Log.d(TAG, "getInvitation: no data");
							return;
						}

						// Extract invite
						FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(data);
						if (invite != null) {
							String invitationId = invite.getInvitationId();
							Log.d(TAG, "invitationId:" + invitationId);
						}

						// Get and handle the deep link
						Uri deepLink = data.getLink();
						Log.d(TAG, "deepLink:" + deepLink);
						if (deepLink != null) {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setPackage(getPackageName());
							intent.setData(deepLink);
							startActivity(intent);
						}
					}
				})
				.addOnFailureListener(this, new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Log.w(TAG, "getDynamicLink:onFailure", e);
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

		if (requestCode == REQUEST_INVITE) {
			if (resultCode == RESULT_OK) {
				// Get the invitation IDs of all sent messages
				String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
				for (String id : ids) {
					Log.d(TAG, "onActivityResult: sent invitation " + id);
				}
			} else {
				Toast.makeText(this, R.string.send_failed, Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.invite_button) {
			onInviteClicked();
		}
	}

	private void onInviteClicked() {
		Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
				.setMessage(getString(R.string.invitation_message))
				.setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
				//.setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
				//.setCallToActionText(getString(R.string.invitation_cta))
				.setEmailHtmlContent("<a href='%%APPINVITE_LINK_PLACEHOLDER%%'><h1>" + getString(R.string.invitation_cta)+ "</h1></a>")
				.setEmailSubject(getString(R.string.invitation_subject))
				.build();
		startActivityForResult(intent, REQUEST_INVITE);
	}
}