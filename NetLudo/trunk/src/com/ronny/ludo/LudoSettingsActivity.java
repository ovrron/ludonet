package com.ronny.ludo;

/**
 * 
 * @author ovrron
 * 
 */

import java.util.Vector;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ronny.ludo.helper.BoardDialog;
import com.ronny.ludo.helper.ParseBoardDefinitionHelper;
import com.ronny.ludo.model.GameHolder;

public class LudoSettingsActivity extends Activity 
{
	private String TAG = "-Settings-:";
	
	/** Tilgjengelige ludobrett */
	private Vector<String> availableBoards = null;
	/** Dialog for å velge brett */	
	private BoardDialog boardDialog = null;
	/** Navn på definisjonsfil for valgt brett */
	private String chosenBoardFile = null;
	/** SharedPreferences */
	SharedPreferences settings = null;
	/** Textview som viser navn/id for valgt brett */
	private TextView textViewBoard = null;
	/** Radiobuttongroup for takeoff valg */
	private RadioGroup radioGroupTakeOff = null;
	/** Radiobuttongroup for no of attemts valg */
	private RadioGroup radioGroupNoOfAttemts = null;
	/** Radiobuttongroup for reroll valg */
	private RadioGroup radioGroupReRoll = null;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);
		
		init();
	}
	

	/**
	 * Initierer variabler, knapper og valg som tidligere er lagret i sharedpreferences
	 */
	private void init()
	{
		textViewBoard = (TextView)findViewById(R.id.textChosenBoard);
		
		/** Henter info om tilgjengelige brett */
		final ParseBoardDefinitionHelper ph = new ParseBoardDefinitionHelper();
		availableBoards = ph.parseBoardsAvailable(getResources().getXml(R.xml.boarddefinition));
		
		/** SharedPreferences */
		importSettings();
		
		/** Initierer brettdialogen*/
      	boardDialog = new BoardDialog(this, availableBoards, ParseBoardDefinitionHelper.SEPARATOR,  textViewBoard.getText().toString());
      	/** Initierer knapp for å velge brett*/
      	ImageButton imageButtonChooseBoard = (ImageButton)findViewById(R.id.imageButtonChooseBoard);
      	imageButtonChooseBoard.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				boardDialog.show();
			}
		});

      	/** Henter valgt brett fra brettdialogen */
    	boardDialog.setOnDismissListener(new OnDismissListener()
    	{
			@Override
			public void onDismiss(DialogInterface dialog)
			{
				String chosenBoard = boardDialog.getChosenBoard();
				String[] boardDef = chosenBoard.split(ParseBoardDefinitionHelper.SEPARATOR);
				textViewBoard.setText(boardDef[0]);
				chosenBoardFile = boardDef[1];
			}
    	});
		
    	/** Initierer neste knappen */
		Button buttonNext = (Button)findViewById(R.id.buttonNext);
		buttonNext.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				/** Oppdaterer settings til regler, samt lagrer de i sharedpreferences */
				if(updateSettings())
				{
					Intent ludoIntent = new Intent(v.getContext(),LudoStartNewGameActivity.class);
					startActivity(ludoIntent);
				}
				else
				{
					Toast.makeText(v.getContext(), R.string.settings_toast_missingboard, Toast.LENGTH_LONG);
				}

			}
		});
	}
	
	/**
	 * Importerer  valg som tidligere er lagret i sharedpreferences
	 */
	private void importSettings()
	{
		/** initierer settings */
		settings = getSharedPreferences((String) getResources().getText(R.string.sharedpreferences_name), MODE_PRIVATE);
		/** henter forrige valgt brett */
		chosenBoardFile = settings.getString((String) getResources().getText(R.string.sharedpreferences_ludoboardfile), null);
		String chosenBoardName = settings.getString((String) getResources().getText(R.string.sharedpreferences_ludoboardname), null);
		/** Hvis ingen tidligere valgt */
		if(chosenBoardFile==null || chosenBoardName==null)
		{
			
			String board = null;
			if(availableBoards.size()>0)
			{
				if(availableBoards.size()>1)
				{
					board = availableBoards.get(1);
				}
				else
				{
					board = availableBoards.get(0);
				}
				String[] boardDef = board.split(ParseBoardDefinitionHelper.SEPARATOR);
				chosenBoardName = boardDef[0];
				chosenBoardFile = boardDef[1];
			}
			else
			{
				chosenBoardName = (String) getResources().getText(R.string.settings_text_noboard);
				chosenBoardFile=null;
			}
		}
		textViewBoard.setText(chosenBoardName);
		
		/** henter forrige valgt settings for takeoff og viser i gui */
		String fromPrefTakeOff = settings.getString((String) getResources().getText(R.string.sharedpreferences_takeoff), null);
		radioGroupTakeOff = (RadioGroup)findViewById(R.id.radioTakeOff);
		if(fromPrefTakeOff != null)
		{
			for(int i=0; i<radioGroupTakeOff.getChildCount(); i++)
			{
				RadioButton radioButton = (RadioButton) radioGroupTakeOff.getChildAt(i);
				if(radioButton.getText().toString().equals(fromPrefTakeOff))
				{
					radioButton.setChecked(true);
					break;
				}
			}
		}
		
		/** henter forrige valgt settings for no of attemts og viser i gui */
		String fromPrefNoOfAttemts = settings.getString((String) getResources().getText(R.string.sharedpreferences_noofattemts), null);
		radioGroupNoOfAttemts = (RadioGroup)findViewById(R.id.radioNoOfAttemts);
		if(fromPrefNoOfAttemts != null)
		{
			for(int i=0; i<radioGroupNoOfAttemts.getChildCount(); i++)
			{
				RadioButton radioButton = (RadioButton) radioGroupNoOfAttemts.getChildAt(i);
				if(radioButton.getText().toString().equals(fromPrefNoOfAttemts))
				{
					radioButton.setChecked(true);
					break;
				}
			}
		}
		
		/** henter forrige valgt settings for reroll og viser i gui */		
		String fromPrefReRoll = settings.getString((String) getResources().getText(R.string.sharedpreferences_reroll), null);
		radioGroupReRoll = (RadioGroup)findViewById(R.id.radioReRoll);
		if(fromPrefReRoll != null)
		{
			for(int i=0; i<radioGroupReRoll.getChildCount(); i++)
			{
				RadioButton radioButton = (RadioButton) radioGroupReRoll.getChildAt(i);
				if(radioButton.getText().toString().equals(fromPrefReRoll))
				{
					radioButton.setChecked(true);
					break;
				}
			}
		}
	}
	
	/**
	 * Distribuerer valgte settings til regler og LudoActivity.
	 * Lagrer også valgene i sharedpreferences
	 * @return true, dersom brett er valgt, eller false
	 */
	private boolean updateSettings() {
		
		if(chosenBoardFile==null)
		{
			return false;
		}

		SharedPreferences.Editor prefEditor = settings.edit(); 
		
		/** Board */
		prefEditor.putString((String) getResources().getText(R.string.sharedpreferences_ludoboardfile), chosenBoardFile); 
		prefEditor.putString((String) getResources().getText(R.string.sharedpreferences_ludoboardname), textViewBoard.getText().toString());
		GameHolder.getInstance().getRules().setLudoBoard(textViewBoard.getText().toString(), chosenBoardFile);
		
		/** TakeOff */
		RadioButton radioButtonTakeOff = (RadioButton)findViewById(radioGroupTakeOff.getCheckedRadioButtonId());
		int[] numbers = parseRadioButtonText(radioButtonTakeOff.getText());
		GameHolder.getInstance().getRules().setTakeOffNumbers(numbers);
		prefEditor.putString((String) getResources().getText(R.string.sharedpreferences_takeoff), radioButtonTakeOff.getText().toString());

		/** Antall forsøk */
		RadioButton radioButtonNoOfAttemts = (RadioButton)findViewById(radioGroupNoOfAttemts.getCheckedRadioButtonId());
		numbers = parseRadioButtonText(radioButtonNoOfAttemts.getText());
		GameHolder.getInstance().getRules().setNoOfAttemts(numbers[0]);
		prefEditor.putString((String) getResources().getText(R.string.sharedpreferences_noofattemts), radioButtonNoOfAttemts.getText().toString());
		
		/** Reroll */
		RadioButton radioButtonReRoll = (RadioButton)findViewById(radioGroupReRoll.getCheckedRadioButtonId());
		numbers = parseRadioButtonText(radioButtonReRoll.getText());
		GameHolder.getInstance().getRules().setReRollNumbers(numbers);
		prefEditor.putString((String) getResources().getText(R.string.sharedpreferences_reroll), radioButtonReRoll.getText().toString());		

		prefEditor.commit();
		
		
		
		//TEST AV JSON
		//String jSonString = GameHolder.getInstance().getRules().getSettings();
		//GameHolder.getInstance().getRules().setSettings(jSonString);
		
		return true;
	}

	/**
	 * Hjelpemetode som parser valg til tall
	 * @param cs, gjeldende valg fra radiobutton (f.eks. "2,4,6", eller "6")
	 * @return tabell med int
	 */
	private int[] parseRadioButtonText(CharSequence cs)
	{
		int[] retVal = null;
		String tmp = cs.toString();
		if(tmp.contains(","))
		{
			String[] numbers = tmp.split(",");
			retVal = new int[numbers.length];
			for(int i=0;i<numbers.length; i++)
			{
				retVal[i]=Integer.valueOf(numbers[i]);
			}
		}
		else
		{
			retVal = new int[1];
			retVal[0] = Integer.valueOf(tmp);
		}
		return retVal;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if ((keyCode == KeyEvent.KEYCODE_BACK)) 
		{
	        //Log.d(this.getClass().getName(), "back button pressed");
			this.finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}
}