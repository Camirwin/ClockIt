package com.example.clockit.app;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clockit.app.db.ClockItDataSource;
import com.example.clockit.app.model.Client;
import com.example.clockit.app.model.TimeStamp;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Allows the user to manage a single client entry. From this page the user can
 * view and update the client's information as well as delete the client from
 * the database. The user can also view the client's associated contacts and the
 * client's invoice.
 * 
 * @author Cameron Irwin
 * 
 */
public class ViewClientActivity extends Activity {

	/** Data source that interacts with database using CRUD operations. */
	ClockItDataSource datasource;

	// Views

	/** Text view associated with the client's name. */
	TextView tvClientName;

	/** Edit text view associated with the client's name. */
	EditText etClientName;

	/** Edit text view associated with the client's description. */
	EditText etClientDescription;

	/** Button view used to start editing or to save an edit. */
	Button btnEditClient;

	/** Button view used to cancel editing or return to manage clients page. */
	Button btnCancel;

	/**
	 * Spinner view used to select which associated item to view for the client.
	 */
	Spinner ddlItems;

	// Page Properties

	/** A full object reference to the currently viewed client. */
	Client client;

	/** Indicates if the page is in edit mode. */
	Boolean editable = false;

	/** Indicates if there are existing changes to be saved. */
	Boolean edited = false;

	/** Indicates if the current input is valid. */
	Boolean valid = true;

	// Overridden Methods

