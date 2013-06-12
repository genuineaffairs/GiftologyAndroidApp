package com.giftology.notifications.service_rest;

public class NewGiftCommand implements Command {

	private RESTConnect restConnect;

	public NewGiftCommand(RESTConnect restConnect) {
		super();
		this.restConnect = restConnect;
	}

	@Override
	public String execute(String userId) {
		return this.restConnect.new_gift(userId);

	}

}
