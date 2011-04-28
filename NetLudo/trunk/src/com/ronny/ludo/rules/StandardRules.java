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

	public void setNoOfAttemts(int noOfAttemts) {
		this.noOfAttemts = noOfAttemts;
	}

	
	/**
	 * @return int, hvor brikken kan flyttes, -10 hvis den ikke kan flyttes 
	 */
	public boolean isLegalMove(IPiece piece, int eyes)
	{
		//Brikken er i mål
		if(piece.isAtGoal())
		{
			return false;  // Allerede i mål
		}
		//Brikken er fremdeles hjemme
		if(piece.isHome())
		{
			if(takeOffNumbers.contains(eyes))
			{
				return true;   // Flytte brikke ut
			}
			else
			{
				return false;   // Brikke blir stående
			}
		}
		
		//Brikken er på vei inn til mål
		if(piece.isOnWayToGoal())
		{
			if(piece.getBoardPosition() + eyes <= piece.getOwner().getWayHomePositions().lastElement().pos)
			{
			    // Fortsatt på vei hjem
			    return true;
			}
			else
			{
			    // Fortsatt på vei hjem men terning er mer enn kun til mål
				return false;  
			}
		}
		//Brikken er på fellesområdet
		if(piece.getBoardPosition() + eyes > piece.getOwner().getStartWayHomePosition())
		{
			if((piece.getBoardPosition() + eyes - piece.getOwner().getStartWayHomePosition()) 
			        <= piece.getOwner().getWayHomePositions().size())
			{
			    // På vei hjem men ikke i mål
				return true; 
			}
			else
			{
			    // På vei hjem men lenger enn mål
				return false;  
			}
		}
		else
		{
		    // Gyldig flytt på fellesområdet
			return true;  
		}
		
	}

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
    
	//TODO IKKE I BRUK. ER FLYTTET TIL GAME
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
                    // Tar eksisternde tårn inn i tårn
					if (p.getInTowerWith()!= null){
					    for (IPiece pUnder : p.getInTowerWith()){
					        piece.addInTowerWith(pUnder);
					    }
					}
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
					if (p.getInTowerWith()!= null){
					    // løser opp alle brikker i tårnet og slår hjem
                        for (IPiece pUnder : p.getInTowerWith()){
                            pUnder.setEnabled(true);
                            pUnder.placePieceInHouse();
                        }
                    }
					p.clearInTowerWith();
					p.setEnabled(true);
				}				
			}
			return true;
		}
	}

	public boolean canPlayerReRoll(int currentThrow)
	{
		if(reRollNumbers.contains(currentThrow))
		{
			return true;
		}
		return false;
	}
	
	public boolean hasPlayerMoreAttemts(int noOfAttemts)
	{
		if(noOfAttemts<this.noOfAttemts)
		{
			return true;
		}
		return false;
	}
	
	public void setLudoBoard(String ludoBoardName, String ludoBoardFile)
	{
		this.ludoBoardName = ludoBoardName;
		this.ludoBoardFile = ludoBoardFile;
	}

	public String getLudoBoardName()
	{
		return ludoBoardName;
	}

	public String getLudoBoardFile()
	{
		return ludoBoardFile;
	}

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retVal.toString();
	}
}
