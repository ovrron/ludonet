package com.ronny.ludo;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ronny.ludo.helper.IPAddressHelper;

/**
 * 
 * @author ovrron
 *
 */
public class LudoStartNewGameActivity extends Activity 
{
	/**
	 * 
	 * @author ovrron
	 * Hjelpeklasse for å teste ut valgene på spillerne
	 * Må vel ta i bruk en framtidig turnmanager isteden kanskje
	 *
	 */
	private class Player
	{
		private PlayerState playerState;
		private ImageButton button;
		
		public Player(PlayerState playerState, ImageButton button)
		{
			this.playerState = playerState;
			this.button = button;
		}
		
		//Troggle mellom de forskjellige statene
		public void setNextState()
		{
			boolean connected = false;
			playerState = playerState.nextState();
			if(playerState.compareTo(PlayerState.LOCAL)==0) connected = true;
			setPlayerState(playerState, button);
			setConnected(connected);
			
		}
		
		//Lokal er alltid connected. Dersom ekstern spiller er tilkoplet må også denne kalles
		public void setConnected(boolean connected)
		{
			if(connected)
			{
				startPlayerFrameAnimation(button);
			}
			else
			{
				stopPlayerFrameAnimation(button);
			}
		}
	}
	
	/**
	 * 
	 * @author ovrron
	 * 
	 */
	private enum PlayerState 
	{
		FREE, LOCAL, REMOTE;
		public PlayerState nextState()
		{
			if(this.compareTo(FREE)==0)
			{
				return LOCAL;
			}
			else if(this.compareTo(LOCAL)==0)
			{
				return REMOTE;
			}
			else
			{
				return FREE;
			}
		}
	};
	private String ipAddressCurrent = null;
	private EditText editTextIP = null;
	private Button buttonPlayGame = null;
	private Button buttonInvite = null;
	private ImageButton imageButtonPlayerRed = null;
	private ImageButton imageButtonPlayerGreen = null;
	private ImageButton imageButtonPlayerYellow = null;
	private ImageButton imageButtonPlayerBlue = null;
	private Player playerRed = null;
	private Player playerGreen = null;
	private Player playerYellow = null;
	private Player playerBlue = null;
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
    	editTextIP = (EditText) findViewById(R.id.editTextIP);
    	buttonInvite = (Button) findViewById(R.id.buttonIP);
    	buttonPlayGame = (Button) findViewById(R.id.buttonPlayGame);
    	imageButtonPlayerRed = (ImageButton) findViewById(R.id.imageButtonPlayerRed);
    	imageButtonPlayerGreen = (ImageButton) findViewById(R.id.imageButtonPlayerGreen);
    	imageButtonPlayerYellow = (ImageButton) findViewById(R.id.imageButtonPlayerYellow);
    	imageButtonPlayerBlue = (ImageButton) findViewById(R.id.imageButtonPlayerBlue);
    	playerRed = new Player(PlayerState.LOCAL, imageButtonPlayerRed);
    	playerGreen = new Player(PlayerState.FREE, imageButtonPlayerGreen);
    	playerYellow = new Player(PlayerState.FREE, imageButtonPlayerYellow);
    	playerBlue = new Player(PlayerState.FREE, imageButtonPlayerBlue);
    	
