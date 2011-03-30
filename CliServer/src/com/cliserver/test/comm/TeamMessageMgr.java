package com.cliserver.test.comm;

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
import java.util.List;
import java.util.Vector;

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

	private final static int PORTIN = 11700; // Clients connect to this
	private String currentRole = "<none>";

	private List<Handler> listeners = null; // Listeners for client messages
	private List<Handler> adminListeners = null; // Listeners for admin-messages
	private volatile boolean isInRegistratingMode = false;
	// The list of clients connected.
	private volatile List<ClientHandlerThread> clients = null;

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
		clients = new Vector<TeamMessageMgr.ClientHandlerThread>();
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
			sendAdmMsg("Registration is already running.");
		} else {
			sendAdmMsg("Registration is opened.");
			// Start registration thread
			startRegistrationThread();
		}
	}

	public void closeRegistration() {
		sendAdmMsg("Registration is closed.");

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
		private boolean stillRunning = true;
		private boolean isSocketConnected = false;

		public ClientHandlerThread(Socket socket) {
			this.setName("ClientListener");
			this.socket = socket;

			Log.d("ClientHandlerThread(" + currentRole + ")",
					"Socket isConnected check ");
			isSocketConnected = socket.isConnected();
			Log.d("ClientHandlerThread(" + currentRole + ")",
					"Socket isConnected : " + isSocketConnected);

			if (isSocketConnected) {
				try {
					socket.setSoTimeout(3000);
					// Create pipelines
					out = new PrintWriter(this.socket.getOutputStream(), true);
					in = new BufferedReader(new InputStreamReader(
							this.socket.getInputStream()));
				} catch (IOException e) {
					Log.d("ClientHandlerThread(" + currentRole + ")",
							"Exception constructor:" + e.getMessage());
					e.printStackTrace();
				}
			}

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
							distributeMessage(receivedString);
						}
					} catch (SocketTimeoutException ste) {
						Log.d("ClientHandlerThread(" + currentRole + ")",
								"--SocketTimeout");
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
				socket.close();
			} catch (IOException e) {
				// e.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				// e.printStackTrace();
			}
			synchronized (clients) {
				clients.remove(this);
				stillRunning = false;
			}
		}

		public void disconnect() {

			try {

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
				sendAdmMsg("ST: start server....");
				serverSocket = new ServerSocket(PORTIN);
				serverSocket.setSoTimeout(5000);
				Log.i(TAG, "serversocket created, wait for client....");
				sendAdmMsg("ST: serversocket created, wait for client....");

				while (true) {
					if (isInterrupted()) {
						sendAdmMsg("IS INTERRUPTED");
					}
					Log.d("TeamServerRegistrationListener(" + currentRole + ")",
							" Accept : " + ihc(serverSocket));

					if (isInRegistratingMode) {
						try {
							s = serverSocket.accept();

							Log.d("TeamServerRegistrationListener("
									+ currentRole + ")", " new socket : "
									+ ihc(s));
							Log.v(TAG, "client connected...");
							sendAdmMsg("ST: client connected...");
							addClientThread(s);
						} catch (SocketTimeoutException ioio) {
							Log.d("TeamServerRegistrationListener("
									+ currentRole + ")", "OK Interrupt");
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

				// while (isInRegistratingMode) {
				// if (isInterrupted()) {
				// sendAdmMsg("IS INTERRUPTED");
				// }
				// Log.d("TeamServerRegistrationListener(" + currentRole +
				// ")"," Accept : " + ihc(serverSocket));
				//
				// try {
				// s = serverSocket.accept();
				//
				// Log.d("TeamServerRegistrationListener(" + currentRole
				// + ")", " new socket : " + ihc(s));
				// Log.v(TAG, "client connected...");
				// sendAdmMsg("ST: client connected...");
				// addClientThread(s);
				// } catch (SocketTimeoutException ioio) {
				// Log.d("TeamServerRegistrationListener(" + currentRole
				// + ")", "OK Interrupt");
				// }
				// }
			} catch (IOException e) {
				e.printStackTrace();
				sendAdmMsg("ST: IOException " + e.toString());
			} finally {// close sockets!!
				try {
					sendAdmMsg("ST: Finally");
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Distribute message to all clients via handler.
	 * 
	 * @param theMessage
	 */
	private void distributeMessage(Serializable theMessage) {
		Log.d("TEAMmessageMANAGER", "Distribute message: " + theMessage);
		for (Handler l : listeners) {
			try {
				Message toClient = l.obtainMessage();
				toClient.obj = theMessage;
				l.sendMessage(toClient);
			} catch (Exception e) {
				sendAdmMsg("Exception : " + e.toString());

				Log.d("TeamMsgMgr(" + currentRole + ")",
						"Exception : ****************************************");
				Log.d("TeamMsgMgr(" + currentRole + ")",
						"Exception : " + e.toString());
				Log.d("TeamMsgMgr(" + currentRole + ")",
						"Exception : ****************************************");
			}
		}
	}

	/**
	 * 
	 */
	public void addListener(Handler messageBroakerClient) {
		listeners.add(messageBroakerClient);
	}

	public void removeListener(Handler messageBroakerClient) {
		listeners.remove(messageBroakerClient);
	}

	public void addAdminListener(Handler adminListenerHandler) {
		adminListeners.add(adminListenerHandler);
	}

	public void removeAdminListener(Handler adminListenerHandler) {
		adminListeners.remove(adminListenerHandler);
	}

	/***
	 * Send a message to all connected clients.
	 */
	public void sendMessage(Serializable outgoingMessage) {
		// Testing sendAdmMsg("TeamMessageMgr send : \"" + outgoingMessage +
		// "\"");
		synchronized (clients) {
			for (ClientHandlerThread cli : clients) {
				if (cli.isSocketConnected()) {
					cli.sendMessage(outgoingMessage);
				}
			}
		}

	}

	/**
	 * Determine if the registration is open or not
	 */
	public boolean isRegistrationOpen() {
		return isInRegistratingMode;
	}

	/**
	 * Send an admin-message to listeners
	 * 
	 * @param msg
	 */
	private void sendAdmMsg(String msg) {
		Message toMain = null;
		for (Handler adms : adminListeners) {
			toMain = adms.obtainMessage();
			toMain.obj = msg;
			adms.sendMessage(toMain);
		}
	}

	/**
	 * Call to initiate a client connection to an ip-address
	 * 
	 * @param ipadr
	 * @return 0 if success
	 */
	public int initClientConnection(InetAddress ipadr) {
		currentRole = "Client";
		Socket s = null;
		int ret = 0;
		try {
			Log.d("TeamMessageMgr(" + currentRole + ")",
					"C: Connecting to server " + PORTIN);
			s = new Socket(ipadr, PORTIN);
			Log.d("TeamMessageMgr(" + currentRole + ")",
					"C: Connected to server" + s.toString());
			ret = addClientThread(s);
		} catch (IOException e) {
			e.printStackTrace();
			ret = 2;
		}
		return ret;
	}

	/**
	 * Call to initiate a client connection to an ip-address
	 * 
	 * @param ipadr
	 * @return
	 */
	public int initClientConnection(String ipadr) {
		currentRole = "Client";
		Socket s = null;
		int ret = 0;
		try {
			Log.d("TeamMessageMgr(" + currentRole + ")",
					"C: Connecting to server " + ipadr + ":" + PORTIN);
			s = new Socket(ipadr, PORTIN);
			Log.d("TeamMessageMgr(" + currentRole + ")",
					"C: Connected to server" + s.toString() + " Socket:"
							+ ihc(s));
			ret = addClientThread(s);
			if (ret == 0) {
				sendAdmMsg("Client connected ok");
			}
		} catch (IOException e) {
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
		ClientHandlerThread cht = new ClientHandlerThread(s);
		int ret = 0;
		if (cht.isSocketConnected()) {
			synchronized (clients) {
				clients.add(cht);
			}
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
		serverSocket.close();

		// TODO More cleanup - check...?

		super.finalize();
	}

	public void disconnect() {
		// Disconnect clients
		synchronized (clients) {
			for (ClientHandlerThread client : clients) {
				client.disconnect();
				client.interrupt();
			}
		}
	}
}
