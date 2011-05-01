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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.ronny.ludo.communication.LudoMessageBroker;
import com.ronny.ludo.communication.TeamMessageMgr;
import com.ronny.ludo.helper.IPAddressHelper;
import com.ronny.ludo.model.GameHolder;
import com.ronny.ludo.model.PlayerColor;
import com.ronny.ludo.model.TurnManager;
import com.ronny.ludo.model.TurnManager.PlayerLocation;

/**
 * Copyright: (c) 2011 Ronny Heitmann Andersen, Ronny Øvereng and Karl-Erik Moberg
 * 
 * Starter et nytt spill.
 * 
 * @author ovrron
 * 
 */
public class LudoStartNewGameActivity extends Activity {
	
	/**
	 * Hjelpeklasse for å holde orden på spillere
	 * @author ronny
	 *
	 */
	private class HelperPlayers { // Må ikke forveksles med Hjelpepleiere :-)
	
		private Vector<HelperPlayer> players = new Vector<HelperPlayer>();
		
		public HelperPlayers() {
		}
		
		public void addPlayer(HelperPlayer player){
			players.add(player);
		}
		
		public HelperPlayer getPlayer(PlayerColor playerColor) {
			for (HelperPlayer p:players) {
				if(p.getPlayerColor()==playerColor) {
					return p;
				}
			}
			return null;
		}
		
		public HelperPlayer getPlayer(ImageButton playerButton) {
			for (HelperPlayer p:players) {
				if(p.getPlayerButton().getId()==playerButton.getId()) {
					return p;
				}
			}
			return null;
		}
		
		public Vector<HelperPlayer> getPlayers() {
			return players;
		}
		
		public HelperPlayer getByColor(PlayerColor color) {
			for(HelperPlayer pl : players) {
				if(pl.getPlayerColor()==color) {
					return pl;
				}
			}
			return null;
		}
	}

	/**
	 * Hjelpeklasse for å holde orden på en spiller
	 * @author ronny
	 *
	 */
	private class HelperPlayer {
		private ImageButton playerButton;
		private PlayerColor playerColor;

		public HelperPlayer(PlayerColor playerColor, ImageButton playerButton) {
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
		
		public void setLocation(PlayerLocation playerLocation) {
			GameHolder.getInstance().getTurnManager().setLoaction(playerColor, playerLocation);
			setPlayerState(playerLocation, playerButton);
			if(playerLocation==PlayerLocation.LOCAL) {
				setConnected(true);
			}
			else if(playerLocation==PlayerLocation.FREE) {
				setConnected(false);
			}
		}
		
		public PlayerLocation getLocation() {
			return GameHolder.getInstance().getTurnManager().getLocation(playerColor);
		}
		
		public PlayerColor getPlayerColor() {
			return playerColor;
		}
		
		public ImageButton getPlayerButton() {
			return playerButton;
		}
	}

	private String ipAddressCurrent = null;
	private EditText editTextIP = null;
	private Button buttonPlayGame = null;
	private Button buttonInvite = null;
	private HelperPlayers players = new HelperPlayers();
	private ImageView o = null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startnewgame);
		
		// Create server object - always done
		GameHolder.getInstance().setMessageManager(new TeamMessageMgr());
		// Create message broker - always done 
		GameHolder.getInstance().setMessageBroker(new LudoMessageBroker(GameHolder.getInstance().getMessageManager()));
		// Create Turn Manager - always done
		GameHolder.getInstance().setTurnManager(new TurnManager());
		// Reset players
		GameHolder.getInstance().resetPlayers();

