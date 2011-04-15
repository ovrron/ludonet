package com.ronny.ludo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

/**
 * 
 * @author ovrron
 *
 */
public class LudoChooseGameActivity extends Activity 
{
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choosegame);
        initButtonListeners();
    }
    
    /**
     * 
     */
    private void initButtonListeners()
    {
    	Button buttonStartNewGame = (Button) findViewById(R.id.buttonStartNewGame);
    	buttonStartNewGame.setOnClickListener(new OnClickListener()
		{
			
			public void onClick(View v)
			{
				Intent intent = new Intent(v.getContext(), LudoStartNewGameActivity.class);
				
				startActivity(intent);
				//startActivity(new Intent(LudoStartActivity.this, LudoActivity.class));
			}
		});
    
    	Button buttonJoinGame = (Button) findViewById(R.id.buttonJoinGame);
    	buttonJoinGame.setOnClickListener(new OnClickListener()
		{
			
			public void onClick(View v)
			{
				Intent intent = new Intent(v.getContext(), LudoJoinGameActivity.class);
				
				startActivity(intent);
				//startActivity(new Intent(LudoStartActivity.this, LudoActivity.class));
			}
		});

    }
    
}
