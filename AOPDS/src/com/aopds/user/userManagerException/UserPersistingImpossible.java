package com.aopds.user.userManagerException;

public class UserPersistingImpossible extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserPersistingImpossible(Throwable throwable) {
		super("Impossible to store user.", throwable);

	}

	
	
}
