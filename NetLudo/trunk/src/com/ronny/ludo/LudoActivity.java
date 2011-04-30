package com.ronny.ludo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.ronny.ludo.board.LudoSurfaceView;
import com.ronny.ludo.communication.LudoMessageBroker;
import com.ronny.ludo.helper.ImageDialog;
import com.ronny.ludo.helper.ParseBoardDefinitionHelper;
import com.ronny.ludo.helper.SoundPlayer;
import com.ronny.ludo.model.Die;
import com.ronny.ludo.model.Game;
import com.ronny.ludo.model.GameHolder;
import com.ronny.ludo.model.PlayerColor;


/**
 * 
 * @author ovrron
 *
 */
public class LudoActivity extends Activity implements SensorEventListener {
	private String TAG = "-Ludo-:";

	private ImageButton zoomInButton;
	private ImageButton zoomFitButton;
	private ImageButton zoomOutButton;
	private LudoSurfaceView surface;
	SharedPreferences settings = null;
	private SoundPlayer soundPlayer = null;

	private Handler brokerMessages;

	Die die = null;
	ImageButton imgButtonDie = null;
	boolean firstTime = true;

	// RHA
	// RelativeLayout rl2;

	// private enum Moves {
	// NONE, DRAG, ZOOM;
	// };

	// RHA
	// private Moves currentTouchtype = Moves.NONE;

	// For shake motion detection.
	private SensorManager sensorMgr;
	private long lastUpdate = -1;
	private float x, y, z;
	private float last_x, last_y, last_z;
	private static final int SHAKE_THRESHOLD = 800;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Sett Game instans
		GameHolder.getInstance().setGame(new Game());

		// Load board definisjoner - lastes før inflating.
		ParseBoardDefinitionHelper ph = new ParseBoardDefinitionHelper();

		// Brett er lagt inn i regler - for lasting på klient.  Dette er et bevisst valg siden egene brett kan ha egene regler (framtidig utvidelse)
		String boardFile = GameHolder.getInstance().getRules().getLudoBoardFile();
		int iidd = getResources().getIdentifier(boardFile, "xml", "com.ronny.ludo");
		// parseXmlDefs();
		if (!ph.parseBoardDefinition(getResources().getXml(iidd))) {
			// TODO Håndter feil ved lasting av brettdefinisjon
			// Vis feilmelding og ev. avslutt
		}

		// settings = getSharedPreferences((String)
		// getResources().getText(R.string.sharedpreferences_name),
		// MODE_PRIVATE);
		// if (settings.contains((String)
		// getResources().getText(R.string.sharedpreferences_ludoboardfile)) ==
		// true)
		// {
		// String boardFile = settings.getString((String)
		// getResources().getText(R.string.sharedpreferences_ludoboardfile),
		// null);
		// int iidd = getResources().getIdentifier(boardFile, "xml",
		// "com.ronny.ludo");
		// //parseXmlDefs();
		// if(!ph.parseBoardDefinition(getResources().getXml(iidd))){
		// //TODO Håndter feil ved lasting av brettdefinisjon
		// //Vis feilmelding og ev. avslutt
		// }
		//
		// }
		// // TODO på lasting av board
		// Vector<String> boards =
		// ph.parseBoardsAvailable(getResources().getXml(R.xml.boarddefinition));
		// int iidd = getResources().getIdentifier(boards.get(0), "xml",
		// "com.ronny.ludo");

		// GameHolder.getInstance().getGame().DumpGame();

		// DENNE ER FLYTTET SIDEN VI TRENGER Størrelsen på bildet før vi tar en
		// recalc.
		// Game.getInstance().getLudoBoard().recalcPositions();
		// End load board.

		GameHolder.getInstance().getGame().DumpGame();

		setContentView(R.layout.main);

		zoomInButton = (ImageButton) findViewById(R.id.zoomIn);
		zoomFitButton = (ImageButton) findViewById(R.id.zoomFit);
		zoomOutButton = (ImageButton) findViewById(R.id.zoomOut);

