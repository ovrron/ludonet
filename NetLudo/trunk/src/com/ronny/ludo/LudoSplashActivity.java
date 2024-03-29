package com.ronny.ludo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.ronny.ludo.helper.SoundPlayer;

/**
 * Copyright: (c) 2011 Ronny Heitmann Andersen, Ronny Øvereng and Karl-Erik Moberg
 * 
 * Splashscreen
 * 
 * @author ovrron
 * 
 */
public class LudoSplashActivity extends Activity {

	private ImageView logo;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        startAnimation();
 
        SoundPlayer soundPlayer = new SoundPlayer(this);
        soundPlayer.playSound(SoundPlayer.SPLASH);
    }
    
    /**
     * Starter animasjon
     */
    private void startAnimation() {
    	Animation animationLogo = AnimationUtils.loadAnimation(this, R.anim.logo);
    	logo = (ImageView)findViewById(R.id.imageViewLogo);
    	logo.startAnimation(animationLogo);
    	animationLogo.setAnimationListener(new AnimationListener() {
    	
    		public void onAnimationEnd(Animation animation) {
    			logo.setVisibility(ImageView.INVISIBLE);
    			startActivity(new Intent(LudoSplashActivity.this, LudoChooseGameActivity.class));
            	LudoSplashActivity.this.finish();
		    }
		
		    public void onAnimationRepeat(Animation animation) {
		    }
		
		    public void onAnimationStart(Animation animation) {
    			logo.setVisibility(ImageView.VISIBLE);
		    }
		});    	
    }

    
    @Override
    protected void onPause() {
        super.onPause();
        logo.clearAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }    
}
