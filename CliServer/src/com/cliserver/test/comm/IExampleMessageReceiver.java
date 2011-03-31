package com.cliserver.test.comm;

public interface IExampleMessageReceiver {
	public void handleIncomingMessage(String theMessage, Integer clientId);
}
