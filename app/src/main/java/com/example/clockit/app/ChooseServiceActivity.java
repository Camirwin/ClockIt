package com.example.clockit.app;

import java.util.List;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clockit.app.db.ClockItDataSource;
import com.example.clockit.app.model.Services;

/**
 * Allows the user to select a service to clock in to and clocks the user in.
 * 
 * @author Cameron Irwin
 * 
 */
public class ChooseServiceActivity extends ListActivity {

	/** String used to send the service id back to the main activity. */
	public static final String SERVICE_ID = "service_id";

	/** Data source that interacts with database using CRUD operations. */
	ClockItDataSource datasource;

	/** A reference to the list of available services. */
	List<Services> services;

	/**
	 * Retrieves the list of services and sets up the page's display.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_service);

		// Set data source
		datasource = new ClockItDataSource(this);

		// Retrieve all existing services and display in list.
		services = datasource.getAllServices();
		ServiceAdapter adapter = new ServiceAdapter(this, services);
		setListAdapter(adapter);
	}

	/**
	 * Creates the options menu for the activity (currently not changed)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_service, menu);
		return true;
	}

	/**
	 * Sends the user back to the main activity with the selected service's id
	 * and clocks the user in.
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Redirect the intent to main activity.
		Intent intent = getIntent();
		intent.setClass(getBaseContext(), MainActivity.class);

		// Retrieve the client id from the previous activity
		int clientId = intent.getIntExtra(ChooseClientActivity.CLIENT_ID, -1);

		// Get the selected service's id
		int serviceId = services.get(position).getId();

		// Add selected service's id as an extra
		intent.putExtra(SERVICE_ID, serviceId);

		// Send to new activity
		startActivity(intent);

		// Clock the user in
		datasource.createTimeStamp(clientId, serviceId);
		// Notify user of clock in
		Toast.makeText(
				this,
				"Clocked in for \nClient: " + clientId + "\nService: "
						+ serviceId, Toast.LENGTH_LONG).show();

		// Finish activity
		this.finish();
	}

}
