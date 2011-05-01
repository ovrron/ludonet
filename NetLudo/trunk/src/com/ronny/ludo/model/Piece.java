/** 
* IRules.java 
* Copyright: (c) 2011 Ronny Heitmann Andersen, Ronny Øvereng and Karl-Erik Moberg
*/

package com.ronny.ludo.model;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class Piece implements IPiece{
	private IPlayer owner;
	private int boardPosition = -1;
	private int housePosition = -1; // position in the 'house'
	private boolean isAtGoal = false; // true hvis brikke er i mål
	private boolean enabled = true; //brikken er med i spillet (ikke som en del av et tårn)
	private List<IPiece> inTowerWith = null;
	private boolean highlight = false;
	private boolean isOnWayToGoal = false; // true hvis brikke er på tur i mål

    /**
     * Konstruktør for brikke 
     * 
     * @param owner     spiller som eier brikke
     */ 
	public Piece(IPlayer owner) {
		this.owner = owner;
	}
	
	/**
     * Sjekker om brikke er enablet 
     * 
     * @return boolean      true hvis brikke er enablet, false ellers
     */ 
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
     * Setter brikke enablet.  
     * 
     * @param enabled       er true hvis brikke skal være enablet, false ellers
     */ 
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	/**
     * Finner andre brikker som er i tårn med brikke 
     * 
     * @return inTowerWith      liste med brikker som er i tårn med brikke
     */ 
	public List<IPiece> getInTowerWith()
	{
		return inTowerWith;
	}

	/**
     * Legger brikke i tårn 
     * 
     * @param piece     en annen brikke som skal inn i tårn til brikke
     */ 
	public void addInTowerWith(IPiece piece)
	{
		if(inTowerWith==null)
		{
			inTowerWith = new ArrayList<IPiece>();
		}
		inTowerWith.add(piece);
	}
	
	/**
     * Fjerner tårn 
     */ 
	public void clearInTowerWith()
	{
		inTowerWith=null;
	}
	
	/**
     * Setter isAtGoal 
     * 
     * @param owner     spiller som eier brikke
     */ 
	public void setIsAtGoal() {
		isAtGoal = true;
	}
    
	/**
     * Sjekker om en brikke er kommet i mål 
     * 
     * @return isAtGoal
     */ 
	public boolean isAtGoal() {
		return isAtGoal;
	}
	
    /**
     * Sjekker om en brikke er i hjemmeposisjon 
     * 
     * @return boardPosition    Hvis denne er -1 så er brikken hjemme
     */ 	
	public boolean isHome() {
		return boardPosition==-1;
	}
	
    /**
     * Sjekker om en brikke er i hjemmeposisjon 
     * 
     * @return boardPosition    Hvis denne er -1 så er brikken hjemme
     */ 
	public IPlayer getOwner() {
		return owner;
	}

    /**
     * Finner brikkens farge 
     * 
     * @return color
     */ 
	public PlayerColor getColor() {
		return owner.getColor();
	}

    /**
     * Setter posisjon til brikke 
     * 
     * @return boardPosisition      Brikkens nye posisjon
     */ 
	public void setBoardPosition(int boardPosition) {
		this.boardPosition = boardPosition;
	}

    /**
     * Finner brikkens brettposisjon 
     * 
     * @return boardPosition
     */ 
	public int getBoardPosition() {
		return boardPosition;
	}
	
    /**
     * Setter brikkens hjemmeposisjon 
     * 
     * @para housePostition     Brikkens posisjon hjemme, mulige verdier 0,1,2,3.
     */ 
	public void setHousePosition(int housePosition) {
		this.housePosition = housePosition;
	}

    /**
     * Finner brikkens hjemmeposisjon 
     * 
     * @return housePosition        Brikkens posisjon hjemme, mulige verdier 0,1,2,3.
     */ 
	public int getHousePosition() {
		return housePosition;
	}

	/**
	 * Returner current koordinat for brikke
	 * 
	 * @return koordinat
	 */
	public Coordinate getCurrentPosition() {
		Coordinate co = null;
		if (boardPosition < 0) {
			// Still in house - position housePosition
			co = owner.getHomePositions().elementAt(housePosition);
		} else {
			if( boardPosition > owner.getStartWayHomePosition()) {
				// Vi er på vei inn
				int wayHpos = boardPosition - owner.getStartWayHomePosition() - 1;
				co = owner.getWayHomePositions().elementAt(wayHpos);
			} else {
				co = owner.getBoardPosition(boardPosition);
			}
		}
		return co;
	}

    /**
     * Flytter brikken til første posisjon på brettet.  
     */ 
	public void placePieceOnBoard() {
		boardPosition = owner.getFirstBoardPosition();
	}
	
    /**
     * Flytter brikken til hjemmeposisjon. Verdien -1 betyr hjemmeposisjon. 
     */ 
	public void placePieceInHouse()
	{
		boardPosition = -1; // Settes til 'i huset'
	}

    /**
     * Get the string id til bitmap denne representerer 
     * 
     * @return stringid
     */ 
	public String getId() {
		String ret = null;
		if(enabled)
		{
			if(inTowerWith==null || inTowerWith.size()==0)
			{
				ret = owner.getIconPrefix() + "1";
			}
			else
			{
				ret = owner.getIconPrefix() + Integer.toString(inTowerWith.size()+1);
			}
		}
		return ret;
	}

    /**
     * Flytte en brikke litt
     * 
     * @param theMove   Antall flytt
     */ 
	public void moveForward(int theMove) {

	    if (isAtGoal) {
			return;
		}
		
		Log.d("IPiece"," Col: "+owner.getColor().toString()+"IPiece: "+housePosition+" Current pos: "+boardPosition+" move: "+theMove);
        boardPosition += theMove;
        
		if (!isOnWayToGoal() && getBoardPosition() > owner.getStartWayHomePosition()) {
		    isOnWayToGoal = true;
		}
	    if(isOnWayToGoal() && getBoardPosition() == owner.getStartWayHomePosition()+ 1 + getOwner().getWayHomePositions().lastElement().pos){
//	        boardPosition = owner.getWayHomePositions().size() + owner.getStartWayHomePosition();
	        isAtGoal = true;
	    }
	}

    /**
     * Sjekker om en brikke er på vei mot mål i sitt målområde 
     * 
     * @return isOnWayToGoal    Er true hvis brikken er i sitt målområde, false ellers.
     */ 
	public boolean isOnWayToGoal()
	{
		return isOnWayToGoal;
	}

    /**
     * Finn koordinater til brikke på et visst antall flytt. 
     * 
     * @param newPos        posisjon (boardPosition)
     * @return Coordinate   eller null hvis antallet er lengre enn Goal
     */
	public Coordinate getPositionAtBoardPosition(int newPos) {
		// 
		if(newPos >= (owner.getWayHomePositions().size() + owner.getStartWayHomePosition())) {
			return null;
		}
		
		Coordinate co = null;
		if (newPos < 0) {
			// Still in house - position housePosition
			co = owner.getHomePositions().elementAt(housePosition);
		} else {
		    if(isOnWayToGoal() && getBoardPosition() + newPos <= getOwner().getWayHomePositions().lastElement().pos)
		    {
				int wayHpos = newPos - owner.getStartWayHomePosition() - 1;
				co = owner.getWayHomePositions().elementAt(wayHpos);
			} else {
				co = owner.getBoardPosition(newPos);
			}
		}
		
		return co;
	}
    
    /**
     * Kan en brikke flytte så mange plasser fram 
     * 
     * @param numPos    antall posisjoner fram
     * @return true     hvis den kan flytte, false hvis den ikke kan det.
     */
	public boolean canPieceMove(int numPos) {
	    Boolean ret=false;
	    ret = GameHolder.getInstance().getRules().isLegalMove(this, numPos);
	    return ret; 
	}
	
	public String toString() {
		return owner.getBoardPosition(boardPosition).toString();
	}

    /**
     * Sjekker om en brikke er highlitet
     */
	public boolean highLight()
	{
		return highlight;
	}

    /**
     * Setter en brikke til highlited eller ikke 
     * 
     * @param highlight
     */	
	public void highLight(boolean highlight)
	{
		this.highlight = highlight;
		
	}

}
