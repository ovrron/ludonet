package com.ronny.ludo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.joingame);

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
				final String[] messageParts = theMessage.split("\\,");
				if (messageParts[1].equals("CA")) {
					if (messageParts.length == 3) {
						if (messageParts[2] != null) {
							PlayerColor plc = PlayerColor.getColorFromString(messageParts[2]);
							// Save color for client
							GameHolder.getInstance().setLocalClientColor(plc);
							// Remove myself as listener
							GameHolder.getInstance().getMessageManager().removeListener(this);
							// Start game
							Intent ludoIntent = new Intent(LudoJoinGameActivity.this.getApplicationContext(), LudoActivity.class);
							startActivity(ludoIntent);

							// End me
							LudoJoinGameActivity.this.finish();
						}
					}
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
		Button buttonJoin = (Button) findViewById(R.id.buttonIP);

		buttonJoin.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				EditText editTextIP = (EditText) findViewById(R.id.editTextIP);
				int rc = GameHolder.getInstance().getMessageManager()
						.initClientConnection(editTextIP.getText().toString());
				Log.d("CONNECT", "RC=" + rc);
				if (rc == 0) {
					// Allocate color
					GameHolder.getInstance().getMessageBroker().sendGimmeAColor();
				}
			}
		});
	}
}
