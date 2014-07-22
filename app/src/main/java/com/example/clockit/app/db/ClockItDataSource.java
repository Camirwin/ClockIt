package com.example.clockit.app.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.clockit.app.model.Client;
import com.example.clockit.app.model.Contact;
import com.example.clockit.app.model.Services;
import com.example.clockit.app.model.TimeStamp;

/**
 * Class that facilitates access to database and use of CRUD operations on
 * available data.
 * 
 * @author Cameron Irwin
 * 
 */
public class ClockItDataSource {

	/** Tag used for logcat */
	public static final String LOGTAG = "CLOCKIT";

	/** Variable to access the database helper. */
	SQLiteOpenHelper dbhelper;

	/** Variable to access the database. */
	SQLiteDatabase database;

	/** List of all columns of the clients table */
	private static final String[] allClientColumns = {
			ClockItDBOpenHelper.CLIENTS_ID, ClockItDBOpenHelper.CLIENTS_NAME,
			ClockItDBOpenHelper.CLIENTS_DESCRIPTION };

	/** List of all columns of the services table */
	private static final String[] allServiceColumns = {
			ClockItDBOpenHelper.SERVICES_ID, ClockItDBOpenHelper.SERVICES_NAME,
			ClockItDBOpenHelper.SERVICES_DESCRIPTION,
			ClockItDBOpenHelper.SERVICES_RATE };

	/** List of all columns of the time stamps table */
	private static final String[] allTimeStampColumns = {
			ClockItDBOpenHelper.TIME_STAMPS_ID,
			ClockItDBOpenHelper.TIME_STAMPS_CLIENT_TO_SERVICE_ID,
			ClockItDBOpenHelper.TIME_STAMPS_CLOCK_IN,
			ClockItDBOpenHelper.TIME_STAMPS_CLOCK_OUT,
			ClockItDBOpenHelper.TIME_STAMPS_DESCRIPTION };

	/** List of all columns of the clients to services table */
	private static final String[] allClientToServiceColumns = {
			ClockItDBOpenHelper.CLIENTS_TO_SERVICES_ID,
			ClockItDBOpenHelper.CLIENTS_TO_SERVICES_CLIENT_ID,
			ClockItDBOpenHelper.CLIENTS_TO_SERVICES_SERVICE_ID };

	/** List of all columns of the contacts table */
	private static final String[] allContactColumns = {
			ClockItDBOpenHelper.CONTACTS_ID,
			ClockItDBOpenHelper.CONTACTS_FIRST_NAME,
			ClockItDBOpenHelper.CONTACTS_LAST_NAME,
			ClockItDBOpenHelper.CONTACTS_EMAIL,
			ClockItDBOpenHelper.CONTACTS_NUMBER };

	/** List of all columns of the clients to contacts table */
	private static final String[] allClientToContactColumns = {
			ClockItDBOpenHelper.CLIENTS_TO_CONTACTS_ID,
			ClockItDBOpenHelper.CLIENTS_TO_CONTACTS_CLIENT_ID,
			ClockItDBOpenHelper.CLIENTS_TO_CONTACTS_CONTACT_ID };

	/** Variable that indicates if database is open */
	Boolean open = false;

	/**
	 * Constructor gets instance of the database helper and opens the database.
	 * 
	 * @param context
	 *            Context in which the class is being instantiated.
	 */
	public ClockItDataSource(Context context) {
		// Sets helper variable to a new instance of the helper class
		dbhelper = new ClockItDBOpenHelper(context);

		// Open the database
		open();
	}

	/**
	 * If closed, opens the database, creates log, and turns foreign keys on
	 */
	public void open() {
		// Log to log cat
		// Log.i(LOGTAG, "Database opened");

		if (!open) {
			// Open database
			database = dbhelper.getWritableDatabase();

			open = true;

			// Enable foreign key constraints
			if (!database.isReadOnly()) {
				database.execSQL("PRAGMA foreign_keys = ON;");
			}
		}
	}

	/**
	 * If opened, closes the database. Used when app is paused or closed.
	 */
	public void close() {
		// Log to log cat
		// Log.i(LOGTAG, "Database closed");

		if (open) {
			// Close database
			dbhelper.close();

			open = false;
		}
	}

	/**
	 * Lets the user know if the database is open.
	 * 
	 * @return True if database is open and false otherwise.
	 */
	public Boolean isOpen() {
		return open;
	}

