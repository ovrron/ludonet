package com.ronny.ludo;

import java.util.Vector;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

import com.ronny.ludo.board.LudoSurfaceView;
import com.ronny.ludo.helper.ParseBoardDefinitionHelper;
import com.ronny.ludo.model.Die;
import com.ronny.ludo.model.Game;
import com.ronny.ludo.model.GameHolder;

public class LudoActivity extends Activity {
	private String TAG = "-Ludo-:";

	private ImageButton zoomInButton;
	private ImageButton zoomFitButton;
	private ImageButton zoomOutButton;
	private LudoSurfaceView surface;

	// RHA
	// RelativeLayout rl2;

	// private enum Moves {
	// NONE, DRAG, ZOOM;
	// };

	// RHA
	// private Moves currentTouchtype = Moves.NONE;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Load board definisjoner - lastes før inflating.
		ParseBoardDefinitionHelper ph = new ParseBoardDefinitionHelper();
		
		// TODO på lasting av board
		Vector<String> boards = ph.parseBoardsAvailable(getResources().getXml(R.xml.boarddefinition));
		int iidd = getResources().getIdentifier(boards.get(0), "xml", "com.ronny.ludo");
		
//		GameHolder.getInstance().getGame().DumpGame();
		
		
		
		
		
		
		// HER SKAL VI HA VALGT ET GAME - SOM ER OPPRETTET TIDLIGERE....
		// VI LEGGER DET INN HER FOR � F� TING TIL � SNURRE
		GameHolder.getInstance().setGame(new Game());
		
		
		
		
		
		
		
		
		
		
		
		
		//parseXmlDefs();
		if(!ph.parseBoardDefinition(getResources().getXml(iidd))){
			//TODO Håndter feil ved lasting av brettdefinisjon
			//Vis feilmelding og ev. avslutt
		}
	
// DENNE ER FLYTTET SIDEN VI TRENGER Størrelsen på bildet før vi tar en recalc.		
//		Game.getInstance().getLudoBoard().recalcPositions();
		// End load board.

		GameHolder.getInstance().getGame().DumpGame();
		
		setContentView(R.layout.main);

		zoomInButton = (ImageButton) findViewById(R.id.zoomIn);
		zoomFitButton = (ImageButton) findViewById(R.id.zoomFit);
		zoomOutButton = (ImageButton) findViewById(R.id.zoomOut);
		surface = (LudoSurfaceView) findViewById(R.id.image);

		zoomInButton.setOnClickListener(zoomInListener);
		zoomFitButton.setOnClickListener(zoomFitListener);
		zoomOutButton.setOnClickListener(zoomOutListener);
		
		initDie();
		
