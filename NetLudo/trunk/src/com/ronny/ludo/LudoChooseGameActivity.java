package com.ronny.ludo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.ronny.ludo.communication.LudoMessageBroker;
import com.ronny.ludo.communication.TeamMessageMgr;
import com.ronny.ludo.model.GameHolder;
import com.ronny.ludo.model.TurnManager;

/**
 * Copyright: (c) 2011 Ronny Heitmann Andersen, Ronny Øvereng and Karl-Erik Moberg
 * 
 * Activity for choosing type of game - be a server (start a game) or join a game.
 * 
 * @author ovrron
 * 
 */
public class LudoChooseGameActivity extends Activity {

	private ImageView o = null;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choosegame);
		initButtonListeners();
		startAnimation();
	}

	/**
     * Initierer knappene
     */
	private void initButtonListeners()
    {
    	Button buttonStartNewGame = (Button) findViewById(R.id.buttonStartNewGame);
    	buttonStartNewGame.setOnClickListener(new OnClickListener()
		{
			
			public void onClick(View v)
			{
				Intent intent = new Intent(v.getContext(), LudoSettingsActivity.class);
				startActivity(intent);
			}
		});
    
    	Button buttonJoinGame = (Button) findViewById(R.id.buttonJoinGame);
    	buttonJoinGame.setOnClickListener(new OnClickListener()
		{
			
			public void onClick(View v)
			{
				Intent intent = new Intent(v.getContext(), LudoJoinGameActivity.class);
				startActivity(intent);
			}
		});
    }
	
	/**
	 * Starter animasjon av bokstaven O
	 */
    private void startAnimation()
    {
    	Animation animationLogo = AnimationUtils.loadAnimation(this, R.anim.rotate);
    	o = (ImageView) findViewById(R.id.imageViewO);
    	o.startAnimation(animationLogo);
    	animationLogo.setAnimationListener(new AnimationListener() 
    	{
    		public void onAnimationEnd(Animation animation) {
		    }
		    public void onAnimationRepeat(Animation animation) {
		    }
		    public void onAnimationStart(Animation animation) {
		    }
		});    	
    }
}
