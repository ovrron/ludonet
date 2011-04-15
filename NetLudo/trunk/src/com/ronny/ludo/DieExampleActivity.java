package com.ronny.ludo;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

import com.ronny.ludo.model.Die;


public class DieExampleActivity extends _SLETT_LudoCommonActivity 
{

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dieexample);
		final Die die = new Die();
		final ImageButton imgButtonDie = (ImageButton) findViewById(R.id.imageButtonDie);
		imgButtonDie.setOnClickListener(new OnClickListener()
		{
			
			public void onClick(View v)
			{
				imgButtonDie.setImageBitmap(null);
				final int eyes = die.roll();
				//int imageResourceId = getResources().getIdentifier("die"+eyes, "drawable", "com.ronny.ludo");
				int animationId = getResources().getIdentifier("die"+eyes+"anim", "drawable", "com.ronny.ludo");
				imgButtonDie.clearAnimation();
				imgButtonDie.setBackgroundResource(animationId);
	        	final AnimationDrawable frameAnimation = (AnimationDrawable) imgButtonDie.getBackground();
	        	imgButtonDie.post(new Runnable()
	            {
	    			public void run()
	    			{
	    				MediaPlayer mp;
	    				if(eyes==6)
	    				{
	    					 mp = MediaPlayer.create(getBaseContext(),R.raw.shake_and_roll_6);
	    				}
	    				else
	    				{
	    					 mp = MediaPlayer.create(getBaseContext(),R.raw.shake_and_roll);
	    				}
	    		        mp.start();
	    				frameAnimation.start();
	    			}  		        
	            });
	        	//imgButtonDie.setBackgroundDrawable(null);
				//imgButtonDie.setImageResource(imageResourceId);
			}
		});
		
	}

	
	

}