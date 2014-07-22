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
import com.example.clockit.app.model.Client;

/**
 * Displays a list of created clients, allows each to be selected to be viewed
 * and gives the option to create a new client.
 * 
 * @author Cameron Irwin
 * 
 */
public class ManageClientsActivity extends ListActivity {

	/** String used to send the client id to the view client activity. */
	public static final String CLIENT_ID = "client_id";

	/** Data source that interacts with database using CRUD operations. */
	ClockItDataSource datasource;

	/** A reference to the list of available clients. */
	List<Client> clients;

	/**
	 * Retrieves the list of clients and sets up the page's display.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_clients);

		// Set data source
		datasource = new ClockItDataSource(this);

		// Retrieve all existing clients and display in list.
		clients = datasource.getAllClients();
        ClientAdapter adapter = new ClientAdapter(this, clients);
		setListAdapter(adapter);
	}

	/**
	 * Sets up the options menu (currently unchanged).
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_clients, menu);
		return true;
	}

	/**
	 * Handles the event of an item in the list being selected. Directs the user
	 * to the view client page associated with the selected client.
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Start a new intent directed to ViewClientActivity
		Intent intent = new Intent(this, ViewClientActivity.class);

		// Add selected clients id as an extra
		intent.putExtra(CLIENT_ID, clients.get(position).getId());

		// Send to new activity
		startActivity(intent);
	}

	/**
	 * Send user to the create client activity
	 * 
	 * @param view
	 *            The button that called the method.
	 */
	public void toCreateClient(View view) {
		// Start a new intent directed to CreateClientActivity
		Intent intent = new Intent(this, CreateClientActivity.class);
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
