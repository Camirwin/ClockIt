package com.example.clockit.app.model;

import java.util.Date;

/**
 * Class for an object representation for entries from the time stamp table of
 * the database.
 * 
 * @author Cameron Irwin
 * 
 */
public class TimeStamp {

	/** The time stamp's id. */
	private int Id;

	/** The time stamp's reference to both its client and service. */
	private int ClientToServiceId;

	/** The time stamp's clock in time in milliseconds. */
	private long ClockIn;

	/** The time stamp's clock out time in milliseconds. */
	private long ClockOut;

	/** The time stamp's description. */
	private String Description;

	/** The service for the time stamp. */
	private Services Service;

	/** The client for the time stamp. */
	private Client Client;

	/**
	 * Constructor that takes in column values and sets to properties.
	 * 
	 * @param id
	 *            Id of time stamp.
	 * @param clientToServiceId
	 *            Id connection to the client and service for the time stamp.
	 * @param clockIn
	 *            The time in milliseconds of the clock in for the time stamp.
	 * @param clockOut
	 *            The time in milliseconds of the clock out for the time stamp.
	 * @param service
	 *            The service for the time stamp.
	 * @param client
	 *            The client for the time stamp.
	 */
	public TimeStamp(int id, int clientToServiceId, long clockIn,
			long clockOut, String description, Services service, Client client) {
		// Set properties to supplied values
		Id = id;
		ClientToServiceId = clientToServiceId;
		ClockIn = clockIn;
		ClockOut = clockOut;
		Description = description;
		Service = service;
		Client = client;
	}

	/**
	 * Allows access to private Id variable.
	 * 
	 * @return The time stamps id.
	 */
	public int getId() {
		return Id;
	}

	/**
	 * Allows access to private ClientToServiceId variable.
	 * 
	 * @return The time stamp's ClientToServiceId.
	 */
	public int getClientToServiceId() {
		return ClientToServiceId;
	}

	/**
	 * Allows access to private ClockIn variable.
	 * 
	 * @return The time stamp's clock in time in milliseconds.
	 */
	public long getClockIn() {
		return ClockIn;
	}

	/**
	 * Allows access to private ClockOut variable.
	 * 
	 * @return The time stamp's clock out time in milliseconds.
	 */
	public long getClockOut() {
		return ClockOut;
	}

	/**
	 * Allows access to private Description variable.
	 * 
	 * @return The time stamp's description.
	 */
	public String getDescription() {
		return Description;
	}

	/**
	 * Allows access to private Service variable.
	 * 
	 * @return The time stamp's service.
	 */
	public Services getService() {
		return Service;
	}

	/**
	 * Allows access to private Client variable.
	 * 
	 * @return The time stamp's client.
	 */
	public Client getClient() {
		return Client;
	}

	/**
	 * Gives a simple string representation of the object's properties.
	 * 
	 * @return String representation of the time stamp.
	 */
	public String toString() {
		Date clockIn = new Date(ClockIn);
		Date clockOut = new Date(ClockOut);

		return Client.getName() + "\n" + Service.getName() + "\n\n"
				+ clockIn.toString() + "\n" + clockOut.toString()
				+ "\n\nDescription: " + Description + "\nEarned Income: "
				+ getEarnedIncome() + "\n\n";
	}

    public double getHoursWorked() {
        if (ClockOut != -1) {
            return (Double.valueOf((ClockOut - ClockIn) / 3600000.00));
        } else {
            return (Double.valueOf((System.currentTimeMillis() - ClockIn) / 3600000.00));
        }
    }

	/**
	 * Calculates the income earned during this time stamp.
	 * 
	 * @return The amount earned in dollars.
	 */
	public double getEarnedIncome() {
		return getHoursWorked() * getService().getRate();
	}

}