	/**
	 * Retrieves the client to view and sets up the page's display.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_client);

		// Set data source
		datasource = new ClockItDataSource(this);

		// Set references to views
		tvClientName = (TextView) findViewById(R.id.tvClientName);
		etClientName = (EditText) findViewById(R.id.etClientName);
		etClientDescription = (EditText) findViewById(R.id.etClientDescription);
		btnEditClient = (Button) findViewById(R.id.btnEditClient);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		ddlItems = (Spinner) findViewById(R.id.ddlItems);

		// Get the intent that started the activity
		Intent intent = getIntent();

		// Retrieve client id from intent and return corresponding client
		client = datasource.getClientById(intent.getIntExtra(
				ManageClientsActivity.CLIENT_ID, 0));

		// Set text fields to client's associated values
		etClientName.setText(client.getName());
		etClientDescription.setText(client.getDescription());

		// Create text watcher to determine if text has been edited and if input
		// exists and is valid
		TextWatcher tw = new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// Set edited to true if either of current values don't match
				// original values
				edited = !etClientName.getText().toString()
						.equals(client.getName())
						|| !etClientDescription.getText().toString()
								.equals(client.getDescription());

				valid = true;

				// Sets an error if client name field is empty
				if (etClientName.getText().toString().isEmpty()) {
					valid = false;
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

		};

		// Attach text watcher to each field
		etClientName.addTextChangedListener(tw);
		etClientDescription.addTextChangedListener(tw);

	}

	/**
	 * Sets up the options menu (currently unchanged).
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_client, menu);
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
			// to input a name
			if (valid) {
				message = "You have made changes that have not yet been saved. Would you like to save or revert these changes?";
				negativeButton = "Save";
			} else {
				message = "You have made changes that have not yet been saved. The name field is required and the current changes will not be saved unless a name is supplied. Would you like to add a name or revert changes?";
				negativeButton = "Add Name";
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
										datasource.updateClient(etClientName
												.getText().toString(),
												etClientDescription.getText()
														.toString(), client
														.getId());
										navigateUp();
									} else {
										etClientName.requestFocus();
									}
								}
							})
					.setNeutralButton(neutralButton,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// Return to manage clients page
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
			// If not in edit mode just send user to manage clients
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
	public void editClient(View view) {
		if (editable) {
			// If edit mode is on we need to save changes if any were made and
			// turn edit mode off
			if (edited) {
				// If the input isn't valid (no name) then show a toast, direct
				// focus to name field, and exit function
				if (!valid) {
					Toast toast = Toast.makeText(this, "Name Required",
							Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					etClientName.requestFocus();
					return;
				}
				// If the input is valid then prompt the user to save changes
				new AlertDialog.Builder(this)
						.setTitle("Update Client")
						.setMessage(
								"Clicking update will save the changes that have been made to the client's data. You will not be able to recover the replaced information later.")
						.setNegativeButton("Update",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// Update client and turn edit mode off
										client = datasource.updateClient(
												etClientName.getText()
														.toString(),
												etClientDescription.getText()
														.toString(), client
														.getId());
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
	 * clients page.
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
								"Clicking revert will revert any unsaved changes to the clients information. You will be unable to recover these unsaved changes.")
						.setNegativeButton("Revert",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										setEditMode(false);
									}
								})
						.setPositiveButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								}).create().show();
			} else {
				// If no changes have been made just turn edit mode off
				setEditMode(false);
			}
		} else {
			// If not in edit mode return user to manage clients
			navigateUp();
		}
	}

	/**
	 * Displays a dialog to confirm removal. Deletes client and returns to
	 * manage clients page if confirmed.
	 * 
	 * @param view
	 *            The view that was clicked to call this method.
	 */
	public void displayConfirmationPopup(View view) {
		new AlertDialog.Builder(this)
				.setTitle("Remove Client")
				.setMessage(
						"Clicking remove will delete the current client. You will not be able to recover the information later.")
				.setCancelable(false)
				.setNegativeButton("Remove",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Delete the client
								datasource.deleteClient(client.getId());
								// Return to manage clients list
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
	 * Allows the user to view the client's contacts, services, or invoice based
	 * on what is selected from the drop down list.
	 * 
	 * @param view
	 *            The view that was clicked to call this method.
	 */
	public void toAssociationActivity(View view) {

		// If contacts is selected, send to ManageClientContactsActivity
		if (ddlItems.getSelectedItem().toString().equals("Contacts")) {
			Intent intent = new Intent(this, ManageClientContactsActivity.class);
			intent.putExtra(ManageClientsActivity.CLIENT_ID, client.getId());
			startActivity(intent);
		}

		// If time stamps is selected, create invoice and open with users
		// default pdf viewer
		if (ddlItems.getSelectedItem().toString().equals("Time Stamps")) {
			new AlertDialog.Builder(this)
					.setTitle("Group Time Stamps?")
					.setMessage(
							"Would you like to group your hours by job or display each time stamp separately for this invoice?")
					.setNegativeButton("Group",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									createPDF(true);
									viewPDF();
								}
							})
					.setPositiveButton("Separate",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									createPDF(false);
									viewPDF();
								}
							}).create().show();
		}
	}

	// Private Methods

	/**
	 * Sends user back to the manage clients page.
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
			etClientName.setFocusable(true);
			etClientName.setFocusableInTouchMode(true);
			etClientName.setBackgroundResource(R.drawable.edittext_bg);

			// Allow description to be focused and change to the appropriate
			// background
			etClientDescription.setFocusable(true);
			etClientDescription.setFocusableInTouchMode(true);
			etClientDescription.setBackgroundResource(R.drawable.edittext_bg);

			// Set appropriate text for button's actions
			btnEditClient.setText("Save");
			btnCancel.setText("Cancel");
		} else {
			edited = false;

			// Remove ability to focus on name and change to appropriate
			// background
			etClientName.setFocusable(false);
			etClientName.setFocusableInTouchMode(false);
			etClientName.setBackgroundResource(R.drawable.edittext_bg2);

			// Remove ability to focus on description and change to appropriate
			// background
			etClientDescription.setFocusable(false);
			etClientDescription.setFocusableInTouchMode(false);
			etClientDescription.setBackgroundResource(R.drawable.edittext_bg2);

			// Set appropriate text for button's actions
			btnEditClient.setText("Edit");
			btnCancel.setText("Return");

			// Set text of fields to clients current name and description
			etClientName.setText(client.getName());
			etClientDescription.setText(client.getDescription());
		}
	}

	/**
	 * Generates a pdf of the current client's invoice using the iText library.
	 */
	public void createPDF(Boolean group) {

		// Create the document to build
		Document doc = new Document();

		try {
			// Reference to location the document will be stored
			String path = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/ClientInvoice";

			// Check to make sure the full path exists, and if it doesn't,
			// try to create the missing directories
			File dir = new File(path);
			if (!dir.exists())
				dir.mkdirs();

			// Name the file within the directory and opens a stream to write to
			// the document
			File file = new File(dir, "sample.pdf");
			FileOutputStream fOut = new FileOutputStream(file);

			// Set up the PDF writer
			PdfWriter.getInstance(doc, fOut);

			// Open the document
			doc.open();

			// Format the ClockIt logo to use in the PDF
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			Bitmap bitmap = BitmapFactory.decodeResource(getBaseContext()
					.getResources(), R.drawable.clockit_logo);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			Image myImg = Image.getInstance(stream.toByteArray());
			myImg.setAlignment(Image.MIDDLE);
			myImg.scalePercent(60);

			// Add the ClockIt logo to the PDF
			doc.add(myImg);

			// Add spacing below the logo
			doc.add(new Paragraph(" "));

			// Create table to add data to
			PdfPTable table;

			// Retrieve the time stamps for the client
			List<TimeStamp> timeStamps = datasource.getClientsTimeStamps(client.getId());

			if (group) {
				table = new PdfPTable(2);
				table.setWidthPercentage(90);

				// Add the header cells for each column
				PdfPCell c1 = new PdfPCell(new Phrase("Service"));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);
				c1 = new PdfPCell(new Phrase("Earned Income"));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);
				table.setHeaderRows(1);

				Map<String, Double> jobs = new HashMap<String, Double>();
				for (TimeStamp timeStamp : timeStamps) {
					if (!jobs.containsKey(timeStamp.getService().getName())) {
						jobs.put(timeStamp.getService().getName(),
								timeStamp.getEarnedIncome());
					} else {
						jobs.put(timeStamp.getService().getName(),
								jobs.get(timeStamp.getService().getName())
										+ timeStamp.getEarnedIncome());
					}
				}
				
				Iterator<String> iterator = jobs.keySet().iterator();
				while (iterator.hasNext()) {
					String key = iterator.next().toString();
					table.addCell(key);
					table.addCell("$" + jobs.get(key).toString());
				}
			} else {
				table = new PdfPTable(3);
				table.setWidthPercentage(90);

				// Add the header cells for each column
				PdfPCell c1 = new PdfPCell(new Phrase("Service"));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);
				c1 = new PdfPCell(new Phrase("Description"));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);
				c1 = new PdfPCell(new Phrase("Earned Income"));
				c1.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(c1);
				table.setHeaderRows(1);

				// Retrieve the pieces of data to display for each time stamp
				for (TimeStamp timeStamp : timeStamps) {
					table.addCell(timeStamp.getService().getName());
					table.addCell(timeStamp.getDescription());
					table.addCell("$" + Math.round(timeStamp.getEarnedIncome()*100) / 100.0);
				}
			}

			// Add the table to the document
			doc.add(table);

		} catch (DocumentException de) {
			Log.e(ClockItDataSource.LOGTAG, "DocumentException:" + de);
		} catch (IOException e) {
			Log.e(ClockItDataSource.LOGTAG, "ioException:" + e);
		} finally {
			// Close the document to commit to memory
			doc.close();
		}

	}

	/**
	 * Prompts the user to choose a PDF viewer to view the created PDF, or lets
	 * the user know they have no viewer.
	 */
	public void viewPDF() {
		// Reference to invoice that was just created
		File file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/ClientInvoice/sample.pdf");

		if (file.exists()) {
			// Create an intent to send user to view the PDF
			Uri path = Uri.fromFile(file);
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(path, "application/pdf");
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			try {
				// Attempt to send the user to the PDF
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				// If there was no PDF viewer on the device, tell user
				Toast.makeText(this, "No Application Available to View PDF",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}