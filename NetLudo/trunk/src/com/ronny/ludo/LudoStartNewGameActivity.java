package com.ronny.ludo;

import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
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
import android.widget.ImageButton;
import android.widget.Toast;

import com.ronny.ludo.helper.IPAddressHelper;
import com.ronny.ludo.model.GameHolder;
import com.ronny.ludo.model.PlayerColor;
import com.ronny.ludo.model.TurnManager.PlayerLocation;

/**
 * 
 * @author ovrron
 * 
 */
public class LudoStartNewGameActivity extends Activity {
	
	private class Players
	{
		private Vector<Player> players = new Vector<Player>();
		
		public Players()
		{}
		
		public void addPlayer(Player player)
		{
			players.add(player);
		}
		
		public Player getPlayer(PlayerColor playerColor)
		{
			for (Player p:players) 
			{
				if(p.getPlayerColor()==playerColor)
				{
					return p;
				}
			}
			return null;
		}
		
		public Player getPlayer(ImageButton playerButton)
		{
			for (Player p:players) 
			{
				if(p.getPlayerButton().getId()==playerButton.getId())
				{
					return p;
				}
			}
			return null;
		}
		
		public Vector<Player> getPlayers()
		{
			return players;
		}
	}
	/**
	 * 
	 */
	private class Player {
		private ImageButton playerButton;
		private PlayerColor playerColor;

		public Player(PlayerColor playerColor, ImageButton playerButton) {
			this.playerColor = playerColor;
			this.playerButton = playerButton;
			GameHolder.getInstance().getTurnManager().addPlayer(playerColor);
		}

		// Lokal er alltid connected. Dersom ekstern spiller er tilkoplet må
		// også denne kalles
		public void setConnected(boolean connected) {
			if (connected) {
				startPlayerFrameAnimation(playerButton);
			} else {
				stopPlayerFrameAnimation(playerButton);
			}
		}
		public void setLocation(PlayerLocation playerLocation)
		{
			GameHolder.getInstance().getTurnManager().setLoaction(playerColor, playerLocation);
			setPlayerState(playerLocation, playerButton);
			if(playerLocation==PlayerLocation.LOCAL)
			{
				setConnected(true);
			}
			else if(playerLocation==PlayerLocation.FREE)
			{
				setConnected(false);
			}
		}
		
		public PlayerLocation getLocation() {
			return GameHolder.getInstance().getTurnManager().getLocation(playerColor);
		}
		
		public PlayerColor getPlayerColor()
		{
			return playerColor;
		}
		
		public ImageButton getPlayerButton()
		{
			return playerButton;
		}
	}

