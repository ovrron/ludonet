package com.ronny.ludo.rules;

import java.util.List;
import com.ronny.ludo.model.IPiece;
import com.ronny.ludo.model.PlayerColor;

public interface IRules
{
	public boolean isLegalMove(IPiece piece, int eyes);
	public boolean handleMove(IPiece piece, List<IPiece> pieces);
	public PlayerColor getNextPlayerColor(PlayerColor currentPlayerColor, int eyes);
	public void setTakeOffNumbers(int... takeOffNumbers);
	public void setReRollNumbers(int... reRollNumbers);
}

