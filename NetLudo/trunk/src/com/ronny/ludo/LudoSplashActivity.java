package com.ronny.ludo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class LudoSplashActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        startAnimating();
    }

    /**
     * Helper method to start the animation on the splash screen
     */
    private void startAnimating() {
        // Fade in top title
        TextView splashtext = (TextView) findViewById(R.id.textSplash);
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate_splashtext);
        splashtext.startAnimation(rotate);

        // Transition to Main Menu when bottom title finishes animating
        rotate.setAnimationListener(new AnimationListener() {

            public void onAnimationEnd(Animation animation) {
                // The animation has ended, transition to the Main Menu screen
                startActivity(new Intent(LudoSplashActivity.this, LudoStartActivity.class));
                LudoSplashActivity.this.finish();
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop the animation
        TextView splashtext = (TextView) findViewById(R.id.textSplash);
        splashtext.clearAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Start animating at the beginning so we get the full splash screen experience
        startAnimating();
    }

}
