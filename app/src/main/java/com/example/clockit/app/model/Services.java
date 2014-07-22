package com.example.clockit.app.model;

/**
 * Class for an object representation for entries from the services table of the
 * database.
 * 
 * @author Cameron Irwin
 * 
 */
public class Services {

	/** The service's id. */
	private int Id;
	
	/** The service's name. */
	private String Name;
	
	/** The service's description. */
	private String Description;
	
	/** The service's rate. */
	private double Rate;

	/**
	 * Constructor that takes in column values and sets them to properties.
	 * 
	 * @param id
	 *            The id of the service.
	 * @param name
	 *            The name of the service.
	 * @param description
	 *            The description of the service.
	 * @param rate
	 *            The pay rate of the service.
	 */
	public Services(int id, String name, String description, double rate) {
		// Set property values
		Id = id;
		Name = name;
		Description = description;
		Rate = rate;
	}

	/**
	 * Allows access to private Id variable.
	 * 
	 * @return The service's id.
	 */
	public int getId() {
		return Id;
	}

	/**
	 * Allows access to private Name variable.
	 * 
	 * @return The service's name.
	 */
	public String getName() {
		return Name;
	}

	/**
	 * Allows access to private Description variable.
	 * 
	 * @return The service's description.
	 */
	public String getDescription() {
		return Description;
	}

	/**
	 * Allows access to private Rate variable.
	 * 
	 * @return The service's pay rate.
	 */
	public double getRate() {
		return Rate;
	}

	/**
	 * Gives a simple string representation of the object's properties.
	 * 
	 * @return String representation of the service.
	 */
	public String toString() {
		return Name + "\n" + Rate + "/hour";
	}

}
