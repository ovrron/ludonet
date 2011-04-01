package com.cliserver.test;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.cliserver.test.comm.ILudoMessageReceiver;
import com.cliserver.test.comm.LudoMessageBroker;
import com.cliserver.test.comm.TeamMessageMgr;

public class Client extends Activity implements ILudoMessageReceiver {

	private Button btnConnect = null;
	private Button btnSendmsg = null;
	private Button btnDisconnect = null;

	private ListView debugListView = null;
	private ArrayAdapter<String> dataAdapter;

	private TeamMessageMgr tmm = null;
//	private ExampleMessageBroker emb = null;
	private LudoMessageBroker emb = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client);
		btnConnect = (Button) findViewById(R.id.btnConnect);
		btnConnect.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				TextView ip = (TextView) findViewById(R.id.edIpaddress);
				String ipTxt = ip.getText().toString();

				int retCode = tmm.initClientConnection(ipTxt);
				if (retCode != 0) {
					addLogMessage("Not connected:" + retCode);
				}
			}
		});
		btnSendmsg = (Button) findViewById(R.id.btnSendMsg);
		btnSendmsg.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				EditText txt = (EditText) findViewById(R.id.editClientMessage);
				String theMsg = txt.getText().toString();
				addLogMessage("Send:" + theMsg);
				emb.distributeMessage(theMsg);
			}
		});

		btnDisconnect = (Button) findViewById(R.id.btnDisConnect);
		btnDisconnect.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String theMsg = "MGR,LEAVE,GREEN,0";
				addLogMessage("Send:" + theMsg);
				emb.distributeMessage(theMsg);
				emb.disconnect();
			}
		});

		dataAdapter = new ArrayAdapter<String>(this, R.layout.item,
				R.id.itemName);
		dataAdapter.add("Meldinger");

		debugListView = (ListView) findViewById(R.id.listViewCli);
		debugListView.setAdapter(dataAdapter);

		// Create server message handle
		Handler hnd = new Handler() {

			/*
			 * Messages from the server is received here...
			 * 
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				Integer msgtype = msg.getData().getInt(TeamMessageMgr.BUNDLE_OPERATION);
				Integer client = msg.getData().getInt(TeamMessageMgr.BUNDLE_CLIENTID);
				String theMessage = msg.getData().getSerializable(TeamMessageMgr.BUNDLE_MESSAGE).toString();
				dataAdapter.add("A/"+msgtype+"/"+client+"/"+theMessage);
//				dataAdapter.add("*A*" + (String) msg.obj);
//				msg.recycle();
			}

		};
		// Create server object
		tmm = new TeamMessageMgr();
		tmm.addAdminListener(hnd); // Administrative messages

//      // Can do this to recive message directly to myself...		
//      // Create server handle - client messages from server
//		Handler hndCli = new Handler() {
//
//			/*
//			 * This is the message handler which receives messages from the
//			 * TeamMessageManager
//			 * 
//			 * @see android.os.Handler#handleMessage(android.os.Message)
//			 */
//			@Override
//			public void handleMessage(Message msg) {
//				dataAdapter.add("*C*" + (String) msg.obj);
//			}
//
//		};
//		tmm.addListener(hndCli);

		// Create broker object
//		emb = new ExampleMessageBroker(this, tmm);
	
		emb = new LudoMessageBroker(this, tmm);
		// Start the server
		tmm.start();

	}

	public void addLogMessage(String msg) {
		dataAdapter.add(msg);
		debugListView.setSelection(dataAdapter.getCount());
	}

	/**
	 * Handle messages from incoming clients (via the broker)
	 */
	public void handleIncomingMessage(String fromClients, Integer clientId) {
		addLogMessage("C/"+clientId+"/" + fromClients);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.d("Client",
					"****************** Finish activity ******************");
			emb.disconnect();
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}