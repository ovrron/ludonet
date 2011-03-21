package com.ronny.ludo.model;

public class Brikke {
	private PlayerColor owner;
	private int boardPosition = -1;  	// holder for board position - managed by Game
	private int numberOfElements = 1; 	// Antall brikker i høyden 1..4
	private int housePosition = -1; 	// position in the 'house'
	
	public Brikke(PlayerColor owner) {
		this.owner = owner;
	}

	public PlayerColor getOwner() {
		return owner;
	}

	public void setBoardPosition(int boardPosition) {
		this.boardPosition = boardPosition;
	}

	public int getBoardPosition() {
		return boardPosition;
	}

	public void setHousePosition(int housePosition) {
		this.housePosition = housePosition;
	}

	public int getHousePosition() {
		return housePosition;
	}
	
	
}
