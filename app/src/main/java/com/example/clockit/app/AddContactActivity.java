package com.example.clockit.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.example.clockit.app.db.ClockItDataSource;
import com.example.clockit.app.model.Client;

/**
 * Allows the user to add a contact to a client. User can select from their
 * phone contacts, or input values to create a new contact.
 * 
 * @author Cameron Irwin
 * 
 */
public class AddContactActivity extends Activity {

	/** Number used to send user to the contact list and retrieve the result. */
	public static final int CONTACT_PICKER_RESULT = 1337;

	/** Data source that interacts with database using CRUD operations. */
	ClockItDataSource datasource;

	/** Edit text view associated with the contact's first name. */
	EditText etContactFirstName;

	/** Edit text view associated with the contact's last name. */
	EditText etContactLastName;

	/** Edit text view associated with the contact's email. */
	EditText etContactEmail;

	/** Edit text view associated with the contact's phone number. */
	EditText etContactPhone;

	/** Reference to the client that the contact is being added to. */
	Client client;

	/**
	 * Sets up the page and references to all page variables.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);

		// Set references to views
		etContactFirstName = (EditText) findViewById(R.id.etContactFirstName);
		etContactLastName = (EditText) findViewById(R.id.etContactLastName);
		etContactEmail = (EditText) findViewById(R.id.etContactEmail);
		etContactPhone = (EditText) findViewById(R.id.etContactPhone);

		// Get the intent that started the activity
		Intent intent = getIntent();

		// Set data source
		datasource = new ClockItDataSource(this);

		// Retrieve client id from intent and return corresponding client
		client = datasource.getClientById(intent.getIntExtra(
				ManageClientsActivity.CLIENT_ID, 0));
	}

	/**
	 * Sets up the options menu (currently unchanged).
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_contact, menu);
		return true;
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
	 * Sends user to their phone's contacts so they can select one to add.
	 * 
	 * @param view
	 *            The view that was clicked to call this method.
	 */
	public void populateFromContacts(View view) {
		// Create intent directed to the ContactsContract
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI);

		// Start the intent
		startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
	}

	/**
	 * Handles the result that is recieved from the contact selection page.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Make sure the result is ok
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			// Make sure the result is from the intent we sent
			case CONTACT_PICKER_RESULT:
				Cursor cursor = null;
				try {
					// Retrieve the uri of the selected contact
					Uri result = data.getData();
					Log.v(ClockItDataSource.LOGTAG, "Got a contact result: "
							+ result.toString());

					// Get the contact id from the Uri
					// String contactId = result.getLastPathSegment();

					// Query the selected contact's data
					cursor = getContentResolver().query(result, null, null,
							null, null);
					// cursor = getContentResolver().query(
					// ContactsContract.RawContacts.CONTENT_URI, null,
					// ContactsContract.RawContacts._ID + "=?", new String[] {
					// contactId },
					// null);

					// Make sure there are results
					if (cursor.moveToFirst()) {

						// Logs all of the contact's data
						for (int i = 0; i < cursor.getColumnCount(); i++) {
							Log.i(ClockItDataSource.LOGTAG,
									cursor.getColumnName(i) + ": "
											+ cursor.getString(i));
						}
					} else {
						Log.w(ClockItDataSource.LOGTAG, "No results");
					}
				} catch (Exception e) {
					Log.e(ClockItDataSource.LOGTAG, "Failed to get data", e);
				} finally {
					if (cursor != null) {
						cursor.close();
					}
				}
				break;
			}
		} else {
			Log.w(ClockItDataSource.LOGTAG, "Warning: activity result not ok");
		}
	}

	/**
	 * Creates an entry in the contacts table from the provided information,
	 * sends the user back to ManageClientContactsActivity, and ends the current
	 * activity.
	 * 
	 * @param view
	 *            The view that was clicked to call this method.
	 */
	public void addContact(View view) {
		// Create the contact in the database
		datasource.createContact(client.getId(), etContactFirstName.getText()
				.toString(), etContactLastName.getText().toString(),
				etContactEmail.getText().toString(), etContactPhone.getText()
						.toString());

		// Create and start an intent directed to ManageClientContactsActivity
		Intent intent = new Intent(this, ManageClientContactsActivity.class);
		intent.putExtra(ManageClientsActivity.CLIENT_ID, client.getId());
		startActivity(intent);

		// End the current activity
		this.finish();
	}
}
