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
        soundPlayer.PlaySound(SoundPlayer.SPLASH);
    }

    
    private void startAnimation()
    {
    	Animation animationLogo = AnimationUtils.loadAnimation(this, R.anim.logo);
    	logo = (ImageView) findViewById(R.id.imageViewLogo);
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
    
//    private void startAnimation()
//    {
//    	Animation lAnimation1 = AnimationUtils.loadAnimation(this, R.anim.logoletters_in1);
//    	Animation lAnimation2 = AnimationUtils.loadAnimation(this, R.anim.logoletters_in2);
//    	Animation lAnimation3 = AnimationUtils.loadAnimation(this, R.anim.logoletters_in3);
//    	Animation lAnimation4 = AnimationUtils.loadAnimation(this, R.anim.logoletters_in4);
//    	
//    	l = (ImageView) findViewById(R.id.imageViewL);
//    	u = (ImageView) findViewById(R.id.imageViewU);
//    	d = (ImageView) findViewById(R.id.imageViewD);
//    	o = (ImageView) findViewById(R.id.imageViewO);
//    	
//        l.setVisibility(ImageView.VISIBLE);
//        u.setVisibility(ImageView.VISIBLE);
//        d.setVisibility(ImageView.VISIBLE);
//        o.setVisibility(ImageView.VISIBLE);
//        
//        l.startAnimation(lAnimation1);
//        u.startAnimation(lAnimation2);
//        d.startAnimation(lAnimation3);
//        o.startAnimation(lAnimation4);
//        
//
//    
//        lAnimation1.setAnimationListener(new AnimationListener() {
//
//            public void onAnimationEnd(Animation animation) {
//                // The animation has ended, transition to the Main Menu screen
//            	startDieFrameAnimation(false);
//            	endAnimation();
//            }
//
//            public void onAnimationRepeat(Animation animation) {
//            }
//
//            public void onAnimationStart(Animation animation) {
//            }
//        });
//    }
    
//    private void endAnimation()
//    {
//    	Animation lAnimation1 = AnimationUtils.loadAnimation(this, R.anim.logoletters_out1);
//    	Animation lAnimation2 = AnimationUtils.loadAnimation(this, R.anim.logoletters_out2);
//    	Animation lAnimation3 = AnimationUtils.loadAnimation(this, R.anim.logoletters_out3);
//    	Animation lAnimation4 = AnimationUtils.loadAnimation(this, R.anim.logoletters_out4);
//    	
//        
//    	l.startAnimation(lAnimation1);
//    	u.startAnimation(lAnimation2);
//        d.startAnimation(lAnimation3);
//        o.startAnimation(lAnimation4);
//        
//        
//        lAnimation1.setAnimationListener(new AnimationListener() {
//
//            public void onAnimationEnd(Animation animation) {
//                // The animation has ended, transition to the Main Menu screen
//                die.setVisibility(ImageView.INVISIBLE);
//                l.setVisibility(ImageView.INVISIBLE);
//                u.setVisibility(ImageView.INVISIBLE);
//                d.setVisibility(ImageView.INVISIBLE);
//                o.setVisibility(ImageView.INVISIBLE);
//            	startActivity(new Intent(LudoSplashActivity.this, LudoStartNewGameActivity.class));
//            	LudoSplashActivity.this.finish();
//            }
//
//            public void onAnimationRepeat(Animation animation) {
//            }
//
//            public void onAnimationStart(Animation animation) {
//            }
//        });
//    }
    

//    private void startDieFrameAnimation(boolean in)
//    {
//        die = (ImageView) findViewById(R.id.imageViewAnim);
//        int res;
//        if(in)
//        {
//        	res = R.drawable.animdice_in;
//        }
//        else
//        {
//        	res = R.drawable.animdice_out;
//        }
//        die.setBackgroundResource(res);
//    	final AnimationDrawable frameAnimation = (AnimationDrawable) die.getBackground();
////        long frameAnmimationDuration=0;
////        for(int i = 0; i< frameAnimation.getNumberOfFrames();i++)
////        {
////        	frameAnmimationDuration += frameAnimation.getDuration(i);
////        }
//        die.post(new Runnable()
//        {
////			@Override
//			public void run()
//			{
//				frameAnimation.start();
//			}  		        
//        });
//	}

    
    @Override
    protected void onPause() {
        super.onPause();
        // Stop the animation
//        if(die!=null)
//        	die.clearAnimation();
//        if(l!=null)
//        	l.clearAnimation();
//        if(u!=null)
//        	u.clearAnimation();
//        if(d!=null)
//        	d.clearAnimation();
//        if(o!=null)
//        	o.clearAnimation();        
    }

    @Override
    protected void onResume() {
        super.onResume();
//        startAnimation();
    }
    
}
