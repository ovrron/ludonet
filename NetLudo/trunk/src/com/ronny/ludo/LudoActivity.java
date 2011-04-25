package com.ronny.ludo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.ronny.ludo.board.LudoSurfaceView;
import com.ronny.ludo.helper.ParseBoardDefinitionHelper;
import com.ronny.ludo.helper.SoundPlayer;
import com.ronny.ludo.model.Die;
import com.ronny.ludo.model.Game;
import com.ronny.ludo.model.GameHolder;
import com.ronny.ludo.model.PlayerColor;

public class LudoActivity extends Activity {
	private String TAG = "-Ludo-:";

	private ImageButton zoomInButton;
	private ImageButton zoomFitButton;
	private ImageButton zoomOutButton;
	private LudoSurfaceView surface;
	SharedPreferences settings = null;
	private SoundPlayer soundPlayer = null;

	// RHA
	// RelativeLayout rl2;

	// private enum Moves {
	// NONE, DRAG, ZOOM;
	// };

	// RHA
	// private Moves currentTouchtype = Moves.NONE;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		
		// HER SKAL VI HA VALGT ET GAME - SOM ER OPPRETTET TIDLIGERE....
		// VI LEGGER DET INN HER FOR Å FÅ TING TIL Å SNURRE
		GameHolder.getInstance().setGame(new Game());


		// Load board definisjoner - lastes før inflating.
		ParseBoardDefinitionHelper ph = new ParseBoardDefinitionHelper();
		
		//Henter valgt bord fra settings
		//TODO håndter feil
		String boardFile = GameHolder.getInstance().getRules().getLudoBoardFile();
		int iidd = getResources().getIdentifier(boardFile, "xml", "com.ronny.ludo");
		//parseXmlDefs();
		if(!ph.parseBoardDefinition(getResources().getXml(iidd))){
			//TODO Håndter feil ved lasting av brettdefinisjon
			//Vis feilmelding og ev. avslutt
		}
		
//		settings = getSharedPreferences((String) getResources().getText(R.string.sharedpreferences_name), MODE_PRIVATE);
//    	if (settings.contains((String) getResources().getText(R.string.sharedpreferences_ludoboardfile)) == true)
//    	{ 
//    		String boardFile = settings.getString((String) getResources().getText(R.string.sharedpreferences_ludoboardfile), null);
//    		int iidd = getResources().getIdentifier(boardFile, "xml", "com.ronny.ludo");
//    		//parseXmlDefs();
//    		if(!ph.parseBoardDefinition(getResources().getXml(iidd))){
//    			//TODO Håndter feil ved lasting av brettdefinisjon
//    			//Vis feilmelding og ev. avslutt
//    		}
//
//    	}
//		// TODO på lasting av board
//		Vector<String> boards = ph.parseBoardsAvailable(getResources().getXml(R.xml.boarddefinition));
//		int iidd = getResources().getIdentifier(boards.get(0), "xml", "com.ronny.ludo");
		
//		GameHolder.getInstance().getGame().DumpGame();
			
// DENNE ER FLYTTET SIDEN VI TRENGER Størrelsen på bildet før vi tar en recalc.		
//		Game.getInstance().getLudoBoard().recalcPositions();
		// End load board.

		GameHolder.getInstance().getGame().DumpGame();
		
		setContentView(R.layout.main);

		zoomInButton = (ImageButton) findViewById(R.id.zoomIn);
		zoomFitButton = (ImageButton) findViewById(R.id.zoomFit);
		zoomOutButton = (ImageButton) findViewById(R.id.zoomOut);
		
		initDie();
		initSoundButton();
		surface = (LudoSurfaceView) findViewById(R.id.image);
		surface.setParentActivity(this);

//		Vector<PlayerColor> activePlayers = GameHolder.getInstance().getTurnManager().getPlayers();
//		for(PlayerColor pc:activePlayers)
//		{
//			if(GameHolder.getInstance().getTurnManager().isLocal(pc))
//			{
//				surface.addPlayer(pc);
//			}
//		}
		
		zoomInButton.setOnClickListener(zoomInListener);
		zoomFitButton.setOnClickListener(zoomFitListener);
		zoomOutButton.setOnClickListener(zoomOutListener);
		
		
		//Dette må vi kun gjøre for current player
		//resetDie();
		
