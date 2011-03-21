package com.ronny.ludo.model;

import java.util.Vector;

public class Player {
	private PlayerColor color;
	private boolean isActive = false;
	private int firstPositionOnBoard = 0;
	private int lastPositionBeforeWayHome = 0;
	private Vector<Coordinate> homePositions = null;
	private Vector<Coordinate> wayHomePositions = null;

	// Brikkene tilh�rer egentlig Game, men er fordelt p� spiller
	private Brikke brikker[] = new Brikke[4];

	public Player(PlayerColor color) {
		this.setColor(color);
		for (int i = 0; i < 4; i++) {
			brikker[i] = new Brikke(color);
			brikker[i].setHousePosition(i);
		}
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public void setColor(PlayerColor color) {
		this.color = color;
	}

	public PlayerColor getColor() {
		return color;
	}

	// Sette f�este posisjon ut fra home
	public void setFirstBoardPosition(int firsMovePosition) {
		firstPositionOnBoard = firsMovePosition;
	}

	// Siste posisjon f�r vi starter p� vi inn i brettet.
	public void setStartWayHomePosition(int fieldToStartWayHome) {
		lastPositionBeforeWayHome = fieldToStartWayHome;
	}

	// Sette koordinater for posisjon i home
	public void setHomePositions(Vector<Coordinate> baseHome) {
		homePositions = baseHome;
	}

	// Setter koordinater for 'way home' til m�l
	public void setWayHomePositions(Vector<Coordinate> wayHome) {
		wayHomePositions = wayHome;		
	}

}
