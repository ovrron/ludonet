package com.ronny.ludo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ronny.ludo.board.LudoSurfaceView;
import com.ronny.ludo.helper.ParseBoardDefinitionHelper;
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
		
		//Henter valgt bord fra settings dersom server
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
	}
	
	public void setCurrentPlayer(PlayerColor color)
	{
		//Kanskje vise location også?
		ImageView imageCurrentPlayer = (ImageView) findViewById(R.id.imagePlayerCurrent);
		int id = getResources().getIdentifier("player_" + color.toString().toLowerCase(), "drawable", "com.ronny.ludo");
		imageCurrentPlayer.setBackgroundResource(id);
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
	    				surface.setThrow(eyes);
	    				surface.setPickingPiece(true);
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