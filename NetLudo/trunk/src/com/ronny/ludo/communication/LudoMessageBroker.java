package com.ronny.ludo.communication;

import java.io.Serializable;
import java.util.Vector;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ronny.ludo.model.Game;
import com.ronny.ludo.model.GameHolder;
import com.ronny.ludo.model.PlayerColor;
import com.ronny.ludo.model.TurnManager;

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
	// private ILudoMessageReceiver messageReceiver = null;
	// private Vector<Handler> messageReceivers = new
	// Vector<ILudoMessageReceiver>();
	private Vector<Handler> messageReceivers = new Vector<Handler>();

	public LudoMessageBroker(ITeamMessageManager msgServer) {
		currentServer = msgServer;

		// Create a handler for messages from the server
		msgServer.addListener(new Handler() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				// Parse message
				Integer client = msg.getData().getInt(
						TeamMessageMgr.BUNDLE_CLIENTID);
				String theMessage = msg.getData()
						.getSerializable(TeamMessageMgr.BUNDLE_MESSAGE)
						.toString();
				handleTeamMessage(theMessage, client);
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

	// private void distributeMessageToClients() {
	//
	// }

	/**
	 * Add receivers to messages
	 * 
	 * @param receiver
	 */
	public void addListener(Handler receiver) {
		messageReceivers.add(receiver);
	}

	public void removeListener(Handler listener) {
		messageReceivers.remove(listener);
	}

	/**
	 * Incoming message from the team message server. Game messages goes to GAME
	 * and to other listeners.
	 * 
	 * @param message
	 *            message from server
	 */
	public void handleTeamMessage(Serializable message, Integer clientId) {
		Log.d("Ludo(C)IN:", clientId + "/" + message.toString());

		// Safety check
		if (message.toString() == null) {
			return;
		}

		// boolean isDistributing = true; // Should be distributed to clients ?

		// Split the message to parts
		String msg = message.toString();
		final String[] messageParts = msg.split("\\,");

		/**
		 * ************************ Protokoll
		 * 
		 * <code>
		 * Meldinger for Ludo er <type>,<kommando>,<rest...> Deretter er det
		 * verdi for mottaker A for administering - gui som styrer
		 * oppkobling/frakobling etc. 
		 * G for Game - selve spill-interaksjon Eks
		 * "L,A,C,tekst" Tolkes Ludo-melding, Administrativ, Connect, tekst
		 * 
		 * Administrative verdier (A): 
		 * CI - Client is checking in with color 
		 * 		A,CI,<color> 
		 * CO - Client is checking out (disconnect) - with color
		 * 		A,CO,<color>
		 * C  - Client is asking for color (C)
		 * 		A,C,<color>
		 * CA - Color allocated
		 * 		A,CA,<color>
		 * 		A,CA,NONE for ingen farge 
		 * CT - Color is taken (notification to clients)
		 *   	A,CT,<color>
		 * CP - Current Player :
		 *  	A,CP,<color>
		 * Game verdier (G) 
		 *  T - terning er kastet T,farge, �yne
		 *  	G,T,<color>,<value> 
		 *  M - Flytt en brikke
		 *  	G,M,<color>,<piece id>,<moves>
		 *  	G,M,RED,0,3
		 *  
		 * </code>
		 */

		// **************************
		// GAME MESSAGES
		// **************************
		if (messageParts[0].equals("G")) { // Game messages
			if (messageParts[1].equals("T")) {
				// Terning kastet - melding til alle

			}
			if (messageParts[1].equals("M")) {
				// Flytt en brikke
				PlayerColor plc = PlayerColor
						.getColorFromString(messageParts[2]);
				int theBrikke = Integer.parseInt(messageParts[3]);
				int theMove = Integer.parseInt(messageParts[4]);
				// isDistributing = false;
				GameHolder.getInstance().getGame().playerMove(plc, theBrikke, theMove);
			}
		}
		// **************************
		// Administrative MESSAGES
		// **************************
		if (messageParts[0].equals("A")) { // Administrative messages

			if (messageParts[1].equals("C")) {
				// Klient sp�r etter farge
				PlayerColor pc = PlayerColor.NONE;
				PlayerColor plc = PlayerColor.NONE;
				Log.d("Ludo(A):", clientId + " Asking for color");
				if(messageParts.length<3) {
					// No color asked for - give a random
					plc = PlayerColor.RED;
				} else {
					plc = PlayerColor.getColorFromString(messageParts[2]);
				}
				pc = GameHolder.getInstance().getTurnManager().getFreeColor(TurnManager.PlayerLocation.REMOTE,plc,true);
				// Send answer to client klient
				currentServer.sendMessageToClient("A,CA," + pc.toString(), clientId);
			}

			// Client is checking in with a color
			if (messageParts[1].equals("CI")) {
				Log.d("Ludo(C):", clientId + " Checking in with color "
						+ messageParts[2]);
			}

			// TODO
			if (messageParts[1].equals("CO")) {
				// Klient sjekker ut
				Log.d("Ludo(C):", clientId + " Checking out");
			}

			// Color is taken (CT)
			if (messageParts[1].equals("CA")) {
				// Server har svart med farge
				Log.d("Ludo(C):", clientId + " Got color " + messageParts[2]);
			}
			
			// Color allocated (CA)
			if (messageParts[1].equals("CA")) {
				// Server har svart med farge
				Log.d("Ludo(C):", clientId + " Got color " + messageParts[2]);
			}
		}

		// Her skal meldingen ogs� distribueres videre til lyttere av BROKER

		Log.d("BROKER", "Distribute message: " + message);

		for (Handler l : messageReceivers) {
			try {

				l.sendMessage(l.obtainMessage(0, message));

			} catch (Exception e) {

				Log.d("BROKER",
						"Exception : ****************************************");
				Log.d("BROKER", "Exception : " + e.toString());
				Log.d("BROKER",
						"Exception : ****************************************");
			}
		}
	}

	/**
	 * Send message to all participants.
	 * 
	 * This method should be private - only public when debugging.
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

	// ************************** PLAYING COMMUNICATION
	// **************************
	/**
	 * Distribute my move - or another local players' move - to all
	 * participants.
	 * 
	 * @param color
	 *            player color
	 * @param pieceIndex
	 *            Piece index (0..n)
	 * @param numMoves
	 *            number of moves
	 */
	public void sendPieceMove(PlayerColor color, int pieceIndex, int numMoves) {
		distributeMessage("G,M," + color.toString() + "," + pieceIndex + ","
				+ numMoves);
	}

	/**
	 * Send a message that a player color is connected to the server
	 * 
	 * @param color
	 *            the player color
	 */
	public void sendPlayerConnected(PlayerColor color) {
		distributeMessage("A,CI," + color.toString());
	}

	/**
	 * Send a message to tell which color is active / tur
	 * 
	 * @param color
	 *            the player color
	 */
	public void sendPlayerToPlay(PlayerColor color) {
		distributeMessage("A,CI," + color.toString());
	}

	/**
	 * Send a message to the server, requesting a color.
	 */
	public void sendGimmeAColor() {
		distributeMessage("A,C");
	}

}
