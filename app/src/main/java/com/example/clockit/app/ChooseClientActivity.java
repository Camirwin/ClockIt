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
 * Allows the user to select a client to clock in for.
 * 
 * @author Cameron Irwin
 * 
 */
public class ChooseClientActivity extends ListActivity {

	/** String used to send the client id to the choose service activity. */
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
		setContentView(R.layout.activity_choose_client);

		// Set data source
		datasource = new ClockItDataSource(this);

		// Retrieve all existing clients and display in list.
		clients = datasource.getAllClients();
        ClientAdapter adapter = new ClientAdapter(this, clients);
		setListAdapter(adapter);
	}

	/**
	 * Creates the options menu for the activity (currently not changed)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_client, menu);
		return true;
	}

	/**
	 * Sends the user to the ChooseService activity with the selected client's
	 * id.
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Start a new intent directed to ChooseServiceActivity
		Intent intent = new Intent(this, ChooseServiceActivity.class);

		// Add selected clients id as an extra
		intent.putExtra(CLIENT_ID, clients.get(position).getId());

		// Send to new activity
		startActivity(intent);
	}
}
