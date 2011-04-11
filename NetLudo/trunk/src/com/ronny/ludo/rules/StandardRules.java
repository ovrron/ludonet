package com.ronny.ludo.rules;

import java.util.ArrayList;
import java.util.List;

import com.ronny.ludo.model.Game;
import com.ronny.ludo.model.IPiece;
import com.ronny.ludo.model.PlayerColor;

public class StandardRules implements IRules
{
	private static final int STANDARD_TAKE_OFF_NUMBER = 6;
	private static final int STANDAR_REROLL_NUMBER = 6;
	
	private List<Integer> takeOffNumbers;
	private List<Integer> reRollNumbers;

	public StandardRules()
	{
		takeOffNumbers = new ArrayList<Integer>();
		takeOffNumbers.add(STANDARD_TAKE_OFF_NUMBER);
		reRollNumbers = new ArrayList<Integer>();
		reRollNumbers.add(STANDAR_REROLL_NUMBER);
	}
	
	public void setTakeOffNumbers(int... takeOffNumbers)
	{
		this.takeOffNumbers = new ArrayList<Integer>();
		for(int i:takeOffNumbers)
		{
			this.takeOffNumbers.add(i);
		}
	}

	
	public void setReRollNumbers(int... reRollNumbers)
	{
		this.reRollNumbers = new ArrayList<Integer>();
		for(int i:reRollNumbers)
		{
			this.reRollNumbers.add(i);
		}
	}

	
	/**
	 * @return int, hvor brikken kan flyttes, -10 hvis den ikke kan flyttes 
	 */
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
		//TODO må sjekke litt nærmere om dette stemmer
		else
		{
			//Flyttet vil flytte brikken på vei inn til mål
			if(piece.getBoardPosition()+eyes > piece.getOwner().getStartWayHomePosition())
			{
				if(piece.getBoardPosition()+eyes-piece.getOwner().getStartWayHomePosition() <= piece.getOwner().getWayHomePositions().size())
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			else
			{
				return true;
			}
		}
	}

	//TODO Her må det vel inn to lister. Kan jo f.eks. flytte ett tårn oppå et annet
	public boolean handleMove(IPiece piece, List<IPiece> pieces)
	{
		//Det finnes ingen brikker der fra før
		if(pieces==null || pieces.size()==0)
		{
			return false;
		}
		else
		{
			//Det finnes minst en brikke der fra før med samme farge
			if(piece.getOwner().getColor().compareTo(pieces.get(0).getOwner().getColor())==0)
			{
				for(IPiece p:pieces)
				{
					p.setEnabled(false);
					p.clearInTowerWith();
					piece.addInTowerWith(p);
				}
			}
			//Det finnes minst en brikke der fra før med en annen farge			
			else
			{
				for(IPiece p:pieces)
				{
					p.placePieceInHouse();
					p.clearInTowerWith();
					p.setEnabled(true);
				}				
			}
			return true;
		}
	}

	public PlayerColor getNextPlayerColor(PlayerColor currentPlayerColor, int eyes)
	{
		if(reRollNumbers.contains(eyes))
		{
			return currentPlayerColor;
		}
		else
		{
			return Game.getInstance().getLudoBoard().getNextPlayerColor(currentPlayerColor);
		}
	}
}
