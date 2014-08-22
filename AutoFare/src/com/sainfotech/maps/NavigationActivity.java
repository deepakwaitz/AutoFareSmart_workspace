package com.sainfotech.maps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.sainfotech.autofare.R;
import com.sainfotech.autofare.common.Utils;
import com.sherlock.navigationdrawer.compat.SherlockActionBarDrawerToggle;

public class NavigationActivity extends SherlockFragmentActivity implements
		AutoFareDelegate {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private SherlockActionBarDrawerToggle mDrawerToggle;
	private LocProvider locProvider;
	private CharSequence mTitle;
	private String[] mAppModules;
	private boolean isMapClicked = false;

	private boolean shouldPersist;
	private boolean isFromTimerTaskLoc;
	private boolean shouldShowSpinner;
	private static final int REQUEST_CODE_SETTINGS_SHOW_MAP = 300;
	private static final int REQUEST_CODE_SETTINGS_GET_CURRENT_LOCATION = 200;
	private AlertDialog alert = null;

	Integer[] imageId = { R.drawable.get_fare_selector,
			R.drawable.map_selector, R.drawable.tariff_selector,
			R.drawable.complaint_box_selector, R.drawable.about_us_selector };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_fare);
		setTitle(Html.fromHtml("<font color='#000000'>Get Fare</font>"));
		locProvider = LocProvider.getInstance();
		mTitle = getTitle();
		mAppModules = getResources().getStringArray(R.array.app_modules);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		CustomArrayAdapter adapter = new CustomArrayAdapter(this, imageId,
				mAppModules);
		// mDrawerList.setAdapter(new ArrayAdapter<String>(this,
		// R.layout.drawer_list_item, mAppModules));
		mDrawerList.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setBackgroundDrawable(
				getResources().getDrawable(R.drawable.actionbar_background));

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		// mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		// mDrawerLayout, /* DrawerLayout object */
		// R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		// R.string.drawer_open, /* "open drawer" description for accessibility
		// */
		// R.string.drawer_close /* "close drawer" description for accessibility
		// */
		// ) {
		// public void onDrawerClosed(View view) {
		// mDrawerTitle = mTitle;
		// getSupportActionBar().setTitle(mTitle);
		// invalidateOptionsMenu();
		// }
		//
		// public void onDrawerOpened(View drawerView) {
		// getSupportActionBar().setTitle(mDrawerTitle);
		// invalidateOptionsMenu();
		// }
		// };
		// mDrawerLayout.setDrawerListener(mDrawerToggle);

		mDrawerToggle = new SherlockActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_launcher, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				final int position, long id) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					selectItem(position);

				}
			}, 500);

		}
	}

	private void selectItem(int position) {
		Fragment fragment = getSelectedFragment(position);

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(Html.fromHtml("<font color='#000000'>" + mAppModules[position]
				+ "</font>"));
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	private Fragment getSelectedFragment(int position) {
		Fragment fragment = null;
		switch (position) {
		case 0:
			isMapClicked = false;
			fragment = new GetFareFragment();
			return fragment;

		case 1:
			isMapClicked = true;
			// TODO: get the current location and pass it as source
			fragment = MapDetailsFragment.newInstance("", null, true, false);

			return fragment;
		case 2:
			isMapClicked = false;
			fragment = new TariffFragment();
			return fragment;
		case 3:
			isMapClicked = false;
			fragment = new ComplaintFragment();
			return fragment;
		case 4:
			isMapClicked = false;
			fragment = new AboutUsFragment();
			return fragment;
		default:
			break;
		}
		return null;
		// TODO Auto-generated method stub

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(
				Html.fromHtml("<font color='#000000'>" + mTitle + "</font>"));
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		// mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		// mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void showFareDetailScreen(String source, String destination,
			String dayFare, String nightFare, String distance) {
		Fragment fragment = GetFareDetailsFragment.newInstance(source,
				destination, dayFare, nightFare, distance);

		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).addToBackStack(null)
				.commit();

	}

	@Override
	public void showMapDetailScreen(String source, String destination,
			boolean showDestination, boolean isLiveTracker) {
		if (isOnline()) {

			Fragment fragment = MapDetailsFragment.newInstance(source,
					destination, showDestination, isLiveTracker);

			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).addToBackStack(null)
					.commit();

		} else {
			System.out.println("shows alert");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Internet connection failed");
			builder.setMessage("Please check your internet connection");
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							dialog.dismiss();
						}
					});
			alert = builder.create();

			alert.show();
		}
	}

	@Override
	public void invokeParserTask(String result) {
		System.out.println("activity interface");
		if ((getSupportFragmentManager().findFragmentById(R.id.content_frame)) instanceof MapDetailsFragment) {
			((MapDetailsFragment) getSupportFragmentManager().findFragmentById(
					R.id.content_frame)).invokeParserAsyncTask(result);
		}

	}

	@Override
	public void setDetails(String dayFare, String nightFare, String distance) {
		((MapDetailsFragment) getSupportFragmentManager().findFragmentById(
				R.id.content_frame)).getDetails(dayFare, nightFare, distance);

	}

	public void getCurrentLocation(boolean shouldPersistLoc,
			boolean timerTaskLoc,boolean shouldShowSpinnerLoc) {
		shouldPersist = shouldPersistLoc;
		isFromTimerTaskLoc = timerTaskLoc;
		shouldShowSpinner=shouldShowSpinnerLoc;
		System.out.println("getlocation");
		LocationManager locManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		boolean isNWLocEnable = false;

		try {
			if (locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				isNWLocEnable = true;
			}
		} catch (Exception ex) {
			// Log.e(getClass().toString(), ex.toString());
		}

		if (isNWLocEnable) {
			if (isOnline()) {
				if(shouldShowSpinner==true)
				{
					Utils.getInstance().showSpinner(this);
					
				}
					
				
					
				
				
				locProvider.init(this);
				locProvider.initializeLocationSearch();
			} else {
				System.out.println("shows alert");
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Internet connection failed");
				builder.setMessage("Please check your internet connection");
				builder.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								dialog.dismiss();
							}
						});
				alert = builder.create();

				alert.show();
			}
		} else {
			System.out.println("shows alert");
			gpsAlertDialogGetCurrentLocation();
		}
	}

	public boolean gpsAlertDialogGetCurrentLocation() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Location Manager");
		builder.setMessage("Would you like to enable Location Service ?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Launch settings, allowing user to make a change
				System.out.println("intent launches for settings");
				Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivityForResult(i,
						REQUEST_CODE_SETTINGS_GET_CURRENT_LOCATION);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// No location service, no Activity
				dialog.dismiss();
			}
		});
		builder.create().show();

		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent data2) {

		if (resultCode == Activity.RESULT_CANCELED) {
			if (requestCode == REQUEST_CODE_SETTINGS_GET_CURRENT_LOCATION) {
				if (Utils.getInstance().checkLocationSettings(this)) {
					if (isOnline() == true) {
						Utils.getInstance().showSpinner(this);
						locProvider.init(this);
						locProvider.initializeLocationSearch();
					}
				}
			}
		}

	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}

		return false;
	}

	@Override
	public void handleLocationUpdate(Location aLocation, String aProviderName) {
		System.out.println("324" + aLocation + "--->" + aProviderName);
		if (isMapClicked) {
			System.out.println("is from map");
			((MapDetailsFragment) getSupportFragmentManager().findFragmentById(
					R.id.content_frame)).setUserLocation(aLocation);
		} else if (shouldPersist) {
			System.out.println("should persist");
			shouldPersist = false;
			new GetAddressTask(NavigationActivity.this, "Source", null)
					.execute(aLocation);
			((MapDetailsFragment) getSupportFragmentManager().findFragmentById(
					R.id.content_frame)).setSourceLocForTracking(aLocation);
			Utils.getInstance().startUserStateWatcher(this);
		} else if (isFromTimerTaskLoc) {
			System.out.println("tracking loop");
			isFromTimerTaskLoc = false;
			new GetAddressTask(NavigationActivity.this, null, "Destination")
					.execute(aLocation);
			((MapDetailsFragment) getSupportFragmentManager().findFragmentById(
					R.id.content_frame))
					.setDestinationLocForTracking(aLocation);
		} else {
			System.out.println("get address");
			new GetAddressTask(NavigationActivity.this, null, null)
					.execute(aLocation);
		}
	}

	public void updateSourceLocationValue(String address) {
		if (((MapDetailsFragment) getSupportFragmentManager().findFragmentById(
				R.id.content_frame) instanceof MapDetailsFragment)) {
			((MapDetailsFragment) getSupportFragmentManager().findFragmentById(
					R.id.content_frame)).updateSourceLocationValue(address);
		}
	}

	public void updateLocationValue(String address) {
		System.out.println("activity address interface");
		if (((GetFareFragment) getSupportFragmentManager().findFragmentById(
				R.id.content_frame)) instanceof GetFareFragment) {
			((GetFareFragment) getSupportFragmentManager().findFragmentById(
					R.id.content_frame)).updateLoc(address);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		locProvider.cancelLocationListners();
	}

	public void updateDestinationLocationValue(String address) {
		if (((MapDetailsFragment) getSupportFragmentManager().findFragmentById(
				R.id.content_frame) instanceof MapDetailsFragment)) {
			((MapDetailsFragment) getSupportFragmentManager().findFragmentById(
					R.id.content_frame))
					.updateDestinationLocationValue(address);
		}
	}

}
