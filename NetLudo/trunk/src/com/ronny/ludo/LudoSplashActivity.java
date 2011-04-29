package com.ronny.ludo;

import com.ronny.ludo.helper.SoundPlayer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

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
    
    private void startAnimation()
    {
    	Animation animationLogo = AnimationUtils.loadAnimation(this, R.anim.logo);
    	logo.startAnimation(animationLogo);
    	animationLogo.setAnimationListener(new AnimationListener() 
    	{
    	
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
//        startAnimation();
    }
    
}
