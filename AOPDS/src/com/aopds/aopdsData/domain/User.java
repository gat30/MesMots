package com.aopds.aopdsData.domain;

public class User {
	
 	private long id;
	private String firstName;
 	private String lastName;
 	private String email;
 	private String password;

	public User() {}

	public User( 	long id, String firstName, String lastName, 
				  	String email)
	{
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}
	
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "User [firstName=" + firstName + ", lastName=" + lastName
				+ ", email=" + email + "]";
	}

}
