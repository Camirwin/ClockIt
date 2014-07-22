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
import com.example.clockit.app.model.Services;

/**
 * Allows the user to enter values for the name, description, and rate of a new
 * service and either create it or cancel and return to the manage services
 * activity.
 * 
 * @author Cameron Irwin
 * 
 */
public class CreateServiceActivity extends Activity {

	/** Data source that interacts with database using CRUD operations. */
	ClockItDataSource datasource;

	/** Text view associated with the service's name. */
	TextView tvServiceName;

	/** Edit text view associated with the service's name. */
	EditText etServiceName;

	/** Edit text view associated with the service's description. */
	EditText etServiceDescription;

	/** Text view associated with the service's rate. */
	TextView tvServiceRate;

	/** Edit text view associated with the service's rate. */
	EditText etServiceRate;

	/**
	 * Sets up page's display and references to page variables.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_service);

		// Retrieve views for later use
		tvServiceName = (TextView) findViewById(R.id.tvServiceName);
		etServiceName = (EditText) findViewById(R.id.etServiceName);
		etServiceDescription = (EditText) findViewById(R.id.etServiceDescription);
		tvServiceRate = (TextView) findViewById(R.id.tvServiceRate);
		etServiceRate = (EditText) findViewById(R.id.etServiceRate);

		// Add text changed listener to service name to check for valid input
		etServiceName.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().isEmpty()) {
					tvServiceName.setError("A service name is required.");
				} else {
					tvServiceName.setError(null);
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

		// Set the error on services name so users know it is required to start with
		tvServiceName.setError("A service name is required.");

		// Add text changed listener to service rate to check for valid input
		etServiceRate.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().isEmpty()) {
					tvServiceRate.setError("A service rate is required.");
				} else {
					tvServiceRate.setError(null);
				}
				if (s.toString().equals(".")) {
					etServiceRate.setText("0.");
					etServiceRate.setSelection(etServiceRate.length());
				}
				if (s.toString().contains(".")
						&& s.toString().indexOf(".") < (s.toString().length() - 3)) {
					etServiceRate.setText(s.toString().substring(0,
							s.toString().indexOf(".") + 3));
					etServiceRate.setSelection(etServiceRate.length());
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

		// Set the error on services rate so users know it is required to start with
		tvServiceRate.setError("A service rate is required.");

		// Set data source
		datasource = new ClockItDataSource(this);
		
		// Give service name focus so user is ready to type
		etServiceName.requestFocus();
	}

	/**
	 * Sets up the options menu (currently unchanged).
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_service, menu);
		return true;
	}

	/**
	 * Sends the user to the parent activity set in the manifest
	 * (ManageServicesActivity).
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
	 * Creates a new service with the input values and sends the user back to
	 * the parent ManageServicesActivity page.
	 * 
	 * @param view
	 *            The button that triggered this method.
	 */
	public void createService(View view) {
		// If the input isn't valid (no name) then show a toast, direct
		// focus to name field, and exit function
		if (etServiceName.getText().toString().isEmpty()
				|| etServiceRate.getText().toString().isEmpty()) {
			if (etServiceName.getText().toString().isEmpty()
					&& etServiceRate.getText().toString().isEmpty()) {
				Toast toast = Toast.makeText(this, "Name and Rate Required",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				etServiceName.requestFocus();
			} else if (etServiceName.getText().toString().isEmpty()) {
				Toast toast = Toast.makeText(this, "Name Required",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				etServiceName.requestFocus();
			} else {
				Toast toast = Toast.makeText(this, "Rate Required",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				etServiceRate.requestFocus();
			}
			return;
		}

		// Create new service and save into Services object
		Services newService = datasource.createService(etServiceName.getText()
				.toString(), etServiceDescription.getText().toString(), Double
				.valueOf(etServiceRate.getText().toString()));

		// Create toast to notify user of created service's details
		Toast.makeText(this, "Created Service -\n" + newService.toString(),
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