 		ipAddressCurrent = new IPAddressHelper().getLocalIpAddress();
 		if(ipAddressCurrent==null)
    	{
 			editTextIP.setText(getResources().getString(R.string.start_edittext_no_network));
    		buttonInvite.setEnabled(false);
    		buttonPlayGame.setEnabled(false);
    		playerRed.setConnected(false);
    	}
    	else
    	{
    		editTextIP.setText(ipAddressCurrent);
    		buttonInvite.setEnabled(true);
    		buttonPlayGame.setEnabled(true);
    		playerRed.setConnected(true);
    	}
 		editTextIP.setEnabled(false);
 		initImageButtonListeners();
    	initButtonListeners();
    	
    }
    
    
    private void initImageButtonListeners()
    {
    	//,imageButtonPlayerRed.setOnClickListener(imageButtonListener);
    	imageButtonPlayerGreen.setOnClickListener(new OnClickListener() 
    	{
    		public void onClick(View v) 
    		{
    			playerGreen.setNextState();
    		}
    	});
    	imageButtonPlayerYellow.setOnClickListener(new OnClickListener() 
    	{
    		public void onClick(View v) 
    		{
    			playerYellow.setNextState();
    		}
    	});
    	imageButtonPlayerBlue.setOnClickListener(new OnClickListener() 
    	{
    		public void onClick(View v) 
    		{
    			playerBlue.setNextState();
    		}
    	});
    }
    
    /**
     * 
     */
    private void initButtonListeners()
    {
    	buttonPlayGame.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Intent ludoIntent = new Intent(v.getContext(), LudoActivity.class);
				startActivity(ludoIntent);
				
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
    public void startPlayerFrameAnimation(ImageButton... players)
    {
    	for(ImageButton player:players)
    	{
    		player.setBackgroundResource(R.drawable.playeranim);
        	final AnimationDrawable frameAnimation = (AnimationDrawable) player.getBackground();
            player.post(new Runnable()
            {
            	@Override
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
    public void stopPlayerFrameAnimation(ImageButton... players)
    {
       	for(ImageButton player:players)
    	{
       		player.setBackgroundDrawable(null);
//       		AnimationDrawable frameAnimation = (AnimationDrawable) player.getBackground();
//       		frameAnimation.stop();
    	}
    }

    private PlayerState getPlayerState(ImageButton player)
    {
    	Drawable d = player.getDrawable();
    	if(d.equals(getResources().getDrawable(R.drawable.player_red_local))||d.equals(getResources().getDrawable(R.drawable.player_green_local))||d.equals(getResources().getDrawable(R.drawable.player_yellow_local))||d.equals(getResources().getDrawable(R.drawable.player_blue_local)))
    	{
    		return PlayerState.LOCAL;
    	}
    	else if(d.equals(getResources().getDrawable(R.drawable.player_red_remote))||d.equals(getResources().getDrawable(R.drawable.player_green_remote))||d.equals(getResources().getDrawable(R.drawable.player_yellow_remote))||d.equals(getResources().getDrawable(R.drawable.player_blue_remote)))
    	{
    		return PlayerState.REMOTE;
    	}
    	else
    	{
    		return PlayerState.FREE;
    	}
    	
    }
    
    private int getPlayerResID(PlayerState playerState, ImageButton player)
    {
    	Resources res = getResources();
   		
    	final String PLAYER ="player_";
   		String playerName = null;
   		String state = null;
   		
   		switch (playerState)
		{
			case FREE:
	   			state = "_unchecked";			
				break;

			case LOCAL:
				state = "_local";		
				break;
	
			case REMOTE:
				state = "_remote";
				break;

			default:
				state = null;
				break;
		}
   			
   		switch (player.getId())
		{
			case R.id.imageButtonPlayerRed:
				playerName = "red";
				break;

			case R.id.imageButtonPlayerGreen:
				playerName = "green";
				break;

			case R.id.imageButtonPlayerYellow:
				playerName = "yellow";
				break;

			case R.id.imageButtonPlayerBlue:
				playerName = "blue";
				break;

			default:
				playerName = null;
				break;
		}
   		
   		try
   		{
   			return res.getIdentifier(PLAYER+playerName+state, "drawable", "com.ronny.ludo");
   		}
   		catch(Exception e)
   		{
   			return 0;
   		}
    }
    
    /**
     * 
     * @param players, tabell med imageviews som representerer spillere
     */
    public void setPlayerState(PlayerState playerState, ImageButton... players)
    {
       	for(ImageButton player:players)
    	{
       		int resID = getPlayerResID(playerState, player);
       		try
       		{
       			Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), resID);
	        	player.setImageBitmap(imageBitmap);
       		}
       		catch(Exception e)
       		{
       			
       		}
    	}    	
    }
}

