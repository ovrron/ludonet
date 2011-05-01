/** 
* _SLETTES_IGameEvent.java 
* Copyright: (c) 2011 Ronny Heitmann Andersen, Ronny Øvereng and Karl-Erik Moberg
*/

package com.ronny.ludo.model;

public interface _SLETTES_IGameEvent{
	public void setColor(PlayerColor color);
	public PlayerColor getColor();
	public void setAction(LudoAction action);
	public LudoAction getAction();
	public void setAdditionalInfo(int additionalInfo);
	public int getAdditionalInfo();
}
