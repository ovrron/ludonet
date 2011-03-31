package com.cliserver.test.comm;

import java.io.Serializable;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * LudoMessageBroker. Bridge between GUI and Game logic.
 * 
 * 
 * 
 * @author androd
 * 
 */
public class LudoMessageBroker {

	private ITeamMessageManager currentServer = null;
	private ILudoMessageReceiver messageReceiver = null;

	public LudoMessageBroker(ILudoMessageReceiver receiver,
			ITeamMessageManager msgServer) {
		currentServer = msgServer;
		messageReceiver = receiver;

		// Create a handler for messages from the server
		msgServer.addListener(new Handler() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
//				handleTeamMessage((String) msg.obj);
				Integer client = msg.getData().getInt(TeamMessageMgr.BUNDLE_CLIENTID);
				String theMessage = msg.getData().getSerializable(TeamMessageMgr.BUNDLE_MESSAGE).toString();
				handleTeamMessage(theMessage, client);
			}

		});
	}

	/**
	 * Incoming message from the team message server.
	 * 
	 * @param message
	 *            message from server
	 */
	public void handleTeamMessage(Serializable message, Integer clientId) {
		Log.d("ExampleMessageBroker",
				"Got message from server : " + message.toString());

		// Safety check
		if (message.toString() == null) {
			return;
		}

		String msg = message.toString();
		final String[] messageParts = msg.split("\\,");

		/** ************************ Protokoll ***********************************
		 * 
		 * Meldinger for Ludo starter med "L": L,<type>,<kommando>,<rest...>
		 * Deretter er det verdi for mottaker
		 * A for administering - gui som styrer oppkobling/frakobling etc.
		 * G for Game - selve spill-interaksjon
		 * Eks "L,A,C,tekst"   Tolkes Ludo-melding, Administrativ, Connect, tekst
		 * 
		 * Administrative verdier (A):
		 * C - klient kobler dil
		 * D - klient kobler fra
		 * 
		 * Game verdier (G)
		 * C - spørring fra klienter (farge)
		 * T - terning er kastet
		 * M - move
		 */
		 
		
		// eller M for move
		if (messageParts[0].equals("L")) {
			if(messageParts[0].equals("A")) {
				
			}
			if(messageParts[0].equals("G")) {
				
			}

			messageReceiver.handleIncomingMessage(message.toString());			
		}
	}

	/**
	 * Send message to all participants
	 * 
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
