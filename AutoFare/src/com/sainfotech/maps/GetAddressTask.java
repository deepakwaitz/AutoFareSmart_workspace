package com.sainfotech.maps;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.sainfotech.autofare.R;

public class GetAddressTask extends AsyncTask<Location, Void, String> {

	// Store the context passed to the AsyncTask when the system instantiates
	// it.
	Context ctx;
	String sourcereference = null;
	String destinationreference = null;

	// Constructor called by the system to instantiate the task
	public GetAddressTask(Context context, String source, String destination) {

		// Required by the semantics of AsyncTask
		super();

		// Set a Context for the background task
		ctx = context;
		sourcereference = source;
		destinationreference = destination;
	}

	/**
	 * Get a geocoding service instance, pass latitude and longitude to it,
	 * format the returned address, and return the address to the UI thread.
	 */
	@Override
	protected String doInBackground(Location... params) {


		String addressText = null;
		/*
		 * Get a new geocoding service instance, set for localized addresses.
		 * This example uses android.location.Geocoder, but other geocoders that
		 * conform to address standards can also be used.
		 */
		Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());

		// Get the current location from the input parameter list
		Location location = params[0];

		// Create a list to contain the result address
		List<Address> addresses = null;

		// Try to get an address for the current location. Catch IO or network
		// problems.
		try {

			/*
			 * Call the synchronous getFromLocation() method with the latitude
			 * and longitude of the current location. Return at most 1 address.
			 */
			System.out.println("geocoder --" + location);
			if (location != null) {
				addresses = geocoder.getFromLocation(location.getLatitude(),
						location.getLongitude(), 1);
				System.out.println("address-->" + addresses);
			}

			// Catch network or other I/O problems.
		} catch (IOException exception1) {

			// Log an error and return an error message
			Log.w("getaddressTask", exception1);

			// Return an error message
			return null;

			// Catch incorrect latitude or longitude values
		} catch (IllegalArgumentException exception2) {

			Log.w("getaddressTask", exception2);
			// Construct a message containing the invalid arguments
			String errorString = ctx.getString(
					R.string.illegal_argument_exception,
					location.getLatitude(), location.getLongitude());
			// Log the error and print the stack trace
			Log.e(LocationUtils.APPTAG, errorString);
			exception2.printStackTrace();

			//
			return null;
		}
		// If the reverse geocode returned an address
		if (addresses != null && addresses.size() > 0) {

			System.out.println("address not null");
			// Get the first address
			Address address = addresses.get(0);
			String city = "";
			if (address != null) {
				if (address.getLocality() != null) {
					city = address.getLocality();
				}
				if (city.equals("Chennai")) {
					// Format the first line of address
					addressText = ctx.getString(
							R.string.address_output_string,

							// If there's a street address, add it
							address.getMaxAddressLineIndex() > 0 ? address
									.getAddressLine(0) : "",

							// Locality is usually a city
							city);
					return addressText;
				} else {
					System.out.println("retuning empty in else loop");
					return "";
				}

			}
			System.out.println("address --" + addressText);
			// Return the text

			// If there aren't any addresses, post a message
		} else {
			return ctx.getString(R.string.no_address_found);
		}
		return "";
	
	}

	/**
	 * A method that's called once doInBackground() completes. Set the text of
	 * the UI element that displays the address. This method runs on the UI
	 * thread.
	 */
	@Override
	protected void onPostExecute(String address) {
		System.out.println("address in getaddress --" + address);
		if (sourcereference != null) {
			System.out.println("source address--" + address);
			((NavigationActivity) ctx).updateSourceLocationValue(address);
		} else if (destinationreference != null) {
			System.out.println("destination addreess--" + address);
			((NavigationActivity) ctx).updateDestinationLocationValue(address);
		} else {
			((NavigationActivity) ctx).updateLocationValue(address);
		}
	}

}
