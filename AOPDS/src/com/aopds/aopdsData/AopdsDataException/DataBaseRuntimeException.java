package com.aopds.aopdsData.AopdsDataException;

public class DataBaseRuntimeException extends AopdsDatabaseException {

	private static final long serialVersionUID = 1L;

	public DataBaseRuntimeException(String databaseName, int databaseVersion,
			Exception error) {
		super(databaseName, databaseVersion, error, 
			"An error occurred while using the AOPDS database. This can be either A problem opening " + 
			"the database link either a SQL error while enquiring the database.");
	}

	
	
	
}
