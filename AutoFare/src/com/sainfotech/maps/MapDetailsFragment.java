package com.sainfotech.maps;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sainfotech.autofare.R;
import com.sainfotech.autofare.common.Appconstants;
import com.sainfotech.autofare.common.Utils;

public class MapDetailsFragment extends SupportMapFragment {

	private String source;
	private String destination;
	private RelativeLayout mapLayout;
	private GoogleMap mapView;
	private AutoCompleteTextView searchBox;
	private Button go;
	private AlertDialog alert = null;
	private boolean shouldShowSearch;
	private RelativeLayout searchLayout;
	private LatLng sourceLatLng;
	private LatLng destinationLatLng;
	private Marker sourceMarker;
	private Marker destinationMarker;
	private TextView fareView;
	private String dayFare;
	private String nightFare;
	private String distance;
	private boolean isValid = false;
	private boolean isLiveTracker;
	private Location sourceLocForLiveTracker;
	private Location destinationLocForTracker;
	private TextView stopTracking;
	private TextView detailsBtn;
	private List<String> citySuggestionList = new ArrayList<String>();
	private List<Address> sourceAddresses;
	private List<Address> destinationAddresses;
	

	public static Fragment newInstance(String source, String destination,
			boolean showDestination, boolean isLiveTracker) {
		Fragment fragment = new MapDetailsFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Appconstants.SOURCE, source);
		bundle.putString(Appconstants.DESTINATION, destination);
		bundle.putBoolean(Appconstants.SHOW_SEARCH, showDestination);
		bundle.putBoolean(Appconstants.IS_LIVE_TRACKER, isLiveTracker);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mapView = super.onCreateView(inflater, container,
				savedInstanceState);
		View view = inflater.inflate(R.layout.map_details, null);
		searchLayout = (RelativeLayout) view
				.findViewById(R.id.destination_layout);
		searchBox = (AutoCompleteTextView) view.findViewById(R.id.destination);
		fareView = (TextView) view.findViewById(R.id.fare);
		stopTracking = (TextView) view.findViewById(R.id.stop_tracking);
		detailsBtn = (TextView) view.findViewById(R.id.details);

		detailsBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				((AutoFareDelegate) getActivity()).showFareDetailScreen(source,
						destination, dayFare, nightFare, distance);
			}
		});
		stopTracking.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LocProvider.getInstance().cancelLocationListners();
				Utils.getInstance().stopUerStateWatcher();
				((AutoFareDelegate) getActivity()).showFareDetailScreen(source,
						destination, dayFare, nightFare, distance);
			}
		});
	
		go = (Button) view.findViewById(R.id.go);
		go.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				isValid = true;
				validateFields();
			}
		});
		mapLayout = (RelativeLayout) view.findViewById(R.id.map_layout);
		mapLayout.addView(mapView);
		return view;
	}

	private void validateFields() {
		destination = searchBox.getText().toString()+"Chennai";
		if (destination.length() == 0) {
			isValid = false;
			if (alert == null) {
				showAlert(Appconstants.TO_ERROR_MESSAGE);
			} else {
				if (!alert.isShowing()) {
					showAlert(Appconstants.TO_ERROR_MESSAGE);
				}
			}
		}

		if (isValid) {
			//Utils.getInstance().showSpinner(getActivity());
			drawRoute();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		System.out.println("saving instabce");
		outState.putString(Appconstants.DAY_FARE, fareView.getText().toString());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		loadSuggestions();
		System.out.println("map detail on activity created");
		source = getArguments().getString(Appconstants.SOURCE);
		destination = getArguments().getString(Appconstants.DESTINATION);
		shouldShowSearch = getArguments().getBoolean(Appconstants.SHOW_SEARCH);
		isLiveTracker = getArguments().getBoolean(Appconstants.IS_LIVE_TRACKER);
		mapView = this.getMap();
		try {
			MapsInitializer.initialize(getActivity());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (isLiveTracker) {
			((NavigationActivity) getActivity())
					.getCurrentLocation(true, false,false);
			searchLayout.setVisibility(View.GONE);
			fareView.setVisibility(View.VISIBLE);
			stopTracking.setVisibility(View.VISIBLE);
		} else {
			System.out.println("loop1");
			if (shouldShowSearch) {
				System.out.println("loop2");
				searchLayout.setVisibility(View.VISIBLE);
			} else {
				System.out.println("loop3");
				fareView.setVisibility(View.VISIBLE);
				((SherlockFragmentActivity) getActivity())
						.getSupportActionBar().setTitle(
								Html.fromHtml("<font color='#000000'>"
										+ Appconstants.FARE_DETAILS_TITLE
										+ "</font>"));
				searchLayout.setVisibility(View.GONE);

				if (plotLocations()) {
					if (sourceAddresses.size() > 0) {
						addSourceMarker(sourceAddresses.get(0).getLatitude(),
								sourceAddresses.get(0).getLongitude());
					}
					if (destinationAddresses.size() > 0) {
						addDestinationMarker(destinationAddresses.get(0)
								.getLatitude(), destinationAddresses.get(0)
								.getLongitude());
					}
					plotDirections();
				} else {
					// TODO: show pop up as invalid locations
				}

			}
		}
	}

	

	public void loadSuggestions() {
		citySuggestionList.add("Abiramapuram");
		citySuggestionList.add("Adambakkam");
		citySuggestionList.add("Adyar");
		citySuggestionList.add("Agaram");
		citySuggestionList.add("Akkarai");
		citySuggestionList.add("Alandur");
		citySuggestionList.add("Alapakkam");
		citySuggestionList.add("Alwarpet");
		citySuggestionList.add("Alwarthirunagar");
		citySuggestionList.add("Ambattur");
		citySuggestionList.add("HO Ambattur Industrial Estate");
		citySuggestionList.add("Aminjikarai");
		citySuggestionList.add("Anakaputhur");
		citySuggestionList.add("Anna Nagar");
		citySuggestionList.add("Arumbakkam");
		citySuggestionList.add("Ashok Nagar");
		citySuggestionList.add("Avadi");
		citySuggestionList.add("Avadi Camp HO");
		citySuggestionList.add("Avadi IAF");
		citySuggestionList.add("Ayanavaram");
		citySuggestionList.add("Ayapakkam");
		citySuggestionList.add("Besant Nagar");
		citySuggestionList.add("Boat Club Road");
		citySuggestionList.add("Broadway");
		citySuggestionList.add("Cathedral Road");
		citySuggestionList.add("Chamiers Road");
		citySuggestionList.add("Chemmenchery Chennai High Court Bldgs");
		citySuggestionList.add("Chetpet");
		citySuggestionList.add("Chinmaya Nagar \n");
		citySuggestionList.add("Chinthadripet ");
		citySuggestionList.add("Chitlapakkam ");
		citySuggestionList.add("Choolai ");
		citySuggestionList.add("Choolaimedu ");
		citySuggestionList.add("Chromepet ");
		citySuggestionList.add("East Coast Road");
		citySuggestionList.add("Egmore ");
		citySuggestionList.add("Ekkaduthangal ");
		citySuggestionList.add("Enjambakkam ");
		citySuggestionList.add("Ennore ");
		citySuggestionList.add("Flowers Road ");
		citySuggestionList.add("Gerugambakkam ");
		citySuggestionList.add("Gopalapuram ");
		citySuggestionList.add("Gowrivakkam ");
		citySuggestionList.add("Greams Road");
		citySuggestionList.add("GST Road ");
		citySuggestionList.add("Guduvanchery ");
		citySuggestionList.add("Guindy ");
		citySuggestionList.add("Guindy Industrial Estate");
		citySuggestionList.add("ICF Colony");
		citySuggestionList.add("Indira Nagar");
		citySuggestionList.add("Iyyappanthangal ");
		citySuggestionList.add("Jafferkhanpet");
		citySuggestionList.add(" Jawahar Nagar");
		citySuggestionList.add("K K Nagar ");
		citySuggestionList.add("Kaatupakkam ");
		citySuggestionList.add("Kamaraj Nagar ");
		citySuggestionList.add("Kanathur ");
		citySuggestionList.add("Kandanchavadi ");
		citySuggestionList.add("Kattankulathur ");
		citySuggestionList.add("Keelkattalai ");
		citySuggestionList.add("Kelambakkam ");
		citySuggestionList.add("Kellys ");
		citySuggestionList.add("Kilpauk ");
		citySuggestionList.add("Kodambakkam ");
		citySuggestionList.add("Kodungaiyur ");
		citySuggestionList.add("Kolapakkam ");
		citySuggestionList.add("Kolathur ");
		citySuggestionList.add("Korattur ");
		citySuggestionList.add("Kosapettai ");
		citySuggestionList.add("Kottivakkam");
		citySuggestionList.add("Kotturpuram ");
		citySuggestionList.add("Kovilambakkam ");
		citySuggestionList.add("Kovur ");
		citySuggestionList.add("Koyambedu ");
		citySuggestionList.add("Kundrathur ");
		citySuggestionList.add("Kunnathur ");
		citySuggestionList.add("Madambakkam ");
		citySuggestionList.add("Madhavaram ");
		citySuggestionList.add("Milk Colony ");
		citySuggestionList.add("Madipakkam ");
		citySuggestionList.add("Maduravoil ");
		citySuggestionList.add("Mahalingapuram ");
		citySuggestionList.add("Mambakkam ");
		citySuggestionList.add("Manali ");
		citySuggestionList.add("Manali New Town");
		citySuggestionList.add("Manapakkam");
		citySuggestionList.add("Mandaveli ");
		citySuggestionList.add("Mangadu ");
		citySuggestionList.add("Mannivakkam ");
		citySuggestionList.add("Maraimalai Nagar ");
		citySuggestionList.add("Medavakkam ");
		citySuggestionList.add("Meenambakkam ");
		citySuggestionList.add("Meenjur ");
		citySuggestionList.add("Metha Nagar");
		citySuggestionList.add("Mogappair ");
		citySuggestionList.add("Moolakadai ");
		citySuggestionList.add("Mount Road ");
		citySuggestionList.add("Mudichur ");
		citySuggestionList.add("Mugalivakkam ");
		citySuggestionList.add("Mylapore ");
		citySuggestionList.add("Nandambakkam ");
		citySuggestionList.add("Nandanam ");
		citySuggestionList.add("Nanganallur ");
		citySuggestionList.add("Nanmangalam ");
		citySuggestionList.add("Navalur ");
		citySuggestionList.add("Neelankarai ");
		citySuggestionList.add("Nerkundram  ");
		citySuggestionList.add("Nesapakkam ");
		citySuggestionList.add("Nolambur ");
		citySuggestionList.add("Nungambakkam ");
		citySuggestionList.add("Old Mahabalipuram Road ");
		citySuggestionList.add("Old Pallavaram ");
		citySuggestionList.add("Otiyambakkam ");
		citySuggestionList.add("Otteri ");
		citySuggestionList.add("Padapai ");
		citySuggestionList.add("Padi");
		citySuggestionList.add("Padur ");
		citySuggestionList.add("Palavakkam ");
		citySuggestionList.add("Pallavaram ");
		citySuggestionList.add("Pallikkaranai ");
		citySuggestionList.add("Pammal ");
		citySuggestionList.add("Panayur ");
		citySuggestionList.add("Park Town ");
		citySuggestionList.add("Parrys ");
		citySuggestionList.add("Pattabiram");
		citySuggestionList.add("Pattipulam ");
		citySuggestionList.add("Pavunjur ");
		citySuggestionList.add("Payanur ");
		citySuggestionList.add("Pazhavanthangal ");
		citySuggestionList.add("Perambur ");
		citySuggestionList.add("Perambur Barracks ");
		citySuggestionList.add("Periyar Nagar ");
		citySuggestionList.add("Perumbakkam ");
		citySuggestionList.add("Perungalathur ");
		citySuggestionList.add("Perungudi ");
		citySuggestionList.add("Poes Garden ");
		citySuggestionList.add("Polichalur ");
		citySuggestionList.add("Ponmar ");
		citySuggestionList.add("Poonamallee ");
		citySuggestionList.add("Poonamallee High road ");
		citySuggestionList.add("Porur ");
		citySuggestionList.add("Purasavakkam ");
		citySuggestionList.add("Puzhal ");
		citySuggestionList.add("Puzhuthivakkam ");
		citySuggestionList.add("Raj Bhavan ");
		citySuggestionList.add("Raja Annamalaipuram");
		citySuggestionList.add("Rajakilpakkam ");
		citySuggestionList.add("Ramapuram ");
		citySuggestionList.add("Red Hills Red Hills ");
		citySuggestionList.add("Royapettah ");
		citySuggestionList.add("Royapuram ");
		citySuggestionList.add("Saidapet ");
		citySuggestionList.add("Saligramam ");
		citySuggestionList.add("Santhome ");
		citySuggestionList.add("Satyamurthi Nagar ");
		citySuggestionList.add("Selaiyur ");
		citySuggestionList.add("Sembakkam ");
		citySuggestionList.add("Sembarambakkam ");
		citySuggestionList.add("Sevvapet ");
		citySuggestionList.add("Shenoy Nagar ");
		citySuggestionList.add("Sholinganallur ");
		citySuggestionList.add("Sidco Estate ");
		citySuggestionList.add("Singaperumal koil ");
		citySuggestionList.add("Siruseri ");
		citySuggestionList.add("Sithalapakkam ");
		citySuggestionList.add("Sowcarpet ");
		citySuggestionList.add("Srinivasa Nagar ");
		citySuggestionList.add("Sriperambathur ");
		citySuggestionList.add("St. Marys Road ");
		citySuggestionList.add("St. Thomas Mount ");
		citySuggestionList.add("Sunnambu Kolathur (S. Kolathur)");
		citySuggestionList.add(" T.Nagar ");
		citySuggestionList.add("Tambaram ");
		citySuggestionList.add("Taramani ");
		citySuggestionList.add("Teynampet ");
		citySuggestionList.add("Thamarai Pakkam ");
		citySuggestionList.add("Thandalam - Near Kundrathur ");
		citySuggestionList.add("Thirumangalam ");
		citySuggestionList.add("Thirumullaivoyal ");
		citySuggestionList.add("Thiruninravur ");
		citySuggestionList.add("Thiruvanmiyur ");
		citySuggestionList.add("Thiruvika Nagar ");
		citySuggestionList.add("Thoraipakkam ");
		citySuggestionList.add("Thousand Lights ");
		citySuggestionList.add("Tiruverkadu ");
		citySuggestionList.add("Tiruvottiyur ");
		citySuggestionList.add("Tondiarpet ");
		citySuggestionList.add("Triplicane ");
		citySuggestionList.add("TTK Road ");
		citySuggestionList.add("Ullagaram ");
		citySuggestionList.add("Urappakkam ");
		citySuggestionList.add("Uthandi ");
		citySuggestionList.add("Vadamperumbakkam ");
		citySuggestionList.add("Vadapalani ");
		citySuggestionList.add("Valasaravakkam ");
		citySuggestionList.add("Vanagaram ");
		citySuggestionList.add("Vandalur ");
		citySuggestionList.add("Velachery ");
		citySuggestionList.add("Velappanchavadi ");
		citySuggestionList.add("Vengaivasal ");
		citySuggestionList.add("Vepery ");
		citySuggestionList.add("Veppampatu ");
		citySuggestionList.add("Villivakkam ");
		citySuggestionList.add("Virugambakkam ");
		citySuggestionList.add("Vyasarpadi ");
		citySuggestionList.add("Washermanpet ");
		citySuggestionList.add("West Mambalam");

		if (citySuggestionList != null) {

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getActivity(), android.R.layout.simple_dropdown_item_1line,
					citySuggestionList);
			searchBox.setAdapter(adapter);
			// System.out.println("First Name sugg loaded");
		}
	}

	public void setUserLocation(Location loc) {
		System.out.println("set user location");
		if (loc != null && destination != null) {
			new GetAddressTask(getActivity(), "Source", null).execute(loc);
			addSourceMarker(loc.getLatitude(), loc.getLongitude());
			System.out.println("got source location");
			Geocoder geoCoder = new Geocoder(getActivity());
			List<Address> destinationAddresses = null;
			try {
				destinationAddresses = geoCoder.getFromLocationName(
						destination, 1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("Destination Address--" + destinationAddresses);

			if (destinationAddresses != null && destinationAddresses.size() > 0) {
				addDestinationMarker(destinationAddresses.get(0).getLatitude(),
						destinationAddresses.get(0).getLongitude());
			}
			plotDirections();
			Utils.getInstance().dismisssSpinnerDialog();
		}
	}

	public void setSourceLocForTracking(Location loc) {
		sourceLocForLiveTracker = loc;
		if (loc != null) {
			addSourceMarker(loc.getLatitude(), loc.getLongitude());
		}
	}

	public void setDestinationLocForTracking(Location loc) {
		if (destinationMarker != null) {
			System.out.println("clearing mapview--" + destinationMarker + "-->"
					+ destinationMarker.isVisible());
			if (destinationMarker.isVisible()) {
				destinationMarker.remove();
				System.out.println("removing marker");
			}
		}

		// mapView.clear();
		destinationLocForTracker = loc;
		System.out.println("Live Tracking -- source loc-->"
				+ sourceLocForLiveTracker + " dest--->"
				+ destinationLocForTracker);
		if (sourceLocForLiveTracker != null && destinationLocForTracker != null) {
			addSourceMarker(sourceLocForLiveTracker.getLatitude(),
					sourceLocForLiveTracker.getLongitude());
			addDestinationMarker(destinationLocForTracker.getLatitude(),
					destinationLocForTracker.getLongitude());
		}
		plotDirections();
	}

	private void plotDirections() {
		animatetoLocation();
		System.out.println("plot direction --" + sourceLatLng + "desti--->"
				+ destinationLatLng);
		String url = getDirectionsUrl(sourceLatLng, destinationLatLng);
		DownloadTask downloadTask = new DownloadTask(getActivity());
		downloadTask.execute(url);

	}

	private String getDirectionsUrl(LatLng home, LatLng office) {

		// Origin of route
		String str_origin = "origin=" + home.latitude + "," + home.longitude;

		// Destination of route
		String str_dest = "destination=" + office.latitude + ","
				+ office.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor;

		// Output format
		String output = "json";

		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		return url;

	}

	private boolean plotLocations() {
		System.out.println("getac--" + getActivity());

		Geocoder geoCoder = new Geocoder(getActivity());

		try {
			sourceAddresses = geoCoder.getFromLocationName(source, 1);
			destinationAddresses = geoCoder.getFromLocationName(destination, 1);

			System.out.println("source Address--" + sourceAddresses);
			System.out.println("Destination Address--" + destinationAddresses);
			System.out.println("source locality--"
					+ sourceAddresses.get(0).getLocality());
			System.out.println("destination locality--"
					+ destinationAddresses.get(0).getLocality());
			if (sourceAddresses.get(0).getLocality().equals("Chennai")
					&& destinationAddresses.get(0).getLocality()
							.equals("Chennai")) {
				return true;
				
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("Place not found");
				builder.setMessage("Please enter chennai location");
				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						dialog.dismiss();
					}
				});
				alert = builder.create();

				alert.show();
			}

		} catch (IOException e) { // TODO Auto-generated catch block

			e.printStackTrace();
		}
		return false;

	}

	private void addDestinationMarker(double lat, double lon) {
		double latitude = lat;
		double longtitude = lon;
		destinationLatLng = new LatLng(latitude, longtitude);
		System.out.println("lat --" + latitude + "--lon ---" + longtitude);
		MarkerOptions markeroptions = new MarkerOptions().position(
				destinationLatLng).icon(
				BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED));

		destinationMarker = mapView.addMarker(markeroptions.title(destination));

	}

	private void addSourceMarker(double lat, double lon) {
		double latitude = lat;
		double longtitude = lon;
		sourceLatLng = new LatLng(latitude, longtitude);
		System.out.println("lat --" + latitude + "--lon ---" + longtitude);
		MarkerOptions marker = new MarkerOptions().position(sourceLatLng).icon(
				BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
		if (source != null) {
			sourceMarker = mapView.addMarker(marker.title(source));
		} else {
			sourceMarker = mapView.addMarker(marker.title("Current Location"));
		}

	}

	private void animatetoLocation() {
		if (sourceMarker != null && destinationMarker != null) {
			LatLngBounds.Builder builder = new LatLngBounds.Builder();

			builder.include(sourceMarker.getPosition());
			builder.include(destinationMarker.getPosition());

			LatLngBounds bounds = builder.build();

			int padding = 40; // offset from edges of the map in pixels
			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
					padding);

			// mapView.animateCamera(cu);
		}
	}

	private void drawRoute() {
		if (searchBox.getText() != null && searchBox.getText().length() > 0) {
			destination = searchBox.getText().toString();
			((NavigationActivity) getActivity()).getCurrentLocation(false,
					false,false);
		} else {
			showAlert(Appconstants.DESTINATION_ERROR_MESSAGE);
		}

	}

	private void showAlert(String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(message);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		builder.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					dialog.dismiss();
				}
				return true;
			}
		});
		alert = builder.create();

		alert.show();
	}

	public class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
		// private static ParserTask parserTask;

		Location originLocation = new Location("gps");
		Location destinationLocation = new Location("gps");
		float distance;
		double dayFare;
		Context ctx;

		public ParserTask(Context ctx) {
			this.ctx = ctx;
		}

		/*
		 * public static ParserTask getInstance(){ if(parserTask == null){
		 * parserTask = new ParserTask(); } return parserTask; }
		 */
		@Override
		public List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		@Override
		public void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;

			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(5);
				lineOptions.color(Color.BLUE);
			}
			if (lineOptions != null) {
				// Drawing polyline in the Google Map for the i-th route
				mapView.addPolyline(lineOptions);
			}

			// Zoom map

			LatLngBounds.Builder builder = new LatLngBounds.Builder();

			builder.include(sourceMarker.getPosition());
			builder.include(destinationMarker.getPosition());

			LatLngBounds bounds = builder.build();

			int padding = 40; // offset from edges of the map in pixels
			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
					padding);

			mapView.animateCamera(cu);

			originLocation.setLatitude(sourceLatLng.latitude);
			originLocation.setLongitude(sourceLatLng.longitude);
			destinationLocation.setLatitude(destinationLatLng.latitude);
			destinationLocation.setLongitude(destinationLatLng.longitude);

			// Calculating Distance

			autoFare();

		}

		public void autoFare() {
			distance = (originLocation.distanceTo(destinationLocation) / 1000);
			System.out.println("time-->" + originLocation.getTime());
			Log.d("Distance", String.valueOf(distance));
			if (distance <= 1.8) {
				dayFare = 25;
				Log.d("Fare", String.valueOf(dayFare));

			} else {
				double addKm = distance - 1.8;
				dayFare = 25 + (addKm * 12);
				Log.d("Fare", String.valueOf(dayFare));
			}
			double nightFare = nightFare(dayFare);
			((AutoFareDelegate) ctx).setDetails(roundTwoDecimals(dayFare),
					roundTwoDecimals(nightFare), roundTwoDecimals(distance));
		}

		public double nightFare(double fare) {
			double nightFare = fare + (fare / 2);
			Log.d("NightFare", String.valueOf(nightFare));
			return nightFare;
		}

	}

	public String roundTwoDecimals(double d) {
		try {
			NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
			DecimalFormat twoDForm = (DecimalFormat) nf;
			twoDForm.applyPattern("#.##");
			String price = Double.toString(Double.valueOf(twoDForm.format(d)));
			int index = price.indexOf('.');
			String temp = price.substring(index, price.length());
			if (temp.length() == 2) {
				price = price + "0";
			}
			if (price.equals("0.00")) {
				price = "<0.01";
			}
			return price;
		} catch (Exception e) {
			// Log.w(getClass().toString(), e);
		}
		return "";
	}

	public void invokeParserAsyncTask(String result) {
		System.out.println("fragment method--" + result);
		ParserTask parserTask = new ParserTask(getActivity());

		// Invokes the thread for parsing the JSON data
		parserTask.execute(result);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		LocProvider.getInstance().cancelLocationListners();
		Utils.getInstance().stopUerStateWatcher();
	}

	public void getDetails(String dayFare, String nightFare, String distance) {

		this.dayFare = dayFare;
		this.nightFare = nightFare;
		this.distance = distance;
		fareView.setVisibility(View.VISIBLE);
		StringBuffer sb = new StringBuffer();
		if (this.dayFare != null && this.dayFare.length() > 0) {
			sb.append("<b>Day Fare : </b>" + "Rs " + this.dayFare);
		}
		System.out.println("updating fare text view--" + this.distance);
		if (this.distance != null) {
			fareView.setText(Html.fromHtml(sb.toString()
					+ "   <b>Distance : </b>" + this.distance + " Km"));
		} else {
			fareView.setText(Html.fromHtml(sb.toString()));
		}

		if (isLiveTracker) {
			detailsBtn.setVisibility(View.GONE);
			stopTracking.setVisibility(View.VISIBLE);
		} else {
			detailsBtn.setVisibility(View.VISIBLE);
			stopTracking.setVisibility(View.GONE);
		}
	}

	public void updateSourceLocationValue(String address) {
		if (address != null) {
			source = address;
		}

	}

	public void updateDestinationLocationValue(String address) {
		if (address != null) {
			destination = address;
		}

	}

}
