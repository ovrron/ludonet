package com.ronny.ludo.model.impl;

import com.ronny.ludo.model.GameEvent;
import com.ronny.ludo.model.LudoAction;
import com.ronny.ludo.model.PlayerColor;

public class GameEventImpl implements GameEvent {
	private PlayerColor color;
	private LudoAction action;
	private int additionalInfo = 0;
	
	public GameEventImpl() {
	}

	public GameEventImpl(PlayerColor playerColor, LudoAction action) {
		this.color = playerColor;
		this.setAction(action);
	}

	public GameEventImpl(PlayerColor playerColor, LudoAction action, int additionalInfo) {
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
