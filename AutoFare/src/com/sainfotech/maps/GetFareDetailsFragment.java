package com.sainfotech.maps;

import java.io.IOException;
import java.util.List;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sainfotech.autofare.R;
import com.sainfotech.autofare.common.Appconstants;
import com.sainfotech.maps.MapDetailsFragment.ParserTask;

public class GetFareDetailsFragment extends SupportMapFragment {

	private String source;
	private String destination;
	private String fare;
	private TextView sourceView;
	private TextView destinationView;
	private TextView distanceView;
	private TextView dayFareView;
	private TextView nightFareView;
	private RelativeLayout mapLayout;
	private GoogleMap mapView;
	private String time;
	private String distance;
	private String dayFare;
	private String nightFare;

	public static Fragment newInstance(String source, String destination,
			String dayFare, String nightFare, String distance) {
		Fragment fragment = new GetFareDetailsFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Appconstants.SOURCE, source);
		bundle.putString(Appconstants.DESTINATION, destination);
		bundle.putString(Appconstants.DAY_FARE, dayFare);
		bundle.putString(Appconstants.NIGHT_FARE, nightFare);
		bundle.putString(Appconstants.DISTANCE, distance);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View mapView = super.onCreateView(inflater, container,
				savedInstanceState);
		View view = inflater.inflate(R.layout.get_fare_detail, null);
		sourceView = (TextView) view.findViewById(R.id.source);
		destinationView = (TextView) view.findViewById(R.id.destination);
		distanceView = (TextView) view.findViewById(R.id.distance);
		dayFareView = (TextView) view.findViewById(R.id.dayFare);
		nightFareView = (TextView) view.findViewById(R.id.nightFare);
		mapLayout = (RelativeLayout) view.findViewById(R.id.map_layout);
		// mapLayout.addView(mapView);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		((SherlockFragmentActivity) getActivity()).getSupportActionBar()
				.setTitle(Html.fromHtml("<font color='#000000'>"
						+ Appconstants.FARE_DETAILS_TITLE
						+ "</font>"));
		source = getArguments().getString(Appconstants.SOURCE);
		destination = getArguments().getString(Appconstants.DESTINATION);
		dayFare = getArguments().getString(Appconstants.DAY_FARE);
		nightFare = getArguments().getString(Appconstants.NIGHT_FARE);
		distance = getArguments().getString(Appconstants.DISTANCE);

		mapView = GetFareDetailsFragment.this.getMap();

		System.out.println("map object --" + mapView);

		System.out.println("so---" + source + destination);
		updateUiControls();

	}

	private void updateUiControls() {
		if (source != null) {
			sourceView.setText(source);
		}
		if (destination != null) {
			destinationView.setText(destination);
		}
		if (distance != null) {
			distanceView.setText(" " + distance);
		}
		if (dayFare != null) {
			dayFareView.setText(dayFare);
		}
		if (nightFare != null) {
			nightFareView.setText(" " + nightFare);
		}

	}
}
