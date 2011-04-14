package com.ronny.ludo.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * adb -s emulator-5556 forward tcp:11700 tcp:11700
 * 
 * The TeamMessageMgr is managing socket connections between collaborating
 * clients. The manager may act as a server for creating the initial connection
 * pool of clients, but may also act as a client. The connection and connection
 * behaviour is transparent.
 * 
 * @author ANDROD
 * 
 */
public class TeamMessageMgr extends Thread implements ITeamMessageManager {

	public static String BUNDLE_CLIENTID = "id";
	public static String BUNDLE_MESSAGE = "msg";
	public static String BUNDLE_OPERATION = "op";

	public static final int ADMIN_OPERATION_NOTHING = 1000;
	public static final int ADMIN_OPERATION_CLIENT_CONNECT = 1001; // Client connects
	public static final int ADMIN_OPERATION_CLIENT_DISCONNECT = 1002; // Client disconnects
	public static final int ADMIN_OPERATION_REG_OPEN = 1003; // Registration opened
	public static final int ADMIN_OPERATION_REG_CLOSED = 1004; // Registration closed
	public static final int ADMIN_OPERATION_CLIENT_SOCKET_NOT_CONNECTED = 1005; // Client thread is loosining the connection
	public static final int ADMIN_OPERATION_SERVER_SOCKET_OPENED = 1006; // Server socket is opened
	public static final int ADMIN_OPERATION_SERVER_SOCKET_CLOSED = 1007; // Server socket is closed
	public static final int ADMIN_OPERATION_DISCONNECT = 1008; // Disconnect clients

	public static final int ADMIN_OPERATION_EXCEPTION = 1010; // Some exception

	public static Integer ADMIN_MESSAGE_ID = new Integer(-1); // Admin message identifier

	private final static int PORTIN = 11700; // Clients connect to this
	private String currentRole = "<none>";

	private List<Handler> listeners = null; // Listeners for client messages
	private List<Handler> adminListeners = null; // Listeners for admin-messages
	private volatile boolean isInRegistratingMode = false;

	// The list of clients connected.
	// private volatile List<ClientHandlerThread> clients = null;
	private volatile HashMap<Integer, ClientHandlerThread> clients = null;
	// clients counter
	private Integer clientCounter = new Integer(0);

	// Common server socket to listen on
	private volatile ServerSocket serverSocket = null;

	// Listener thread for incoming calls
	private TeamServerRegistrationListener theListener = null;

	// Which role do I play ? client or server ?
	boolean isServer = true; // Default server.

	public TeamMessageMgr() {
		this.setName("TeamMessageMgr");
		listeners = new ArrayList<Handler>();
		adminListeners = new ArrayList<Handler>();
		clients = new HashMap<Integer, TeamMessageMgr.ClientHandlerThread>();
	}

	/*
	 * Server starting
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		// Accept incoming calls if active
		if (isInRegistratingMode) {
			startRegistrationThread();
		}
	}

	public void openRegistration() {
		currentRole = "Server";
		if (isInRegistratingMode) {
			sendAdmMsg(ADMIN_MESSAGE_ID,"Registration is already running.",ADMIN_OPERATION_NOTHING);
		} else {
			sendAdmMsg(ADMIN_MESSAGE_ID,"Registration is opened.",ADMIN_OPERATION_REG_OPEN);
			// Start registration thread
			startRegistrationThread();
		}
	}

	public void closeRegistration() {
		sendAdmMsg(ADMIN_MESSAGE_ID,"Registration is closed.",ADMIN_OPERATION_REG_CLOSED);

		// End registration thread
		stopRegistrationThread();
	}

	private void startRegistrationThread() {
		if (isInRegistratingMode) {
			// Ignore - we're already running
		} else {
			isInRegistratingMode = true;
			if (theListener != null) {
				Log.d("TeamMsgMgr(" + currentRole + ")",
						"Interrupt sleeping thread...");
				theListener.interrupt();
			} else {
				theListener = new TeamServerRegistrationListener();
				theListener.start();
			}
		}
	}

	private void stopRegistrationThread() {
		if (isInRegistratingMode) {
			isInRegistratingMode = false;
			Log.d("TeamMsgMgr(" + currentRole + ")",
					"Set registration mode false");
			// serverSocket.close();
		}
	}

	/***************************************************************************************
	 * Private class to listen for incoming messages from the server
	 * 
	 * @author ANDROD
	 * 
	 **************************************************************************************/
	private class ClientHandlerThread extends Thread {

		private volatile Socket socket;
		private volatile PrintWriter out = null;
		private BufferedReader in = null;
		private boolean stillRunning = false;
		private boolean isSocketConnected = false;
		private Integer myNetId = null;

