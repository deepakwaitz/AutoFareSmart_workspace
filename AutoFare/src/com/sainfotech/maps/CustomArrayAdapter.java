package com.sainfotech.maps;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sainfotech.autofare.R;

public class CustomArrayAdapter extends BaseAdapter {

	private final Activity context;
	private final Integer[] imageId;
	private final String[] title ;
	

	public CustomArrayAdapter(Activity context, Integer[] imageId, String[] title ) {
		this.context = context;
		this.title = title;
		this.imageId = imageId;

	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.drawerlist_item, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.text);

		/*ImageView imageView = (ImageView) rowView.findViewById(R.id.image);*/
		txtTitle.setText(title[position]);

		/*imageView.setImageResource(imageId[position]);*/
		return rowView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return imageId.length;
	}
	
	public void setSelection(int position){
		
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
