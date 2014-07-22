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
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clockit.app.db.ClockItDataSource;
import com.example.clockit.app.model.Services;

/**
 * Allows the user to manage a single service entry. From this page the user can
 * view and update the service's information as well as delete the service from
 * the database.
 * 
 * @author Cameron Irwin
 * 
 */
public class ViewServiceActivity extends Activity {

	/** Data source that interacts with database using CRUD operations. */
	ClockItDataSource datasource;

	// Views

	/** Text view associated with the service's name. */
	TextView tvServiceName;

	/** Edit text view associated with the service's name. */
	EditText etServiceName;

	/** Edit text view associated with the client's description. */
	EditText etServiceDescription;

	/** Text view associated with the service's rate. */
	TextView tvServiceRate;

	/** Edit text view associated with the service's rate. */
	EditText etServiceRate;

	/** Button view used to start editing or to save an edit. */
	Button btnEditService;

	/** Button view used to cancel editing or return to manage services page. */
	Button btnCancel;

	// Page Properties

	/** A full object reference to the currently viewed service. */
	Services service;

	/** Indicates if the page is in edit mode. */
	Boolean editable = false;

	/** Indicates if there are existing changes to be saved. */
	Boolean edited = false;

	/** Indicates if the current input is valid. */
	Boolean valid = true;

	// Overridden Methods

