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
import com.example.clockit.app.model.Contact;

/**
 * Allows the user to view a list of the contacts for a single client entry and
 * add a new contact if desired.
 * 
 * @author Cameron Irwin
 * 
 */
public class ManageClientContactsActivity extends ListActivity {

	/** Data source that interacts with database using CRUD operations. */
	ClockItDataSource datasource;

	/** A reference to the list of contacts for the client. */
	List<Contact> contacts;

	/** A reference to the client whose contacts are being viewed. */
	Client client;

	/**
	 * Retrieves the list of clients and sets up the page's display.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manage_client_contacts);

		// Set data source
		datasource = new ClockItDataSource(this);

		// Get the intent that started the activity
		Intent intent = getIntent();

		// Retrieve client id from intent and return corresponding client
		client = datasource.getClientById(intent.getIntExtra(
				ManageClientsActivity.CLIENT_ID, 0));

		// Retrieve the contacts associated with the client
		contacts = datasource.getClientContacts(client.getId());

		// Display the contacts in the list
		ArrayAdapter<Contact> adapter = new ArrayAdapter<Contact>(this,
				android.R.layout.simple_list_item_1, contacts) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);

                textView.setTextColor(Color.BLACK);
                return textView;
            }
        };
		setListAdapter(adapter);
	}

	/**
	 * Sets up the options menu (currently unchanged).
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manage_client_contacts, menu);
		return true;
	}

	/**
	 * Handles the event of an item in the list being selected. Directs the user
	 * to the view contact page associated with the selected contact.
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		return;

		// Start a new intent directed to ViewContactActivity
		// Intent intent = new Intent(this, ViewContactActivity.class);

		// Add selected clients id as an extra
		// intent.putExtra(CONTACT_ID, contacts.get(position).getId());

		// Send to new activity
		// startActivity(intent);
	}

	/**
	 * Send user to the add contact activity
	 * 
	 * @param view
	 *            The button that called the method.
	 */
	public void toAddContact(View view) {
		// Start a new intent directed to AddContactActivity
		Intent intent = new Intent(this, AddContactActivity.class);
		intent.putExtra(ManageClientsActivity.CLIENT_ID, client.getId());
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
