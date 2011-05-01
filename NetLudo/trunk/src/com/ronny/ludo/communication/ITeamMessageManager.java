package com.ronny.ludo.communication;

import java.io.Serializable;

import android.os.Handler;

/**
 * Copyright: (c) 2011 Ronny Heitmann Andersen, Ronny Øvereng and Karl-Erik Moberg
 * 
 * Team message manager interface
 * 
 * @author ANDROD
 *
 */
public interface ITeamMessageManager {

	/**
	 * Handler registration for client messages from the message system 
	 * @param messageBroakerClient
	 */
	public void addListener(Handler messageBroakerClient);
	/**
	 * Handler remover for client messages from the message system
	 * @param messageBroakerClient
	 */
	public void removeListener(Handler messageBroakerClient);
	
	/**
	 *  Public a send message to all connected clients
	 * @param outgoingMessage
	 */
	public void sendMessageToClients(Serializable outgoingMessage);
	/**
	 *  Send message to a specific client
	 * @param outgoingMessage
	 * @param targetClient
	 */
	public void sendMessageToClient(Serializable outgoingMessage, Integer targetClient);
	
	/**
	 * Check if the collaboration registration is open or not
	 * @return true if registration is open
	 */
	public boolean isRegistrationOpen();
	/**
	 * Open for new client connections
	 */
	public void openRegistration();
	/**
	 * Close for new client connections
	 */
	public void closeRegistration();
	
	public boolean isServer();
	
	/**
	 * Handler remover for administrative/debug messages from the message system
	 * @param handle
	 */
	public void removeAdminListener(Handler adminListenerHandler);
	/**
	 * Handler registration for administrative/debug messages from the message system
	 * @param adminListenerHandler
	 */
	public void addAdminListener(Handler adminListenerHandler);
	/**
	 * Disconnect all clients/server
	 */
	public void disconnect();
}
