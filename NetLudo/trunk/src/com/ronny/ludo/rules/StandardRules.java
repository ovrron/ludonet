package com.ronny.ludo.rules;

import java.util.ArrayList;
import java.util.List;
import com.ronny.ludo.model.IPiece;

public class StandardRules implements IRules
{
	private static final int STANDARD_TAKE_OFF_NUMBER = 6;
	private List<Integer> takeOffNumbers;

	public StandardRules()
	{
		takeOffNumbers = new ArrayList<Integer>();
		takeOffNumbers.add(STANDARD_TAKE_OFF_NUMBER);
	}
	
	public StandardRules(int... takeOffNumbers)
	{
		this.takeOffNumbers = new ArrayList<Integer>();
		for(int i:takeOffNumbers)
		{
			this.takeOffNumbers.add(i);
		}
	}
	
	/**
	 * @return int, hvor brikken kan flyttes, -10 hvis den ikke kan flyttes 
	 */
	@Override
	public boolean isLegalMove(IPiece piece, int eyes)
	{
		//Brikken er i mål
		if(piece.isAtGoal())
		{
			return false;
		}
		//Brikken er fremdeles hjemme
		else if(piece.isHome())
		{
			if(takeOffNumbers.contains(eyes))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		//Brikken er på vei inn til mål
		else if(piece.isOnWayToGoal())
		{
			if(piece.getBoardPosition()+eyes <= piece.getOwner().getWayHomePositions().lastElement().pos)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		//Brikken er på fellesområdet
		else
		{
			//Flyttet vil flytte brikken på vei inn til mål
			if(piece.getBoardPosition()+eyes > piece.getOwner().getStartWayHomePosition())
			{
				return true;
			}
			//Flyttet vil flytte brikken i fellesområdet
			else
			{
				return false;
			}
		}
	}

	@Override
	public boolean handleMove(IPiece piece, ArrayList<IPiece> pieces)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
