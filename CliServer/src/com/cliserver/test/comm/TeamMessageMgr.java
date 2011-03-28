package com.cliserver.test.comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
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
	// private final static int PORTOUT = 11701; // Connect back to client
	// private final static int PORTIN = 4000; // Clients connect to this
	// private final static int PORTOUT = 4001; // Connect back to client
	private String currentRole = "<none>";

	private List<Handler> listeners;
	private Handler debugListener = null;
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

		private Socket socket;
		private PrintWriter out = null;
		private BufferedReader in = null;
		private boolean stillRunning = true;
		private boolean isSocketConnected = false;

		public ClientHandlerThread(Socket socket) {
			this.setName("ClientListener");
			this.socket = socket;
			try {
				Log.d("CLIENT:", "Socket isConnected check ");
				isSocketConnected = socket.getInetAddress().isReachable(10000);
				Log.d("CLIENT:", "Socket isConnected : " + isSocketConnected);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				Log.d("CLIENT:",
						"Socket isConnected : exception " + e1.getMessage());
				e1.printStackTrace();
			}
			if (isSocketConnected) {
				try {
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
			while (stillRunning) {
				try {
					Log.d("ClientHandlerThread(" + currentRole + ")", " IN : "
							+ ihc(in));
					receivedString = in.readLine();
					Log.d("ClientHandlerThread(" + currentRole + ")",
							"Receieve : " + receivedString);
					if (receivedString == null) {
						stillRunning = false;
					} else {
						// Check management messages
						if (receivedString.startsWith("MGR,", 0)) {
							if (receivedString.startsWith("LEAVE", 4)) {
								// Client is leaving...
								terminateRelationship();
								Log.d("ClientHandlerThread(" + currentRole
										+ ")", "Client is leaving...");
								distributeMessage(receivedString);
							}
						} else {
							// distribute incoming to all listeners
							distributeMessage(receivedString);
						}
					}
				} catch (IOException e) {
					Log.d("ClientHandlerThread(" + currentRole + ")",
							"receive loop IOException : " + e.getMessage());
					e.printStackTrace();
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
			clients.remove(this);
			stillRunning = false;
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
		Log.d("TEAMmessageMANAGER", "Distr: " + theMessage);
		for (Handler l : listeners) {
			try {
				Message toClient = debugListener.obtainMessage();
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

	public void addListener(Handler messageBroakerClient) {
		listeners.add(messageBroakerClient);
	}

	public void removeListener(Handler messageBroakerClient) {
		listeners.remove(messageBroakerClient);
	}

	public void sendMessage(Serializable outgoingMessage) {
		sendAdmMsg("TeamMessageMgr send : \"" + outgoingMessage + "\"");
		for (ClientHandlerThread cli : clients) {
			if (cli.isSocketConnected()) {
				cli.sendMessage(outgoingMessage);
			}
		}
	}

	/**
	 * Determine if the registration is open or not
	 */
	public boolean isRegistrationOpen() {
		return isInRegistratingMode;
	}

	private void sendAdmMsg(String msg) {
		if (debugListener != null) {
			Message toMain = debugListener.obtainMessage();
			toMain.obj = msg;
			debugListener.sendMessage(toMain);
		}
	}

	public void setHandler(Handler handle) {
		debugListener = handle;
	}

	public int initClientConnection(InetAddress ipadr) {
		currentRole = "Client";
		Socket s = null;
		int ret = 0;
		try {
			Log.d("CLIENT ", "C: Connecting to server " + PORTIN);
			s = new Socket(ipadr, PORTIN);
			Log.d("CLIENT ", "C: Connected to server" + s.toString());
			ret = addClientThread(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret = 2;
		}
		return ret;
	}

	public int initClientConnection(String ipadr) {
		currentRole = "Client";
		Socket s = null;
		int ret = 0;
		try {
			Log.d("CLIENT ", "C: Connecting to server " + ipadr + ":" + PORTIN);
			s = new Socket(ipadr, PORTIN);
			Log.d("CLIENT ", "C: Connected to server" + s.toString()
					+ " Socket:" + ihc(s));
			ret = addClientThread(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		ClientHandlerThread cht = new ClientHandlerThread(s);
		int ret = 0;
		if (cht.isSocketConnected()) {
			clients.add(cht);
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
		super.finalize();
	}

}
