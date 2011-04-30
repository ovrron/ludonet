package com.ronny.ludo.rules;

import java.util.List;

import com.ronny.ludo.model.IPiece;
import com.ronny.ludo.model.PieceAction;

public interface IRules
{
	public boolean isLegalMove(IPiece piece, int eyes);
	public List<PieceAction> getPieceActionList(IPiece piece, List<IPiece> pieces);
	public boolean canPlayerReRoll(int currentThrow);
	public boolean hasPlayerMoreAttemts(int noOfAttemts);
	public void setTakeOffNumbers(int... takeOffNumbers);
	public void setReRollNumbers(int... reRollNumbers);
	public void setNoOfAttemts(int noOfAttemts);
	public void setLudoBoard(String ludoBoardName, String ludoBoardFile);
	public String getLudoBoardName();
	public String getLudoBoardFile();
	public void setSettings(String settings);
	public String getSettings();
}

