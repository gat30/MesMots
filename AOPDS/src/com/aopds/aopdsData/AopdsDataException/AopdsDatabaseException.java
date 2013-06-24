package com.aopds.aopdsData.AopdsDataException;


public abstract class AopdsDatabaseException extends Exception {


	private static final long serialVersionUID = 1L;
	private String databaseName = "";
	private int databaseVersion = 0;
	private Exception error; 
	
	public AopdsDatabaseException(String databaseName, int databaseVersion,	Exception error, String message) 
	{
		
		super( 
			"AopdsDatabaseException: (" + databaseName + " version " + databaseVersion + " ) " +
			message,
			error
		);
		
		this.databaseName = databaseName;
		this.databaseVersion = databaseVersion;
		this.error = error;
	}

	public String getFullDebugMessage() 
	{
		
		String returning = "";
		
		returning += this.getClass().getCanonicalName() + 
			": (" + databaseName + " version " + databaseVersion + " ) \n";
		
		returning += "Message: \n" + this.getMessage() + "\n";
		returning += "Stack Trace : \n";
		
		for (StackTraceElement ste: this.getStackTrace() ) 
		{
			returning += ste.getClassName() + "(" + ste.getFileName() + ":" + 
				ste.getLineNumber() + ") " + ste.getMethodName() + "\n";
		}
		
		returning += "\n\n";
		
		if (error != null) 
		{
			returning += "... Caused by " + error.getClass().getName() + "\n";
			returning += "with message : " + error.getMessage() + "\n";
			returning += "Stack Trace : \n";
			
			for (StackTraceElement ste: this.getStackTrace() ) {
				returning += ste.getClassName() + "(" + ste.getFileName() + ":" + 
					ste.getLineNumber() + ") " + ste.getMethodName() + "\n";
			}
			
			returning += "\n\n";
		}
		
		return returning;
	}
	
	
	public String getDatabaseName() {
		return databaseName;
	}

	public int getDatabaseVersion() {
		return databaseVersion;
	}

	public Exception getError() {
		return error;
	}


	
	
}
