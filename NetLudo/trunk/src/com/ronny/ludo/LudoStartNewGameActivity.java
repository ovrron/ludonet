package com.ronny.ludo;

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
	/**
	 * 
	 * @author ovrron Hjelpeklasse for å teste ut valgene på spillerne Må vel
	 *         ta i bruk en framtidig turnmanager isteden kanskje
	 * 
	 */
	private class Player {
		private ImageButton button;
		PlayerColor color;

		public Player(PlayerColor col, ImageButton button) {
			this.color = col;
			this.button = button;
		}

		// Lokal er alltid connected. Dersom ekstern spiller er tilkoplet må
		// også denne kalles
		public void setConnected(boolean connected) {
			if (connected) {
				startPlayerFrameAnimation(button);
			} else {
				stopPlayerFrameAnimation(button);
			}
		}

		public PlayerLocation getLocation() {
			return GameHolder.getInstance().getTurnManager().getLocation(color);
		}
	}

	private String ipAddressCurrent = null;
	private EditText editTextIP = null;
	private Button buttonPlayGame = null;
	private Button buttonInvite = null;
	private ImageButton imageButtonPlayerRed = null;
	private ImageButton imageButtonPlayerGreen = null;
	private ImageButton imageButtonPlayerYellow = null;
	private ImageButton imageButtonPlayerBlue = null;
	private Player playerRed = null;
	private Player playerGreen = null;
	private Player playerYellow = null;
	private Player playerBlue = null;

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

		imageButtonPlayerRed = (ImageButton) findViewById(R.id.imageButtonPlayerRed);
		imageButtonPlayerGreen = (ImageButton) findViewById(R.id.imageButtonPlayerGreen);
		imageButtonPlayerYellow = (ImageButton) findViewById(R.id.imageButtonPlayerYellow);
		imageButtonPlayerBlue = (ImageButton) findViewById(R.id.imageButtonPlayerBlue);

		playerRed = new Player(PlayerColor.RED, imageButtonPlayerRed);
		playerGreen = new Player(PlayerColor.GREEN, imageButtonPlayerGreen);
		playerYellow = new Player(PlayerColor.YELLOW, imageButtonPlayerYellow);
		playerBlue = new Player(PlayerColor.BLUE, imageButtonPlayerBlue);

		ipAddressCurrent = new IPAddressHelper().getLocalIpAddress();
		if (ipAddressCurrent == null) {
			editTextIP.setText(getResources().getString(
					R.string.start_edittext_no_network));
			buttonInvite.setEnabled(false);
			buttonPlayGame.setEnabled(false);
			// playerRed.setConnected(false);
		} else {
			editTextIP.setText(ipAddressCurrent);
			buttonInvite.setEnabled(true);
			buttonPlayGame.setEnabled(true);
			// playerRed.setConnected(true);
		}
		
		// Init TurnManager 
		PlayerColor pc = GameHolder.getInstance().getTurnManager()
				.getFreeColor(PlayerLocation.LOCAL, PlayerColor.RED, false);

		setIconForColor(pc, PlayerLocation.LOCAL);

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
	private void setIconForColor(PlayerColor pc, PlayerLocation loc) {
		switch (pc) {
		case RED:
			playerRed.setConnected(true);
			break;
		case GREEN:
			playerGreen.setConnected(true);
			break;
		case YELLOW:
			playerYellow.setConnected(true);
			break;
		case BLUE:
			playerBlue.setConnected(true);
			break;
		}
		if (loc == PlayerLocation.FREE) {
			GameHolder.getInstance().getTurnManager().freeColor(pc);
		}

		// Free and allocated resources - distribute message to clients...
		if (loc == PlayerLocation.FREE) {
			GameHolder.getInstance().getMessageBroker()
					.distributeMessage("A,CI," + pc.toString());
		} else {
			GameHolder.getInstance().getMessageBroker()
					.distributeMessage("A,CO," + pc.toString());
		}
	}

	private void initImageButtonListeners() {
		// ,imageButtonPlayerRed.setOnClickListener(imageButtonListener);

		imageButtonPlayerRed.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				// Check remote - if not local/free, then forget.
				if (GameHolder.getInstance().getTurnManager().isRemote(PlayerColor.RED)) {
					return;
				}

				// No remote issues here - check local

				// Check locally on/off
				if (GameHolder.getInstance().getTurnManager()
						.isFree(PlayerColor.RED)) {
					// If free - then allocate the color and set local image.
					PlayerColor pc = GameHolder
							.getInstance()
							.getTurnManager()
							.getFreeColor(PlayerLocation.LOCAL,
									PlayerColor.RED, false);
					// I could could allocate -
					if (pc != PlayerColor.NONE) {
						// Place the house on, and start anim
						playerRed.setConnected(true);
					}
				} else {
					// Already allocated => free the color, remove the house and
					// stop anim
					playerRed.setConnected(false);
					GameHolder.getInstance().getTurnManager()
							.freeColor(PlayerColor.RED);
				}
			}
		});

		imageButtonPlayerGreen.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				// Check remote - if not local/free, then forget.
				if (GameHolder.getInstance().getTurnManager()
						.isRemote(PlayerColor.GREEN)) {
					return;
				}

				// No remote issues here - check local

				// Check locally on/off
				if (GameHolder.getInstance().getTurnManager()
						.isFree(PlayerColor.GREEN)) {
					// If free - then allocate the color and set local image.
					PlayerColor pc = GameHolder
							.getInstance()
							.getTurnManager()
							.getFreeColor(PlayerLocation.LOCAL,
									PlayerColor.GREEN, false);
					// I could could allocate -
					if (pc != PlayerColor.NONE) {
						// Place the house on, and start anim
						playerGreen.setConnected(true);
					}
				} else {
					// Already allocated => free the color, remove the house and
					// stop anim
					playerGreen.setConnected(false);
					GameHolder.getInstance().getTurnManager()
							.freeColor(PlayerColor.GREEN);
				}
			}
		});
		imageButtonPlayerYellow.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				// Check remote - if not local/free, then forget.
				if (GameHolder.getInstance().getTurnManager()
						.isRemote(PlayerColor.YELLOW)) {
					return;
				}

				// No remote issues here - check local

				// Check locally on/off
				if (GameHolder.getInstance().getTurnManager()
						.isFree(PlayerColor.YELLOW)) {
					// If free - then allocate the color and set local image.
					PlayerColor pc = GameHolder
							.getInstance()
							.getTurnManager()
							.getFreeColor(PlayerLocation.LOCAL,
									PlayerColor.YELLOW, false);
					// I could could allocate -
					if (pc != PlayerColor.NONE) {
						// Place the house on, and start anim
						playerYellow.setConnected(true);
					}
				} else {
					// Already allocated => free the color, remove the house and
					// stop anim
					playerYellow.setConnected(false);
					GameHolder.getInstance().getTurnManager()
							.freeColor(PlayerColor.YELLOW);
				}
			}
		});
		imageButtonPlayerBlue.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				// Check remote - if not local/free, then forget.
				if (GameHolder.getInstance().getTurnManager()
						.isRemote(PlayerColor.BLUE)) {
					return;
				}

				// No remote issues here - check local

				// Check locally on/off
				if (GameHolder.getInstance().getTurnManager()
						.isFree(PlayerColor.BLUE)) {
					// If free - then allocate the color and set local image.
					PlayerColor pc = GameHolder
							.getInstance()
							.getTurnManager()
							.getFreeColor(PlayerLocation.LOCAL,
									PlayerColor.BLUE, false);
					// I could could allocate -
					if (pc != PlayerColor.NONE) {
						// Place the house on, and start anim
						playerBlue.setConnected(true);
					}
				} else {
					// Already allocated => free the color, remove the house and
					// stop anim
					playerBlue.setConnected(false);
					GameHolder.getInstance().getTurnManager()
							.freeColor(PlayerColor.BLUE);
				}
			}
		});
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
				GameHolder.getInstance().getMessageManager().openRegistration();
				
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
