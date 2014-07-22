package com.example.clockit.app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.clockit.app.model.TimeStamp;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TimeStampAdapter extends ArrayAdapter<TimeStamp> {

    private final Context context;
    private final List<TimeStamp> TimeStamps;

    public TimeStampAdapter(Context context, List<TimeStamp> timeStamps) {
        super(context, R.layout.time_stamp_row, timeStamps);
        this.context = context;
        this.TimeStamps = timeStamps;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.time_stamp_row, parent, false);

        TextView tvClient = (TextView) rowView.findViewById(R.id.tvClientName);
        TextView tvService = (TextView) rowView.findViewById(R.id.tvServiceName);
        TextView tvClockInDate = (TextView) rowView.findViewById(R.id.tvClockInDate);
        TextView tvClockInTime = (TextView) rowView.findViewById(R.id.tvClockInTime);
        TextView tvClockOutDate = (TextView) rowView.findViewById(R.id.tvClockOutDate);
        TextView tvClockOutTime = (TextView) rowView.findViewById(R.id.tvClockOutTime);
        TextView tvHours = (TextView) rowView.findViewById(R.id.tvHours);
        TextView tvEarnings = (TextView) rowView.findViewById(R.id.tvEarnings);

        TimeStamp timestamp = TimeStamps.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d", Locale.US);
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");

        tvClient.setText(timestamp.getClient().getName());
        tvService.setText(timestamp.getService().getName());
        tvClockInDate.setText(dateFormat.format(timestamp.getClockIn()));
        tvClockInTime.setText(timeFormat.format(timestamp.getClockIn()));
        tvClockOutDate.setText(dateFormat.format(timestamp.getClockOut()));
        tvClockOutTime.setText(timeFormat.format(timestamp.getClockOut()));
        tvHours.setText(String.valueOf(decimalFormat.format(timestamp.getEarnedIncome()/timestamp.getService().getRate())));
        tvEarnings.setText("$" + decimalFormat.format(timestamp.getEarnedIncome()));

        return rowView;
    }

}