		init();
		startAnimation();
	}

	private Handler brokerMessages = null;

	
	/**
     * Initiering
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
				String message = (String) msg.obj;
				final String[] messageParts = message.split(LudoMessageBroker.SPLITTER);
				Log.d("LStartNewGame", "In msg: " + message);
				if (messageParts[0].equals("A")) { // Administrative messages
					if (messageParts[1].equals("CT")) { // Color Allocated
						PlayerColor plc = PlayerColor
								.getColorFromString(messageParts[2]);
						// Here, we should update the button for the color taken remotely
						HelperPlayer thePlayer = players.getByColor(plc);
						if(thePlayer != null) {
							thePlayer.setConnected(true);
							thePlayer.setLocation(PlayerLocation.REMOTE);
						}
					}
					if (messageParts[1].equals("CO")) { // Client checking out
						PlayerColor plc = PlayerColor
								.getColorFromString(messageParts[2]);
						// Here, we should update the button for the color taken remotely
						HelperPlayer thePlayer = players.getByColor(plc);
						if(thePlayer != null) {
							thePlayer.setConnected(false);
							thePlayer.setLocation(PlayerLocation.FREE);
						}
					}
				}
			}

		};
		GameHolder.getInstance().getMessageBroker().addListener(brokerMessages);

		// Graphics
		editTextIP = (EditText) findViewById(R.id.editTextIP);
		buttonInvite = (Button) findViewById(R.id.buttonIP);
		buttonPlayGame = (Button) findViewById(R.id.buttonPlayGame);
		
		players.addPlayer(new HelperPlayer(PlayerColor.RED, (ImageButton) findViewById(R.id.imageButtonPlayerRed)));
		players.addPlayer(new HelperPlayer(PlayerColor.GREEN, (ImageButton) findViewById(R.id.imageButtonPlayerGreen)));
		players.addPlayer(new HelperPlayer(PlayerColor.YELLOW, (ImageButton) findViewById(R.id.imageButtonPlayerYellow)));
		players.addPlayer(new HelperPlayer(PlayerColor.BLUE, (ImageButton) findViewById(R.id.imageButtonPlayerBlue)));
		//Setter rød spiller til lokal
		players.getPlayer(GameHolder.getInstance().getTurnManager().getFreeColor(PlayerLocation.LOCAL, PlayerColor.RED, false)).setLocation(PlayerLocation.LOCAL);
		
		//Henter ip
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

		editTextIP.setEnabled(false);
		initImageButtonListeners();
		initButtonListeners();

		// open registration
		GameHolder.getInstance().getMessageManager().openRegistration();
	}

	/**
	 * Start animering av bokstaven O
	 */
    private void startAnimation()
    {
    	Animation animationLogo = AnimationUtils.loadAnimation(this, R.anim.rotate);
    	o = (ImageView) findViewById(R.id.imageViewO);
    	o.startAnimation(animationLogo);
    	animationLogo.setAnimationListener(new AnimationListener() 
    	{
    	
    		public void onAnimationEnd(Animation animation) {
 
		    }
		
		    public void onAnimationRepeat(Animation animation) {
		    }
		
		    public void onAnimationStart(Animation animation) {
		    }
		});    	
    }
    
    /**
     * Initierer spillerknappene
     */
	private void initImageButtonListeners() {
		OnClickListener listener = new OnClickListener() 
		{
			public void onClick(View v) {

				HelperPlayer player = players.getPlayer((ImageButton)v);
				
				// Check remote - if not local/free, then forget.
				if (GameHolder.getInstance().getTurnManager().isRemote(player.getPlayerColor())){
					Toast.makeText(v.getContext(), R.string.start_toast_remote_player, Toast.LENGTH_LONG).show();
					return;
				}

				// No remote issues here - check local
				// Check locally on/off
				if (GameHolder.getInstance().getTurnManager().isFree(player.getPlayerColor())) {
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
		for(HelperPlayer p:players.getPlayers())
		{
			p.getPlayerButton().setOnClickListener(listener);
		}
	}

	/**
     * Initierer knapper
     */
	private void initButtonListeners() {
		buttonPlayGame.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				for(HelperPlayer p:players.getPlayers())
				{
					if(p.getLocation()==PlayerLocation.LOCAL)
					{
						GameHolder.getInstance().addLocalClientColor(p.getPlayerColor());
					}
				}
				
				
				// Closeregistation
				GameHolder.getInstance().getMessageManager().closeRegistration();
				// Remove listener for the game registration part.
				GameHolder.getInstance().getMessageBroker().removeListener(brokerMessages);

				GameHolder.getInstance().getMessageBroker().distributeMessage("A"+ LudoMessageBroker.SPLITTER + "START");

				Intent ludoIntent = new Intent(v.getContext(),
						// Start game
						LudoActivity.class);
				startActivity(ludoIntent);
				LudoStartNewGameActivity.this.finish();
			}
		});
		//Sender invitasjon via sms
		buttonInvite.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Send invitasjon
				String msgText = ipAddressCurrent + " "
						+ getResources().getString(R.string.start_invite_text);

				Intent sendIntent = new Intent(Intent.ACTION_VIEW);
				sendIntent.putExtra("sms_body", msgText);
				sendIntent.setType("vnd.android-dir/mms-sms");
				startActivity(sendIntent);
			}
		});
	}

	/**
	 * Starter animering rundt spillerknappene
	 * @param players, tabell med imagebuttons som representerer spillere
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
	 * Stopper animering rundt spillerknappene
	 * @param players, tabell med imageviews som representerer spillere
	 */
	public void stopPlayerFrameAnimation(ImageButton... players) {
		for (ImageButton player : players) {
			player.setBackgroundDrawable(null);
		}
	}

	/**
	 * Hjelpemetode for å hente id til riktig drawable
	 * @param playerState
	 * @param player
	 * @return id
	 */
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
	 * @param playerState
	 * @param players
	 */
	public void setPlayerState(PlayerLocation playerState, ImageButton... players) {
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
			GameHolder.getInstance().getMessageBroker().quitGame();
			this.finish();
	    }
		return super.onKeyDown(keyCode, event);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		Log.d(this.getClass().getName(), "ON START HAPPEND");
		super.onStart();
	}

}
