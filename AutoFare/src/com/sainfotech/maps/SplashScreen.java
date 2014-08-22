package com.sainfotech.maps;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.sainfotech.autofare.R;

public class SplashScreen extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_background));
		getSupportActionBar().setTitle(Html.fromHtml("<font color='#000000'>Auto Fare</font>"));
		overridePendingTransition(R.anim.pull_in_from_left, R.anim.hold);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				launchHomeScreen();
			}
		}, 2000);
	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	private void launchHomeScreen() {
		Intent intent = new Intent(this, NavigationActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);
		finish();
	}

}
