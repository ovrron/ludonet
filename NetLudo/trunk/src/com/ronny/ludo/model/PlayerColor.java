/** 
* PlayerColor.java 
* Copyright: (c) 2011 Ronny Heitmann Andersen, Ronny Øvereng and Karl-Erik Moberg
*/

package com.ronny.ludo.model;

/**
 * Definerer farger
*/ 
public enum PlayerColor {
	BLUE, GREEN, YELLOW, RED, NONE;
	
	public static PlayerColor getColorFromString(String theColor) {
		if(theColor  == null) {
			return NONE;
		}
		for(PlayerColor plc : PlayerColor.values()) {
			if(plc.toString().compareTo(theColor)==0) {
				return plc;
			}
		}
		return NONE;
	}

    /**
     * Finner norsk betegnelse på farge
     * 
     * @return fargen   norsk betegnelse
     */ 	
	public String toNorwegian()
	{
		switch (this)
		{
		case BLUE:
			return "Blå";
		case GREEN:
			return "Grønn";
		case YELLOW:
			return "Gul";
		case RED:
			return "Rød";
		case NONE:
			return "Ingen farge";
		default:
			return "Ingen farge";
		}
	}
}
