package com.ronny.ludo;


import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ronny.ludo.helper.ParseBoardDefinitionHelper;
import com.ronny.ludo.model.GameHolder;

public class LudoSettingsActivity extends Activity {
	private String TAG = "-Settings-:";
	private Vector<String> availableBoards = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);
		Button buttonNext = (Button)findViewById(R.id.buttonNext);
		buttonNext.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateSettings();
				Intent ludoIntent = new Intent(v.getContext(),LudoStartNewGameActivity.class);
				startActivity(ludoIntent);

			}
		});
		ParseBoardDefinitionHelper ph = new ParseBoardDefinitionHelper();
		availableBoards = ph.parseBoardsAvailable(getResources().getXml(R.xml.boarddefinition));
		
		
	}

	private void updateSettings() {
		//TakeOff
		RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioTakeOff);
		RadioButton radioButton = (RadioButton)findViewById(radioGroup.getCheckedRadioButtonId());
		int[] numbers = parseRadioButtonText(radioButton.getText());
		GameHolder.getInstance().getRules().setTakeOffNumbers(numbers);

		//Antall forsøk
		radioGroup = (RadioGroup)findViewById(R.id.radioNoOfAttemts);
		radioButton = (RadioButton)findViewById(radioGroup.getCheckedRadioButtonId());
		numbers = parseRadioButtonText(radioButton.getText());
		GameHolder.getInstance().getRules().setNoOfAttemts(numbers);
	
		//Reroll
		radioGroup = (RadioGroup)findViewById(R.id.radioReRoll);
		radioButton = (RadioButton)findViewById(radioGroup.getCheckedRadioButtonId());
		numbers = parseRadioButtonText(radioButton.getText());
		GameHolder.getInstance().getRules().setReRollNumbers(numbers);
		
		//Board
		//TODO
	}

	private int[] parseRadioButtonText(CharSequence cs)
	{
		int[] retVal = null;
		String tmp = cs.toString();
		if(tmp.contains(",")){
			String[] numbers = tmp.split(",");
			retVal = new int[numbers.length];
			for(int i=0;i<numbers.length; i++){
				retVal[i]=Integer.valueOf(numbers[i]);
			}
		}
		else{
			retVal = new int[0];
			retVal[0] = Integer.valueOf(tmp);
		}
		return retVal;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        //Log.d(this.getClass().getName(), "back button pressed");
			//TODO Disconnect other players
			this.finish();
	    }
	    return super.onKeyDown(keyCode, event);

	}
	
}