	/**
	 * Creates a new client entry in the database from the supplied name and
	 * description values.
	 * 
	 * @param name
	 *            The name of the client to be created.
	 * @param description
	 *            The description of the client to be created.
	 * @return The newly created client entry as an object.
	 */
	public Client createClient(String name, String description) {
		// Variable to hold map of values to columns
		ContentValues values = new ContentValues();

		// Put supplied values into variable
		values.put(ClockItDBOpenHelper.CLIENTS_NAME, name);
		values.put(ClockItDBOpenHelper.CLIENTS_DESCRIPTION, description);

		// Insert new entry and return the generated id
		int insertId;
		try {
			insertId = (int) database.insert(ClockItDBOpenHelper.TABLE_CLIENTS,
					null, values);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		Log.i(LOGTAG, "Created client " + insertId);

		// Return a client object corresponding to the entry
		return new Client(insertId, name, description, null);
	}

	/**
	 * Retrieves a list of all entries in client table as Client objects.
	 * 
	 * @return The list of clients stored in the database.
	 */
	public List<Client> getAllClients() {
		// Variable to hold clients
		List<Client> clients = new ArrayList<Client>();

		// Cursor holding query to database for all clients
		Cursor cursor = database.query(ClockItDBOpenHelper.TABLE_CLIENTS,
				allClientColumns, null, null, null, null, null);

		int clientId;
		if (cursor.getCount() > 0) {
			// Loop through values retrieved by cursor
			while (cursor.moveToNext()) {
				// Create client object from cursor location
				clientId = cursor.getInt(cursor
						.getColumnIndex(ClockItDBOpenHelper.CLIENTS_ID));
				Client client = new Client(
						clientId,
						cursor.getString(cursor
								.getColumnIndex(ClockItDBOpenHelper.CLIENTS_NAME)),
						cursor.getString(cursor
								.getColumnIndex(ClockItDBOpenHelper.CLIENTS_DESCRIPTION)),
						getClientServices(clientId));

				// Add client to list
				clients.add(client);
			}
		}
		cursor.close();

		Log.i(LOGTAG, "Retrieved " + clients.size() + " clients");

		// Return full list of clients
		return clients;
	}

	/**
	 * Retrieves a single client that corresponds to the supplied id.
	 * 
	 * @param clientId
	 *            The id of the client to retrieve.
	 * @return The client or null.
	 */
	public Client getClientById(int clientId) {
		// Cursor holding query for client by id
		Cursor cursor = database.query(ClockItDBOpenHelper.TABLE_CLIENTS,
				allClientColumns, ClockItDBOpenHelper.CLIENTS_ID + " = ?",
				new String[] { String.valueOf(clientId) }, null, null, null);

		// Empty client variable to hold the client from the table if it exists
		Client client = null;

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			// Creates client object from cursor location
			client = new Client(
					cursor.getInt(cursor
							.getColumnIndex(ClockItDBOpenHelper.CLIENTS_ID)),
					cursor.getString(cursor
							.getColumnIndex(ClockItDBOpenHelper.CLIENTS_NAME)),
					cursor.getString(cursor
							.getColumnIndex(ClockItDBOpenHelper.CLIENTS_DESCRIPTION)),
					getClientServices(clientId));

			Log.i(LOGTAG, "Retrieved client " + clientId);
		} else {
			Log.e(LOGTAG, "No client " + clientId + " found");
		}
		cursor.close();

		// Return the retrieved client or null if none was found.
		return client;
	}

	/**
	 * Retrieves the services that have been completed for a client.
	 * 
	 * @param clientId
	 *            The id of the client to retrieve the services for.
	 * @return The list of Services objects associated with the client.
	 */
	public List<Services> getClientServices(int clientId) {
		// Variable to hold services
		List<Services> services = new ArrayList<Services>();

		// Cursor holding query to database for client to services associated
		// with the client
		Cursor cursor = database.query(
				ClockItDBOpenHelper.TABLE_CLIENTS_TO_SERVICES,
				allClientToServiceColumns,
				ClockItDBOpenHelper.CLIENTS_TO_SERVICES_CLIENT_ID + " = ?",
				new String[] { String.valueOf(clientId) }, null, null, null);

		if (cursor.getCount() > 0) {
			// Loop through values retrieved by cursor
			while (cursor.moveToNext()) {
				// Add each service to the list of services
				services.add(getServiceById(cursor.getInt(cursor
						.getColumnIndex(ClockItDBOpenHelper.CLIENTS_TO_SERVICES_SERVICE_ID))));
			}
		}

		// Return the list of services
		return services;
	}

