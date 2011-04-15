package com.ronny.ludo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ronny.ludo.helper.IPAddressHelper;

/**
 * 
 * @author ovrron
 *
 */
public class LudoStartNewGameActivity extends Activity 
{
	private String ipAddressCurrent = null;
	private EditText editTextIP = null;
	private Button buttonPlayGame = null;
	private Button buttonInvite = null;
//	private TextView textViewIP = null;
	private ImageView imageViewPlayerRed = null;
	private ImageView imageViewPlayerGreen = null;
	private ImageView imageViewPlayerYellow = null;
	private ImageView imageViewPlayerBlue = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startnewgame);
        init();
    }
    
    /**
     * 
     */
    private void init()
    {
//    	textViewIP = (TextView) findViewById(R.id.textIp);
    	editTextIP = (EditText) findViewById(R.id.editTextIP);
    	buttonInvite = (Button) findViewById(R.id.buttonIP);
    	buttonPlayGame = (Button) findViewById(R.id.buttonPlayGame);
    	imageViewPlayerRed = (ImageView) findViewById(R.id.imageViewPlayerRed);
    	imageViewPlayerGreen = (ImageView) findViewById(R.id.imageViewPlayerGreen);
    	imageViewPlayerYellow = (ImageView) findViewById(R.id.imageViewPlayerYellow);
    	imageViewPlayerBlue = (ImageView) findViewById(R.id.imageViewPlayerBlue);
 		ipAddressCurrent = new IPAddressHelper().getLocalIpAddress();
 		if(ipAddressCurrent==null)
    	{
 			editTextIP.setText(getResources().getString(R.string.start_edittext_no_network));
    		buttonInvite.setEnabled(false);
    		buttonPlayGame.setEnabled(false);
    		stopPlayerFrameAnimation(imageViewPlayerRed);
    	}
    	else
    	{
    		editTextIP.setText(ipAddressCurrent);
    		buttonInvite.setEnabled(true);
    		buttonPlayGame.setEnabled(true);
    		startPlayerFrameAnimation(imageViewPlayerRed);
    		//imageViewPlayerRed.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.player_red_check));
    	}
 		editTextIP.setEnabled(false);
// 		buttonInvite.setText(getResources().getString(R.string.start_button_ip_server));
// 		textViewIP.setText(getResources().getString(R.string.start_tekst_ip_server));
    	
    	
    	initButtonListeners();
    	//configureGuiElements();
    	//initEditTextListener();
    }
    
    /**
     * 
     */
//    private void initRadioButtonListener()
//    {
//
//    	RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroupGameType);
//    	rg.setOnCheckedChangeListener(new OnCheckedChangeListener() 
//    	{
//    		public void onCheckedChanged (RadioGroup group, int checkedId)
//    		{
//    			configureGuiElements();
//    		}
//    	});
//    }

    /**
     * 
     */
//    private void configureGuiElements()
//    {
//    	if(radioButtonHostGame.isChecked())
//    	{
//     		ipAddressCurrent = new IPAddressHelper().getLocalIpAddress();
//     		if(ipAddressCurrent==null)
//        	{
//     			editTextIP.setText(getResources().getString(R.string.start_edittext_no_network));
//        		buttonInviteJoin.setEnabled(false);
//        		buttonStartGame.setEnabled(false);
//        		stopPlayerFrameAnimation(imageViewPlayerRed);
//        	}
//        	else
//        	{
//        		editTextIP.setText(ipAddressCurrent);
//        		buttonInviteJoin.setEnabled(true);
//        		buttonStartGame.setEnabled(true);
//        		startPlayerFrameAnimation(imageViewPlayerRed);
//        		//imageViewPlayerRed.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.player_red_check));
//        	}
//     		editTextIP.setEnabled(false);
//     		buttonInviteJoin.setText(getResources().getString(R.string.start_button_ip_server));
//     		textViewIP.setText(getResources().getString(R.string.start_tekst_ip_server));
//    	}
//		else
//		{
//     		buttonInviteJoin.setText(getResources().getString(R.string.start_button_ip_client));
//     		textViewIP.setText(getResources().getString(R.string.start_tekst_ip_client));
//     		editTextIP.setText(ipAddressJoin);
//     		editTextIP.setEnabled(true);
//    		if(ipAddressCurrent==null)
//    		{
//				buttonInviteJoin.setEnabled(false);
//	    		buttonStartGame.setEnabled(false);
//    		}
//    		else
//    		{
//				buttonInviteJoin.setEnabled(true);
//    		}
//    		stopPlayerFrameAnimation(imageViewPlayerRed);
//		}
//    }
    
    /**
     * 
     */
//    private void initEditTextListener()
//    {
//    	editTextIP.setOnKeyListener(new OnKeyListener()
//    	{
//
//			//@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event)
//			{
//				ipAddressJoin = editTextIP.getText().toString();
//				return false;
//			}
//    		
//    	});
//    }
    
    /**
     * 
     */
    private void initButtonListeners()
    {
    	if(ipAddressCurrent==null)
    	{

    	}    	
    	
    	buttonPlayGame.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Intent ludoIntent = new Intent(v.getContext(), LudoActivity.class);
				startActivity(ludoIntent);
				//startActivity(new Intent(LudoStartActivity.this, LudoActivity.class));
				
			}
		});
    
    	buttonInvite.setOnClickListener(new OnClickListener()
    	{
			public void onClick(View v)
			{
				//Send invitasjon
				//TODO legg inn tekst som skal sendes i strings.xml
				//TODO lage mulighet for en venneliste med telefonnr som man kan velge fra
				//TODO kanskje mulighet for å sende epost også
				String msgText = ipAddressCurrent + " "+ getResources().getString(R.string.start_invite_text);
		    	//Intent msg = new Intent(Intent.ACTION_SEND);
		    	//msg.setType("text/plain");
		    	//msg.putExtra("sms_body", msgText);
		    	//msg.putExtra(Intent.EXTRA_TEXT, "email body");
		    	//msg.putExtra(Intent.EXTRA_SUBJECT, "email subject");
		        //startActivity(msg);		
		        //startActivity(Intent.createChooser(msg, "Velg sms"));
		        
		        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		        sendIntent.putExtra("sms_body", msgText);
//		        String tlfno = "90177797, 99001155, 1234678";
//		        sendIntent.putExtra("address", tlfno);
		        sendIntent.setType("vnd.android-dir/mms-sms");
		        startActivity(sendIntent);
			}
		});
    }
    
    /**
     * 
     * @param players, tabell med imageviews som representerer spillere
     */
    private void startPlayerFrameAnimation(ImageView... players)
    {
    	for(ImageView player:players)
    	{
    		player.setBackgroundResource(R.drawable.playeranim);
        	final AnimationDrawable frameAnimation = (AnimationDrawable) player.getBackground();
            player.post(new Runnable()
            {
 //   			@Override
    			public void run()
    			{
    				frameAnimation.start();
    			}  		        
            });
    		
    	}
	}
    
    /**
     * 
     * @param players, tabell med imageviews som representerer spillere
     */
    private void stopPlayerFrameAnimation(ImageView... players)
    {
       	for(ImageView player:players)
    	{
       		player.setBackgroundDrawable(null);
//       		AnimationDrawable frameAnimation = (AnimationDrawable) player.getBackground();
//       		frameAnimation.stop();
    	}
    }
}