		initDie();
		initSoundButton();
		surface = (LudoSurfaceView) findViewById(R.id.image);
		surface.setParentActivity(this);

		// Vector<PlayerColor> activePlayers =
		// GameHolder.getInstance().getTurnManager().getPlayers();
		// for(PlayerColor pc:activePlayers)
		// {
		// if(GameHolder.getInstance().getTurnManager().isLocal(pc))
		// {
		// surface.addPlayer(pc);
		// }
		// }

		zoomInButton.setOnClickListener(zoomInListener);
		zoomFitButton.setOnClickListener(zoomFitListener);
		zoomOutButton.setOnClickListener(zoomOutListener);

		// Dette må vi kun gjøre for current player
		// resetDie();

		// TEST
		// Test av mod
		// int start = 0;
		// int maxVal = 13;
		//
		// for(int j=0;j<20;j++) {
		// System.out.println("J: "+j+" - "+(start+j)%maxVal);
		// }
		//
		// start = 7;
		// for(int j=0;j<20;j++) {
		// System.out.println("J: "+j+" - "+(start+j)%maxVal);
		// }
		// TEST END

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
				// final String[] messageParts = message.split("\\,");
				final String[] messageParts = message.split(LudoMessageBroker.SPLITTER);
				Log.d("LA:handleMessage", "In msg: " + message);

