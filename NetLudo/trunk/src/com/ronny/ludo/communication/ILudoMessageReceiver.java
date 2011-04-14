package com.ronny.ludo.communication;

/**
 * Message broker interface gui/game+net
 * @author androd
 *
 */
public interface ILudoMessageReceiver {
	public void handleIncomingMessage(String theMessage, Integer clientId);
}
