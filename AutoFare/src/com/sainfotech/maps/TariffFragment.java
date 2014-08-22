package com.sainfotech.maps;

import android.annotation.TargetApi;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.sainfotech.autofare.R;

public class TariffFragment extends Fragment {
    private String row1[] = {"1.8", "2", "2.2", "2.4", "2.6", "2.8", "3", "3.2", "3.4", "3.6", "3.8",
            "4", "4.2", "4.4", "4.6", "4.8", "5", "5.2", "5.4", "5.6", "5.8", "6", "6.2", "6.4", "6.6",
            "6.8", "7", "7.2", "7.4", "7.6", "7.8", "8", "8.2", "8.4", "8.6", "8.8", "9", "9.2", "9.4",
            "9.6", "9.8", "10", "10.2", "10.4", "10.6", "10.8", "11", "11.2", "11.4", "11.6", "11.8", "12",
            "12.2", "12.4", "12.6", "12.8", "13", "13.2", "13.4", "13.6", "13.8", "14", "14.2", "14.4",
            "14.6", "14.8", "15", "15.2", "15.4", "15.6", "15.8", "16", "16.2", "16.4", "16.6", "16.8",
            "17", "17.2", "17.4", "17.6", "17.8", "18", "18.2", "18.4", "18.6", "18.8", "19", "19.2",
            "19.4", "19.6", "19.8", "20"};
    private String row2[] = {"25","27.40","29.80","32.20","34.60","37","39.40","41.80","44.20","46.60","49",
            "51.40","53.80","56.20","58.60","61","63.40","65.80","68.20","70.60","73","75.40","77.80","80.20",
            "82.60","85","87.40","89.80","92.20","94.60","97","99.40","101.80","104.20","106.60","109","111.40",
            "113.80","116.20","118.60","121","123.40","125.80","128.20","130.60","133","135.40","137.80","140.20",
            "142.60","145","147.40","149.80","152.20","154.60","157","159.40","161.80","164.20","166.60","169","171.40",
            "173.80","176.2","178.60","181","183.40","185.80","188.20","190.60","193","195.40","197.80","200.20","202.60",
            "205","207.40","209.80","212.20","214.60","217","219.40","221.80","224.20","226.60","229","231.40","233.80","236.20",
            "238.60","241","243.40"};
    private String row3[] = {"37.5","41.1","44.7","48.3","51.9","55.5","59.1","62.7","66.3","69.9","73.5","77.1","80.7","84.3",
            "87.9","91.5","95.10","98.70","102.30","105.90","109.50","113.10","116.70","120.30","123.90","127.50","131.10","134.70",
            "138.30","141.90","145.50","149.10","152.70","156.30","159.90","163.50","167.10","170.70","174.30","177.90","181.50",
            "185.10","188.70","192.30","195.90","199.50","203.10","206.70","210.30","213.90","217.50","221.10","224.70","228.30",
            "231.90","235.50","239.10","242.70","246.30","249.90","253.50","257.10","260.70","264.30","267.90","271.50","275.10",
            "278.70","282.30","285.90","289.50","293.10","296.70","300.30","303.90","307.50","311.10","314.70","318.30","321.90",
            "325.50","329.10","332.70","336.30","339.90","343.50","347.10","350.70","354.30","357.90","361.50","365.10"};

    private TableLayout tl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tariff, null);
        tl = (TableLayout) view.findViewById(R.id.table_layout);
        createTableHeader();
        createTableRows();
        return view;
    }

   
    private void createTableRows() {

        for (int i = 0; i < row1.length; i++) {
            /* Create a new row to be added. */
            TableRow tr = new TableRow(getActivity());
            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            TextView column1 = new TextView(getActivity());
            column1.setText(row1[i]);
            TableRow.LayoutParams params = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            column1.setBackgroundDrawable(getActivity().getResources().getDrawable(
                    R.drawable.cell_shape_item));
            column1.setTextColor(getActivity().getResources().getColor(
                    R.color.black));
            column1.setTypeface(null, Typeface.BOLD);
            column1.setTextSize(16);
            column1.setLayoutParams(params);

            TextView column2 = new TextView(getActivity());
            column2.setText(row2[i]);
            column2.setBackgroundDrawable(getActivity().getResources().getDrawable(
                    R.drawable.cell_shape_item));
            column2.setTextColor(getActivity().getResources().getColor(
                    R.color.black));
            column2.setTypeface(null, Typeface.BOLD);
            column2.setTextSize(16);
            TableRow.LayoutParams params1 = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT);
            column2.setLayoutParams(params1);

            TextView column3 = new TextView(getActivity());
            column3.setText(row3[i]);
            column3.setTextColor(getActivity().getResources().getColor(
                    R.color.black));
            column3.setTypeface(null, Typeface.BOLD);
            column3.setTextSize(16);
            column3.setBackgroundDrawable(getActivity().getResources().getDrawable(
                    R.drawable.cell_shape_item));
            column3.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            tr.addView(column1);
            tr.addView(column2);
            tr.addView(column3);

            tl.addView(tr, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    
    private void createTableHeader() {
        /* Create a new row to be added. */
        TableRow tr = new TableRow(getActivity());
        tr.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        TextView kmView = new TextView(getActivity());
        kmView.setText("KM");
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        kmView.setBackgroundDrawable(getActivity().getResources().getDrawable(
                R.drawable.cell_shape_km));
        kmView.setTextColor(getActivity().getResources()
                .getColor(R.color.black));
        kmView.setTypeface(null, Typeface.BOLD);
        kmView.setTextSize(16);
        kmView.setLayoutParams(params);

        TextView dayFareView = new TextView(getActivity());
        dayFareView.setText("Day Fare");
        dayFareView.setBackgroundDrawable(getActivity().getResources().getDrawable(
                R.drawable.cell_shape_header));
        dayFareView.setTextColor(getActivity().getResources().getColor(
                R.color.black));
        dayFareView.setTypeface(null, Typeface.BOLD);
        dayFareView.setTextSize(16);
        dayFareView.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        TextView nightFareView = new TextView(getActivity());
        nightFareView.setText("Night Fare");
        nightFareView.setTextColor(getActivity().getResources().getColor(
                R.color.black));
        nightFareView.setTypeface(null, Typeface.BOLD);
        nightFareView.setTextSize(16);
        nightFareView.setBackgroundDrawable(getActivity().getResources().getDrawable(
                R.drawable.cell_shape_header));
        nightFareView.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        tr.addView(kmView);
        tr.addView(dayFareView);
        tr.addView(nightFareView);

        tl.addView(tr, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

    }
}
