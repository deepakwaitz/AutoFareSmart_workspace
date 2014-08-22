package com.sainfotech.maps;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.sainfotech.autofare.R;

public final class LocProvider {

	private static LocProvider instance = new LocProvider();
	private Location tempLocation;
	private String referenceLoc = "";
	private boolean locationChanged = false;
	private int locationContext = 1001;

	private String zipcode;
	private double latitude = 0;
	private double longitude = 0;
	private String providerName;

	private boolean gpsEnabled = false;
	private boolean networkEnabled = false;

	private final static String LOC_PROVIDER_TAG = "LocationProvider";
	private Timer locTimer;
	private boolean isTimerRunning = false;
	private final Handler handler = new Handler();
	private static final String GPS = "GPS";
	private static final String NETWORK = "Network";
	private String userGPSLoc;
	private LocationManager locationManager;
	private Context context;

	public static LocProvider getInstance() {
		return instance;
	}

	public void init(Context aContext) {
		context = aContext;
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
	}

	public void cancelLocationListners() {
		if (gpsEnabled) {
			locationManager.removeUpdates(locationListenerGps);
		}
		if (networkEnabled) {
			locationManager.removeUpdates(locationListenerNetwork);
		}
		if (isTimerRunning) {
			if (locTimer != null) {
				locTimer.cancel();
				locTimer.purge();
			}
			isTimerRunning = false;
		}
	}

	public void init(Context aContext, boolean isRefreshLocation) {
		init(aContext);
		if (isRefreshLocation) {
			initializeLocationSearch();
		}
	}