				if (messageParts[0].equals("A")) { // Administrative messages

					if (messageParts[1].equals("CO")) { // Client checking out
						PlayerColor plc = PlayerColor.getColorFromString(messageParts[2]);

						if (plc != PlayerColor.NONE) {
							// Check if the player is the current player.
							PlayerColor currPlc = GameHolder.getInstance().getTurnManager().getCurrentPlayerColor();
							if (currPlc == plc) {
								// check if remote player - local players can
								// not leave
								if (GameHolder.getInstance().getTurnManager().isRemote(plc)) {
									GameHolder.getInstance().getMessageBroker().sendGimmeNextPlayer();
								}
							}
							// Free the color
							GameHolder.getInstance().getTurnManager()
									.freeColor(PlayerColor.getColorFromString(messageParts[2]));

							ImageDialog erd = new ImageDialog();
							erd.setOnDismissListener(new OnDismissListener() {
								public void onDismiss(DialogInterface dialog) {
								}
							});
							erd.showDialog(
									LudoActivity.this,
									getResources().getText(R.string.msg_network_player_gone).toString(),
									getResources().getText(R.string.msg_network_player).toString()
											+ " "
											+ plc.toNorwegian()
											+ " "
											+ getResources().getText(R.string.msg_network_player_checked_out)
													.toString(), R.drawable.cry);
							soundPlayer.playSound(SoundPlayer.DISCONNECT);
						}
						surface.reDraw();
					}

					// Server is leaving game...
					if (messageParts[1].equals("COS")) { // Server checking out

						ImageDialog erd = new ImageDialog();
						erd.setOnDismissListener(new OnDismissListener() {
							public void onDismiss(DialogInterface dialog) {
								tearDownGame();
								LudoActivity.this.finish();
							}
						});
						erd.showDialog(LudoActivity.this, getResources().getText(R.string.msg_network_player_gone)
								.toString(), getResources().getText(R.string.msg_network_server).toString() + " "
								+ getResources().getText(R.string.msg_network_player_checked_out).toString(),
								R.drawable.strive);

						soundPlayer.playSound(SoundPlayer.DISCONNECT);
					}

					// Lost connection to server
					if (messageParts[1].equals("LOST")) {
						if (!GameHolder.getInstance().getMessageManager().isServer()) {
							ImageDialog erd = new ImageDialog();
							erd.setOnDismissListener(new OnDismissListener() {
								public void onDismiss(DialogInterface dialog) {
									Log.d("LOST", "SERVER CONNECTION");
									tearDownGame();
									LudoActivity.this.finish();
								}
							});
							erd.showDialog(LudoActivity.this, getResources().getText(R.string.msg_error_heading)
									.toString(), getResources().getText(R.string.msg_network_lost).toString(),
									R.drawable.scared);
						}
						soundPlayer.playSound(SoundPlayer.DISCONNECT);
					}
				}	
			}
		};

		GameHolder.getInstance().getMessageBroker().addListener(brokerMessages);

		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
		boolean accelSupported = sensorMgr.registerListener(this,
				sensorMgr.getDefaultSensor(SensorManager.SENSOR_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
		if (!accelSupported) {
			// non accelerometer on this device
			sensorMgr.unregisterListener(this);
		}

	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	public void onSensorChanged(SensorEvent event) {
		if (imgButtonDie.isEnabled()) {
			Sensor mySensor = event.sensor;
			if (mySensor.getType() == SensorManager.SENSOR_ACCELEROMETER) {
				long curTime = System.currentTimeMillis();
				// only allow one update every 100ms.
				if ((curTime - lastUpdate) > 100) {
					long diffTime = (curTime - lastUpdate);
					lastUpdate = curTime;

					x = event.values[SensorManager.DATA_X];
					y = event.values[SensorManager.DATA_Y];
					z = event.values[SensorManager.DATA_Z];

					float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
					if (speed > SHAKE_THRESHOLD) {
						// yes, this is a shake action!
						rollDie();
					}
					last_x = x;
					last_y = y;
					last_z = z;
				}
			}
		}
	}

	private OnClickListener zoomInListener = new OnClickListener() {
		public void onClick(View v) {
			Log.d(TAG, "Zoom in");
			surface.zoomIn();
		}
	};

	private OnClickListener zoomFitListener = new OnClickListener() {
		public void onClick(View v) {
			Log.d(TAG, "Zoom FIT");
			surface.setScaleFullBoard();
		}
	};

	private OnClickListener zoomOutListener = new OnClickListener() {
		public void onClick(View v) {
			Log.d(TAG, "Zoom out");
			surface.zoomOut();
		}
	};

	public void resetDie() {
		surface.setPickingPiece(false);
		ImageButton imgButtonDie = (ImageButton) findViewById(R.id.imageButtonDie);
		// imgButtonDie.setBackgroundResource(R.drawable.die);
		imgButtonDie.setEnabled(true);
		imgButtonDie.clearAnimation();
		imgButtonDie.setBackgroundResource(R.drawable.die_roll_anim);
		final AnimationDrawable frameAnimation = (AnimationDrawable) imgButtonDie.getBackground();
		imgButtonDie.post(new Runnable() {
			public void run() {
				frameAnimation.start();
			}
		});
	}

	public void setDie(int eyes) {
		ImageButton imgButtonDie = (ImageButton) findViewById(R.id.imageButtonDie);
		int id = getResources().getIdentifier("die" + eyes, "drawable", "com.ronny.ludo");
		imgButtonDie.setBackgroundResource(id);
		imgButtonDie.setEnabled(false);
		surface.setPickingPiece(false);
	}

	public void setWinnerPlayer(PlayerColor color) {
		ImageDialog erd = new ImageDialog();
		if(GameHolder.getInstance().getLocalClientColor().contains(color)) {
			erd.setOnDismissListener(new OnDismissListener() {
				public void onDismiss(DialogInterface dialog) {
					tearDownGame();
					LudoActivity.this.finish();
				}
			});
			erd.showDialog(
					LudoActivity.this,
					getResources().getText(R.string.msg_winnermetitle).toString(),
					getResources().getText(R.string.msg_winnerme).toString(), 
					R.drawable.winner);
		}
		else {
			erd.setOnDismissListener(new OnDismissListener() {
				public void onDismiss(DialogInterface dialog) {
					tearDownGame();
					LudoActivity.this.finish();
				}
			});
			erd.showDialog(
					LudoActivity.this,
					getResources().getText(R.string.msg_winnertitle).toString(),
					color.toNorwegian() + " " + getResources().getText(R.string.msg_winner).toString(), 
					R.drawable.sad);
		}
	}

	public void setCurrentPlayer(PlayerColor color) {
		ImageView imageCurrentPlayer = (ImageView) findViewById(R.id.imagePlayerCurrent);
		int id = getResources().getIdentifier("player_" + color.toString().toLowerCase(), "drawable", "com.ronny.ludo");
		imageCurrentPlayer.setBackgroundResource(id);

		String toastMessage = null;
		int toastLength = 0;

		// Dette er meg og jeg er bare en
		if (GameHolder.getInstance().getLocalClientColor().size() == 1
				&& GameHolder.getInstance().getLocalClientColor().contains(color)) {
			toastMessage = getResources().getText(R.string.game_toast_playersturnyou).toString();
		}
		// Dette er en annen spiller
		else {
			toastMessage = color.toNorwegian() + " "
					+ getResources().getText(R.string.game_toast_playersturn).toString();
		}

		if (firstTime) {
			firstTime = false;
			toastMessage += "\n" + getResources().getText(R.string.game_toast_firsttime).toString();
			toastLength = Toast.LENGTH_LONG;
		} else {
			toastLength = Toast.LENGTH_SHORT;
		}

		if (GameHolder.getInstance().getTurnManager().getNumPlayers() > 1) {
			Toast.makeText(getBaseContext(), toastMessage, toastLength).show();
		}
	}

	private void initSoundButton() {
		soundPlayer = new SoundPlayer(getBaseContext());
		settings = getSharedPreferences((String) getResources().getText(R.string.sharedpreferences_name), MODE_PRIVATE);
		GameHolder.getInstance().setSoundOn(
				settings.getBoolean((String) getResources().getText(R.string.sharedpreferences_sound), true));
		final ImageButton imgButtonSound = (ImageButton) findViewById(R.id.imageButtonSound);
		if (GameHolder.getInstance().isSoundOn()) {
			imgButtonSound.setImageDrawable(getResources().getDrawable(R.drawable.sound_on));
		} else {
			imgButtonSound.setImageDrawable(getResources().getDrawable(R.drawable.sound_off));
		}
		imgButtonSound.setOnClickListener(new OnClickListener() {
			// @Override
			public void onClick(View v) {
				GameHolder.getInstance().setSoundOn(!GameHolder.getInstance().isSoundOn());
				if (GameHolder.getInstance().isSoundOn()) {
					imgButtonSound.setImageDrawable(getResources().getDrawable(R.drawable.sound_on));
				} else {
					imgButtonSound.setImageDrawable(getResources().getDrawable(R.drawable.sound_off));
				}
				SharedPreferences.Editor prefEditor = settings.edit();
				prefEditor.putBoolean((String) getResources().getText(R.string.sharedpreferences_sound), GameHolder
						.getInstance().isSoundOn());
				prefEditor.commit();
			}
		});
	}

	private void rollDie() {
		imgButtonDie.setEnabled(false);
		imgButtonDie.setImageBitmap(null);
		final int eyes = die.roll();
		int animationId = getResources().getIdentifier("die" + eyes + "anim", "drawable", "com.ronny.ludo");
		imgButtonDie.clearAnimation();
		imgButtonDie.setBackgroundResource(animationId);
		final AnimationDrawable frameAnimation = (AnimationDrawable) imgButtonDie.getBackground();

		final Handler handler = new Handler();
		final Runnable runnable = new Runnable() {

			public void run() {
				handler.removeCallbacks(this);
				if (surface.setThrow(eyes)) {
					surface.setPickingPiece(true);
				} else {
					if (soundPlayer == null) {
						soundPlayer = new SoundPlayer(getBaseContext());
					}
					soundPlayer.playSound(SoundPlayer.NO_LEGAL_MOVE);
					Toast.makeText(getBaseContext(), R.string.game_toast_nolegalmoves, Toast.LENGTH_SHORT).show();
				}
			}
		};

		imgButtonDie.post(new Runnable() {
			public void run() {
				int soundDuration = 0;
				if (soundPlayer == null) {
					soundPlayer = new SoundPlayer(getBaseContext());
				}
				if (eyes == 6) {
					soundDuration = soundPlayer.playSound(SoundPlayer.ROLL6);
				} else {
					soundDuration = soundPlayer.playSound(SoundPlayer.ROLL);
				}
				if (soundDuration == 0) {
					soundDuration = soundPlayer.getDuration(SoundPlayer.ROLL);
				}
				int duration = 0;
				for (int i = 0; i < frameAnimation.getNumberOfFrames(); i++) {
					duration = +frameAnimation.getDuration(i);
				}
				if (duration < soundDuration) {
					duration = soundDuration;
				}
				frameAnimation.start();
				handler.postDelayed(runnable, duration);
			}
		});
	}

	private void initDie() {
		die = new Die();
		imgButtonDie = (ImageButton) findViewById(R.id.imageButtonDie);
		imgButtonDie.setEnabled(false);
		imgButtonDie.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				rollDie();
				// imgButtonDie.setEnabled(false);
				// imgButtonDie.setImageBitmap(null);
				// final int eyes = die.roll();
				// int animationId = getResources().getIdentifier("die" + eyes +
				// "anim", "drawable", "com.ronny.ludo");
				// imgButtonDie.clearAnimation();
				// imgButtonDie.setBackgroundResource(animationId);
				// final AnimationDrawable frameAnimation = (AnimationDrawable)
				// imgButtonDie.getBackground();
				//
				// final Handler handler = new Handler();
				// final Runnable runnable = new Runnable() {
				//
				// public void run() {
				// handler.removeCallbacks(this);
				// if (surface.setThrow(eyes)) {
				// surface.setPickingPiece(true);
				// } else {
				// if (soundPlayer == null) {
				// soundPlayer = new SoundPlayer(getBaseContext());
				// }
				// soundPlayer.PlaySound(SoundPlayer.NO_LEGAL_MOVE);
				// Toast.makeText(getBaseContext(),
				// R.string.game_toast_nolegalmoves, Toast.LENGTH_SHORT)
				// .show();
				// }
				// }
				// };
				//
				// imgButtonDie.post(new Runnable() {
				// public void run() {
				// int soundDuration = 0;
				// if (soundPlayer == null) {
				// soundPlayer = new SoundPlayer(getBaseContext());
				// }
				// if (eyes == 6) {
				// soundDuration = soundPlayer.PlaySound(SoundPlayer.ROLL6);
				// } else {
				// soundDuration = soundPlayer.PlaySound(SoundPlayer.ROLL);
				// }
				// if (soundDuration == 0) {
				// soundDuration = soundPlayer.getDuration(SoundPlayer.ROLL);
				// }
				// int duration = 0;
				// for (int i = 0; i < frameAnimation.getNumberOfFrames(); i++)
				// {
				// duration = +frameAnimation.getDuration(i);
				// }
				// if (duration < soundDuration) {
				// duration = soundDuration;
				// }
				// frameAnimation.start();
				// handler.postDelayed(runnable, duration); // Put this
				// // where you
				// // start
				// // your
				// // animation
				// // surface.setThrow(eyes);
				// // surface.setPickingPiece(true);
				// }
				// });
			}
		});
	}

	/**
	 * Shutting down this intent
	 */
	private void tearDownGame() {
		GameHolder.getInstance().getMessageBroker().quitGame();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			// Log.d(this.getClass().getName(), "back button pressed");
			// TODO Disconnect other players
			sensorMgr.unregisterListener(this);
			tearDownGame();
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		GameHolder.getInstance().getMessageBroker().removeListener(brokerMessages);
		super.onDestroy();
	}

}