		public ClientHandlerThread(Socket socket, Integer givenServerId) {
			this.setName("ClientListener");
			this.socket = socket;
			this.myNetId = givenServerId;

			Log.d("ClientHandlerThread(" + currentRole + ")",
					"Socket isConnected check ");
			isSocketConnected = socket.isConnected();
			Log.d("ClientHandlerThread(" + currentRole + ")",
					"Socket isConnected : " + isSocketConnected);

			if (isSocketConnected) {
				try {
					stillRunning = true; // Optimistisk på denne
					socket.setSoTimeout(3000); // Timeout for read - mulighet
												// for interrupt av prosess

					// Create pipelines
					out = new PrintWriter(this.socket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(
							this.socket.getInputStream()));
				} catch (IOException e) {
					stillRunning = false;
					Log.d("ClientHandlerThread(" + currentRole + ")",
							"Exception constructor:" + e.getMessage());
					e.printStackTrace();
					sendAdmMsg(ADMIN_MESSAGE_ID,"Cli:Fx:" + e.getMessage(),ADMIN_OPERATION_NOTHING);
				}
			} else {
				sendAdmMsg(ADMIN_MESSAGE_ID,"Socket not connected",ADMIN_OPERATION_CLIENT_SOCKET_NOT_CONNECTED);
			}

		}

		public final Integer getServerId() {
			return myNetId;
		}

		public boolean isSocketConnected() {
			return isSocketConnected;
		}

		/**
		 * Running the client - looping on incoming messages.
		 */
		public void run() {
			String receivedString = null;
			while (stillRunning && !isInterrupted()) {
				try {
					try {
						receivedString = in.readLine();

						Log.d("ClientHandlerThread(" + currentRole + ")",
								"Receieve : " + receivedString);

						if (receivedString == null) {
							stillRunning = false;
						} else {
							distributeMessage(receivedString, this);
						}
					} catch (SocketTimeoutException ste) {
//						Log.d("ClientHandlerThread(" + currentRole + ")",
//								"--SocketTimeout");
					}
				} catch (IOException e) {
					Log.d("ClientHandlerThread(" + currentRole + ")",
							"receive loop IOException : " + e.getMessage());
					e.printStackTrace();
					// Disconnect client on failure
					terminateRelationship();
				} catch (Exception other) {
					Log.d("ClientHandlerThread(" + currentRole + ")",
							"receive loop Exception : " + other.getMessage());
					other.printStackTrace();
					terminateRelationship();
				}
			}
			Log.d("ClientHandlerThread(" + currentRole + ")",
					"********* Finished thread reading **************");
		}

		private void terminateRelationship() {
			Log.d("ClientHandlerThread(" + currentRole + ")",
					"Removing self from client list. #of clients before remove:"
							+ clients.size());
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				// e.printStackTrace();
			}
			try {
				socket.close();
			} catch (IOException e) {
				sendAdmMsg(this.myNetId,"Sockex:" + e.getMessage());
			}

			stillRunning = false;
			synchronized (clients) {
				clients.remove(this); // Remove from thread pool
			}
		}

		/**
		 * Disconnect thread from server. This is done by interrupting the
		 * client thread - which results in a thread/socket shotdown.
		 */
		public void disconnect() {

			try {
				sendAdmMsg(ADMIN_MESSAGE_ID,"Close client socket",ADMIN_OPERATION_CLIENT_DISCONNECT);
				Log.d("ClientHandlerThread-disconnect(" + currentRole + ")",
						"*** Closing interrupt");
				this.interrupt();

				Log.d("ClientHandlerThread-disconnect(" + currentRole + ")",
						"*** Closing socket");

				socket.close();
				Log.d("ClientHandlerThread-disconnect(" + currentRole + ")",
						"*** Closing file in OK");
			} catch (IOException e) {
				Log.d("ClientHandlerThread-disconnect(" + currentRole + ")",
						"*** Excp " + e.getMessage());
				e.printStackTrace();
			}
		}

