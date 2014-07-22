package com.example.clockit.app.model;

import java.util.List;

/**
 * Class for an object representation for entries from the client table of the
 * database.
 * 
 * @author Cameron Irwin
 * 
 */
public class Client {

	/** The client's id. */
	private int Id;
	
	/** The client's name. */
	private String Name;
	
	/** The client's description. */
	private String Description;
	
	/** The client's associated services. */
	private List<Services> Services;

	/**
	 * Constructor that takes in column values and sets them to properties.
	 * 
	 * @param id
	 *            The clients id.
	 * @param name
	 *            The clients name.
	 * @param description
	 *            The clients description.
	 * @param services
	 * 			  The services associated with the client.
	 */
	public Client(int id, String name, String description, List<Services> services) {
		// Set properties to the supplied values
		Id = id;
		Name = name;
		Description = description;
		Services = services;
	}

	/**
	 * Allows access to private Id variable.
	 * 
	 * @return The client's id.
	 */
	public int getId() {
		return Id;
	}

	/**
	 * Allows access to private Name variable.
	 * 
	 * @return The client's name.
	 */
	public String getName() {
		return Name;
	}

	/**
	 * Allows access to private Description variable.
	 * 
	 * @return The client's description.
	 */
	public String getDescription() {
		return Description;
	}
	
	/**
	 * Allows access to private Services variable.
	 * 
	 * @return The client's services.
	 */
	public List<Services> getServices() {
		return Services;
	}

	/**
	 * Gives a simple string representation of the object's properties.
	 * 
	 * @return String representation of the client.
	 */
	public String toString() {
		return Name;
	}

}
