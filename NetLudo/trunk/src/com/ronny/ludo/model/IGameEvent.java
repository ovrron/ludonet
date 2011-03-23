package com.ronny.ludo.model;


public interface IGameEvent{
	public void setColor(PlayerColor color);

	public PlayerColor getColor();

	public void setAction(LudoAction action);

	public LudoAction getAction();

	public void setAdditionalInfo(int additionalInfo);

	public int getAdditionalInfo();
}
