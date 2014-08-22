package com.sainfotech.maps;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.sainfotech.autofare.R;

public class ComplaintFragment extends Fragment{

private TextView tv;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.complaint, null);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        SpannableString content = new SpannableString("For complaints");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tv.setText(content);

		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}
}
