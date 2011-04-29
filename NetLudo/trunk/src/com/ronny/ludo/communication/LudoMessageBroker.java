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

	// In V1.0, we use a static message manager to communicate to 'the world'
	private ITeamMessageManager currentServer = null;

	// messageReceivers are clients to the broker - that would be on the corrent
	// phone.
	private Vector<Handler> messageReceivers = new Vector<Handler>();

	public static final String SPLITTER = ";";

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
				Integer client = msg.getData().getInt(TeamMessageMgr.BUNDLE_CLIENTID);
				String theMessage = msg.getData().getSerializable(TeamMessageMgr.BUNDLE_MESSAGE).toString();
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

				Integer msgtype = msg.getData().getInt(TeamMessageMgr.BUNDLE_OPERATION);
				Integer client = msg.getData().getInt(TeamMessageMgr.BUNDLE_CLIENTID);
				String theMessage = msg.getData().getSerializable(TeamMessageMgr.BUNDLE_MESSAGE).toString();

				handleAdminTeamMessage(theMessage, client, msgtype.intValue());
				// msg.recycle();
			}

		});
	}

	public void handleAdminTeamMessage(Serializable message, Integer clientId, int operation) {
		Log.d("Ludo(ATM):", clientId + "/" + operation + "/" + message.toString());
		// messageReceiver.handleIncomingMessage(message.toString(), clientId);
		if (GameHolder.getInstance().getMessageManager().isServer()) {
			Log.d("Ludo(ATMS):", clientId + "/" + operation + "/" + message.toString());
		}
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
			// Lost connecttion to server
			sendMessageToBrokerListeners("A" + SPLITTER + "LOST");
			break;
		case TeamMessageMgr.ADMIN_OPERATION_SERVER_SOCKET_OPENED:
			break;
		case TeamMessageMgr.ADMIN_OPERATION_SERVER_SOCKET_CLOSED:
			break;
		case TeamMessageMgr.ADMIN_OPERATION_DISCONNECT:
			break;
		case TeamMessageMgr.ADMIN_OPERATION_EXCEPTION:
			break;
		case TeamMessageMgr.ADMIN_OPERATION_COMMUNICATION_LOST:
			// sendMessageToBrokerListeners("A" + SPLITTER + "LOST" );
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
		String tmp = null;
		if (currentServer.isServer()) {
			tmp = "===== SERVER =====";
		} else {
			tmp = "===== KLIENT =====";
		}
		Log.d("Ludo(C)IN:", "JEG ER " + tmp);

		// Safety check
		if (message.toString() == null) {
			return;
		}

		// boolean isDistributing = true; // Should be distributed to clients ?

		// Split the message to parts
		String msg = message.toString();
		// final String[] messageParts = msg.split("\\,");
		final String[] messageParts = msg.split(SPLITTER);

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
		 * COS - Server is disconnecting/closing down
		 * 		A,COS
		 * C  - Client is asking for color (C)
		 * 		A,C,<color>
		 * CA - Color allocated
		 * 		A,CA,<color>
		 * 		A,CA,NONE for ingen farge 
		 * S - Client is asking for settings
		 * 		A,S
		 * SS - Send settings to client
		 * 		A,SS,<settings>
		 * P - Client is asking for players
		 * 		A,P
		 * SP - Send players to client
		 * 		A,PS,<players>
		 * CT - Color is taken (notification to clients)
		 *   	A,CT,<color>
		 * NP - Request Next Player :
		 *  	A,NP		 
		 * LOST - Server was closing connection
		 *      A,LOST
		 *    
		 * Game verdier (G) 
		 *  T - terning er kastet T,farge, �yne
		 *  	G,T,<color>,<value> 
		 *  M - Flytt en brikke
		 *  	G,M,<color>,<piece id>,<moves>
		 *  	G,M,RED,0,3
		 * CP - Current Player :
		 *  	G,CP,<color>
		 *  
		 * </code>
		 */

		// **************************
		// GAME MESSAGES
		// **************************
		if (messageParts[0].equals("G")) { // Game messages
			if (messageParts[1].equals("T")) {
				Log.d("Ludo(C):", "Terning kastet, melding til alle: " + message.toString());
				// Terning kastet - melding til alle
				// sendMessageToBrokerListeners(message.toString());
				// currentServer.sendMessageToClients(message.toString());
				int eyes = Integer.parseInt(messageParts[3]);
				GameHolder.getInstance().getSurfaceView().setDie(eyes);
				GameHolder.getInstance().getSurfaceView().reDraw();
			}
			if (messageParts[1].equals("M")) {
				// Flytt en brikke
				Log.d("Ludo(C):", "Brikke flyttet, melding til alle: " + message.toString());
				PlayerColor plc = PlayerColor.getColorFromString(messageParts[2]);
				int theBrikke = Integer.parseInt(messageParts[3]);
				int theMove = Integer.parseInt(messageParts[4]);
				// isDistributing = false;
//				GameHolder.getInstance().getGame().playerMove(plc, theBrikke, theMove);
//				GameHolder.getInstance().getSurfaceView().reDraw();
				GameHolder.getInstance().getSurfaceView().playerMove(plc, theBrikke, theMove);
				// TODO Skal denne innover ?
				// sendMessageToBrokerListeners(message.toString());
			}
			if (messageParts[1].equals("CP")) {
				// Send current player til alle
				Log.d("Ludo(C):", "Current player er nå : " + messageParts[2]);
				// sendMessageToBrokerListeners(message.toString());
				PlayerColor plc = PlayerColor.getColorFromString(messageParts[2]);
				GameHolder.getInstance().getSurfaceView().initNewPlayer(plc);
				GameHolder.getInstance().getSurfaceView().reDraw();
			}
		}
		// **************************
		// Administrative MESSAGES
		// **************************

		if (messageParts[0].equals("A")) { // Administrative messages

			if (messageParts[1].equals("NP")) {
				// Spør server om neste spiller
				if (currentServer.isServer()) {
					Log.d("Ludo(C):", "Spør server om neste spiller: " + message.toString());
					PlayerColor plc = GameHolder.getInstance().getTurnManager().advanceToNextPlayer();
					// sendMessageToBrokerListeners("G"+SPLITTER+"CP"+SPLITTER+plc);
					currentServer.sendMessageToClients("G" + SPLITTER + "CP" + SPLITTER + plc);
					GameHolder.getInstance().getSurfaceView().initNewPlayer(plc);
					// currentServer.sendMessageToClient("G"+SPLITTER+"CP"+SPLITTER+plc,
					// clientId);
				}
			}

			if (messageParts[1].equals("C")) {
				// Klient sp�r etter farge
				PlayerColor pc = PlayerColor.NONE;
				PlayerColor plc = PlayerColor.NONE;
				Log.d("Ludo(A):", clientId + " Asking for color");
				if (messageParts.length < 3) {
					// No color asked for - give a random
					plc = PlayerColor.RED;
				} else {
					plc = PlayerColor.getColorFromString(messageParts[2]);
				}
				pc = GameHolder.getInstance().getTurnManager()
						.getFreeColor(TurnManager.PlayerLocation.REMOTE, plc, true);
				// Send answer to client klient
				// currentServer.sendMessageToClient("A,CA," + pc.toString(),
				// clientId);
				currentServer.sendMessageToClient("A" + SPLITTER + "CA" + SPLITTER + pc.toString(), clientId);
				// Send info to local listeners - color taken
				// sendMessageToBrokerListeners("A,CT,"+pc.toString());
				sendMessageToBrokerListeners("A" + SPLITTER + "CT" + SPLITTER + pc.toString());
			}

			// Client is checking in with a color
			if (messageParts[1].equals("CI")) {
				Log.d("Ludo(C):", clientId + " Checking in with color " + messageParts[2]);
				sendMessageToBrokerListeners(message.toString());
			}

			// User check out from game
			if (messageParts[1].equals("CO")) {
				// Klient sjekker ut
				Log.d("Ludo(C):", clientId + " Checking out with color " + messageParts[2]);
				// Dette gjøres i LudoActivity...
				sendMessageToBrokerListeners(message.toString());
			}

			// Server quits
			if (messageParts[1].equals("COS")) {
				Log.d("Ludo(C):", clientId + " Checking out SERVER");
				// Dette gjøres i LudoActivity...
				sendMessageToBrokerListeners(message.toString());
			}

			// Color is taken (CT)
			if (messageParts[1].equals("CA")) {
				// Server har svart med farge
				Log.d("Ludo(C):", clientId + " Got color " + messageParts[2]);
				sendMessageToBrokerListeners(message.toString());
			}

			// Color allocated (CA)
			if (messageParts[1].equals("CA")) {
				// Server har svart med farge
				Log.d("Ludo(C):", clientId + " Got color " + messageParts[2]);
				sendMessageToBrokerListeners(message.toString());
			}

			// Client asking for settings
			if (messageParts[1].equals("S")) {
				// Client spør om settings
				Log.d("Ludo(C):", clientId + "Asking for settings");
				// sendMessageToBrokerListeners("A,SS,"+GameHolder.getInstance().getRules().getSettings());
				sendMessageToBrokerListeners("A" + SPLITTER + "SS" + SPLITTER
						+ GameHolder.getInstance().getRules().getSettings());
				// Send answer to client klient
				// currentServer.sendMessageToClient("A,SS," +
				// GameHolder.getInstance().getRules().getSettings(), clientId);
				currentServer.sendMessageToClient("A" + SPLITTER + "SS" + SPLITTER
						+ GameHolder.getInstance().getRules().getSettings(), clientId);
			}

			// Sending settings (SS)
			if (messageParts[1].equals("SS")) {
				// Server har sendt settings
				Log.d("Ludo(C):", clientId + "Sending settings: " + messageParts[2]);
				sendMessageToBrokerListeners(message.toString());
			}

			// Client asking for players
			if (messageParts[1].equals("P")) {
				// Client spør om players
				Log.d("Ludo(C):", clientId + "Asking for players");
				sendMessageToBrokerListeners("A" + SPLITTER + "SP" + SPLITTER
						+ GameHolder.getInstance().getTurnManager().getPlayersJSON());
				// Send answer to client klient
				currentServer.sendMessageToClient("A" + SPLITTER + "SP" + SPLITTER
						+ GameHolder.getInstance().getTurnManager().getPlayersJSON(), clientId);
			}

			// Sending players (SP)
			if (messageParts[1].equals("SP")) {
				// Server har sendt players
				Log.d("Ludo(C):", clientId + "Sending players: " + messageParts[2]);
				sendMessageToBrokerListeners(message.toString());
			}
		}

	}

	/**
	 * Send a message to all local clients (listeneres to messagebroker)
	 * 
	 * @param message
	 */
	public void sendMessageToBrokerListeners(String message) {

		Log.d("BROKER", "Distribute message: " + message);

		for (Handler l : messageReceivers) {
			try {

				l.sendMessage(l.obtainMessage(0, message));

			} catch (Exception e) {

				Log.d("BROKER", "Exception : ****************************************");
				Log.d("BROKER", "Exception : " + e.toString());
				Log.d("BROKER", "Exception : ****************************************");
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
	public void playerMove(PlayerColor color, int pieceIndex, int numMoves) {
		// distributeMessage("G,M," + color.toString() + "," + pieceIndex + ","+
		// numMoves);
		distributeMessage("G" + SPLITTER + "M" + SPLITTER + color.toString() + SPLITTER + pieceIndex + SPLITTER
				+ numMoves);
	}

	public void dieThrowed(PlayerColor color, int eyes) {
		distributeMessage("G" + SPLITTER + "T" + SPLITTER + color.toString() + SPLITTER + eyes);
	}

	/**
	 * Send a message that a player color is connected to the server
	 * 
	 * @param color
	 *            the player color
	 */
	public void sendPlayerConnected(PlayerColor color) {
		// distributeMessage("A,CI," + color.toString());
		distributeMessage("A" + SPLITTER + "CI" + SPLITTER + color.toString());
	}

	/**
	 * Send a message to tell which color is active / tur
	 * 
	 * @param color
	 *            the player color
	 */
	public void sendPlayerToPlay(PlayerColor color) {
		// distributeMessage("A,CI," + color.toString());
		distributeMessage("A" + SPLITTER + "CI" + SPLITTER + color.toString());
	}

	/**
	 * Send a message to the server, requesting a color.
	 */
	public void sendGimmeAColor() {
		// distributeMessage("A,C");
		distributeMessage("A" + SPLITTER + "C");
	}

	/**
	 * Send a message to the server, requesting settings.
	 */
	public void sendGimmeSettings() {
		Log.d("Ludo(C):", "Asking server for settings");
		// distributeMessage("A,S");
		distributeMessage("A" + SPLITTER + "S");
	}

	/**
	 * Send a message to the server, requesting players.
	 */
	public void sendGimmePlayers() {
		Log.d("Ludo(C):", "Asking server for active players");
		// distributeMessage("A,P");
		distributeMessage("A" + SPLITTER + "P");
	}

	public void sendGimmeNextPlayer() {
		Log.d("Ludo(C):", "Asking server for next player");
		if (currentServer.isServer()) {
			PlayerColor plc = GameHolder.getInstance().getTurnManager().advanceToNextPlayer();
			currentServer.sendMessageToClients("G" + SPLITTER + "CP" + SPLITTER + plc);
			GameHolder.getInstance().getSurfaceView().initNewPlayer(plc);
			GameHolder.getInstance().getSurfaceView().reDraw();
		} else {
			distributeMessage("A" + SPLITTER + "NP");
		}

	}

	/**
	 * Quitting game
	 */
	public void quitGame() {
		
		// Reset TurnManager - free colors.
		GameHolder.getInstance().getTurnManager().resetGame();
		
		// Send message to server or clients that I'm leaving.
		if (GameHolder.getInstance().getMessageManager().isServer()) {
			// GameHolder.getInstance().getMessageManager().sendMessageToClients("A,COS,NONE");
			// // Send Admin, CheckOutServer
			GameHolder.getInstance().getMessageManager()
					.sendMessageToClients("A" + SPLITTER + "COS" + SPLITTER + "NONE"); // Send
																						// Admin,
																						// CheckOutServer
		} else {
			// GameHolder.getInstance().getMessageManager().sendMessageToClients("A,CO,"
			// + GameHolder.getInstance().getLocalClientColor().toString()); //
			// Check out client color
			PlayerColor plc = GameHolder.getInstance().getLocalClientColor().elementAt(0);
			// I'm a client - send message that I'm quitting
			GameHolder.getInstance().getMessageManager()
					.sendMessageToClients("A" + SPLITTER + "CO" + SPLITTER + plc.toString()); // Check
																								// out
																								// client
																								// color
		}

		GameHolder.getInstance().getMessageManager().disconnect();
	}

}