		/**
		 * Send a message to the client.
		 * 
		 * @param msg
		 */
		public void sendMessage(Serializable msg) {
			try {
				out.println(msg);
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/***************************************************************************************
	 * Managing server registration task and message distribution. Populates a
	 * list of clients which is connected to this server.
	 * 
	 * @author ANDROD
	 * 
	 **************************************************************************************/
	private class TeamServerRegistrationListener extends Thread {
		private String TAG = "SVR-Listen";

		public TeamServerRegistrationListener() {
			this.setName("TeamServerRegistrationListener");
		}

		public void run() {
			Socket s = null;

			try {
				Log.i(TAG, "start server....");
				sendAdmMsg(ADMIN_MESSAGE_ID,"ST: start server....");
				serverSocket = new ServerSocket(PORTIN);
				serverSocket.setSoTimeout(5000);
				Log.i(TAG, "serversocket created, wait for client....");
				sendAdmMsg(ADMIN_MESSAGE_ID,"ST: serversocket created, wait for client....");

				while (true) {
//					if (isInterrupted()) {
//						sendAdmMsg("IS INTERRUPTED");
//					}
//					Log.d("TeamServerRegistrationListener(" + currentRole + ")",
//							" Accept : " + ihc(serverSocket));

					if (isInRegistratingMode) {
						try {
							s = serverSocket.accept();

							Log.d("TeamServerRegistrationListener("
									+ currentRole + ")", " new socket : "
									+ ihc(s));
							Log.v(TAG, "client connected...");
							sendAdmMsg(ADMIN_MESSAGE_ID,"ST: client connected...");
							addClientThread(s);
						} catch (SocketTimeoutException ioio) {
//							Log.d("TeamServerRegistrationListener("
//									+ currentRole + ")", "OK Interrupt");
						}
					} else {
						try {
							Log.d("TeamServerRegistrationListener("
									+ currentRole + ")", "Going to sleep");
							synchronized (this) {
								this.wait();
							}
						} catch (InterruptedException e) {
							Log.d("TeamServerRegistrationListener("
									+ currentRole + ")",
									"Interrupted from sleep");
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				sendAdmMsg(ADMIN_MESSAGE_ID,"ST: IOException " + e.toString(),ADMIN_OPERATION_EXCEPTION);
			} finally {// close sockets!!
				try {
					sendAdmMsg(ADMIN_MESSAGE_ID,"ST: Finally");
				} catch (Exception e) {
				}
			}
		}
	}

	// /**
	// * Distribute message to all clients via handler.
	// *
	// * @param theMessage
	// */
	// private void distributeMessage(Serializable theMessage) {
	// Log.d("TEAMmessageMANAGER", "Distribute message: " + theMessage);
	// for (Handler l : listeners) {
	// try {
	// Message toClient = l.obtainMessage();
	// toClient.obj = theMessage;
	// l.sendMessage(toClient);
	// } catch (Exception e) {
	// sendAdmMsg("Exception : " + e.toString());
	//
	// Log.d("TeamMsgMgr(" + currentRole + ")",
	// "Exception : ****************************************");
	// Log.d("TeamMsgMgr(" + currentRole + ")",
	// "Exception : " + e.toString());
	// Log.d("TeamMsgMgr(" + currentRole + ")",
	// "Exception : ****************************************");
	// }
	// }
	// }

	/**
	 * Distribute message to all listening clients via handlers.
	 * 
	 * @param theMessage
	 */
	private void distributeMessage(Serializable theMessage,
			ClientHandlerThread caller) {
		Log.d("TEAM*MANAGER", "Distribute message: " + theMessage);
		
		for (Handler l : listeners) {
			try {
				Message toClient = l.obtainMessage();

				Bundle b = toClient.getData();
				// The client id
				b.putInt(BUNDLE_CLIENTID, caller.getServerId());
				// The message
				b.putSerializable(BUNDLE_MESSAGE, theMessage);
				
				// Send the message
				l.sendMessage(toClient);
			} catch (Exception e) {
				sendAdmMsg(ADMIN_MESSAGE_ID,"Exception : " + e.toString(),ADMIN_OPERATION_EXCEPTION);

				Log.d("TEAM*MANAGER(" + currentRole + ")",
						"Exception : ****************************************");
				Log.d("TEAM*MANAGER(" + currentRole + ")",
						"Exception : " + e.toString());
				Log.d("TEAM*MANAGER(" + currentRole + ")",
						"Exception : ****************************************");
			}
		}
	}

	public void addListener(Handler messageBroakerClient) {
		listeners.add(messageBroakerClient);
	}

	public void addAdminListener(Handler adminListenerHandler) {
		adminListeners.add(adminListenerHandler);
	}

	public void removeListener(Handler messageBroakerClient) {
		listeners.remove(messageBroakerClient);
	}

	public void removeAdminListener(Handler adminListenerHandler) {
		adminListeners.remove(adminListenerHandler);
	}

	/***
	 * Send a message to all connected clients.
	 */
	public void sendMessageToClients(Serializable outgoingMessage) {
		// Testing sendAdmMsg("TeamMessageMgr send : \"" + outgoingMessage +
		// "\"");
		synchronized (clients) {
			for (ClientHandlerThread cli : clients.values()) {
				if (cli.isSocketConnected()) {
					cli.sendMessage(outgoingMessage);
				}
			}
		}

	}

	/**
	 * Send a message to a spesific client
	 */
	public void sendMessageToClient(Serializable outgoingMessage, Integer targetClient) {
		ClientHandlerThread cli = clients.get(targetClient);
		if (cli != null) {
			if (cli.isSocketConnected()) {
				cli.sendMessage(outgoingMessage);
			}
		}
	}

	
	/**
	 * Administration:
	 * Determine if the registration is open or not
	 */
	public boolean isRegistrationOpen() {
		return isInRegistratingMode;
	}

	
	private void sendAdmMsg(Integer clientId, String msg ) {
		sendAdmMsg(clientId,msg,ADMIN_OPERATION_NOTHING);
	}

	/**
	 * Send an admin-message to local listeners (Gui or control program)
	 * 
	 * @param clientId the client-id or -1 if N/A
	 * @param msg message in a visualized form
	 * @param operation a constant describing admin operations
	 */
	private void sendAdmMsg(Integer clientId, String msg, int operation ) {
//		Message toMain = null;
		for (Handler adms : adminListeners) {
			Message toClient = adms.obtainMessage();
			Bundle b = toClient.getData();
			b.putInt(BUNDLE_CLIENTID, clientId);
			b.putInt(BUNDLE_OPERATION, operation);
			b.putSerializable(BUNDLE_MESSAGE, msg);
			adms.sendMessage(toClient);
//			
//			toMain = adms.obtainMessage();
//			toMain.obj = msg;
//			adms.sendMessage(toMain);
		}
	}


	/**
	 * Check state - ig this is a server instance or client instance.
	 * @return true if this manager is acting as a server.
	 */
	public boolean isServer() {
		return isServer;
	}
	
	/**
	 * Call to initiate a client connection to an ip-address
	 * 
	 * @param ipadr
	 * @return
	 */
	public int initClientConnection(String ipadr) {
		currentRole = "Client";
		isServer = false;
		Socket s = null;
		int ret = 0;
		try {
			Log.d("TeamMessageMgr(" + currentRole + ")",
					"C: Connecting to server " + ipadr + ":" + PORTIN);
			sendAdmMsg(ADMIN_MESSAGE_ID,"Create soc : "+ipadr+":"+PORTIN);
			s = new Socket(ipadr, PORTIN);
			Log.d("TeamMessageMgr(" + currentRole + ")",
					"C: Connected to server" + s.toString() + " Socket:"
							+ ihc(s));
			ret = addClientThread(s);
			if (ret == 0) {
				sendAdmMsg(ADMIN_MESSAGE_ID,"Client connected ok",ADMIN_OPERATION_CLIENT_CONNECT);
			}
		} catch (IOException e) {
			sendAdmMsg(ADMIN_MESSAGE_ID,"Conn:"+e.getMessage(),ADMIN_OPERATION_EXCEPTION);
			Log.d("TeamMessageMgr(" + currentRole + ")",
					"C: Exception " + e.getMessage());
			e.printStackTrace();
			ret = 2;
		}
		return ret;
	}

	/**
	 * Add a client connection to the connection pool.
	 * 
	 * @param s
	 *            the client socket
	 */
	private int addClientThread(Socket s) {
		Log.d("TeamMessageMgr(" + currentRole + ")", "Start addClientThread()");

		// Increase client id counter
		clientCounter++;
		ClientHandlerThread cht = new ClientHandlerThread(s, clientCounter);
		int ret = 0;
		if (cht.isSocketConnected()) {
			synchronized (clients) {
				// clients.add(cht);
				clients.put(clientCounter, cht);
			}
			sendAdmMsg(clientCounter,"Client connected.",ADMIN_OPERATION_CLIENT_CONNECT);
			cht.start();
			ret = 0;
		} else {
			ret = 1; // Not connected
		}
		return ret;
	}

	/**
	 * Get information about the number of clients connected
	 * 
	 * @return number of clients
	 */
	public int getNumberOfClients() {
		return clients.size();
	}

	public String ihc(Object object) {
		return Integer.toString(System.identityHashCode(object));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		// Close server socket on object destruction
		if (serverSocket != null) {
			serverSocket.close();
		}

		// Disconnect clients
		for (ClientHandlerThread client : clients.values()) {
			client.disconnect();
		}

		super.finalize();
	}

	public void disconnect() {
		sendAdmMsg(ADMIN_MESSAGE_ID,"Close server socket",ADMIN_OPERATION_DISCONNECT);
		try {
			finalize();
		} catch (Throwable e) {
			sendAdmMsg(ADMIN_MESSAGE_ID,"FX:" + e.getMessage(),ADMIN_OPERATION_EXCEPTION);
		}
	}

}