		//TEST
		// Test av mod
//		int start = 0;
//		int maxVal = 13;
//		
//		for(int j=0;j<20;j++) {
//			System.out.println("J: "+j+" - "+(start+j)%maxVal);
//		}
//
//		start = 7;
//		for(int j=0;j<20;j++) {
//			System.out.println("J: "+j+" - "+(start+j)%maxVal);
//		}
		//TEST END 
		
		
	}

	/*
	 * ovrron 2011.03.22
	 * Laget egen helperklasse som tar seg av dette
	 * 
	private void parseXmlDefs() {
		// Xml parse
		XmlResourceParser defs = getResources().getXml(R.xml.boarddefinition);
		int eventType = -1;
		// Find Score records from XML
		try {
			while (eventType != XmlResourceParser.END_DOCUMENT) {
				if (eventType == XmlResourceParser.START_TAG) {
					// Get the name of the tag (eg scores or score)
					String strName = defs.getName();
					if (strName.equals("commonfields")) {
						behandleCommons(defs);
					}
					if (strName.equals("itemdef")) {
						behandleSpillerData(defs);
					}
					if (strName.equals("basedonresolution")) {
						int x = Integer.parseInt(defs.getAttributeValue(null,
								"x"));
						int y = Integer.parseInt(defs.getAttributeValue(null,
								"y"));
						Game.getInstance().getLudoBoard().setDefinitionResolution(x,y);
					}

				}
				eventType = defs.next();
			}
		} catch (Exception e) {
			Log.e("ERROR", "Failed to load defs", e);
		}

	}

	private void behandleSpillerData(XmlResourceParser defs) {
		boolean ferdig = false;
		// String theText = new String();
		int eventType = -1;
		String strName = null;
		String col = "unknown"; // IPlayer color
		int whatToParse = 0; // 1 is base, 2=way home
		Vector<Coordinate> wayHome = new Vector<Coordinate>();
		Vector<Coordinate> baseHome = new Vector<Coordinate>();

		// Alle felles felter
		try {
			eventType = defs.getEventType();
			while (!ferdig) {
				strName = defs.getName();
				if (eventType == XmlResourceParser.START_TAG) {
					if (strName.equals("itemdef")) {
						col = defs.getAttributeValue(null, "col");
						int pos = Integer.parseInt(defs.getAttributeValue(null,
								"firstmove"));
						Game.getInstance().getLudoBoard()
								.addPlayerInfo(col, pos);
					}
					if (strName.equals("path")) {
						int pos = Integer.parseInt(defs.getAttributeValue(null,
								"pos"));
						int x = Integer.parseInt(defs.getAttributeValue(null,
								"x"));
						int y = Integer.parseInt(defs.getAttributeValue(null,
								"y"));
						Coordinate co = new Coordinate();
						co.pos = pos;
						co.x = x;
						co.y = y;

						switch (whatToParse) {
						case 1:
							wayHome.add(co); // Last track home
							break;
						case 2:
							baseHome.add(co); // Home base
							break;
						}
					}
					if (strName.equals("base")) {
						whatToParse = 2;
					}
					if (strName.equals("wayhome")) {
						whatToParse = 1;
						int pos = Integer.parseInt(defs.getAttributeValue(null,
								"start"));
						Game.getInstance().getLudoBoard()
								.setWayHomePosition(col, pos);
					}
				} else if (defs.getEventType() == XmlResourceParser.END_TAG) {
					if (defs.getName().equals("itemdef")) {
						ferdig = true;
					}
				}
				if (!ferdig) {
					eventType = defs.next();
				}
			}
			Game.getInstance().getLudoBoard().addBaseHomeDefs(col, baseHome);
			Game.getInstance().getLudoBoard().addWayHomeDefs(col, wayHome);

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void behandleCommons(XmlResourceParser defs) {
		boolean ferdig = false;
		// String theText = new String();
		int eventType = -1;
		String strName = null;

		// Alle felles felter
		try {
			while (!ferdig) {
				strName = defs.getName();
				if (eventType == XmlResourceParser.START_TAG) {
					if (strName.equals("common")) {
						int pos = Integer.parseInt(defs.getAttributeValue(null,
								"pos"));
						int x = Integer.parseInt(defs.getAttributeValue(null,
								"x"));
						int y = Integer.parseInt(defs.getAttributeValue(null,
								"y"));
						Game.getInstance().getLudoBoard().addCommon(pos, x, y);
						Log.d("Xml load", "Board pos " + x + ", " + y);
					}
				} else if (defs.getEventType() == XmlResourceParser.END_TAG) {
					if (defs.getName().equals("commonfields")) {
						ferdig = true;
					}
				}
				if (!ferdig) {
					eventType = defs.next();
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
*/
	
	
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

	private void initDie()
	{
		final Die die = new Die();
		final ImageButton imgButtonDie = (ImageButton) findViewById(R.id.imageButtonDie);
		imgButtonDie.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				imgButtonDie.setImageBitmap(null);
				final int eyes = die.roll();
				int animationId = getResources().getIdentifier("die"+eyes+"anim", "drawable", "com.ronny.ludo");
				imgButtonDie.clearAnimation();
				imgButtonDie.setBackgroundResource(animationId);
	        	final AnimationDrawable frameAnimation = (AnimationDrawable) imgButtonDie.getBackground();
	        	imgButtonDie.post(new Runnable()
	            {
	    			public void run()
	    			{
	    				MediaPlayer mp;
	    				if(eyes==6)
	    				{
	    					 mp = MediaPlayer.create(getBaseContext(),R.raw.shake_and_roll_6);
	    				}
	    				else
	    				{
	    					 mp = MediaPlayer.create(getBaseContext(),R.raw.shake_and_roll);
	    				}
	    		        mp.start();
	    				frameAnimation.start();
	    				surface.setThrow(eyes);
	    			}  		        
	            });
			}
		});
	}

	/* (non-Javadoc)
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