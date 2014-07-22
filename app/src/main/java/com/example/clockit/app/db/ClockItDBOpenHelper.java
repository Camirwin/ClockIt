package com.example.clockit.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Class that facilitates the act of creating and updating the local database.
 * 
 * @author Cameron Irwin
 * 
 */
public class ClockItDBOpenHelper extends SQLiteOpenHelper {

	// Tag used for logcat
	private static final String LOGTAG = "CLOCKIT";

	// Databases name and original version
	private static final String DATABASE_NAME = "clock_juvo.db";
	private static final int DATABASE_VERSION = 1;

	// Client table and associated columns
	public static final String TABLE_CLIENTS = "clients";
	public static final String CLIENTS_ID = "_id";
	public static final String CLIENTS_NAME = "name";
	public static final String CLIENTS_DESCRIPTION = "description";

	// Statement to create client table
	private static final String TABLE_CLIENTS_CREATE = "CREATE TABLE "
			+ TABLE_CLIENTS + " (" + CLIENTS_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + CLIENTS_NAME
			+ " TEXT NOT NULL, " + CLIENTS_DESCRIPTION + " TEXT NOT NULL" + ")";

	// Service table and associated columns
	public static final String TABLE_SERVICES = "services";
	public static final String SERVICES_ID = "_id";
	public static final String SERVICES_NAME = "name";
	public static final String SERVICES_DESCRIPTION = "description";
	public static final String SERVICES_RATE = "rate";

	// Statement to create service table
	private static final String TABLE_SERVICES_CREATE = "CREATE TABLE "
			+ TABLE_SERVICES + " (" + SERVICES_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + SERVICES_NAME
			+ " TEXT NOT NULL, " + SERVICES_DESCRIPTION + " TEXT NOT NULL, "
			+ SERVICES_RATE + " REAL NOT NULL" + ")";

	// Client to service table and associated columns
	public static final String TABLE_CLIENTS_TO_SERVICES = "clients_to_services";
	public static final String CLIENTS_TO_SERVICES_ID = "_id";
	public static final String CLIENTS_TO_SERVICES_CLIENT_ID = "client_id";
	public static final String CLIENTS_TO_SERVICES_SERVICE_ID = "service_id";

	// Statement to create client to service table
	private static final String TABLE_CLIENTS_TO_SERVICES_CREATE = "CREATE TABLE "
			+ TABLE_CLIENTS_TO_SERVICES
			+ " ("
			+ CLIENTS_TO_SERVICES_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ CLIENTS_TO_SERVICES_CLIENT_ID
			+ " INTEGER NOT NULL, "
			+ CLIENTS_TO_SERVICES_SERVICE_ID
			+ " INTEGER NOT NULL, "
			+ "FOREIGN KEY("
			+ CLIENTS_TO_SERVICES_CLIENT_ID
			+ ") REFERENCES "
			+ TABLE_CLIENTS
			+ "(_id) ON DELETE CASCADE, "
			+ "FOREIGN KEY("
			+ CLIENTS_TO_SERVICES_SERVICE_ID
			+ ") REFERENCES "
			+ TABLE_SERVICES
			+ "(_id) ON DELETE CASCADE" + ")";

	// Time stamp table and associated columns
	public static final String TABLE_TIME_STAMPS = "time_stamps";
	public static final String TIME_STAMPS_ID = "_id";
	public static final String TIME_STAMPS_CLIENT_TO_SERVICE_ID = "client_to_service_id";
	public static final String TIME_STAMPS_CLOCK_IN = "clock_in";
	public static final String TIME_STAMPS_CLOCK_OUT = "clock_out";
	public static final String TIME_STAMPS_DESCRIPTION = "description";

