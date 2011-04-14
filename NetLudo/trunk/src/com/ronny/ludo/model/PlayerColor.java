package com.ronny.ludo.model;

public enum PlayerColor {
	BLUE, GREEN, YELLOW, RED, NONE;
	
	public static PlayerColor getColorFromString(String theColor) {
		for(PlayerColor plc : PlayerColor.values()) {
			if(plc.toString().compareTo(theColor)==0) {
				return plc;
			}
		}
		return null;
	}
}