		//TEST
		// Test av mod
//		int start = 0;
//		int maxVal = 13;
//		
//		for(int j=0;j<20;j++) {
//			System.out.println("J: "+j+" - "+(start+j)%maxVal);
//		}
//
//		start = 7;
//		for(int j=0;j<20;j++) {
//			System.out.println("J: "+j+" - "+(start+j)%maxVal);
//		}
		//TEST END 

	}
	
	
	private OnClickListener zoomInListener = new OnClickListener() {
		public void onClick(View v) {
			Log.d(TAG, "Zoom in");
			surface.zoomIn();
		}
	};

	private OnClickListener zoomFitListener = new OnClickListener() {
		public void onClick(View v) {
			Log.d(TAG, "Zoom FIT");
			surface.setScaleFullBoard();
		}
	};

	private OnClickListener zoomOutListener = new OnClickListener() {
		public void onClick(View v) {
			Log.d(TAG, "Zoom out");
			surface.zoomOut();
		}
	};

	public void resetDie()
	{
		surface.setPickingPiece(false);
		ImageButton imgButtonDie = (ImageButton) findViewById(R.id.imageButtonDie);
		//imgButtonDie.setBackgroundResource(R.drawable.die);
		imgButtonDie.setEnabled(true);
		imgButtonDie.clearAnimation();
		imgButtonDie.setBackgroundResource(R.drawable.die_roll_anim);
    	final AnimationDrawable frameAnimation = (AnimationDrawable) imgButtonDie.getBackground();
    	imgButtonDie.post(new Runnable()
        {
			public void run()
			{
				frameAnimation.start();
			}  		        
        });
	}
	
	public void setDie(int eyes)
	{
		ImageButton imgButtonDie = (ImageButton) findViewById(R.id.imageButtonDie);
		int id = getResources().getIdentifier("die" + eyes, "drawable", "com.ronny.ludo");
		imgButtonDie.setBackgroundResource(id);
		imgButtonDie.setEnabled(false);
		surface.setPickingPiece(false);
	}
	
	public void setCurrentPlayer(PlayerColor color)
	{
		ImageView imageCurrentPlayer = (ImageView) findViewById(R.id.imagePlayerCurrent);
		int id = getResources().getIdentifier("player_" + color.toString().toLowerCase(), "drawable", "com.ronny.ludo");
		imageCurrentPlayer.setBackgroundResource(id);
	
		//TODO Kanskje her en idé å skille på om den er din tur eller en annen sin tur
		//Din tur, skrive at det er din tur
		//En annen sin tur, skrive at vi venter på  xxx
		if(GameHolder.getInstance().getTurnManager().getNumPlayers()>1)
		{
			Toast.makeText(getBaseContext(), color.toNorwegian() + " " + getResources().getText(R.string.game_toast_spillersintur), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void initSoundButton()
	{
		settings = getSharedPreferences((String) getResources().getText(R.string.sharedpreferences_name), MODE_PRIVATE);
		GameHolder.getInstance().setSoundOn(settings.getBoolean((String) getResources().getText(R.string.sharedpreferences_sound), true)); 
		final ImageButton imgButtonSound = (ImageButton) findViewById(R.id.imageButtonSound);
		if(GameHolder.getInstance().isSoundOn())
		{
			imgButtonSound.setImageDrawable(getResources().getDrawable(R.drawable.sound_on));
		}
		else
		{
			imgButtonSound.setImageDrawable(getResources().getDrawable(R.drawable.sound_off));
		}
		imgButtonSound.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				GameHolder.getInstance().setSoundOn(!GameHolder.getInstance().isSoundOn());
				if(GameHolder.getInstance().isSoundOn())
				{
					imgButtonSound.setImageDrawable(getResources().getDrawable(R.drawable.sound_on));
				}
				else
				{
					imgButtonSound.setImageDrawable(getResources().getDrawable(R.drawable.sound_off));
				}
				SharedPreferences.Editor prefEditor = settings.edit();
				prefEditor.putBoolean((String) getResources().getText(R.string.sharedpreferences_sound), GameHolder.getInstance().isSoundOn());
				prefEditor.commit();
			}
		});
	}
	
	private void initDie()
	{
		final Die die = new Die();
		final ImageButton imgButtonDie = (ImageButton) findViewById(R.id.imageButtonDie);
		imgButtonDie.setEnabled(false);
		imgButtonDie.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				imgButtonDie.setEnabled(false);
				imgButtonDie.setImageBitmap(null);
				final int eyes = die.roll();
				int animationId = getResources().getIdentifier("die"+eyes+"anim", "drawable", "com.ronny.ludo");
				imgButtonDie.clearAnimation();
				imgButtonDie.setBackgroundResource(animationId);
	        	final AnimationDrawable frameAnimation = (AnimationDrawable) imgButtonDie.getBackground();

	        	final Handler handler = new Handler();
	        	final Runnable runnable = new Runnable() {

	        	    public void run() {
	        	        handler.removeCallbacks(this);
	    				if (surface.setThrow(eyes))
	    				{
		    				surface.setPickingPiece(true);
	    				}
	    				else
	    				{
		    				if(soundPlayer==null)
		    				{
		    					soundPlayer = new SoundPlayer(getBaseContext());
		    				}
		    				soundPlayer.PlaySound(SoundPlayer.NO_LEGAL_MOVE);
	    			        Toast.makeText(getBaseContext(), R.string.game_toast_nolegalmoves, Toast.LENGTH_SHORT).show();
	    				}
	        	    }
	        	};
	        	
	        	imgButtonDie.post(new Runnable()
	            {
	    			public void run()
	    			{
	    				int soundDuration = 0;
	    				if(soundPlayer==null)
	    				{
	    					soundPlayer = new SoundPlayer(getBaseContext());
	    				}
	    				if(eyes==6)
	    				{
	    					soundDuration = soundPlayer.PlaySound(SoundPlayer.ROLL6);
	    				}
	    				else
	    				{
	    					soundDuration = soundPlayer.PlaySound(SoundPlayer.ROLL);
	    				}
	    				if(soundDuration==0)
	    				{
	    					soundDuration = soundPlayer.getDuration(SoundPlayer.ROLL);
	    				}
	    		        int duration = 0;
	    		        for(int i=0;i<frameAnimation.getNumberOfFrames();i++)
	    		        {
	    		        	duration =+ frameAnimation.getDuration(i);
	    		        }
	    		        if(duration < soundDuration)
	    		        {
		    		        duration = soundDuration;
	    		        }
	    				frameAnimation.start();
	    	        	handler.postDelayed(runnable, duration); //Put this where you start your animation
//	    				surface.setThrow(eyes);
//	    				surface.setPickingPiece(true);
	    			}  		        
	            });
			}
		});
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        //Log.d(this.getClass().getName(), "back button pressed");
			//TODO Disconnect other players
			GameHolder.getInstance().getMessageBroker().quitGame();
			this.finish();
	    }
	    return super.onKeyDown(keyCode, event);

	}
	
}