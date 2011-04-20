package com.ronny.ludo.helper;

import java.io.IOException;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;
import android.util.Log;

import com.ronny.ludo.model.Game;
import com.ronny.ludo.model.Coordinate;
import com.ronny.ludo.model.GameHolder;
import com.ronny.ludo.model.IPlayer;

public class ParseBoardDefinitionHelper implements IParseBoardDefinitionHelper{

	private static final String TAG = "-ParseBoardDefinitionHelper-:";
	
	
	/**
	 * Parse definisjon på de ludo-boards vi har tilgjengelige i denne versjonen.
	 *  
	 * @param defs
	 * @return
	 */
	public Vector<String> parseBoardsAvailable(XmlResourceParser defs){
		Vector<String> v = new Vector<String>();
		
		//TODO PARSE
		int eventType = -1;
		// Parse definisjonsdata
		try {
			while (eventType != XmlResourceParser.END_DOCUMENT) {
				if (eventType == XmlResourceParser.START_TAG) {
					String strName = defs.getName();
					if (strName.equals("name")) {
						v.add(new String(defs.getAttributeValue(null,"file")));
					}
				}
				eventType = defs.next();
			}
		} catch (Exception e) {
			Log.e(TAG, "parseBoardsAvailable to load defs", e);
		}
		return v;
	}
	/**
	 * Parser board-definisjon og legger opp strukturen slik at Game-klassen er frisk og fin
	 * til ny omgang.
	 */
	public boolean parseBoardDefinition(XmlResourceParser defs){
		boolean retVal = true;
		int eventType = -1;
//		Game.getInstance(); // Initiering av brikker etc..?
		GameHolder.getInstance().getGame().resetGame();
		// TODO Reset Game-klassen.
		
		
		// Parse definisjonsdata
		try {
			while (eventType != XmlResourceParser.END_DOCUMENT) {
				if (eventType == XmlResourceParser.START_TAG) {
					String strName = defs.getName();

					if (strName.equals("image")) {
						String thename = defs.getAttributeValue(null,"name");
						GameHolder.getInstance().getGame().setGameImageName(defs.getAttributeValue(null,"name"));
					}

					if (strName.equals("commonfields")) {
						retVal = behandleCommons(defs);
					}
					
					if (strName.equals("itemdef")) {
						retVal = behandleSpillerData(defs);
					}
					if (strName.equals("basedonresolution")) {
						int x = Integer.parseInt(defs.getAttributeValue(null,
								"x"));
						int y = Integer.parseInt(defs.getAttributeValue(null,
								"y"));
						GameHolder.getInstance().getGame().getLudoBoard().setDefinitionResolution(x,y);
					}
				}
				eventType = defs.next();
			}
		} catch (Exception e) {
			Log.e(TAG, "parseBoardDefinition:Failed to load defs", e);
		}
		return retVal;
	}

	/**
	 * Commons - den veien alle må gå på brettet
	 * 
	 * @param defs
	 * @return
	 */
	private boolean behandleCommons(XmlResourceParser defs) {
		boolean retVal = true;
		boolean ferdig = false;
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
						GameHolder.getInstance().getGame().getLudoBoard().addCommon(pos, x, y);
//						Log.d("Xml load", "Board pos " + x + ", " + y);
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
			Log.e(TAG, "behandleCommons:Failed to load defs", e);
			retVal = false;
		} catch (IOException e) {
			Log.e(TAG, "behandleCommons:Failed to load defs", e);
			retVal = false;
		}
		return retVal;
	}
	
	/**
	 * 
	 * Spilledata - dvs. Første steg ut på brettet, vei hjem og siste definisjon på common path
	 * før vi vandrer hjemover.
	 * 
	 * @param defs
	 * @return
	 */
	//TODO Her må vi også legge inn mal for brikkenavn - 
	// dvs. xxx1 er brikke med 1, xxx2 er 2 brikker i høyden etc...
	// Må også kunne benyttes i Piece-klassen...
	private boolean behandleSpillerData(XmlResourceParser defs) {
		boolean retVal = true;
		boolean ferdig = false;
		int eventType = -1;
		String strName = null;
		String col = "unknown"; // Player color
		
		int whatToParse = 0; // 1 is base, 2=way home
		Vector<Coordinate> wayHome = new Vector<Coordinate>();
		Vector<Coordinate> baseHome = new Vector<Coordinate>();

		try {
			eventType = defs.getEventType();
			while (!ferdig) {
				strName = defs.getName();
				if (eventType == XmlResourceParser.START_TAG) {
					if (strName.equals("itemdef")) {
						col = defs.getAttributeValue(null, "col");
						int pos = Integer.parseInt(defs.getAttributeValue(null,
								"firstmove"));
						GameHolder.getInstance().getGame().getLudoBoard()
								.addPlayerInfo(col, pos);
					} 
					if (strName.equals("icon")) {
						IPlayer pl = GameHolder.getInstance().getGame().getPlayerInfo(col);
						String thePrefix = defs.getAttributeValue(null,"prefix");
						pl.setIconPrefix(thePrefix);
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
						GameHolder.getInstance().getGame().getLudoBoard()
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
			GameHolder.getInstance().getGame().getLudoBoard().addBaseHomeDefs(col, baseHome);
			GameHolder.getInstance().getGame().getLudoBoard().addWayHomeDefs(col, wayHome);

		} catch (XmlPullParserException e) {
			Log.e(TAG, "behandleSpillerData:Failed to load defs", e);
			retVal = false;
		} catch (IOException e) {
			Log.e(TAG, "behandleSpillerData:Failed to load defs", e);
			retVal = false;
		}
		return retVal;
	}
}
