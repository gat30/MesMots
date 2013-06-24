package com.aopds.aopdsServiceClient;

public class AopdsServiceAuthorizationToken {

	private String userId;
	private String userPassword;
	
	
	public AopdsServiceAuthorizationToken (String userId,String userPassword) {
		this.userId = userId;
		this.userPassword = userPassword;
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getUserPassword() {
		return userPassword;
	}


	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	
	
	
}
