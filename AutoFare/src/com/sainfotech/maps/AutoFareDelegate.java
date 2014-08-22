package com.sainfotech.maps;

import android.location.Location;

public interface AutoFareDelegate {

	public void showFareDetailScreen(String source, String destination, String dayFare, String nightFare, String distance);
	public void showMapDetailScreen(String source, String destination, boolean showDestination, boolean isLiveTracker);
	public void invokeParserTask(String result);
	public void setDetails(String dayFare, String nightFare, String distance);
	public void handleLocationUpdate(final Location aLocation, final String aProviderName);
}
