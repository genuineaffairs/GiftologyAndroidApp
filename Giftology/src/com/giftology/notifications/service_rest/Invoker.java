package com.giftology.notifications.service_rest;

public class Invoker {

	Command theCommand;

	public Invoker(Command theCommand) {
		super();
		this.theCommand = theCommand;
	}

	public String call(String userId) {
		return theCommand.execute(userId);
	}
}
