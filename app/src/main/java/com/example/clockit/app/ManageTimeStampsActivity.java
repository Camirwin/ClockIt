package com.example.clockit.app;

import java.util.List;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.clockit.app.db.ClockItDataSource;
import com.example.clockit.app.model.TimeStamp;

/**
 * Allows the user to view a list of the time stamps they have recorded.
 * 
 * @author Cameron Irwin
 * 
 */
public class ManageTimeStampsActivity extends ListActivity {

	/** String used to send the time stamp id to the view time stamp activity. */
	public static final String TIME_STAMP_ID = "time_stamp_id";
	
	/** Data source that interacts with database using CRUD operations. */
	ClockItDataSource datasource;

	/** A reference to the list of available time stamps. */
	List<TimeStamp> timeStamps;

	/**
	 * Retrieves the list of time stamps and sets up the activity's view.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_time_stamps);

		// Set data source
		datasource = new ClockItDataSource(this);

		// Retrieve all existing time stamps and display in list.
		timeStamps = datasource.getAllTimeStamps();
        TimeStampAdapter adapter = new TimeStampAdapter(this, timeStamps);
		setListAdapter(adapter);
	}

	/**
	 * Creates the options menu for the activity (currently unchanged)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_time_stamps, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Start a new intent directed to ViewTimeStampActivity
		Intent intent = new Intent(this, ViewTimeStampActivity.class);

		// Add selected time stamp's id as an extra
		intent.putExtra(TIME_STAMP_ID, timeStamps.get(position).getId());

		// Send to new activity
		startActivity(intent);
	}

	/**
	 * Returns the user to the home activity and ends the current activity.
	 * 
	 * @param view
	 *            The view that was clicked to call this method.
	 */
	public void toHome(View view) {
		// Create intent directed to MainActivity and start it
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);

		// End the current activity
		this.finish();
	}

	/**
	 * Closes the data source when the activity is paused (or deleted) to free
	 * resources.
	 */
	@Override
	public void onPause() {
		super.onPause();

		// Close data source if open
		if (datasource.isOpen()) {
			datasource.close();
		}
	}

	/**
	 * Opens the data source when activity is resumed (or started) to allow
	 * access.
	 */
	@Override
	public void onResume() {
		super.onResume();

		// Open data source if closed
		if (!datasource.isOpen()) {
			datasource.open();
		}
	}

}
