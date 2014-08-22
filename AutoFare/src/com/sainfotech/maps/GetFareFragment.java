package com.sainfotech.maps;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;

import com.sainfotech.autofare.R;
import com.sainfotech.autofare.common.Appconstants;
import com.sainfotech.autofare.common.Utils;

public class GetFareFragment extends Fragment {

	// private EditText fromEditText;
	private AutoCompleteTextView fromEditText;
	private AutoCompleteTextView toEditText;
	private String source;
	private String destination;
	private Button getFare;
	private AlertDialog alert = null;
	private boolean isValid = false;
	private List<String> citySuggestionList = new ArrayList<String>();
	private Button currentLocation;
	private ProgressBar mActivityIndicator;
	private Button startTracking;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_screen, null);
		// fromEditText = (EditText) view.findViewById(R.id.from_edittext);
		fromEditText = (AutoCompleteTextView) view
				.findViewById(R.id.from_edittext);
		toEditText = (AutoCompleteTextView) view.findViewById(R.id.to_edittext);
		getFare = (Button) view.findViewById(R.id.get_fare);
		currentLocation = (Button) view.findViewById(R.id.location);
		mActivityIndicator = (ProgressBar) view
				.findViewById(R.id.address_progress);

		startTracking = (Button) view.findViewById(R.id.tratckLoc);
		startTracking.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((AutoFareDelegate) getActivity()).showMapDetailScreen(null,
						null, false, true);
			}
		});
		currentLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				((NavigationActivity) getActivity()).getCurrentLocation(false,
						false,true);
			}
		});
		getFare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager im = (InputMethodManager) getActivity()
						.getApplicationContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				im.hideSoftInputFromWindow(getActivity().getWindow()
						.getDecorView().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				isValid = true;
				validateFields();

			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		loadSuggestions();
	}

	private void validateFields() {
		source = fromEditText.getText().toString()+"Chennai";
		destination = toEditText.getText().toString()+"Chennai";

		if (source.length() == 0) {
			isValid = false;
			if (alert == null) {
				showAlert(Appconstants.FROM_ERROR_MESSAGE);
			} else {
				if (!alert.isShowing()) {
					showAlert(Appconstants.FROM_ERROR_MESSAGE);
				}
			}
		}
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
			
			((AutoFareDelegate) getActivity()).showMapDetailScreen(source,
					destination, false, false);
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
			fromEditText.setAdapter(adapter);
			toEditText.setAdapter(adapter);
			// System.out.println("First Name sugg loaded");
		}
	}

	public void updateLoc(String address) {
		System.out.println("fragment address interface ---" + address);
		// mActivityIndicator.setVisibility(View.GONE);
		Utils.getInstance().dismisssSpinnerDialog();
		if (address != null) {
			fromEditText.setText(address);
		}
	}
}
