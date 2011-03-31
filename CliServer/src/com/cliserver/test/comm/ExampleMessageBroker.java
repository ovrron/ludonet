package com.cliserver.test.comm;

import java.io.Serializable;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ExampleMessageBroker {

	private ITeamMessageManager currentServer = null;
	private IExampleMessageReceiver messageReceiver = null;
	
	public ExampleMessageBroker(IExampleMessageReceiver receiver, ITeamMessageManager msgServer) {
		currentServer = msgServer;
		messageReceiver = receiver;
		
		// Create a handler for messages from the server
		msgServer.addListener(new Handler() {
			/* (non-Javadoc)
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				Integer client = msg.getData().getInt(TeamMessageMgr.BUNDLE_CLIENTID);
				String theMessage = msg.getData().getSerializable(TeamMessageMgr.BUNDLE_MESSAGE).toString();
				handleTeamMessage(theMessage, client);
//				msg.recycle();
			}
			
		});
	}

	/**
	 *  Incoming message from the team message server.
	 * @param msg message from server
	 */
	public void handleTeamMessage(String msg, Integer clientId) {
//		final String[] messageParts = message.toString().split("\\,");
		Log.d("ExampleMessageBroker","Intercept message from server : "+msg.toString());
		messageReceiver.handleIncomingMessage(msg, clientId);
	}
	
	/**
	 * Send message to all participants
	 * @param theMsg
	 */
	public void distributeMessage(String theMsg) {
		currentServer.sendMessageToClients(theMsg);		
	}

	/**
	 * Sample to disconnect the client connection
	 */
	public void disconnect() {
		currentServer.disconnect();		
	}
}
