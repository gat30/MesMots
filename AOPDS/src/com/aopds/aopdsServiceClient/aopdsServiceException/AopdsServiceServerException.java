package com.aopds.aopdsServiceClient.aopdsServiceException;

public class AopdsServiceServerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2180047585413473056L;
	public static final int MISSING_PARAMETER = 1;
	public static final int UNDEFINED_FUNCTION = 2;
	public static final int UNKNOWN_FUNCTION = 3;
	public static final int FUNCTION_CALL_FAILED = 4;
	public static final int UNAUTHORIZED_ACCESS = 5;
	public static final int INTERNAL_SERVER_ERROR = 6;
	public static final int WRONG_PARAMETER_TYPE = 7;

	private int serverCode;
	private String serverMessage;

	public AopdsServiceServerException() {
		super("The distant function throwed an exception. ");
	}

	@Override
	public String getMessage() {
		return super.getMessage() + " with the code '" + serverCode
				+ "' and message '" + this.serverMessage + "'";
	}

	public void setMessage(String message) {
		this.serverMessage = message;
	}

	public int getServerCode() {
		return serverCode;
	}

	public void setServerCode(int serverCode) {
		this.serverCode = serverCode;
	}

}
