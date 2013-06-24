package com.aopds.aopdsServiceClient.aopdsServiceException;

public class AopdsServiceMalformedResponseException extends
		AopdsServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7263942059268154933L;
	private String malformedResponse;

	public AopdsServiceMalformedResponseException(String detailMessage,
			String malformedResponse, Throwable throwable) {
		super(detailMessage, throwable);
		this.malformedResponse = malformedResponse;
	}

	public String getMalformedResponse() {
		return malformedResponse;
	}

}
