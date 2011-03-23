package com.ronny.ludo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

import com.ronny.ludo.board.LudoSurfaceView;
import com.ronny.ludo.helper.ParseBoardDefinitionHelper;
import com.ronny.ludo.model.Game;

public class LudoActivity extends LudoCommonActivity {
	private String TAG = "-Ludo-:";

	private ImageButton zoomInButton;
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

		setContentView(R.layout.main);

		zoomInButton = (ImageButton) findViewById(R.id.zoomIn);
		zoomOutButton = (ImageButton) findViewById(R.id.zoomOut);
		surface = (LudoSurfaceView) findViewById(R.id.image);

		zoomInButton.setOnClickListener(zoomInListener);
		zoomOutButton.setOnClickListener(zoomOutListener);
		
		//TEST
		// Test av mod
		int start = 0;
		int maxVal = 13;
		
		for(int j=0;j<20;j++) {
			System.out.println("J: "+j+" - "+(start+j)%maxVal);
		}

		start = 7;
		for(int j=0;j<20;j++) {
			System.out.println("J: "+j+" - "+(start+j)%maxVal);
		}
		//TEST END 
		
		//parseXmlDefs();
		if(!new ParseBoardDefinitionHelper().parseBoardDefinition(getResources().getXml(R.xml.boarddefinition))){
			//TODO Håndter feil ved lasting av brettdefinisjon
			//Vis feilmelding og ev. avslutt
		}
			
		Game.getInstance().getLudoBoard().recalcPositions();

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
		Vector<ICoordinate> wayHome = new Vector<ICoordinate>();
		Vector<ICoordinate> baseHome = new Vector<ICoordinate>();

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
						ICoordinate co = new ICoordinate();
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

	private OnClickListener zoomOutListener = new OnClickListener() {
		public void onClick(View v) {
			Log.d(TAG, "Zoom out");
			surface.zoomOut();
		}
	};

}