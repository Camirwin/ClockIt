package com.example.clockit.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.clockit.app.model.Services;

import java.text.DecimalFormat;
import java.util.List;

public class ServiceAdapter extends ArrayAdapter<Services> {

    private final Context context;
    private final List<Services> services;

    public ServiceAdapter(Context context, List<Services> services) {
        super(context, R.layout.service_row, services);
        this.context = context;
        this.services = services;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.service_row, parent, false);

        TextView tvServiceName = (TextView) rowView.findViewById(R.id.tvServiceName);
        TextView tvServiceRate = (TextView) rowView.findViewById(R.id.tvServiceRate);

        Services service = services.get(position);

        DecimalFormat decimalFormat = new DecimalFormat("#0.00");

        tvServiceName.setText(service.getName());
        tvServiceRate.setText("$" + decimalFormat.format(service.getRate()));

        return rowView;
    }

}
