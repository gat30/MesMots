package com.aopds.aopdsServiceClient.aopdsServiceException;

public class AopdsServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1060273058177625870L;

	public AopdsServiceException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public AopdsServiceException(String detailMessage) {
		super(detailMessage);
	}
	
}
