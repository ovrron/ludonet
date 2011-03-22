package com.ronny.ludo.helper.impl;

import java.io.IOException;
import java.util.Vector;
import org.xmlpull.v1.XmlPullParserException;
import android.content.res.XmlResourceParser;
import android.util.Log;
import com.ronny.ludo.helper.ParseBoardDefinitionHelper;
import com.ronny.ludo.model.Coordinate;
import com.ronny.ludo.model.Game;

public class ParseBoardDefinitionHelperImpl implements ParseBoardDefinitionHelper{

	private static final String TAG = "-ParseBoardDefinitionHelperImpl-:";
	
	@Override
	public boolean parseBoardDefinition(XmlResourceParser defs){
		boolean retVal = true;
		int eventType = -1;
		try {
			while (eventType != XmlResourceParser.END_DOCUMENT) {
				if (eventType == XmlResourceParser.START_TAG) {
					String strName = defs.getName();
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
						Game.getInstance().getLudoBoard().setDefinitionResolution(x,y);
					}
				}
				eventType = defs.next();
			}
		} catch (Exception e) {
			Log.e(TAG, "parseBoardDefinition:Failed to load defs", e);
		}
		return retVal;
	}

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
			Log.e(TAG, "behandleCommons:Failed to load defs", e);
			retVal = false;
		} catch (IOException e) {
			Log.e(TAG, "behandleCommons:Failed to load defs", e);
			retVal = false;
		}
		return retVal;
	}
	
	private boolean behandleSpillerData(XmlResourceParser defs) {
		boolean retVal = true;
		boolean ferdig = false;
		int eventType = -1;
		String strName = null;
		String col = "unknown"; // PlayerImpl color
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
			Log.e(TAG, "behandleSpillerData:Failed to load defs", e);
			retVal = false;
		} catch (IOException e) {
			Log.e(TAG, "behandleSpillerData:Failed to load defs", e);
			retVal = false;
		}
		return retVal;
	}
}
