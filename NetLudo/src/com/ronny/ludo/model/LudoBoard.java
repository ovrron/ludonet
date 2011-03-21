package com.ronny.ludo.model;

import java.util.Vector;

public class LudoBoard {

	private Vector<Coordinate> movingPath = new Vector<Coordinate>();
	
	public LudoBoard() {
		
	}
	
	// Add commond fields - the path to move on
	public void addCommon(int pos, int x, int y) {
		movingPath.add(new Coordinate(pos,x,y));
	}

	
	// Definerer første start i brettet ut fra home base
	public void addPlayerInfo(String playerColor, int firsMovePosition) {
		Player pl = Game.getInstance().getPlayerInfo(playerColor);
		if(pl == null) {
			return;
		}
		pl.setFirstBoardPosition(firsMovePosition);
	}

	// Sets the position to when the
	public void setWayHomePosition(String playerColor, int fieldToStartWayHome) {
		Player pl = Game.getInstance().getPlayerInfo(playerColor);
		if(pl == null) {
			return;
		}
		pl.setStartWayHomePosition(fieldToStartWayHome);
	}

	// Add the home base definitions for a player
	public void addBaseHomeDefs(String col, Vector<Coordinate> baseHome) {
		Player pl = Game.getInstance().getPlayerInfo(col);
		if(pl == null) {
			return;
		}
		pl.setHomePositions(baseHome);
	}

	// Add the way home definitions for a player
	public void addWayHomeDefs(String col, Vector<Coordinate> wayHome) {
		Player pl = Game.getInstance().getPlayerInfo(col);
		if(pl == null) {
			return;
		}
		pl.setWayHomePositions(wayHome);
	}
}
