package com.aopds.aopdsData.domain;

public class ServerComputedSuggestion extends Suggestion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private User user;
	private User admin;
	private int serverId;

	public ServerComputedSuggestion() {
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getAdmin() {
		return admin;
	}

	public void setAdmin(User admin) {
		this.admin = admin;
	}

	@Override
	public String toString() {
		return "ServerComputedSuggestion [ " + super.toString() + ",  user="
				+ user + ", admin=" + admin + ", serverId=" + serverId + "]";
	}

}
