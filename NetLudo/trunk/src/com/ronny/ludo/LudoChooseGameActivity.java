package com.ronny.ludo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ronny.ludo.communication.LudoMessageBroker;
import com.ronny.ludo.communication.TeamMessageMgr;
import com.ronny.ludo.model.GameHolder;
import com.ronny.ludo.model.TurnManager;

/**
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

		// Create server object - always done
		GameHolder.getInstance().setMessageManager(new TeamMessageMgr());
		// Create message broker - always done 
		GameHolder.getInstance().setMessageBroker(new LudoMessageBroker(GameHolder.getInstance().getMessageManager()));
		// Create Turn Manager - always done
		GameHolder.getInstance().setTurnManager(new TurnManager());
		startAnimation();
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
				Intent intent = new Intent(v.getContext(), LudoSettingsActivity.class);
				
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
