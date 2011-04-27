package com.ronny.ludo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ronny.ludo.communication.LudoMessageBroker;
import com.ronny.ludo.communication.TeamMessageMgr;
import com.ronny.ludo.model.GameHolder;
import com.ronny.ludo.model.PlayerColor;
import com.ronny.ludo.model.TurnManager;

/**
 * 
 * @author ovrron
 * 
 */
public class LudoJoinGameActivity extends Activity {

	boolean gotColor = false;
	boolean gotSettings = false;
	boolean gotPlayers = false;
	boolean gotStartGame = false;
	
	String ip = null;
	EditText editTextIP = null;
	/** SharedPreferences */
	SharedPreferences settings = null;
	private ProgressDialog waitDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.joingame);

		/** initierer settings */
		settings = getSharedPreferences((String) getResources().getText(R.string.sharedpreferences_name), MODE_PRIVATE);
		/** henter forrige valgt brett (filnavn)*/
		ip = settings.getString((String) getResources().getText(R.string.sharedpreferences_connectip), null);
		editTextIP = (EditText) findViewById(R.id.editTextIP);
		if(ip!=null)
		{
			editTextIP.setText(ip);
		}
		
		// Create server object - always done
		GameHolder.getInstance().setMessageManager(new TeamMessageMgr());
		// Create message broker - always done
		GameHolder.getInstance().setMessageBroker(new LudoMessageBroker(GameHolder.getInstance().getMessageManager()));
		// Create Turn Manager - always done
		GameHolder.getInstance().setTurnManager(new TurnManager());

		// Her må vi sette opp en listener som tar oss videre til spillet når vi
		// har connect.
		// Create server message handle
		final Handler hnd = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Integer msgtype = msg.getData().getInt(TeamMessageMgr.BUNDLE_OPERATION);
				Integer client = msg.getData().getInt(TeamMessageMgr.BUNDLE_CLIENTID);
				String theMessage = msg.getData().getSerializable(TeamMessageMgr.BUNDLE_MESSAGE).toString();

				// Start game when color arrives...
				Log.d("Client got color msg", "MSG:" + theMessage);
				// Split message
				//final String[] messageParts = theMessage.split("\\,");
				final String[] messageParts = theMessage.split(LudoMessageBroker.SPLITTER);
				if (messageParts[1].equals("CA")) {
					if (messageParts.length == 3) {
						if (messageParts[2] != null) {
							gotColor = true;
							SharedPreferences.Editor prefEditor = settings.edit();
							prefEditor.putString((String) getResources().getText(R.string.sharedpreferences_connectip), editTextIP.getText().toString());
							prefEditor.commit();
							PlayerColor plc = PlayerColor.getColorFromString(messageParts[2]);
							// Save color for client
							GameHolder.getInstance().addLocalClientColor(plc);
							//gotPlayers = true;
							
							// Disable button
							Button buttonJoin = (Button) findViewById(R.id.buttonIP);
							buttonJoin.setEnabled(false);
							
							// Add the awiting game start message...
							waitDialog = new ProgressDialog(LudoJoinGameActivity.this);
							waitDialog.setTitle(getResources().getString(R.string.msg_network_waiting_title));
							waitDialog.setMessage(getResources().getString(R.string.msg_network_waiting));
							waitDialog.show();

						}
					}
				}
				
				// The settings have arrived
				if(messageParts[1].equals("SS")){
					if (messageParts[2] != null) {
						gotSettings = true;
						GameHolder.getInstance().getRules().setSettings(messageParts[2]);
					}
					
				}
				
				// The player info have arrived
				if(messageParts[1].equals("SP")){
					if (messageParts[2] != null) {
						gotPlayers = true;
						GameHolder.getInstance().getTurnManager().setPlayersJSON(messageParts[2]);
					}
				}

				// Let the games begin...
				if(messageParts[1].equals("START")){
						gotStartGame = true;
						// Get game info
						GameHolder.getInstance().getMessageBroker().sendGimmeSettings();
						GameHolder.getInstance().getMessageBroker().sendGimmePlayers();
				}
	
				
				if(gotColor && gotSettings && gotPlayers && gotStartGame){
//					remove toast/dialog
					waitDialog.hide();
					// Remove myself as listener
					GameHolder.getInstance().getMessageManager().removeListener(this);
					// Start game
					Intent ludoIntent = new Intent(LudoJoinGameActivity.this.getApplicationContext(), LudoActivity.class);
					startActivity(ludoIntent);

					// End me
					LudoJoinGameActivity.this.finish();
				}
			}

		};
		GameHolder.getInstance().getMessageManager().addListener(hnd);

		initButtonListeners();
	}

	// /**
	// *
	// */
	// private void initEditTextListener()
	// {
	// EditText editTextIP = (EditText) findViewById(R.id.editTextIP);
	// editTextIP.setOnKeyListener(new OnKeyListener()
	// {
	// @Override
	// public boolean onKey(View v, int keyCode, KeyEvent event) {
	// ipAddressJoin = editTextIP.getText().toString();
	// return false;
	// }
	//
	// });
	// }

	/**
     * 
     */
	private void initButtonListeners() {
		final Button buttonJoin = (Button) findViewById(R.id.buttonIP);

		buttonJoin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				buttonJoin.setEnabled(false);
				int rc = GameHolder.getInstance().getMessageManager()
						.initClientConnection(editTextIP.getText().toString());
				Log.d("CONNECT", "RC=" + rc);
				if (rc == 0) {
					// Allocate color
					GameHolder.getInstance().getMessageBroker().sendGimmeAColor();
				} else {
//					Toast.makeText(LudoJoinGameActivity.this,
//							"Feil med connection: "+rc,
//							Toast.LENGTH_LONG).show();
					ErrDialog erd = new ErrDialog();
					erd.setOnDismissListener(new OnDismissListener() {
						public void onDismiss(DialogInterface dialog) {
							buttonJoin.setEnabled(true);
						}
					});
					erd.showDialog(LudoJoinGameActivity.this, getResources().getText(R.string.msg_error_heading).toString(), 
							getResources().getText(R.string.msg_network_no_connect).toString(), R.drawable.scared);
				
				}
			}
		});
	}
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        Log.d(this.getClass().getName(), "back button pressed");
			//TODO Disconnect other players
			GameHolder.getInstance().getMessageBroker().quitGame();
			this.finish();
	    }
	    return super.onKeyDown(keyCode, event);

	}

}