	public boolean initializeLocationSearch() {
		// exceptions will be thrown if provider is not permitted.
		System.out.println("initializeLocationSearch");
		try {
			gpsEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
			Log.d(LOC_PROVIDER_TAG,
					"Exception in getting data from GPS: " + ex.getMessage());
			// Utils.logMessage(getClass().toString(), ex);
		}
		try {
			networkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
			// Utils.logMessage(getClass().toString(), ex);
			Log.d(LOC_PROVIDER_TAG,
					"Exception in getting data from GPS: " + ex.getMessage());
		}
		// don't start listeners if no provider is enabled
		if (!gpsEnabled && !networkEnabled) {
			return false;
		}
		// Initialize tempLocation so that appropriate location can be decided.
		tempLocation = null;
		if (networkEnabled) {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0,
					locationListenerNetwork);
		}
		if (gpsEnabled) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
		}
		if (isTimerRunning) {
			if (locTimer != null) {
				locTimer.cancel();
				locTimer.purge();
			}
		}
		locTimer = new Timer();
		if (gpsEnabled) {
			locTimer.schedule(new GetLastLocation(), 10000);
		} else {
			locTimer.schedule(new GetLastLocation(), 10000);
		}
		isTimerRunning = true;
		return true;
	}

	private final LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location location) {
			locTimer.cancel();
			providerName = LocationManager.GPS_PROVIDER;
			tempLocation = location;
			locationManager.removeUpdates(this);
			if (networkEnabled) {
				locationManager.removeUpdates(locationListenerNetwork);
			}
			System.out.println(" 419 GPS location changed event" + "Lat: "
					+ tempLocation.getLatitude() + " long: "
					+ tempLocation.getLongitude());
			if (isTimerRunning) {
				updateLocListeners();
				isTimerRunning = false;
			}
			Log.d(LOC_PROVIDER_TAG,
					"GPS location changed event \nLat: "
							+ tempLocation.getLatitude() + "\nlong: "
							+ tempLocation.getLongitude() + "\nAccuracy: "
							+ tempLocation.getAccuracy());
		}

		public void onProviderDisabled(String provider) {
			System.out.println(" 434 GPS disabled event"
					+ "GPS setting unchecked");
		}

		public void onProviderEnabled(String provider) {
			System.out.println(" 438 GPS enabled event"
					+ "GPS setting activated");
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			System.out.println(" 443 GPS STATUS changed event" + "Status: "
					+ status);
		}
	};

	private final LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			providerName = LocationManager.NETWORK_PROVIDER;
			tempLocation = location;
			// if the accuracy is more than MIN_LOCFIX_ACCURACY, do nothing
			// for n/w location change, we are more interested in GPS
			// location change only but if GPS FIX couldn't be obtained then
			// in timer task, n/w fix will get considered.
			// if (tempLocation.getAccuracy() <= AppConsts.MIN_LOCFIX_ACCURACY)
			{
				locTimer.cancel();
				locationManager.removeUpdates(this);
				if (gpsEnabled) {
					locationManager.removeUpdates(locationListenerGps);
				}
				System.out.println(" 462 N/w location changed event" + "Lat: "
						+ tempLocation.getLatitude() + " long: "
						+ tempLocation.getLongitude());
				if (isTimerRunning) {
					updateLocListeners();
					isTimerRunning = false;
				}
			}
			Log.d(LOC_PROVIDER_TAG,
					"N/w location changed event\nLat: "
							+ tempLocation.getLatitude() + "\nlong: "
							+ tempLocation.getLongitude() + "\nAccuracy: "
							+ tempLocation.getAccuracy());
		}

		public void onProviderDisabled(String provider) {
			System.out.println(" 478 N/w setting disabled"
					+ "N/w setting disabled");
		}

		public void onProviderEnabled(String provider) {
			System.out.println(" 482 N/w setting activated"
					+ "n/w settings activated");
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			System.out.println(" 487 " + "N/w status changed event"
					+ "Status: " + status);
		}
	};

	private void updateLocListeners() {

		handler.post(new Runnable() {

			@Override
			public void run() {
				System.out.println("calling handle location update ");
				((AutoFareDelegate) context).handleLocationUpdate(tempLocation,
						providerName);
			}
		});

	}

	class GetLastLocation extends TimerTask {
		@Override
		public void run() {
			isTimerRunning = false;
			locationManager.removeUpdates(locationListenerGps);
			locationManager.removeUpdates(locationListenerNetwork);
			// if GPS fix not available, use the network fix with
			// accuracy more than 200 m.
			if (tempLocation == null) {
				Location netloc = null, gpsloc = null;
				if (gpsEnabled) {
					gpsloc = locationManager
							.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				}
				if (networkEnabled) {
					netloc = locationManager
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				}
				// if there are both values use the most recent one
				int gps = (gpsloc == null) ? 0 : 1;
				int net = (netloc == null) ? 0 : 1;
				if (gps > 0 && net > 0) {
					if (gpsloc.getTime() > netloc.getTime()) {
						providerName = LocationManager.GPS_PROVIDER;
						tempLocation = gpsloc;
					} else {
						providerName = LocationManager.NETWORK_PROVIDER;
						tempLocation = netloc;
					}
				} else if (gps > 0) {
					providerName = LocationManager.GPS_PROVIDER;
					tempLocation = gpsloc;
				} else if (net > 0) {
					providerName = LocationManager.NETWORK_PROVIDER;
					tempLocation = netloc;
				} else {
					providerName = "No Location Provider";
					tempLocation = null;
				}
			}
			if (tempLocation == null) {
				System.out.println("546" + providerName + "Location not found"
						+ "Couldn't get location info");
				Log.d(LOC_PROVIDER_TAG, "TimerTask Location not found");
			} else {
				System.out.println("549" + providerName + "Location Update"
						+ "Lat: " + tempLocation.getLatitude() + " long: "
						+ tempLocation.getLongitude());
				Log.d(LOC_PROVIDER_TAG, "TimerTask providerName "
						+ providerName + "\nLat: " + tempLocation.getLatitude()
						+ "\nlong: " + tempLocation.getLongitude()
						+ "\nAccuracy: " + tempLocation.getAccuracy());
			}
			updateLocListeners();
			return;
		}
	}

}
