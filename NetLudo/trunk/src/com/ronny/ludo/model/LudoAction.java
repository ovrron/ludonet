/** 
* LudoAction.java 
* Copyright: (c) 2011 Ronny Heitmann Andersen, Ronny Øvereng and Karl-Erik Moberg
*/

package com.ronny.ludo.model;

public enum LudoAction {
	MOVE_PAWN, // Flytt en brikke (eller fler)
	RELEASE_PAWN, // Flytt ut brikke fra depot
	MOVE_TO_BASE // Piece slått inn til depot	
}
