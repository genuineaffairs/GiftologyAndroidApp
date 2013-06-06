package com.giftology.notifications.service_rest;

public class NewCelebrationCommand implements Command {

	private RESTConnect restConnect;

	public NewCelebrationCommand(RESTConnect restConnect) {
		super();
		this.restConnect = restConnect;
	}

	@Override
	public String execute(String userId) {
		// TODO Auto-generated method stub
		return this.restConnect.new_celebration(userId);
	}

}
