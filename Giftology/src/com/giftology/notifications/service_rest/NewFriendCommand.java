package com.giftology.notifications.service_rest;

public class NewFriendCommand implements Command {

	private RESTConnect restConnect;

	public NewFriendCommand(RESTConnect restConnect) {
		super();
		this.restConnect = restConnect;
	}

	@Override
	public String execute(String userId) {
		return this.restConnect.new_friend(userId);

	}

}
