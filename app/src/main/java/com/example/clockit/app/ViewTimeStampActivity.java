package com.example.clockit.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.clockit.app.db.ClockItDataSource;
import com.example.clockit.app.model.TimeStamp;

public class ViewTimeStampActivity extends Activity {

	/** Data source that interacts with database using CRUD operations. */
	ClockItDataSource datasource;

	// Views

	EditText etTimeStampEarnedIncome;
	EditText etTimeStampDescription;
	Button btnEditTimeStamp;
	Button btnCancel;

	// Page Properties

	TimeStamp TimeStamp;

	/** Indicates if the page is in edit mode. */
	Boolean editable = false;

	/** Indicates if there are existing changes to be saved. */
	Boolean edited = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_time_stamp);

		// Set data source
		datasource = new ClockItDataSource(this);

		// Set references to views
		etTimeStampEarnedIncome = (EditText) findViewById(R.id.etTimeStampEarnedIncome);
		etTimeStampDescription = (EditText) findViewById(R.id.etTimeStampDescription);
		btnEditTimeStamp = (Button) findViewById(R.id.btnEditTimeStamp);
		btnCancel = (Button) findViewById(R.id.btnCancel);

		// Get the intent that started the activity
		Intent intent = getIntent();

		// Retrieve service id from intent and return corresponding service
		TimeStamp = datasource.getTimeStampById(intent.getIntExtra(
				ManageTimeStampsActivity.TIME_STAMP_ID, 0));

		// Set text fields to service's associated values
		etTimeStampEarnedIncome.setText("$" + TimeStamp.getEarnedIncome());
		etTimeStampDescription.setText(TimeStamp.getDescription());

		// Create text watcher to determine if text has been edited and if input
		// exists and is valid
		TextWatcher tw = new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// Set edited to true if either of current values don't match
				// original values
				edited = !etTimeStampDescription.getText().toString()
						.equals(TimeStamp.getDescription());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

		};

		// Attach text watcher to each field
		etTimeStampDescription.addTextChangedListener(tw);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_time_stamp, menu);
		return true;
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

	/**
	 * Handles the event of pressing the device's default back button. Prompts
	 * the user to save changes if any are unsaved.
	 */
	@Override
	public void onBackPressed() {
		if (editable && edited) {
			// String variables for dialog
			String title = "Unsaved Changes";
			String message;
			String negativeButton;
			String neutralButton = "Revert";
			String positiveButton = "Cancel";

			message = "You have made changes that have not yet been saved. Would you like to save or revert these changes?";
			negativeButton = "Save";

			// Create dialog with set variables
			new AlertDialog.Builder(this)
					.setTitle(title)
					.setMessage(message)
					.setNegativeButton(negativeButton,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									TimeStamp = datasource.updateTimeStamp(etTimeStampDescription.getText().toString(), TimeStamp.getId());
									navigateUp();
								}
							})
					.setNeutralButton(neutralButton,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									navigateUp();
								}
							})
					.setPositiveButton(positiveButton,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// Exit dialog
									dialog.cancel();
								}
							}).create().show();
		} else {
			// If not in edit mode just send user to manage services
			navigateUp();
		}
	}

	public void editTimeStamp(View view) {
		if (editable) {
			// If edit mode is on we need to save changes if any were made and
			// turn edit mode off
			if (edited) {
				// If the input is valid then prompt the user to save changes
				new AlertDialog.Builder(this)
						.setTitle("Update Time Stamp")
						.setMessage(
								"Clicking update will save the changes that have been made to the time stamp's data. You will not be able to recover the replaced information later.")
						.setNegativeButton("Update",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										TimeStamp = datasource.updateTimeStamp(etTimeStampDescription.getText().toString(), TimeStamp.getId());
										// Turn off edit mode
										setEditMode(false);
									}
								})
						.setPositiveButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// Exit dialog
										dialog.cancel();
									}
								}).create().show();
			} else {
				// If no changes have been made simply turn edit mode off
				setEditMode(false);
			}
		} else {
			// If edit mode is off turn it on
			setEditMode(true);
		}
	}

	/**
	 * When in edit mode this prompts to revert changes if they exist then exits
	 * edit mode. When not in edit mode this returns the user to the manage
	 * services page.
	 * 
	 * @param view
	 *            The view that was clicked to call this method.
	 */
	public void cancelClick(View view) {
		if (editable) {
			if (edited) {
				// If the values have been changed then open a dialog asking the
				// user to revert changes or cancel
				new AlertDialog.Builder(this)
						.setTitle("Revert Unsaved Changes")
						.setMessage(
								"Clicking revert will revert any unsaved changes to the time stamp's information. You will be unable to recover these unsaved changes.")
						.setNegativeButton("Revert",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// Turn edit mode off and reset fields
										setEditMode(false);
									}
								})
						.setPositiveButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// Close dialog
										dialog.cancel();
									}
								}).create().show();
			} else {
				// If no changes have been made just turn edit mode off
				setEditMode(false);
			}
		} else {
			// If not in edit mode return user to manage services
			navigateUp();
		}
	}

	/**
	 * Displays a dialog to confirm removal. Deletes service and returns to
	 * manage services page if confirmed.
	 * 
	 * @param view
	 *            The view that was clicked to call this method.
	 */
	public void displayConfirmationPopup(View view) {
		new AlertDialog.Builder(this)
				.setTitle("Remove Time Stamp")
				.setMessage(
						"Clicking remove will delete the current time stamp. You will not be able to recover the information later.")
				.setCancelable(false)
				.setNegativeButton("Remove",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								datasource.deleteTimeStamp(TimeStamp.getId());
								navigateUp();
							}
						})
				.setPositiveButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Close dialog
								dialog.cancel();
							}
						}).create().show();
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

	// Private Methods

	/**
	 * Sends user back to the manage services page.
	 */
	private void navigateUp() {
		NavUtils.navigateUpFromSameTask(this);
	}

	/**
	 * Turns edit mode on or off and sets the appropriate values and displays.
	 * 
	 * @param on
	 *            Represents whether edit mode should be on or off.
	 */
	private void setEditMode(Boolean on) {
		// Set edit mode
		editable = on;

		if (editable) {
			edited = false;

			// Allow name to be focused and change to the appropriate background
			etTimeStampEarnedIncome.setFocusable(true);
			etTimeStampEarnedIncome.setFocusableInTouchMode(true);
			etTimeStampEarnedIncome
					.setBackgroundResource(R.drawable.edittext_bg);

			// Allow description to be focused and change to the appropriate
			// background
			etTimeStampDescription.setFocusable(true);
			etTimeStampDescription.setFocusableInTouchMode(true);
			etTimeStampDescription
					.setBackgroundResource(R.drawable.edittext_bg);

			// Set appropriate text for button's actions
			btnEditTimeStamp.setText("Save");
			btnCancel.setText("Cancel");
		} else {
			edited = false;

			// Remove ability to focus on name and change to appropriate
			// background
			etTimeStampEarnedIncome.setFocusable(false);
			etTimeStampEarnedIncome.setFocusableInTouchMode(false);
			etTimeStampEarnedIncome
					.setBackgroundResource(R.drawable.edittext_bg2);

			// Remove ability to focus on description and change to appropriate
			// background
			etTimeStampDescription.setFocusable(false);
			etTimeStampDescription.setFocusableInTouchMode(false);
			etTimeStampDescription
					.setBackgroundResource(R.drawable.edittext_bg2);

			// Set appropriate text for button's actions
			btnEditTimeStamp.setText("Edit");
			btnCancel.setText("Return");

			// Set text of fields to service's current name, description, and
			// rate
			etTimeStampEarnedIncome.setText("$" + TimeStamp.getEarnedIncome());
			etTimeStampDescription.setText(TimeStamp.getDescription());
		}
	}
}
