package com.cliserver.test.comm;

import java.io.Serializable;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ExampleMessageBroker /*implements ITeamMessageListener*/ {

	private ITeamMessageManager currentServer = null;
	private ILudoMessageReceiver messageReceiver = null;
	
	public ExampleMessageBroker(ILudoMessageReceiver receiver, ITeamMessageManager msgServer) {
		currentServer = msgServer;
		messageReceiver = receiver;
		
		// Create a handler for messages from the server
		msgServer.addListener(new Handler() {

			/* (non-Javadoc)
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				handleTeamMessage((String)msg.obj);
			}
			
		});
	}

	/**
	 *  Incoming message from the team message server.
	 * @param message message from server
	 */
	public void handleTeamMessage(Serializable message) {
		final String[] messageParts = message.toString().split("\\,");
		Log.d("ExampleMessageBroker","Got message from server : "+message.toString());
		messageReceiver.handleIncomingMessage(messageParts[0], messageParts[1], messageParts[2], messageParts[3]);
	}
	
	/**
	 * Send a message to all participants
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 */
	public void distributeMessage(String a, String b, String c, String d) {
		String utmsg = a+","+b+","+c+","+d;
		currentServer.sendMessage(utmsg);
	}

	/**
	 * Send message to all participants
	 * @param theMsg
	 */
	public void distributeMessage(String theMsg) {
		currentServer.sendMessage(theMsg);		
	}
}