	/**
	 * Retrieves the service to view and sets up the page's display.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_service);

		// Set data source
		datasource = new ClockItDataSource(this);

		// Set references to views
		tvServiceName = (TextView) findViewById(R.id.tvServiceName);
		etServiceName = (EditText) findViewById(R.id.etServiceName);
		etServiceDescription = (EditText) findViewById(R.id.etServiceDescription);
		tvServiceRate = (TextView) findViewById(R.id.tvServiceRate);
		etServiceRate = (EditText) findViewById(R.id.etServiceRate);
		btnEditService = (Button) findViewById(R.id.btnEditService);
		btnCancel = (Button) findViewById(R.id.btnCancel);

		// Get the intent that started the activity
		Intent intent = getIntent();

		// Retrieve service id from intent and return corresponding service
		service = datasource.getServiceById(intent.getIntExtra(
				ManageServicesActivity.SERVICE_ID, 0));

		// Set text fields to service's associated values
		etServiceName.setText(service.getName());
		etServiceDescription.setText(service.getDescription());
		etServiceRate.setText(String.valueOf(service.getRate()));

		// Create text watcher to determine if text has been edited and if input
		// exists and is valid
		TextWatcher tw = new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// Set edited to true if either of current values don't match
				// original values
				edited = !etServiceName.getText().toString()
						.equals(service.getName())
						|| !etServiceDescription.getText().toString()
								.equals(service.getDescription())
						|| !etServiceRate.getText().toString()
								.equals(String.valueOf(service.getRate()));

				valid = true;

				// Sets an error if service name field is empty
				if (etServiceName.getText().toString().isEmpty()) {
					valid = false;
					tvServiceName.setError("A service name is required.");
				} else {
					tvServiceName.setError(null);
				}

				// Sets an error if service rate field is empty
				if (etServiceRate.getText().toString().isEmpty()) {
					valid = false;
					tvServiceRate.setError("A service rate is required.");
				} else {
					tvServiceRate.setError(null);
				}

				// Handles input for service rate field
				if (etServiceRate.getText().toString().equals(".")) {
					etServiceRate.setText("0.");
					etServiceRate.setSelection(etServiceRate.length());
				}
				if (etServiceRate.getText().toString().contains(".")
						&& etServiceRate.getText().toString().indexOf(".") < (etServiceRate
								.getText().toString().length() - 3)) {
					etServiceRate.setText(etServiceRate
							.getText()
							.toString()
							.substring(
									0,
									etServiceRate.getText().toString()
											.indexOf(".") + 3));
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

		};

		// Attach text watcher to each field
		etServiceName.addTextChangedListener(tw);
		etServiceDescription.addTextChangedListener(tw);
		etServiceRate.addTextChangedListener(tw);

	}

	/**
	 * Sets up the options menu (currently unchanged).
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_service, menu);
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

			// If valid set a button and message to ask the user to save changes
			// and if not valid set the same button and message to ask the user
			// to input a name or rate (or both)
			if (valid) {
				message = "You have made changes that have not yet been saved. Would you like to save or revert these changes?";
				negativeButton = "Save";
			} else {
				if (etServiceName.getText().toString().isEmpty()
						&& etServiceRate.getText().toString().isEmpty()) {
					message = "You have made changes that have not yet been saved. The name and rate fields are required and the current changes will not be saved unless a name and rate are supplied. Would you like to add a name and rate or revert changes?";
					negativeButton = "Add Info";
				} else if (etServiceName.getText().toString().isEmpty()) {
					message = "You have made changes that have not yet been saved. The name field is required and the current changes will not be saved unless a name is supplied. Would you like to add a name or revert changes?";
					negativeButton = "Add Name";
				} else {
					message = "You have made changes that have not yet been saved. The rate field is required and the current changes will not be saved unless a name is supplied. Would you like to add a rate or revert changes?";
					negativeButton = "Add Rate";
				}
			}

			// Create dialog with set variables
			new AlertDialog.Builder(this)
					.setTitle(title)
					.setMessage(message)
					.setNegativeButton(negativeButton,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									if (valid) {
										// Update the service
										datasource.updateService(etServiceName
												.getText().toString(),
												etServiceDescription.getText()
														.toString(), Double
														.valueOf(etServiceRate
																.getText()
																.toString()),
												service.getId());
										// Return to ManageServices
										navigateUp();
									} else {
										// Give focus to invalid field
										if (etServiceName.getText().toString()
												.isEmpty()) {
											etServiceName.requestFocus();
										} else {
											etServiceRate.requestFocus();
										}
									}
								}
							})
					.setNeutralButton(neutralButton,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// Return to manage services page
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

	// Click Methods

	/**
	 * When in edit mode this prompts the user to save changes if valid changes
	 * were made then exits edit mode. When not in edit mode this turns it on.
	 * 
	 * @param view
	 *            The view that was clicked to call this method.
	 */
	public void editService(View view) {
		if (editable) {
			// If edit mode is on we need to save changes if any were made and
			// turn edit mode off
			if (edited) {
				// If the input isn't valid (no name or no rate) then show a
				// toast, direct
				// focus to name or rate field, and exit function
				if (!valid) {
					if (etServiceName.getText().toString().isEmpty()
							&& etServiceRate.getText().toString().isEmpty()) {
						Toast toast = Toast.makeText(this,
								"Name and Rate Required", Toast.LENGTH_LONG);
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
				// If the input is valid then prompt the user to save changes
				new AlertDialog.Builder(this)
						.setTitle("Update Service")
						.setMessage(
								"Clicking update will save the changes that have been made to the service's data. You will not be able to recover the replaced information later.")
						.setNegativeButton("Update",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// Update service and turn edit mode off
										service = datasource.updateService(
												etServiceName.getText()
														.toString(),
												etServiceDescription.getText()
														.toString(), Double
														.valueOf(etServiceRate
																.getText()
																.toString()),
												service.getId());
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
								"Clicking revert will revert any unsaved changes to the services information. You will be unable to recover these unsaved changes.")
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
				.setTitle("Remove Service")
				.setMessage(
						"Clicking remove will delete the current service. You will not be able to recover the information later.")
				.setCancelable(false)
				.setNegativeButton("Remove",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Delete the current service and return to
								// ManageServicesActivity
								datasource.deleteService(service.getId());
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

	/**
	 * Currently not implemented
	 * 
	 * @param view
	 *            The view that was clicked to call this method.
	 */
	public void toAssociationActivity(View view) {

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
			etServiceName.setFocusable(true);
			etServiceName.setFocusableInTouchMode(true);
			etServiceName.setBackgroundResource(R.drawable.edittext_bg);

			// Allow description to be focused and change to the appropriate
			// background
			etServiceDescription.setFocusable(true);
			etServiceDescription.setFocusableInTouchMode(true);
			etServiceDescription.setBackgroundResource(R.drawable.edittext_bg);

			// Allow rate to be focused and change to the appropriate background
			etServiceRate.setFocusable(true);
			etServiceRate.setFocusableInTouchMode(true);
			etServiceRate.setBackgroundResource(R.drawable.edittext_bg);

			// Set appropriate text for button's actions
			btnEditService.setText("Save");
			btnCancel.setText("Cancel");
		} else {
			edited = false;

			// Remove ability to focus on name and change to appropriate
			// background
			etServiceName.setFocusable(false);
			etServiceName.setFocusableInTouchMode(false);
			etServiceName.setBackgroundResource(R.drawable.edittext_bg2);

			// Remove ability to focus on description and change to appropriate
			// background
			etServiceDescription.setFocusable(false);
			etServiceDescription.setFocusableInTouchMode(false);
			etServiceDescription.setBackgroundResource(R.drawable.edittext_bg2);

			// Remove ability to focus on rate and change to appropriate
			// background
			etServiceRate.setFocusable(false);
			etServiceRate.setFocusableInTouchMode(false);
			etServiceRate.setBackgroundResource(R.drawable.edittext_bg2);

			// Set appropriate text for button's actions
			btnEditService.setText("Edit");
			btnCancel.setText("Return");

			// Set text of fields to service's current name, description, and rate
			etServiceName.setText(service.getName());
			etServiceDescription.setText(service.getDescription());
			etServiceRate.setText(String.valueOf(service.getRate()));
		}
	}
}