package com.example.clockit.app.model;

/**
 * Class for an object representation for entries from the contact table of the
 * database.
 * 
 * @author Cameron Irwin
 * 
 */
public class Contact {

	/** The contact's id. */
	private int Id;
	
	/** The contact's first name. */
	private String FirstName;
	
	/** The contact's last name. */
	private String LastName;
	
	/** The contact's phone number. */
	private String Phone;
	
	/** The contact's email. */
	private String Email;

	/**
	 * Constructor that takes in column values and sets them to properties.
	 * 
	 * @param id
	 *            The id of the contact.
	 * @param firstName
	 *            The contact's first name.
	 * @param lastName
	 *            The contact's last name.
	 * @param phone
	 *            The phone number for the contact.
	 * @param email
	 *            The email address for the contact.
	 */
	public Contact(int id, String firstName, String lastName, String email, String phone) {
		// Set properties to supplied values
		Id = id;
		FirstName = firstName;
		LastName = lastName;
		Phone = phone;
		Email = email;
	}

	/**
	 * Allows access to private Id variable.
	 * 
	 * @return The contacts id.
	 */
	public int getId() {
		return Id;
	}

	/**
	 * Allows access to private FirstName variable.
	 * 
	 * @return The contact's first name.
	 */
	public String getFirstName() {
		return FirstName;
	}
	
	/**
	 * Allows access to private LastName variable.
	 * 
	 * @return The contact's last name.
	 */
	public String getLastName() {
		return LastName;
	}

	/**
	 * Allows access to private Phone variable.
	 * 
	 * @return The contact's phone number.
	 */
	public String getPhone() {
		return Phone;
	}

	/**
	 * Allows access to private Email variable.
	 * 
	 * @return The contact's email address.
	 */
	public String getEmail() {
		return Email;
	}
	
	/**
	 * Gives a simple string representation of the object's properties.
	 * 
	 * @return String representation of the contact.
	 */
	@Override
	public String toString() {
		return FirstName + " " + LastName + "\n" + Email + "\n" + Phone;		
	}

}
