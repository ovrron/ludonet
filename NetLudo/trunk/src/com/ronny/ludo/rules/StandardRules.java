/** 
* IRules.java 
* Copyright: (c) 2011 Ronny Heitmann Andersen, Ronny Øvereng and Karl-Erik Moberg
*/

package com.ronny.ludo.rules;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.ronny.ludo.model.IPiece;
import com.ronny.ludo.model.PieceAction;

public class StandardRules implements IRules
{
	private static final int STANDARD_TAKE_OFF_NUMBER = 6;
	private static final int STANDAR_REROLL_NUMBER = 6;
	
	private List<Integer> takeOffNumbers;
	private List<Integer> reRollNumbers;
	private int noOfAttemts;
	private String ludoBoardName = null;
	private String ludoBoardFile = null;

	 /**
     * Konstruktør
     */
	public StandardRules()
	{
		takeOffNumbers = new ArrayList<Integer>();
		takeOffNumbers.add(STANDARD_TAKE_OFF_NUMBER);
		reRollNumbers = new ArrayList<Integer>();
		reRollNumbers.add(STANDAR_REROLL_NUMBER);
	}
	
    /**
     * Setter hvilke tall som kreves for å starte.
     * 
     * @param takeOffNumbers    tall som skal gjelde for å starte
     */	
	public void setTakeOffNumbers(int... takeOffNumbers)
	{
		this.takeOffNumbers = new ArrayList<Integer>();
		for(int i:takeOffNumbers)
		{
			this.takeOffNumbers.add(i);
		}
	}

    /**
     * Setter hvilke tall som kreves for å få kaste en ekstra gang 
     * 
     * @param reRollNumbers  Tall som skal gjelde for å få kaste på nytt
     */
	public void setReRollNumbers(int... reRollNumbers)
	{
		this.reRollNumbers = new ArrayList<Integer>();
		for(int i:reRollNumbers)
		{
			this.reRollNumbers.add(i);
		}
	}

     /**
     * Setter hvor mange forsøk en spiller skal få 
     * 
     * @param noOfAttemts   Antall forsøk
     */
	public void setNoOfAttemts(int noOfAttemts) {
		this.noOfAttemts = noOfAttemts;
	}

	
	 /**
	 * Sjekker om et flytt er gyldig
	 * 
	 * @param piece    Brikke som skal flyttes
	 * @param eyes     Antall posisjoner som skal flyttes
	 * @return boolean True hvis gyldig flytt, false ellers 
	 */
	public boolean isLegalMove(IPiece piece, int eyes)
	{
	    boolean retval = false;
		//Brikken er i mål
		if(piece.isAtGoal())
		{
		    retval = false;  // Allerede i mål
		}
		else {
    		//Brikken er fremdeles hjemme
    		if(piece.isHome())
    		{
    			if(takeOffNumbers.contains(eyes))
    			{
    			    retval = true;   // Flytte brikke ut
    			}
    			else
    			{
    			    retval = false;   // Brikke blir stående
    			}
    		}
    		else {
        		//Brikken er på vei inn til mål
        		if(piece.isOnWayToGoal())
        		{
        			if(piece.getBoardPosition() + eyes <= 1 + piece.getOwner().getStartWayHomePosition() + piece.getOwner().getWayHomePositions().lastElement().pos)
        			{
        			    // Fortsatt på vei hjem
        			    retval = true;
        			}
        			else
        			{
        			    // Fortsatt på vei hjem men terning er mer enn kun til mål
        			    retval = false;  
        			}
        		}
        		else {
            		//Brikken er på fellesområdet
            		if(piece.getBoardPosition() + eyes > piece.getOwner().getStartWayHomePosition())
            		{
            			if((piece.getBoardPosition() + eyes - piece.getOwner().getStartWayHomePosition()) 
            			        <= piece.getOwner().getWayHomePositions().size())
            			{
            			    // På vei hjem men ikke i mål
            			    retval = true; 
            			}
            			else
            			{
            			    // På vei hjem men lenger enn mål
            			    retval = false;  
            			}
            		}
            		else
            		{
            		    // Gyldig flytt på fellesområdet
            		    retval = true;  
            		}
        		}
    		}
		}
		return retval;
		
	}