	/**
	 * Updates the given client's name and description values if they have
	 * changed.
	 * 
	 * @param name
	 *            The name value to update to.
	 * @param description
	 *            The description value to update to.
	 * @param clientId
	 *            The id of the client to update.
	 * @return The updated Client object.
	 */
	public Client updateClient(String name, String description, int clientId) {
		// Variable to hold map of values to columns
		ContentValues values = new ContentValues();

		// Put supplied values into variable
		values.put(ClockItDBOpenHelper.CLIENTS_NAME, name);
		values.put(ClockItDBOpenHelper.CLIENTS_DESCRIPTION, description);

		// Update entry and return the generated id
		try {
			database.update(ClockItDBOpenHelper.TABLE_CLIENTS, values,
					ClockItDBOpenHelper.CLIENTS_ID + " = ?",
					new String[] { String.valueOf(clientId) });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		Log.i(LOGTAG, "Updated client " + clientId);
		// Return a client object corresponding to the entry
		return new Client(clientId, name, description,
				getClientServices(clientId));
	}

	/**
	 * Deletes the client associated with the supplied id
	 * 
	 * @param clientId
	 *            The id of the client to delete.
	 */
	public void deleteClient(int clientId) {
		// Query the clients table to delete the client with the supplied id
		database.delete(ClockItDBOpenHelper.TABLE_CLIENTS,
				ClockItDBOpenHelper.CLIENTS_ID + " = ?",
				new String[] { String.valueOf(clientId) });

		Log.i(LOGTAG, "Deleted client " + clientId);
	}

	/**
	 * Creates a new service entry in the database from the supplied name,
	 * description, and rate values.
	 * 
	 * @param name
	 *            The name of the service to be created.
	 * @param description
	 *            The description of the service to be created.
	 * @param rate
	 *            The pay rate of the service to be created.
	 * @return The newly created service entry as an object.
	 */
	public Services createService(String name, String description, double rate) {
		// Variable to hold map of values to columns
		ContentValues values = new ContentValues();

		// Put supplied values into variable
		values.put(ClockItDBOpenHelper.SERVICES_NAME, name);
		values.put(ClockItDBOpenHelper.SERVICES_DESCRIPTION, description);
		values.put(ClockItDBOpenHelper.SERVICES_RATE, rate);

		// Insert new entry and return the generated id
		int insertId;
		try {
			insertId = (int) database.insert(
					ClockItDBOpenHelper.TABLE_SERVICES, null, values);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		Log.i(LOGTAG, "Created service " + insertId);

		// Return a service object corresponding to the entry
		return new Services(insertId, name, description, rate);
	}

	/**
	 * Retrieves a list of all entries in service table as Services objects.
	 * 
	 * @return The list of services stored in the database.
	 */
	public List<Services> getAllServices() {
		// Variable to hold services
		List<Services> services = new ArrayList<Services>();

		// Cursor holding query to database for all services
		Cursor cursor = database.query(ClockItDBOpenHelper.TABLE_SERVICES,
				allServiceColumns, null, null, null, null, null);

		if (cursor.getCount() > 0) {
			// Loops through values retrieved by cursor
			while (cursor.moveToNext()) {
				// Creates service object from cursor location
				Services service = new Services(
						cursor.getInt(cursor
								.getColumnIndex(ClockItDBOpenHelper.SERVICES_ID)),
						cursor.getString(cursor
								.getColumnIndex(ClockItDBOpenHelper.SERVICES_NAME)),
						cursor.getString(cursor
								.getColumnIndex(ClockItDBOpenHelper.SERVICES_DESCRIPTION)),
						cursor.getDouble(cursor
								.getColumnIndex(ClockItDBOpenHelper.SERVICES_RATE)));

				// Add service to list
				services.add(service);
			}
		}
		cursor.close();

		Log.i(LOGTAG, "Retrieved " + services.size() + " services");

		// Return list of services
		return services;
	}

	/**
	 * Retrieves a single service that corresponds to the supplied id.
	 * 
	 * @param serviceId
	 *            The id of the service to retrieve.
	 * @return The service or null.
	 */
	public Services getServiceById(int serviceId) {
		// Cursor holding query for service by id
		Cursor cursor = database.query(ClockItDBOpenHelper.TABLE_SERVICES,
				allServiceColumns, ClockItDBOpenHelper.SERVICES_ID + " = ?",
				new String[] { String.valueOf(serviceId) }, null, null, null);

		Services service = null;

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			// Creates service object from cursor location and returns
			service = new Services(
					cursor.getInt(cursor
							.getColumnIndex(ClockItDBOpenHelper.SERVICES_ID)),
					cursor.getString(cursor
							.getColumnIndex(ClockItDBOpenHelper.SERVICES_NAME)),
					cursor.getString(cursor
							.getColumnIndex(ClockItDBOpenHelper.SERVICES_DESCRIPTION)),
					cursor.getDouble(cursor
							.getColumnIndex(ClockItDBOpenHelper.SERVICES_RATE)));
		}
		cursor.close();

		Log.i(LOGTAG, "Retrieved service " + serviceId);

		// Return the service or null if one was not found
		return service;
	}

	/**
	 * Updates the service associated with the given id with the new name,
	 * description, and rate values.
	 * 
	 * @param name
	 *            The new name value for the service.
	 * @param description
	 *            The new description value for the service.
	 * @param rate
	 *            The new rate value for the service.
	 * @param serviceId
	 *            The id of the service to update.
	 * @return The updated service object.
	 */
	public Services updateService(String name, String description, Double rate,
			int serviceId) {
		// Variable to hold map of values to columns
		ContentValues values = new ContentValues();

		// Put supplied values into variable
		values.put(ClockItDBOpenHelper.SERVICES_NAME, name);
		values.put(ClockItDBOpenHelper.SERVICES_DESCRIPTION, description);
		values.put(ClockItDBOpenHelper.SERVICES_RATE, rate);

		// Update entry and return the generated id
		try {
			database.update(ClockItDBOpenHelper.TABLE_SERVICES, values,
					ClockItDBOpenHelper.SERVICES_ID + " = ?",
					new String[] { String.valueOf(serviceId) });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		Log.i(LOGTAG, "Updated service " + serviceId);

		// Return a service object corresponding to the entry
		return new Services(serviceId, name, description, rate);
	}

	/**
	 * Deletes the service associated with the supplied id.
	 * 
	 * @param serviceId
	 *            The id of the service to delete.
	 */
	public void deleteService(int serviceId) {
		// Query the services table to delete the service with the supplied id
		database.delete(ClockItDBOpenHelper.TABLE_SERVICES,
				ClockItDBOpenHelper.SERVICES_ID + " = ?",
				new String[] { String.valueOf(serviceId) });

		Log.i(LOGTAG, "Deleted service " + serviceId);
	}

	/**
	 * Determines whether or not the user is currently clocked in to a job.
	 * 
	 * @return True if the user is clocked in. False otherwise.
	 */
	public boolean isClockedIn() {
		// Cursor holding query for time stamp with no clock out
		Cursor cursor = database.query(ClockItDBOpenHelper.TABLE_TIME_STAMPS,
				allTimeStampColumns, ClockItDBOpenHelper.TIME_STAMPS_CLOCK_OUT
						+ " = ?", new String[] { String.valueOf(-1) }, null,
				null, null);

		// If a time stamp exists with no clock out then user is clocked in
		Boolean isClockedIn = cursor.getCount() > 0;
		cursor.close();

		return isClockedIn;
	}

	public TimeStamp getCurrentTimeStamp() {
		// Cursor holding query for time stamp with no clock out
		Cursor cursor = database.query(ClockItDBOpenHelper.TABLE_TIME_STAMPS,
				allTimeStampColumns, ClockItDBOpenHelper.TIME_STAMPS_CLOCK_OUT
						+ " = ?", new String[] { String.valueOf(-1) }, null,
				null, null);

		TimeStamp timeStamp = null;

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			int clientToServiceId = cursor
					.getInt(cursor
							.getColumnIndex(ClockItDBOpenHelper.TIME_STAMPS_CLIENT_TO_SERVICE_ID));

			timeStamp = new TimeStamp(
					cursor.getInt(cursor
							.getColumnIndex(ClockItDBOpenHelper.TIME_STAMPS_ID)),
					clientToServiceId,
					cursor.getLong(cursor
							.getColumnIndex(ClockItDBOpenHelper.TIME_STAMPS_CLOCK_IN)),
					cursor.getLong(cursor
							.getColumnIndex(ClockItDBOpenHelper.TIME_STAMPS_CLOCK_OUT)),
					cursor.getString(cursor
							.getColumnIndex(ClockItDBOpenHelper.TIME_STAMPS_DESCRIPTION)),
					getServiceById(getClientToServiceServiceId(clientToServiceId)),
					getClientById(getClientToServiceClientId(clientToServiceId)));
		}
		cursor.close();

		return timeStamp;
	}

	/**
	 * Clocks the user in for the supplied client and service.
	 * 
	 * @param clientId
	 *            The client to clock in for.
	 * @param serviceId
	 *            The service to clock in to.
	 * @return The created TimeStamp object.
	 */
	public TimeStamp createTimeStamp(int clientId, int serviceId) {
		// Variable to hold map of values to columns
		ContentValues values = new ContentValues();

		// Get service id if it exists or create if it doesn't
		int clientToServiceId = getClientToServiceId(clientId, serviceId);

		// Get the current time
		long clockIn = System.currentTimeMillis();

		// Put supplied values into variable
		values.put(ClockItDBOpenHelper.TIME_STAMPS_CLIENT_TO_SERVICE_ID,
				clientToServiceId);
		values.put(ClockItDBOpenHelper.TIME_STAMPS_CLOCK_IN, clockIn);
		values.put(ClockItDBOpenHelper.TIME_STAMPS_CLOCK_OUT, -1);

		// Insert new entry and return the generated id
		int insertId;
		try {
			insertId = (int) database.insert(
					ClockItDBOpenHelper.TABLE_TIME_STAMPS, null, values);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		Log.i(LOGTAG, "Created time stamp " + insertId);

		// Return a time stamp object corresponding to the entry
		return new TimeStamp(insertId, clientToServiceId, clockIn, 0, null,
				getServiceById(serviceId), getClientById(clientId));
	}

	/**
	 * Clocks the user out of the current time stamp.
	 * 
	 * @param description
	 *            A description of what was done during the shift.
	 * @return The completed time stamp.
	 */
	public TimeStamp ClockOut(String description) {
		// Retrieve the current jobs time stamp
		Cursor cursor = database.query(ClockItDBOpenHelper.TABLE_TIME_STAMPS,
				allTimeStampColumns, ClockItDBOpenHelper.TIME_STAMPS_CLOCK_OUT
						+ " = ?", new String[] { String.valueOf(-1) }, null,
				null, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			// Retrieve the values
			int timeStampId = cursor.getInt(cursor
					.getColumnIndex(ClockItDBOpenHelper.TIME_STAMPS_ID));
			int clientToServiceId = cursor
					.getInt(cursor
							.getColumnIndex(ClockItDBOpenHelper.TIME_STAMPS_CLIENT_TO_SERVICE_ID));
			long clockIn = cursor.getLong(cursor
					.getColumnIndex(ClockItDBOpenHelper.TIME_STAMPS_CLOCK_IN));
			long clockOut = System.currentTimeMillis();

			// Variable to hold map of values to columns
			ContentValues values = new ContentValues();

			// Put supplied values into variable
			values.put(ClockItDBOpenHelper.TIME_STAMPS_DESCRIPTION, description);
			values.put(ClockItDBOpenHelper.TIME_STAMPS_CLOCK_OUT, clockOut);

			// Update the time stamp with the clock out time and description
			database.update(ClockItDBOpenHelper.TABLE_TIME_STAMPS, values,
					ClockItDBOpenHelper.TIME_STAMPS_ID + " = ?",
					new String[] { String.valueOf(timeStampId) });

			// Create a new TimeStamp object from the data
			TimeStamp timeStamp = new TimeStamp(
					timeStampId,
					clientToServiceId,
					clockIn,
					clockOut,
					description,
					getServiceById(getClientToServiceServiceId(clientToServiceId)),
					getClientById(getClientToServiceClientId(clientToServiceId)));

			Log.i(LOGTAG, "Clocked out of timestamp " + timeStampId);

			cursor.close();

			// Return the timestamp
			return timeStamp;

		} else {
			cursor.close();

			Log.e(LOGTAG, "Failed to retrieve a timestamp to clock out of");

			return null;
		}

	}

	public TimeStamp getTimeStampById(int timeStampID) {
		// Cursor holding query for time stamp with no clock out
		Cursor cursor = database.query(ClockItDBOpenHelper.TABLE_TIME_STAMPS,
				allTimeStampColumns, ClockItDBOpenHelper.TIME_STAMPS_ID
						+ " = ?", new String[] { String.valueOf(timeStampID) }, null,
				null, null);

		TimeStamp timeStamp = null;

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			int clientToServiceId = cursor
					.getInt(cursor
							.getColumnIndex(ClockItDBOpenHelper.TIME_STAMPS_CLIENT_TO_SERVICE_ID));

			timeStamp = new TimeStamp(
					cursor.getInt(cursor
							.getColumnIndex(ClockItDBOpenHelper.TIME_STAMPS_ID)),
					clientToServiceId,
					cursor.getLong(cursor
							.getColumnIndex(ClockItDBOpenHelper.TIME_STAMPS_CLOCK_IN)),
					cursor.getLong(cursor
							.getColumnIndex(ClockItDBOpenHelper.TIME_STAMPS_CLOCK_OUT)),
					cursor.getString(cursor
							.getColumnIndex(ClockItDBOpenHelper.TIME_STAMPS_DESCRIPTION)),
					getServiceById(getClientToServiceServiceId(clientToServiceId)),
					getClientById(getClientToServiceClientId(clientToServiceId)));
		}
		cursor.close();

		return timeStamp;
	}

	/**
	 * Retrieves all of the finished time stamps
	 * 
	 * @return The list of time stamps.
	 */
	public List<TimeStamp> getAllTimeStamps() {
		// Variable to hold time stamps
		List<TimeStamp> timeStamps = new ArrayList<TimeStamp>();

		// Cursor holding query to database for all completed time stamps
		Cursor cursor = database.query(ClockItDBOpenHelper.TABLE_TIME_STAMPS,
				allTimeStampColumns, ClockItDBOpenHelper.TIME_STAMPS_CLOCK_OUT
						+ " != ?", new String[] { String.valueOf(-1) }, null,
				null, null);

		int clientToServiceId;

		if (cursor.getCount() > 0) {
			// Loops through values retrieved by cursor
			while (cursor.moveToNext()) {
				clientToServiceId = cursor
						.getInt(cursor
								.getColumnIndex(ClockItDBOpenHelper.TIME_STAMPS_CLIENT_TO_SERVICE_ID));

				// Creates time stamp object from cursor location
				TimeStamp timeStamp = new TimeStamp(
						cursor.getInt(cursor
								.getColumnIndex(ClockItDBOpenHelper.TIME_STAMPS_ID)),
						clientToServiceId,
						cursor.getLong(cursor
								.getColumnIndex(ClockItDBOpenHelper.TIME_STAMPS_CLOCK_IN)),
						cursor.getLong(cursor
								.getColumnIndex(ClockItDBOpenHelper.TIME_STAMPS_CLOCK_OUT)),
						cursor.getString(cursor
								.getColumnIndex(ClockItDBOpenHelper.TIME_STAMPS_DESCRIPTION)),
						getServiceById(getClientToServiceServiceId(clientToServiceId)),
						getClientById(getClientToServiceClientId(clientToServiceId)));

				// Add time stamp to list
				timeStamps.add(timeStamp);
			}
		}
		cursor.close();

		Log.i(LOGTAG, "Retrieved " + timeStamps.size() + " time stamps");

		Collections.reverse(timeStamps);

		// Return list of time stamps
		return timeStamps;
	}

	public List<TimeStamp> getClientsTimeStamps(int clientID) {
		// Variable to hold time stamps
		List<TimeStamp> timeStamps = getAllTimeStamps();

		Collections.reverse(timeStamps);

		for (TimeStamp timeStamp : timeStamps) {
			if (timeStamp.getClient().getId() != clientID) {
				timeStamps.remove(timeStamp);
			}
		}
		// Return list of time stamps for client
		return timeStamps;
	}
	
	public TimeStamp updateTimeStamp(String description, int timeStampId) {
		// Variable to hold map of values to columns
		ContentValues values = new ContentValues();

		// Put supplied values into variable
		values.put(ClockItDBOpenHelper.TIME_STAMPS_DESCRIPTION, description);

		// Update entry and return the generated id
		try {
			database.update(ClockItDBOpenHelper.TABLE_TIME_STAMPS, values,
					ClockItDBOpenHelper.TIME_STAMPS_ID + " = ?",
					new String[] { String.valueOf(timeStampId) });
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		Log.i(LOGTAG, "Updated time stamp " + timeStampId);

		return getTimeStampById(timeStampId);
	}
	
	public void deleteTimeStamp(int timeStampId) {
		// Query the services table to delete the time stamp with the supplied id
		database.delete(ClockItDBOpenHelper.TABLE_TIME_STAMPS,
				ClockItDBOpenHelper.TIME_STAMPS_ID + " = ?",
				new String[] { String.valueOf(timeStampId) });

		Log.i(LOGTAG, "Deleted time stamp " + timeStampId);
	}

	/**
	 * Retrieves the ClientToService's id that is associated with the supplied
	 * client and service ids.
	 * 
	 * @param clientId
	 *            The client of the client to service.
	 * @param serviceId
	 *            The service of the client to service.
	 * @return The ClientToService's id.
	 */
	public int getClientToServiceId(int clientId, int serviceId) {
		// Cursor holding query for client to service
		Cursor cursor = database.query(
				ClockItDBOpenHelper.TABLE_CLIENTS_TO_SERVICES,
				allClientToServiceColumns,
				ClockItDBOpenHelper.CLIENTS_TO_SERVICES_CLIENT_ID + " = ? AND "
						+ ClockItDBOpenHelper.CLIENTS_TO_SERVICES_SERVICE_ID
						+ " = ?", new String[] { String.valueOf(clientId),
						String.valueOf(serviceId) }, null, null, null);

		int id;
		// If there is an existing one, retrieve it
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			id = cursor
					.getInt(cursor
							.getColumnIndex(ClockItDBOpenHelper.CLIENTS_TO_SERVICES_ID));
			// If not then create a new one
		} else {
			id = createClientToService(clientId, serviceId);
		}
		cursor.close();

		Log.i(LOGTAG, "Retrieved client to service " + id);

		// Return the client to service id
		return id;
	}

	/**
	 * Retrieves the service id from a ClientToService.
	 * 
	 * @param clientToServiceId
	 *            The id of the ClientToService to retrieve from.
	 * @return The service id associated with the ClientToService.
	 */
	public int getClientToServiceServiceId(int clientToServiceId) {
		// Query to database for clients to services given the id
		Cursor cursor = database.query(
				ClockItDBOpenHelper.TABLE_CLIENTS_TO_SERVICES,
				allClientToServiceColumns,
				ClockItDBOpenHelper.CLIENTS_TO_SERVICES_ID + " = ?",
				new String[] { String.valueOf(clientToServiceId) }, null, null,
				null);

		int id = -1;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			// Retrieve the service id
			id = cursor
					.getInt(cursor
							.getColumnIndex(ClockItDBOpenHelper.CLIENTS_TO_SERVICES_SERVICE_ID));
		}
		cursor.close();

		// Return the service id
		return id;
	}

