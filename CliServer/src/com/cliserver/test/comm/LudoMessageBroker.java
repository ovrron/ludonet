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
				// handleTeamMessage((String) msg.obj);
				Integer msgtype = msg.getData().getInt(
						TeamMessageMgr.BUNDLE_OPERATION);
				Integer client = msg.getData().getInt(
						TeamMessageMgr.BUNDLE_CLIENTID);
				String theMessage = msg.getData()
						.getSerializable(TeamMessageMgr.BUNDLE_MESSAGE)
						.toString();
				handleTeamMessage(theMessage, client, msgtype);
			}

		});

		// Add myself to admin messages
		msgServer.addAdminListener(new Handler() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				// Integer client =
				// msg.getData().getInt(TeamMessageMgr.BUNDLE_CLIENTID);
				// String theMessage =
				// msg.getData().getSerializable(TeamMessageMgr.BUNDLE_MESSAGE).toString();

				Integer msgtype = msg.getData().getInt(
						TeamMessageMgr.BUNDLE_OPERATION);
				Integer client = msg.getData().getInt(
						TeamMessageMgr.BUNDLE_CLIENTID);
				String theMessage = msg.getData()
						.getSerializable(TeamMessageMgr.BUNDLE_MESSAGE)
						.toString();

				handleAdminTeamMessage(theMessage, client, msgtype.intValue());
				// msg.recycle();
			}

		});
	}

	public void handleAdminTeamMessage(Serializable message, Integer clientId,
			int operation) {
		Log.d("Ludo(A):", clientId + "/" + operation + "/" + message.toString());
		// messageReceiver.handleIncomingMessage(message.toString(), clientId);

		// Safety check
		if (clientId == null) {
			return;
		}

		switch (operation) {
		case TeamMessageMgr.ADMIN_OPERATION_NOTHING:
			break;
		case TeamMessageMgr.ADMIN_OPERATION_CLIENT_CONNECT:
			break;
		case TeamMessageMgr.ADMIN_OPERATION_CLIENT_DISCONNECT:
			break;
		case TeamMessageMgr.ADMIN_OPERATION_REG_OPEN:
			break;
		case TeamMessageMgr.ADMIN_OPERATION_REG_CLOSED:
			break;
		case TeamMessageMgr.ADMIN_OPERATION_CLIENT_SOCKET_NOT_CONNECTED:
			break;
		case TeamMessageMgr.ADMIN_OPERATION_SERVER_SOCKET_OPENED:
			break;
		case TeamMessageMgr.ADMIN_OPERATION_SERVER_SOCKET_CLOSED:
			break;
		case TeamMessageMgr.ADMIN_OPERATION_DISCONNECT:
			break;
		case TeamMessageMgr.ADMIN_OPERATION_EXCEPTION:
			break;
		}

	}

	/**
	 * Incoming message from the team message server.
	 * 
	 * @param message
	 *            message from server
	 */
	public void handleTeamMessage(Serializable message, Integer clientId,
			int operation) {
		Log.d("Ludo(C):", clientId + "/" + operation + "/" + message.toString());
		messageReceiver.handleIncomingMessage(message.toString(), clientId);

		// Safety check
		if (message.toString() == null) {
			return;
		}

		String msg = message.toString();
		final String[] messageParts = msg.split("\\,");

		/**
		 * ************************ Protokoll
		 * 
		 * Meldinger for Ludo er <type>,<kommando>,<rest...>
		 * Deretter er det verdi for mottaker A for administering - gui som
		 * styrer oppkobling/frakobling etc. G for Game - selve
		 * spill-interaksjon Eks "L,A,C,tekst" Tolkes Ludo-melding,
		 * Administrativ, Connect, tekst
		 * 
		 * Administrative verdier (A): C - klient kobler dil D - klient kobler
		 * fra
		 * 
		 * Game verdier (G) C - spørring fra klienter (farge) T - terning er
		 * kastet M - move
		 */

		// eller M for move
		if (messageParts[0].equals("L")) {
			if (messageParts[0].equals("A")) {

			}
			if (messageParts[0].equals("G")) {

			}

			messageReceiver.handleIncomingMessage(message.toString(), clientId);
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