	// Statement to create time stamp table
	private static final String TABLE_TIME_STAMPS_CREATE = "CREATE TABLE "
			+ TABLE_TIME_STAMPS + " (" + TIME_STAMPS_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TIME_STAMPS_CLIENT_TO_SERVICE_ID + " INTEGER NOT NULL, "
			+ TIME_STAMPS_CLOCK_IN + " INTEGER NOT NULL, "
			+ TIME_STAMPS_CLOCK_OUT + " INTEGER, " + TIME_STAMPS_DESCRIPTION
			+ " TEXT, " + "FOREIGN KEY(" + TIME_STAMPS_CLIENT_TO_SERVICE_ID
			+ ") REFERENCES " + TABLE_CLIENTS_TO_SERVICES
			+ "(_id) ON DELETE CASCADE" + ")";

	// Contacts table and associated columns
	public static final String TABLE_CONTACTS = "contacts";
	public static final String CONTACTS_ID = "_id";
	public static final String CONTACTS_FIRST_NAME = "first_name";
	public static final String CONTACTS_LAST_NAME = "last_name";
	public static final String CONTACTS_NUMBER = "number";
	public static final String CONTACTS_EMAIL = "email";

	// Statement to create contacts table
	private static final String TABLE_CONTACTS_CREATE = "CREATE TABLE "
			+ TABLE_CONTACTS + " (" + CONTACTS_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + CONTACTS_FIRST_NAME
			+ " TEXT NOT NULL, " + CONTACTS_LAST_NAME + " TEXT NOT NULL, "
			+ CONTACTS_NUMBER + " TEXT, " + CONTACTS_EMAIL + " TEXT" + ")";

	// Client to contact table and associated columns
	public static final String TABLE_CLIENTS_TO_CONTACTS = "clients_to_contacts";
	public static final String CLIENTS_TO_CONTACTS_ID = "_id";
	public static final String CLIENTS_TO_CONTACTS_CLIENT_ID = "client_id";
	public static final String CLIENTS_TO_CONTACTS_CONTACT_ID = "contact_id";

	// Statement to create client to contact table
	private static final String TABLE_CLIENTS_TO_CONTACTS_CREATE = "CREATE TABLE "
			+ TABLE_CLIENTS_TO_CONTACTS
			+ " ("
			+ CLIENTS_TO_CONTACTS_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ CLIENTS_TO_CONTACTS_CLIENT_ID
			+ " INTEGER NOT NULL, "
			+ CLIENTS_TO_CONTACTS_CONTACT_ID
			+ " INTEGER NOT NULL, "
			+ "FOREIGN KEY("
			+ CLIENTS_TO_CONTACTS_CLIENT_ID
			+ ") REFERENCES "
			+ TABLE_CLIENTS
			+ "(_id) ON DELETE CASCADE, "
			+ "FOREIGN KEY("
			+ CLIENTS_TO_CONTACTS_CONTACT_ID
			+ ") REFERENCES "
			+ TABLE_CONTACTS
			+ "(_id) ON DELETE CASCADE" + ")";

	/**
	 * Makes a super call to the constructor passing in the given context and
	 * set database name and version.
	 * 
	 * @param context
	 *            Context from which the class is being instantiated.
	 */
	public ClockItDBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Creates each of the databases tables with the previously defined table
	 * create statements.
	 * 
	 * @param db
	 *            The database object to create the tables in.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Execute statements to create each table
		db.execSQL(TABLE_CLIENTS_CREATE);
		db.execSQL(TABLE_SERVICES_CREATE);
		db.execSQL(TABLE_CLIENTS_TO_SERVICES_CREATE);
		db.execSQL(TABLE_TIME_STAMPS_CREATE);
		db.execSQL(TABLE_CONTACTS_CREATE);
		db.execSQL(TABLE_CLIENTS_TO_CONTACTS_CREATE);

		// Record to logcat
		Log.i(LOGTAG, "ClockIt database tables created");
	}

	/**
	 * Deletes each of the database tables if they exist and calls the onCreate
	 * function to recreate the database.
	 * 
	 * @param db
	 *            The database object that is being upgraded
	 * @param oldVersion
	 *            The previous version of the database.
	 * @param newVersion
	 *            The new upgraded version of the database.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop all the tables if they exist
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENTS_TO_SERVICES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TIME_STAMPS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENTS_TO_CONTACTS);

		// Recreate tables with call to onCreate
		onCreate(db);
	}

}
