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

import com.example.clockit.app.db.ClockItDataSource;
import com.example.clockit.app.model.Services;

/**
 * Displays a list of created services, allows each to be selected to be viewed
 * and gives the option to create a new service.
 * 
 * @author Cameron Irwin
 * 
 */
public class ManageServicesActivity extends ListActivity {

	/** String used to send the service id to the view service activity. */
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
		setContentView(R.layout.activity_manage_services);

		// Set data source
		datasource = new ClockItDataSource(this);

		// Retrieve all existing services and display in list.
		services = datasource.getAllServices();
		ServiceAdapter adapter = new ServiceAdapter(this, services);
		setListAdapter(adapter);
	}

	/**
	 * Sets up the options menu (currently unchanged).
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_services, menu);
		return true;
	}

	/**
	 * Handles the event of an item in the list being selected. Directs the user
	 * to the view service page associated with the selected service.
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Start a new intent directed to ViewServiceActivity
		Intent intent = new Intent(this, ViewServiceActivity.class);

		// Add selected service's id as an extra
		intent.putExtra(SERVICE_ID, services.get(position).getId());

		// Send to new activity
		startActivity(intent);
	}

	/**
	 * Send user to the create service activity
	 * 
	 * @param view
	 *            The button that called the method.
	 */
	public void toCreateService(View view) {
		// Start a new intent directed to CreateServiceActivity
		Intent intent = new Intent(this, CreateServiceActivity.class);
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