	 /**
     * Lager liste med aksjoner på brikker som står på samme posisjon som brikke er flyttet til.
     * 
     * @param piece     Brikke som skal flyttes
     * @param pieces    Liste med brikker som står i samme posisjon
     * @return liste    av PieceAction, inneholder en liste med aksjoner
     */
    public List< PieceAction > getPieceActionList(IPiece piece, List< IPiece > pieces) {
       
        List<PieceAction> actionList = new ArrayList<PieceAction>();
        
        //Det finnes ingen brikker der fra før
        if(pieces==null || pieces.size()==0)
        {
            actionList = null;
        }
        else
        {
            //Det finnes minst en brikke der fra før med samme farge
            if(piece.getOwner().getColor().compareTo(pieces.get(0).getOwner().getColor())==0)
            {
                actionList.add(PieceAction.MOVE_TO_TOWER);
            }
            //Det finnes minst en brikke der fra før med en annen farge         
            else
            {
                actionList.add(PieceAction.MOVE_TO_BASE);
            }
        }
        return actionList;
    }
    
    /**
     * Sjekker om en bruker kan kaste på nytt
     * 
     * @param currentThrow      gjeldende kast
     * @return boolean          true hvis nytt kast, false hvis ikke
     */
	public boolean canPlayerReRoll(int currentThrow)
	{
		if(reRollNumbers.contains(currentThrow))
		{
			return true;
		}
		return false;
	}
	
    /**
     * Sjekker om spiller har flere forsøk
     * 
     * @param noOfAttemts   antall forsøk brukt
     * @return boolean      true hvis flere forsøk igjen, false hvis alle forsøk er brukt
     */	
	public boolean hasPlayerMoreAttemts(int noOfAttemts)
	{
		if(noOfAttemts<this.noOfAttemts)
		{
			return true;
		}
		return false;
	}
	
    /**
     * Setter ludoboard og datafil
     * 
     * @param ludoBoardName     board navn
     * @praram ludoBoardFile    filnavn
     */ 
	public void setLudoBoard(String ludoBoardName, String ludoBoardFile)
	{
		this.ludoBoardName = ludoBoardName;
		this.ludoBoardFile = ludoBoardFile;
	}

    /**
     * Finner boardnavn
     * 
     * @return ludoBoardName   boardnavn
     */ 
	public String getLudoBoardName()
	{
		return ludoBoardName;
	}

	/**
     * Finner boardfilnavn
     * 
     * @return ludoBoardFile    boardfilnavn
     */ 
	public String getLudoBoardFile()
	{
		return ludoBoardFile;
	}
    
	/**
     * Setter innstillinger for regler
     * 
     * @param settings  innstillingene som skal gjelde for spillet
     */ 
	public void setSettings(String settings)
	{
		try
		{
			JSONObject jSonObject = new JSONObject(settings);
			JSONArray takeOffArray = jSonObject.getJSONArray("takeOffNumbers");
			takeOffNumbers = new ArrayList<Integer>();
			for(int i=0;i<takeOffArray.length();i++)
			{
				takeOffNumbers.add(takeOffArray.getInt(i));
			}
			
			JSONArray reRollArray = jSonObject.getJSONArray("reRollNumbers");
			reRollNumbers = new ArrayList<Integer>();
			for(int i=0;i<reRollArray.length();i++)
			{
				reRollNumbers.add(reRollArray.getInt(i));
			}
			
			noOfAttemts = jSonObject.getInt("noOfAttemts");
			ludoBoardName = jSonObject.getString("ludoBoardName");
			ludoBoardFile = jSonObject.getString("ludoBoardFile");
		} 
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	  /**
     * Finner innstillingene for regler
     * 
     * @return Settings     innstillingene som gjelder for spillet
     */ 
	public String getSettings()
	{
		JSONObject retVal = new JSONObject();
		try
		{
			JSONArray takeOffArray = new JSONArray();
			for(Integer i:takeOffNumbers)
			{
				takeOffArray.put(i);
			}
			retVal.put("takeOffNumbers", takeOffArray);
			
			JSONArray reRollArray = new JSONArray();
			for(Integer i:reRollNumbers)
			{
				reRollArray.put(i);
			}
			retVal.put("reRollNumbers", reRollArray);
			
			retVal.put("noOfAttemts", noOfAttemts);
			
			retVal.put("ludoBoardName", ludoBoardName);
			
			retVal.put("ludoBoardFile", ludoBoardFile);
		} 
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		return retVal.toString();
	}
}
