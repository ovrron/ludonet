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
import android.widget.ListView;
import android.widget.TextView;

import com.cliserver.test.comm.ILudoMessageReceiver;
import com.cliserver.test.comm.IPAddressHelper;
import com.cliserver.test.comm.LudoMessageBroker;
import com.cliserver.test.comm.TeamMessageMgr;

public class Server extends Activity implements ILudoMessageReceiver {
	private Button btnOpen = null;
	private Button btnClose = null;
	private Button btnSend = null;
	private ArrayAdapter<String> dataAdapter;
	
	private TeamMessageMgr tmm = null;
//	private ExampleMessageBroker emb = null;
	private LudoMessageBroker emb = null;
	private ListView debugListView = null;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.server);
		
		// Create server handle - admin messages from server
		Handler hnd = new Handler() {

			/* This is the message handler which receives messages
			 * from the TeamMessageManager
			 * 
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				Integer msgtype = msg.getData().getInt(TeamMessageMgr.BUNDLE_OPERATION);
				Integer client = msg.getData().getInt(TeamMessageMgr.BUNDLE_CLIENTID);
				String theMessage = msg.getData().getSerializable(TeamMessageMgr.BUNDLE_MESSAGE).toString();
				dataAdapter.add("A/"+msgtype+"/"+client+"/"+theMessage);
			}
			
		};
		// Create server handle - client messages from server
//		Handler hndCli = new Handler() {
//
//			/* This is the message handler which receives messages
//			 * from the TeamMessageManager
//			 * 
//			 * @see android.os.Handler#handleMessage(android.os.Message)
//			 */
//			@Override
//			public void handleMessage(Message msg) {
//				Integer client = msg.getData().getInt(TeamMessageMgr.BUNDLE_CLIENTID);
//				String theMessage = msg.getData().getSerializable(TeamMessageMgr.BUNDLE_MESSAGE).toString();
//				dataAdapter.add("*C("+client+")*"+theMessage);
//			}
//			
//		};
		
		TextView txv = (TextView) findViewById(R.id.textIP);
		txv.setText("IP: " + IPAddressHelper.getLocalIpAddress());
		
		// Create server object
		tmm = new TeamMessageMgr();
		// Set the handler (myself - for debugging) to receive admin messages.
		// This might be used by GUI and application logic, but the MessageBroker is supposed to react on these messages too.
		tmm.addAdminListener(hnd);
		
		// Create broker object
		//emb = new ExampleMessageBroker(this,tmm);
		emb = new LudoMessageBroker(this,tmm);
		
		// Start the server
		tmm.start();
		
		btnSend		= (Button) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				TextView txv = (TextView) findViewById(R.id.editTextToSend);
				String theMsg = txv.getText().toString();
				addLogMessage("Server send:"+theMsg);
				emb.distributeMessage(theMsg);
			}
		});
		
		btnOpen = (Button) findViewById(R.id.btnOpenRegistration);
		btnOpen.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tmm.openRegistration();
			}
		});
		btnClose = (Button) findViewById(R.id.btnCloseRegistration);
		btnClose.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tmm.closeRegistration();
			}
		});

		dataAdapter = new ArrayAdapter<String>(this, R.layout.item,R.id.itemName);
		dataAdapter.add("Meldinger");
//		dataAdapter.add("orange");
//		dataAdapter.add("tomato");

		debugListView = (ListView) findViewById(R.id.listView1);
		debugListView.setAdapter(dataAdapter);

	}
	
	public void addLogMessage(String msg) {
		dataAdapter.add(msg);
		debugListView.setSelection(dataAdapter.getCount());
		
		// Update number of clients
		TextView txv = (TextView) findViewById(R.id.txtInformation);
		txv.setText("# of cli: "+tmm.getNumberOfClients());
	}

	/**
	 * Getting a message from a client via the broker
	 */
	public void handleIncomingMessage(String theMessage, Integer clientId) {
		addLogMessage("C/"+clientId+":"+theMessage);		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.d("Server","****************** Finish activity ******************");
				emb.disconnect();
		        finish();
		        return true;
		    }
		return super.onKeyDown(keyCode, event);
	}


}