	/**
	 * Retrieves the client id from a ClientToService.
	 * 
	 * @param clientToServiceId
	 *            The id of the ClientToService to retrieve from.
	 * @return The client id associated with the ClientToService.
	 */
	public int getClientToServiceClientId(int clientToClientId) {
		// Query to database for clients to services given the id
		Cursor cursor = database.query(
				ClockItDBOpenHelper.TABLE_CLIENTS_TO_SERVICES,
				allClientToServiceColumns,
				ClockItDBOpenHelper.CLIENTS_TO_SERVICES_ID + " = ?",
				new String[] { String.valueOf(clientToClientId) }, null, null,
				null);

		int id = -1;
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			// Retrieve the client id
			id = cursor
					.getInt(cursor
							.getColumnIndex(ClockItDBOpenHelper.CLIENTS_TO_SERVICES_CLIENT_ID));
		}
		cursor.close();

		// Return the client id
		return id;
	}

	/**
	 * Creates a client to service id for a given client and service.
	 * 
	 * @param clientId
	 *            The client id to create the ClientToService for.
	 * @param serviceId
	 *            The service id to create the ClientToService for.
	 * @return The ClientToService's id.
	 */
	public int createClientToService(int clientId, int serviceId) {
		// Variable to hold map of values to columns
		ContentValues values = new ContentValues();

		// Put supplied values into variable
		values.put(ClockItDBOpenHelper.CLIENTS_TO_SERVICES_CLIENT_ID, clientId);
		values.put(ClockItDBOpenHelper.CLIENTS_TO_SERVICES_SERVICE_ID,
				serviceId);

		// Insert new entry and return the generated id
		int insertId;
		try {
			insertId = (int) database
					.insert(ClockItDBOpenHelper.TABLE_CLIENTS_TO_SERVICES,
							null, values);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		Log.i(LOGTAG, "Created client to service " + insertId);

		// Return the client to service id
		return insertId;
	}

	/**
	 * Creates a contact entry from the supplied values.
	 * 
	 * @param clientId
	 *            The client to attach the contact to.
	 * @param firstName
	 *            The first name of the contact.
	 * @param lastName
	 *            The last name of the contact.
	 * @param email
	 *            The email for the contact.
	 * @param phone
	 *            The phone number for the contact.
	 * @return The created contact's id.
	 */
	public int createContact(int clientId, String firstName, String lastName,
			String email, String phone) {
		// Variable to hold map of values to columns
		ContentValues values = new ContentValues();

		// Put supplied values into variable
		values.put(ClockItDBOpenHelper.CONTACTS_FIRST_NAME, firstName);
		values.put(ClockItDBOpenHelper.CONTACTS_LAST_NAME, lastName);
		values.put(ClockItDBOpenHelper.CONTACTS_EMAIL, email);
		values.put(ClockItDBOpenHelper.CONTACTS_NUMBER, phone);

		// Insert new entry and return the generated id
		int insertId;
		try {
			insertId = (int) database.insert(
					ClockItDBOpenHelper.TABLE_CONTACTS, null, values);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		// If the contact was successfully created, connect it to the client
		if (insertId != -1) {
			Log.i(LOGTAG, "Created contact " + insertId);
			createClientToContact(clientId, insertId);
		}

		// Return the created contact's id
		return insertId;
	}

	/**
	 * Creates a ClientToContact entry for a client and contact.
	 * 
	 * @param clientId
	 *            The client's id.
	 * @param contactId
	 *            The contact's id.
	 * @return The created ClientToContact's id.
	 */
	public int createClientToContact(int clientId, int contactId) {
		// Variable to hold map of values to columns
		ContentValues values = new ContentValues();

		// Put supplied values into variable
		values.put(ClockItDBOpenHelper.CLIENTS_TO_CONTACTS_CLIENT_ID, clientId);
		values.put(ClockItDBOpenHelper.CLIENTS_TO_CONTACTS_CONTACT_ID,
				contactId);

		// Insert new entry and return the generated id
		int insertId;
		try {
			insertId = (int) database
					.insert(ClockItDBOpenHelper.TABLE_CLIENTS_TO_CONTACTS,
							null, values);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		Log.i(LOGTAG, "Created client to contact " + insertId);

		// Return the client to contact id
		return insertId;
	}

	/**
	 * Retrieves a list of contacts connected to a supplied client's id.
	 * 
	 * @param clientId
	 *            The id of the client to retrieve contacts for.
	 * @return The list of contacts associated with the client.
	 */
	public List<Contact> getClientContacts(int clientId) {
		// Variable to hold contacts
		List<Contact> contacts = new ArrayList<Contact>();

		// Cursor holding query to database for contacts for client
		Cursor cursor = database.query(
				ClockItDBOpenHelper.TABLE_CLIENTS_TO_CONTACTS,
				allClientToContactColumns,
				ClockItDBOpenHelper.CLIENTS_TO_CONTACTS_CLIENT_ID + " = ?",
				new String[] { String.valueOf(clientId) }, null, null, null);

		// Variable to hold the id of each contact
		int contactId;

		if (cursor.getCount() > 0) {
			// Loops through values retrieved by cursor
			while (cursor.moveToNext()) {
				// Set contactId to the current contact's id
				contactId = cursor
						.getInt(cursor
								.getColumnIndex(ClockItDBOpenHelper.CLIENTS_TO_CONTACTS_CONTACT_ID));

				// Creates contact object from cursor location and adds to the
				// list of contacts
				contacts.add(getContactById(contactId));
			}
		}
		cursor.close();

		Log.i(LOGTAG, "Retrieved " + contacts.size() + " contacts");

		// Return list of contacts
		return contacts;
	}

	/**
	 * Retrieves the contact associated with the supplied id.
	 * 
	 * @param contactId
	 *            The id of the contact to retrieve.
	 * @return The contact or null.
	 */
	public Contact getContactById(int contactId) {
		// Cursor holding query for contact by id
		Cursor cursor = database.query(ClockItDBOpenHelper.TABLE_CONTACTS,
				allContactColumns, ClockItDBOpenHelper.CONTACTS_ID + " = ?",
				new String[] { String.valueOf(contactId) }, null, null, null);

		// Variable to hold contact (null by default)
		Contact contact = null;

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			// Creates contact object from cursor location and returns
			contact = new Contact(
					cursor.getInt(cursor
							.getColumnIndex(ClockItDBOpenHelper.CONTACTS_ID)),
					cursor.getString(cursor
							.getColumnIndex(ClockItDBOpenHelper.CONTACTS_FIRST_NAME)),
					cursor.getString(cursor
							.getColumnIndex(ClockItDBOpenHelper.CONTACTS_LAST_NAME)),
					cursor.getString(cursor
							.getColumnIndex(ClockItDBOpenHelper.CONTACTS_EMAIL)),
					cursor.getString(cursor
							.getColumnIndex(ClockItDBOpenHelper.CONTACTS_NUMBER)));
		}
		cursor.close();

		Log.i(LOGTAG, "Retrieved contact " + contactId);

		// Return the contact or null if one was not found
		return contact;
	}

}
