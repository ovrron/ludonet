package com.ronny.ludo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.ronny.ludo.helper.IPAddressHelper;

public class LudoStartActivity extends Activity 
{
	
	private String ipAddressCurrent = null;
	private String ipAddressJoin = null;
	private EditText editTextIP = null;
	private Button buttonStartGame = null;
	private Button buttonInviteJoin = null;
	private RadioButton radioButtonHostGame = null;
	private TextView textViewIP = null;
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
        setContentView(R.layout.start);
        init();
    }
    
    private void init()
    {
    	editTextIP = (EditText) findViewById(R.id.editTextIP);
    	ipAddressCurrent = new IPAddressHelper().getLocalIpAddress();
    	ipAddressJoin = getResources().getString(R.string.splash_edittext_ip);
    	buttonInviteJoin = (Button) findViewById(R.id.buttonIP);
    	buttonStartGame = (Button) findViewById(R.id.buttonStartGame);
    	radioButtonHostGame = (RadioButton) findViewById(R.id.radioHostGame);
    	imageViewPlayerRed = (ImageView) findViewById(R.id.imageViewPlayerRed);
    	imageViewPlayerGreen = (ImageView) findViewById(R.id.imageViewPlayerGreen);
    	imageViewPlayerYellow = (ImageView) findViewById(R.id.imageViewPlayerYellow);
    	imageViewPlayerBlue = (ImageView) findViewById(R.id.imageViewPlayerBlue);
    	
    	textViewIP = (TextView) findViewById(R.id.textIp); 
    	initRadioButtonListener();
    	initButtonListeners();
    	configureGuiElements();
    	initEditTextListener();
    }
    
    
    private void initRadioButtonListener()
    {

    	RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroupGameType);
    	rg.setOnCheckedChangeListener(new OnCheckedChangeListener() 
    	{
    		public void onCheckedChanged (RadioGroup group, int checkedId)
    		{
    			configureGuiElements();
    		}
    	});
    }

    private void configureGuiElements()
    {
    	if(radioButtonHostGame.isChecked())
    	{
     		ipAddressCurrent = new IPAddressHelper().getLocalIpAddress();
     		if(ipAddressCurrent==null)
        	{
     			editTextIP.setText(getResources().getString(R.string.splash_edittext_no_network));
        		buttonInviteJoin.setEnabled(false);
        		buttonStartGame.setEnabled(false);
        		stopPlayerFrameAnimation(imageViewPlayerRed);
        	}
        	else
        	{
        		editTextIP.setText(ipAddressCurrent);
        		buttonInviteJoin.setEnabled(true);
        		buttonStartGame.setEnabled(true);
        		startPlayerFrameAnimation(imageViewPlayerRed);
        		//imageViewPlayerRed.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.player_red_check));
        	}
     		editTextIP.setEnabled(false);
     		buttonInviteJoin.setText(getResources().getString(R.string.splash_button_ip_server));
     		textViewIP.setText(getResources().getString(R.string.splash_tekst_ip_server));
    	}
		else
		{
     		buttonInviteJoin.setText(getResources().getString(R.string.splash_button_ip_client));
     		textViewIP.setText(getResources().getString(R.string.splash_tekst_ip_client));
     		editTextIP.setText(ipAddressJoin);
     		editTextIP.setEnabled(true);
    		if(ipAddressCurrent==null)
    		{
				buttonInviteJoin.setEnabled(false);
	    		buttonStartGame.setEnabled(false);
    		}
    		else
    		{
				buttonInviteJoin.setEnabled(true);
    		}
    		stopPlayerFrameAnimation(imageViewPlayerRed);
		}
    }
    
    private void initEditTextListener()
    {
    	editTextIP.setOnKeyListener(new OnKeyListener()
    	{

			//@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				ipAddressJoin = editTextIP.getText().toString();
				return false;
			}
    		
    	});
    }
    
    private void initButtonListeners()
    {
    	if(ipAddressCurrent==null)
    	{

    	}    	
    	
    	buttonStartGame.setOnClickListener(new OnClickListener()
		{
			
			public void onClick(View v)
			{
				startActivity(new Intent(LudoStartActivity.this, LudoActivity.class));
				
			}
		});
    
    	buttonInviteJoin.setOnClickListener(new OnClickListener()
    	{
			public void onClick(View v)
			{
				//Send invitasjon
				if(radioButtonHostGame.isChecked())
				{
				
					//TODO legg inn tekst som skal sendes i strings.xml
					//TODO lage mulighet for en venneliste med telefonnr som man kan velge fra
					//TODO kanskje mulighet for å sende epost også
					String msgText = ipAddressCurrent + " "+ getResources().getString(R.string.splash_invite_text);
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
				
				//kople til et eksisterende spill
				else
				{
					//TODO
					
				}
			}
		});
    }
    
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
