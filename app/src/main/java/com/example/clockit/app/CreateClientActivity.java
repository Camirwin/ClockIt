package com.example.clockit.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clockit.app.db.ClockItDataSource;
import com.example.clockit.app.model.Client;

/**
 * Allows the user to enter values for the name and description of a new client
 * and either create it or cancel and return to the manage clients activity.
 * 
 * @author Cameron Irwin
 * 
 */
public class CreateClientActivity extends Activity {

	/** Data source that interacts with database using CRUD operations. */
	ClockItDataSource datasource;

	/** Text view associated with the client's name. */
	TextView tvClientName;

	/** Edit text view associated with the client's name. */
	EditText etClientName;

	/** Edit text view associated with the client's description. */
	EditText etClientDescription;

	/**
	 * Sets up page's display and references to the page variables.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_client);

		// Retrieve views for later use
		tvClientName = (TextView) findViewById(R.id.tvClientName);
		etClientName = (EditText) findViewById(R.id.etClientName);
		etClientDescription = (EditText) findViewById(R.id.etClientDescription);

		// Add text changed listener to client name to check for valid input
		etClientName.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().isEmpty()) {
					tvClientName.setError("A client name is required.");
				} else {
					tvClientName.setError(null);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

		});
		
		// Set the error on clients name so users know it is required to start with
		tvClientName.setError("A client name is required.");

		// Set data source
		datasource = new ClockItDataSource(this);

		// Give client name focus so user is ready to type
		etClientName.requestFocus();
	}

	/**
	 * Sets up the options menu (currently unchanged).
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_client, menu);
		return true;
	}

	/**
	 * Sends the user to the parent activity set in the manifest
	 * (ManageClientsActivity).
	 * 
	 * @param view
	 *            The view that triggered this method.
	 */
	public void toParent(View view) {
		// Navigate to set parent
		NavUtils.navigateUpFromSameTask(this);
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
	 * Creates a new client with the input values and sends the user back to the
	 * parent ManageClientsActivity page.
	 * 
	 * @param view
	 *            The button that triggered this method.
	 */
	public void createClient(View view) {
		// If the input isn't valid (no name) then show a toast, direct
		// focus to name field, and exit function
		if (etClientName.getText().toString().isEmpty()) {
			Toast toast = Toast.makeText(this, "Name Required",
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			etClientName.requestFocus();
			return;
		}

		// Create new client and save into Client object
		Client newClient = datasource.createClient(etClientName.getText()
				.toString(), etClientDescription.getText().toString());

		// Create toast to notify user of created service's details
		Toast.makeText(this, "Created Client -\n" + newClient.toString(),
				Toast.LENGTH_LONG).show();

		// Call toParent to return to parent activity
		toParent(view);
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
