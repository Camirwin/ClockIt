package com.example.clockit.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.clockit.app.db.ClockItDataSource;
import com.example.clockit.app.model.TimeStamp;

/**
 * Allows the user to enter a description for what they did while working and
 * then clock out.
 * 
 * @author Cameron Irwin
 * 
 */
public class ClockOutActivity extends Activity {

	/** Data source that interacts with database using CRUD operations. */
	ClockItDataSource datasource;

	/** Edit text holding the description of what was done while working. */
	EditText etClockOutDescription;

	/**
	 * Sets up the view and retrieves the data source.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clock_out);

		// Set data source
		datasource = new ClockItDataSource(this);

		// Set reference to description text box
		etClockOutDescription = (EditText) findViewById(R.id.etClockOutDescription);
	}

	/**
	 * Creates the options menu for the activity (currently unchanged)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.clock_out, menu);
		return true;
	}

	/**
	 * Clocks the user out and returns them to the main activity.
	 * 
	 * @param view
	 *            The button that calls the method.
	 */
	public void clockOutAndReturn(View view) {
		// Retrieve the supplied description
		String description = etClockOutDescription.getText().toString();

		// Clock the user out
		TimeStamp timeStamp = datasource.ClockOut(description);

		// Create an intent directed to the main activity
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);

		// Notify user that they have been clocked out
		Toast.makeText(
				this,
				"Clocked out of \nTimestamp: " + timeStamp.getId()
						+ "\nDescription: " + timeStamp.getDescription(),
				Toast.LENGTH_LONG).show();

		// Finish the activity
		this.finish();
	}

}
