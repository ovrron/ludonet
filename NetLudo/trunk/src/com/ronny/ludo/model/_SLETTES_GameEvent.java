/** 
* _SLETTES_GameEvent.java 
* Copyright: (c) 2011 Ronny Heitmann Andersen, Ronny Øvereng and Karl-Erik Moberg
*/

package com.ronny.ludo.model;

/**
 * _SLETTES_GameEvent
 * Denne klassen er ikke i bruk....
 *
 */

public class _SLETTES_GameEvent implements _SLETTES_IGameEvent {
	private PlayerColor color;
	private LudoAction action;
	private int additionalInfo = 0;
	
	public _SLETTES_GameEvent() {
	}

	public _SLETTES_GameEvent(PlayerColor playerColor, LudoAction action) {
		this.color = playerColor;
		this.setAction(action);
	}

	public _SLETTES_GameEvent(PlayerColor playerColor, LudoAction action, int additionalInfo) {
		this.color = playerColor;
		this.setAction(action);
		this.setAdditionalInfo(additionalInfo);
	}

	public void setColor(PlayerColor color) {
		this.color = color;
	}

	public PlayerColor getColor() {
		return color;
	}

	public void setAction(LudoAction action) {
		this.action = action;
	}

	public LudoAction getAction() {
		return action;
	}

	public void setAdditionalInfo(int additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public int getAdditionalInfo() {
		return additionalInfo;
	}
	
}
