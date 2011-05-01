/** 
* PieceAction.java 
* Copyright: (c) 2011 Ronny Heitmann Andersen, Ronny Øvereng and Karl-Erik Moberg
*/
package com.ronny.ludo.model;

/**
 * Definerer aksjonstyper for brikke
*/ 
public enum PieceAction {
	MOVE_TO_TOWER, // Flytt ut inn i et tårn
	MOVE_TO_BASE, // Brikke flyttes til til depot	
	MOVE_SIDE_BY_SIDE, // Flytt ut på siden
	NOTHING // Flytt ut på siden
}