	private String ipAddressCurrent = null;
	private EditText editTextIP = null;
	private Button buttonPlayGame = null;
	private Button buttonInvite = null;
	private Players players = new Players();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startnewgame);
		init();
	}

	private Handler brokerMessages = null;

	/**
     * 
     */
	private void init() {
		// Set up game remedies

		// Create server handle - client messages from server
		brokerMessages = new Handler() {
			/*
			 * This is the message handler which receives messages from the
			 * TeamMessageManager. Messages about colors allocated should be
			 * notified all users.
			 * 
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				String message = msg.toString();
				Log.d("LSNGA", "In msg: " + message);
				final String[] messageParts = message.split("\\,");
				if (messageParts[0].equals("A")) { // Administrative messages
					if (messageParts[1].equals("CA")) { // Color Allocated
						PlayerColor plc = PlayerColor
								.getColorFromString(messageParts[2]);
					}
				}
			}

		};
		GameHolder.getInstance().getMessageBroker().addListener(brokerMessages);

		// Graphics
		editTextIP = (EditText) findViewById(R.id.editTextIP);
		buttonInvite = (Button) findViewById(R.id.buttonIP);
		buttonPlayGame = (Button) findViewById(R.id.buttonPlayGame);
		
		players.addPlayer(new Player(PlayerColor.RED, (ImageButton) findViewById(R.id.imageButtonPlayerRed)));
		players.addPlayer(new Player(PlayerColor.GREEN, (ImageButton) findViewById(R.id.imageButtonPlayerGreen)));
		players.addPlayer(new Player(PlayerColor.YELLOW, (ImageButton) findViewById(R.id.imageButtonPlayerYellow)));
		players.addPlayer(new Player(PlayerColor.BLUE, (ImageButton) findViewById(R.id.imageButtonPlayerBlue)));
		players.getPlayer(PlayerColor.RED).setLocation(PlayerLocation.LOCAL);
		
		ipAddressCurrent = new IPAddressHelper().getLocalIpAddress();
		if (ipAddressCurrent == null) {
			editTextIP.setText(getResources().getString(
					R.string.start_edittext_no_network));
			buttonInvite.setEnabled(false);
			buttonPlayGame.setEnabled(false);
		} else {
			editTextIP.setText(ipAddressCurrent);
			buttonInvite.setEnabled(true);
			buttonPlayGame.setEnabled(true);
		}
		
		// Init TurnManager 
//		PlayerColor pc = GameHolder.getInstance().getTurnManager()
//				.getFreeColor(PlayerLocation.LOCAL, PlayerColor.RED, false);

		//setIconForColor(pc, PlayerLocation.LOCAL);

		editTextIP.setEnabled(false);
		initImageButtonListeners();
		initButtonListeners();

		// open registation
		GameHolder.getInstance().getMessageManager().openRegistration();
	}

	/**
	 * Update icons for players and colors
	 * 
	 * @param pc
	 * @param loc
	 */
//	private void setIconForColor(PlayerColor pc, PlayerLocation loc) {
//		switch (pc) {
//		case RED:
//			playerRed.setConnected(true);
//			break;
//		case GREEN:
//			playerGreen.setConnected(true);
//			break;
//		case YELLOW:
//			playerYellow.setConnected(true);
//			break;
//		case BLUE:
//			playerBlue.setConnected(true);
//			break;
//		}
//		if (loc == PlayerLocation.FREE) {
//			GameHolder.getInstance().getTurnManager().freeColor(pc);
//		}
//
//		// Free and allocated resources - distribute message to clients...
//		if (loc == PlayerLocation.FREE) {
//			GameHolder.getInstance().getMessageBroker()
//					.distributeMessage("A,CI," + pc.toString());
//		} else {
//			GameHolder.getInstance().getMessageBroker()
//					.distributeMessage("A,CO," + pc.toString());
//		}
//	}

	private void initImageButtonListeners() {
		OnClickListener listener = new OnClickListener() 
		{
			public void onClick(View v) {

				Player player = players.getPlayer((ImageButton)v);
				
				// Check remote - if not local/free, then forget.
				if (GameHolder.getInstance().getTurnManager().isRemote(player.getPlayerColor())){
					Toast.makeText(v.getContext(), R.string.start_toast_remote_player, Toast.LENGTH_LONG);
					return;
				}

				// No remote issues here - check local
				// Check locally on/off
				if (GameHolder.getInstance().getTurnManager()
						.isFree(player.getPlayerColor())) {
					// If free - then allocate the color and set local image.
					PlayerColor pc = GameHolder
							.getInstance()
							.getTurnManager()
							.getFreeColor(PlayerLocation.LOCAL,
									player.getPlayerColor(), false);
					// I could could allocate -
					if (pc != PlayerColor.NONE) {
						// Place the house on, and start anim
						player.setLocation(PlayerLocation.LOCAL);
					}
				} else {
					// Already allocated => free the color, remove the house and
					// stop anim
					player.setLocation(PlayerLocation.FREE);
				}
			}
		};
		for(Player p:players.getPlayers())
		{
			p.getPlayerButton().setOnClickListener(listener);
		}
//		imageButtonPlayerRed.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//
//				// Check remote - if not local/free, then forget.
//				if (GameHolder.getInstance().getTurnManager().isRemote(PlayerColor.RED)) {
//					return;
//				}
//
//				// No remote issues here - check local
//
//				// Check locally on/off
//				if (GameHolder.getInstance().getTurnManager()
//						.isFree(PlayerColor.RED)) {
//					// If free - then allocate the color and set local image.
//					PlayerColor pc = GameHolder
//							.getInstance()
//							.getTurnManager()
//							.getFreeColor(PlayerLocation.LOCAL,
//									PlayerColor.RED, false);
//					// I could could allocate -
//					if (pc != PlayerColor.NONE) {
//						// Place the house on, and start anim
//						//playerRed.setConnected(true);
//						startPlayerFrameAnimation((ImageButton)v);
//					}
//				} else {
//					// Already allocated => free the color, remove the house and
//					// stop anim
//					//playerRed.setConnected(false);
//					stopPlayerFrameAnimation((ImageButton)v);
//					GameHolder.getInstance().getTurnManager()
//							.freeColor(PlayerColor.RED);
//				}
//			}
//		});
	}

	/**
     * 
     */
	private void initButtonListeners() {
		buttonPlayGame.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				// Test p� turn-manager om den holder vann...
				// PlayerColor p =
				// GameHolder.getInstance().getTurnManager().advanceToNextPlayer();Log.d("---------","Player: "
				// + p.toString());
				// p =
				// GameHolder.getInstance().getTurnManager().advanceToNextPlayer();Log.d("---------","Player: "
				// + p.toString());
				// p =
				// GameHolder.getInstance().getTurnManager().advanceToNextPlayer();Log.d("---------","Player: "
				// + p.toString());
				// p =
				// GameHolder.getInstance().getTurnManager().advanceToNextPlayer();Log.d("---------","Player: "
				// + p.toString());
				// p =
				// GameHolder.getInstance().getTurnManager().advanceToNextPlayer();Log.d("---------","Player: "
				// + p.toString());
				// p =
				// GameHolder.getInstance().getTurnManager().advanceToNextPlayer();Log.d("---------","Player: "
				// + p.toString());
				// p =
				// GameHolder.getInstance().getTurnManager().advanceToNextPlayer();Log.d("---------","Player: "
				// + p.toString());
				// p =
				// GameHolder.getInstance().getTurnManager().advanceToNextPlayer();Log.d("---------","Player: "
				// + p.toString());
				// p =
				// GameHolder.getInstance().getTurnManager().advanceToNextPlayer();Log.d("---------","Player: "
				// + p.toString());
				// p =
				// GameHolder.getInstance().getTurnManager().advanceToNextPlayer();Log.d("---------","Player: "
				// + p.toString());
				// p =
				// GameHolder.getInstance().getTurnManager().advanceToNextPlayer();Log.d("---------","Player: "
				// + p.toString());
				// p =
				// GameHolder.getInstance().getTurnManager().advanceToNextPlayer();Log.d("---------","Player: "
				// + p.toString());
				// p =
				// GameHolder.getInstance().getTurnManager().advanceToNextPlayer();Log.d("---------","Player: "
				// + p.toString());
				// p =
				// GameHolder.getInstance().getTurnManager().advanceToNextPlayer();Log.d("---------","Player: "
				// + p.toString());
				// p =
				// GameHolder.getInstance().getTurnManager().advanceToNextPlayer();Log.d("---------","Player: "
				// + p.toString());
				// p =
				// GameHolder.getInstance().getTurnManager().advanceToNextPlayer();Log.d("---------","Player: "
				// + p.toString());
				// p =
				// GameHolder.getInstance().getTurnManager().advanceToNextPlayer();
				// TEST END

				// Closeregistation
				GameHolder.getInstance().getMessageManager().closeRegistration();
				
				// Start game
				Intent ludoIntent = new Intent(v.getContext(),
						LudoActivity.class);
				startActivity(ludoIntent);

			}
		});

		buttonInvite.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Send invitasjon
				// TODO legg inn tekst som skal sendes i strings.xml
				// TODO lage mulighet for en venneliste med telefonnr som man
				// kan velge fra
				// TODO kanskje mulighet for å sende epost også
				String msgText = ipAddressCurrent + " "
						+ getResources().getString(R.string.start_invite_text);
				// Intent msg = new Intent(Intent.ACTION_SEND);
				// msg.setType("text/plain");
				// msg.putExtra("sms_body", msgText);
				// msg.putExtra(Intent.EXTRA_TEXT, "email body");
				// msg.putExtra(Intent.EXTRA_SUBJECT, "email subject");
				// startActivity(msg);
				// startActivity(Intent.createChooser(msg, "Velg sms"));

				Intent sendIntent = new Intent(Intent.ACTION_VIEW);
				sendIntent.putExtra("sms_body", msgText);
				// String tlfno = "90177797, 99001155, 1234678";
				// sendIntent.putExtra("address", tlfno);
				sendIntent.setType("vnd.android-dir/mms-sms");
				startActivity(sendIntent);
			}
		});
	}

	/**
	 * 
	 * @param players
	 *            , tabell med imageviews som representerer spillere
	 */
	public void startPlayerFrameAnimation(ImageButton... players) {
		for (ImageButton player : players) {
			player.setBackgroundResource(R.drawable.playeranim);
			final AnimationDrawable frameAnimation = (AnimationDrawable) player
					.getBackground();
			player.post(new Runnable() {
				public void run() {
					frameAnimation.start();
				}
			});

		}
	}

	/**
	 * 
	 * @param players
	 *            , tabell med imageviews som representerer spillere
	 */
	public void stopPlayerFrameAnimation(ImageButton... players) {
		for (ImageButton player : players) {
			player.setBackgroundDrawable(null);
			// AnimationDrawable frameAnimation = (AnimationDrawable)
			// player.getBackground();
			// frameAnimation.stop();
		}
	}

	// Call TurnManager
	// private PlayerState getPlayerState(ImageButton player)
	// {
	// Drawable d = player.getDrawable();
	// if(d.equals(getResources().getDrawable(R.drawable.player_red_local))||d.equals(getResources().getDrawable(R.drawable.player_green_local))||d.equals(getResources().getDrawable(R.drawable.player_yellow_local))||d.equals(getResources().getDrawable(R.drawable.player_blue_local)))
	// {
	// return PlayerState.LOCAL;
	// }
	// else
	// if(d.equals(getResources().getDrawable(R.drawable.player_red_remote))||d.equals(getResources().getDrawable(R.drawable.player_green_remote))||d.equals(getResources().getDrawable(R.drawable.player_yellow_remote))||d.equals(getResources().getDrawable(R.drawable.player_blue_remote)))
	// {
	// return PlayerState.REMOTE;
	// }
	// else
	// {
	// return PlayerState.FREE;
	// }
	//
	// }

	private int getPlayerResID(PlayerLocation playerState, ImageButton player) {
		Resources res = getResources();

		final String PLAYER = "player_";
		String playerName = null;
		String state = null;

		switch (playerState) {
		case FREE:
			state = "_unchecked";
			break;

		case LOCAL:
			state = "_local";
			break;

		case REMOTE:
			state = "_remote";
			break;

		default:
			state = null;
			break;
		}

		switch (player.getId()) {
		case R.id.imageButtonPlayerRed:
			playerName = "red";
			break;

		case R.id.imageButtonPlayerGreen:
			playerName = "green";
			break;

		case R.id.imageButtonPlayerYellow:
			playerName = "yellow";
			break;

		case R.id.imageButtonPlayerBlue:
			playerName = "blue";
			break;

		default:
			playerName = null;
			break;
		}

		try {
			return res.getIdentifier(PLAYER + playerName + state, "drawable",
					"com.ronny.ludo");
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 
	 * @param players
	 *            , tabell med imageviews som representerer spillere
	 */
	public void setPlayerState(PlayerLocation playerState,
			ImageButton... players) {
		for (ImageButton player : players) {
			int resID = getPlayerResID(playerState, player);
			try {
				Bitmap imageBitmap = BitmapFactory.decodeResource(
						getResources(), resID);
				player.setImageBitmap(imageBitmap);
			} catch (Exception e) {

			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        //Log.d(this.getClass().getName(), "back button pressed");
			//TODO Disconnect other players
			this.finish();
	    }
		return super.onKeyDown(keyCode, event);
	}
}
