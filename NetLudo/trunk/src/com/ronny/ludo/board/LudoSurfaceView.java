package com.ronny.ludo.board;

// Sjekk ut 
/*
 * svn checkout http://snowservices.googlecode.com/svn/trunk/ snowservices-read-only
 * http://www.youtube.com/watch?v=7-62tRHLcHk&feature=player_embedded
 * svn checkout http://awesomeguy.googlecode.com/svn/trunk/ awesomeguy-read-only
 * 
 * http://www.youtube.com/watch?v=N6YdwzAvwOA&feature=player_embedded
 */
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageButton;

import com.ronny.ludo.ErrDialog;
import com.ronny.ludo.LudoActivity;
import com.ronny.ludo.R;
import com.ronny.ludo.communication.LudoMessageBroker;
import com.ronny.ludo.helper.LudoConstants;
import com.ronny.ludo.helper.SoundPlayer;
import com.ronny.ludo.model.Coordinate;
import com.ronny.ludo.model.GameHolder;
import com.ronny.ludo.model.IPiece;
import com.ronny.ludo.model.IPlayer;
import com.ronny.ludo.model.PlayerColor;

public class LudoSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	int brikSize = 0;
	private float lastTwoXMoves[] = new float[2];
	private float lastTwoYMoves[] = new float[2];
	private int moveHistorySize = 0;
	private int current_X = 0; // Dette er offset i X-retning for bildet i
								// forhold til det vi ser på skjerm
	private int current_Y = 0; // Dette er offset i Y-retning for bildet i
								// forhold til det vi ser på skjerm
	private int minX, maxX;
	private int minY, maxY;
	private int boardImageX, boardImageY; // bredde, høyde på bildet
	private SurfaceHolder holder;
	private Bitmap backgroundImage;
	private float currentScale = 1.0f;

	private boolean pickingPiece = false;
	private PlayerColor pickingColor = PlayerColor.NONE;

	private static String TAG = "SurfView";
	private int currentThrow = 0;
	private int noOfThrows = 0;

	private LudoActivity parentActivity = null;

	private PlayerColor currentPlayer = PlayerColor.NONE;

	private int screenWidth;
	private int screenHeight;

	private DrawingThread mThread = null;

	@SuppressWarnings("unused")
	private ImageButton zoomInButton;
	@SuppressWarnings("unused")
	private ImageButton zoomOutButton;

	private Handler brokerMessages;
	private SoundPlayer soundPlayer = null;

	private LudoSurfaceView(Context context) {
		super(context);

	}

	public LudoSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.d(TAG, "SurfView");

		// Load board image - image name is placed in Game class
		loadImage();

		holder = getHolder();
		holder.addCallback(this);
		mThread = new DrawingThread(holder, this);

		zoomInButton = (ImageButton) findViewById(R.id.zoomIn);
		zoomOutButton = (ImageButton) findViewById(R.id.zoomOut);

		GameHolder.getInstance().setSurfaceView(this);
		soundPlayer = new SoundPlayer(context);

		// Create server handle - client messages from server
		brokerMessages = new Handler() {
			/*
			 * This is the message handler which receives messages from the
			 * TeamMessageManager. Messages about colors allocated should be
			 * notified all users.
			 * 
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				String message = (String) msg.obj;
				// final String[] messageParts = message.split("\\,");
				// final String[] messageParts =
				// message.split(LudoMessageBroker.SPLITTER);
				final String[] messageParts = message.split(LudoMessageBroker.SPLITTER);
				
				// For debug
//				final String[] messageParts = "G;M;GREEN;3;3;GREEN;1;RED;2".split(LudoMessageBroker.SPLITTER);

				Log.d("SW:handleMessage", "In msg: " + message);
				if (messageParts[0].equals("G")) { // Game messages
					if (messageParts[1].equals("T")) {
						PlayerColor plc = PlayerColor.getColorFromString(messageParts[2]);
						//Hvis lokal farge er vises terningen allerede
						if(!GameHolder.getInstance().getLocalClientColor().contains(plc)){
							int eyes = Integer.parseInt(messageParts[3]);
							setDie(eyes);
						}
					}
					else if (messageParts[1].equals("M")) { // Move
						PlayerColor plc = PlayerColor.getColorFromString(messageParts[2]);
						int theBrikke = Integer.parseInt(messageParts[3]);
						int theMove = Integer.parseInt(messageParts[4]);
						
						//Hvis lokal farge er allerede flyttet foretatt i LudoMessageBroker.playerMove(...)
						if(!GameHolder.getInstance().getLocalClientColor().contains(plc)){
							GameHolder.getInstance().getGame().playerMove(plc, theBrikke, theMove);
						}

						// Melding om at noen er slått ut
						if (messageParts.length == 9) {
							PlayerColor kicker = PlayerColor.getColorFromString(messageParts[5]);
							PlayerColor kicked = PlayerColor.getColorFromString(messageParts[7]);
							// Jeg slår ut
							if (GameHolder.getInstance().getLocalClientColor().contains(kicker)) {
								soundPlayer.playSound(SoundPlayer.TOHOUSEGOOD);
							}
							// Jeg blir slått ut
							else if (GameHolder.getInstance().getLocalClientColor().contains(kicked)) {
								soundPlayer.playSound(SoundPlayer.TOHOUSEBAD);
							}
							// Jeg er ikke involvert 
							else {
								soundPlayer.playSound(SoundPlayer.MOVE);
							}
						}
						// Vanlig flytt
						else {
							soundPlayer.playSound(SoundPlayer.MOVE);
						}
						reDraw();
					} 
					
					//Ny spiller
					else if (messageParts[1].equals("CP")) {
						PlayerColor plc = PlayerColor.getColorFromString(messageParts[2]);
						initNewPlayer(plc);
					}
					else if(messageParts[1].equals("W")) {
						PlayerColor plc = PlayerColor.getColorFromString(messageParts[2]);
						parentActivity.setWinnerPlayer(plc, GameHolder.getInstance().getLocalClientColor().contains(plc));
					}
				}
			}
		};

		GameHolder.getInstance().getMessageBroker().addListener(brokerMessages);

		// setFocusable(true); // make sure we get key events
		// setOnTouchListener(metroListener);
	}

	public void reDraw() {
		Canvas c = holder.lockCanvas(null);
		onDraw(c);
		holder.unlockCanvasAndPost(c);
	}

	public void setParentActivity(Activity parentActivity) {
		this.parentActivity = (LudoActivity) parentActivity;
		// Her må vi vite hvilken farge som starter. Må tenke litt på den
		initNewPlayer(GameHolder.getInstance().getTurnManager().getCurrentPlayerColor());
	}

	private void whatNow() {
		// Sjekk om vi skal gå til neste spiller eller fortsette med denne spilleren.
		// Reroll eller vunnet
		
	    
//      if (teller == 0) {
//          GameHolder.getInstance().getGame().playerMove(PlayerColor.RED, 0, 49);
//          GameHolder.getInstance().getGame().playerMove(PlayerColor.RED, 1, 48);
//          GameHolder.getInstance().getGame().playerMove(PlayerColor.RED, 2, 47);
//          GameHolder.getInstance().getGame().playerMove(PlayerColor.RED, 3, 46);
//          GameHolder.getInstance().getGame().playerMove(PlayerColor.GREEN, 0, 49);
//          GameHolder.getInstance().getGame().playerMove(PlayerColor.GREEN, 1, 48);
//          GameHolder.getInstance().getGame().playerMove(PlayerColor.GREEN, 2, 47);
//          GameHolder.getInstance().getGame().playerMove(PlayerColor.GREEN, 3, 46);
//          teller = 1;
//      }
//      
//      
              
		// TODO Få inn håndtering av vinner inkl regler for når en vinner kan
		// kåres.
		if (GameHolder.getInstance().getGame().getPlayerInfo(currentPlayer).isAtGoal()) {
			GameHolder.getInstance().getMessageBroker().sendWinnerPlayer(currentPlayer);
		} 
		else if (GameHolder.getInstance().getRules().canPlayerReRoll(currentThrow)) {
			parentActivity.resetDie();
		}
		// Spiller har ingen brikker i spill
		else if (!GameHolder.getInstance().getGame().getPlayerInfo(currentPlayer).hasPiecesInPlay()) 
		{
			// Har spiller brukt opp sine forsøk
			if (GameHolder.getInstance().getRules().hasPlayerMoreAttemts(noOfThrows)) {
				parentActivity.resetDie();
			}
			// Ingen flere forsøk
			else {
				GameHolder.getInstance().getMessageBroker().sendGimmeNextPlayer();
				// PlayerColor nextPlayer =
				// GameHolder.getInstance().getTurnManager().advanceToNextPlayer();
				// parentActivity.setCurrentPlayer(GameHolder.getInstance().getTurnManager().advanceToNextPlayer());
				// initNewPlayer(nextPlayer); //vet ikke om vi skal kalle denne
				// direkte
			}
		}
		// Har brikker i spill
		else 
		{
			GameHolder.getInstance().getMessageBroker().sendGimmeNextPlayer();
			// PlayerColor nextPlayer =
			// GameHolder.getInstance().getTurnManager().advanceToNextPlayer();
			// parentActivity.setCurrentPlayer(GameHolder.getInstance().getTurnManager().advanceToNextPlayer());
			// initNewPlayer(nextPlayer); //vet ikke om vi skal kalle denne
			// direkte
		}
	}

	private void initNewPlayer(PlayerColor currentPlayer) {
		this.currentPlayer = currentPlayer;
		if (GameHolder.getInstance().getLocalClientColor().contains(currentPlayer)) {
			noOfThrows = 0;
			this.parentActivity.resetDie();
		}
		this.parentActivity.setCurrentPlayer(currentPlayer);
	}

	public void setDie(int eyes) {
		parentActivity.setDie(eyes);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Touch");
		dumpEvent(event);

		if ((event.getAction() == MotionEvent.ACTION_MOVE)) {
			moveHistorySize++;
			lastTwoXMoves[1] = event.getX();
			lastTwoYMoves[1] = event.getY();

			if (moveHistorySize >= 2) {
				current_X += (lastTwoXMoves[1] - lastTwoXMoves[0]);
				current_Y += (lastTwoYMoves[1] - lastTwoYMoves[0]);

				Log.d("TouchEvent", "--------- Action MOVE " + event.getX() + ", " + event.getY() + " : min:" + minX
						+ ", " + minY + "   current:" + current_X + ", " + current_Y);

				// ((TextView)findViewById(R.id.debug)).setText("Action MOVE " +
				// event.getX() + ", "
				// + event.getY() + " : min:" + minX + ", "+minY + "   current:"
				// + current_X + ", "+current_Y);
				checkViewLimits();

				Canvas c = holder.lockCanvas(null);
				synchronized (holder) {
					onDraw(c);
				}
				holder.unlockCanvasAndPost(c);

				lastTwoXMoves[0] = lastTwoXMoves[1];
				lastTwoYMoves[0] = lastTwoYMoves[1];

			}
		} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			lastTwoXMoves[0] = event.getX();
			lastTwoYMoves[0] = event.getY();
			Log.d("TouchEvent", "--------- Action DOWN " + event.getX() + ", " + event.getY());
			moveHistorySize = 1;
		} else if ((event.getAction() == MotionEvent.ACTION_UP) && (moveHistorySize >= 1)) {
			Log.d("TouchEvent", "--------- Action up - redraw " + current_X + ", " + current_Y);
			Log.d("TouchEvent", "--------- ACTION_UP - redraw " + current_X + ", " + current_Y);

			// Check if we are picking a piece to move
			if (pickingPiece) {
				// Check if we got something.
				// pickingColor is the current piece color which can be moved
				boolean flyttetOK = handleMove(event.getX(), event.getY());
				Log.d("TouchEvent", "----------------- " + flyttetOK + " ----------------------------------");
			}
		}

		return true;
	}

	// Utility for debugging - redraw brett
	private void debugRedrawBoard() {
		Canvas c = holder.lockCanvas(null);
		Paint foodPaint = new Paint();
		foodPaint.setColor(Color.BLACK);
		synchronized (holder) {
			onDraw(c);
		}
		holder.unlockCanvasAndPost(c);
	}

	/**
	 * Sjekker om et flytt skal håndteres og utfører dette.
	 */

	private boolean handleMove(float currentX, float currentY) {

		boolean ret = false;
		Log.d("TouchEvent getBrikke", "currentX " + currentX + ", currentY " + currentY);
		Log.d("TouchEvent getBrikke", "current_X " + current_X + ", current_Y " + current_Y);
		Log.d("TouchEvent getBrikke", "boardImageX " + boardImageX + ", boardImageY " + boardImageY);

		GameHolder.getInstance().getGame().getPlayerInfo(PlayerColor.RED).DumpGame();

		// Finn x på board - legg til offset
		double currentXBoard = (double) (-1 * current_X) + currentX / currentScale;
		// Finn y på board - legg til offset
		double currentYBoard = (double) (-1 * current_Y) + currentY / currentScale;

		double delta = 0.08 * boardImageX;
		delta = brikSize * currentScale;
		Log.d("TouchEvent getBrikke", "currentXBoard " + currentXBoard + ", currentYBoard " + currentYBoard
				+ ", delta " + delta);

		IPiece pp = GameHolder.getInstance().getGame()
				.getPieceNearPos(currentPlayer, (int) currentXBoard, (int) currentYBoard, delta);

		// TEST FLYTT
		if (pp != null) {

			boolean bo = pp.canPieceMove(currentThrow);
			if (bo) {
				// Coordinate cc = pp.getPositionAtBoardPosition(currentThrow);

				GameHolder.getInstance().getMessageBroker()
						.playerMove(pp.getColor(), pp.getHousePosition(), currentThrow);

				// GameHolder.getInstance().getMessageBroker().distributeMessage("G,M,"+
				// currentPlayer + "," + currentThrow);

				// Dette skal vel egentlig håndteres i messageBroker?
				// GameHolder.getInstance().getGame().playerMove(pp.getColor(),
				// pp.getHousePosition(), currentThrow);
				debugRedrawBoard();
//				 parentActivity.resetDie(); //

				// Fjern highlighting
				IPlayer currentPlayer = GameHolder.getInstance().getGame().getPlayerInfo(pp.getColor());
				for (IPiece piece : currentPlayer.getBrikker()) 
				{
					piece.highLight(false);
				}
 //				 parentActivity.setCurrentPlayer(GameHolder.getInstance().getTurnManager().advanceToNextPlayer());
				whatNow();
			}
        	else
            {
                if(!canMoove())
                {
                    whatNow();
                }
            }
        }
        
		// ******************************************DEBUG
		// Plot på skjerm
		currentXBoard = currentX / currentScale;
		currentYBoard = currentY / currentScale;

		Canvas c = holder.lockCanvas(null);
		Paint foodPaint = new Paint();
		foodPaint.setColor(Color.BLACK);
		synchronized (holder) {
			onDraw(c);
		}
		c.drawCircle((int) currentXBoard, (int) currentYBoard, (int) delta, foodPaint);
		holder.unlockCanvasAndPost(c);
		// ****************************************** DEBUG END

		// ret = Game.getInstance().handleMove((int) currentXBoard, (int)
		// currentYBoard, delta);
		return ret;
	}

	/**
	 * Sjekke pan limits for � se om disse er forenelig med definert
	 * skalering/st�rrelse
	 */
	private void checkViewLimits() {
		if (current_X < minX) {
			current_X = minX;
		}
		if (current_X > maxX) {
			current_X = maxX;
		}
		if (current_Y < minY) {
			current_Y = minY;
		}
		if (current_Y > maxY) {
			current_Y = maxY;
		}
	}

	/** Show an event in the LogCat view, for debugging */
	private void dumpEvent(MotionEvent event) {
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}
		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";");
		}
		sb.append("]");
		Log.d("TouchEvent", sb.toString());
	}

	// private void loadAndScaleImage() {
	// // se
	// //
	// http://stackoverflow.com/questions/2078768/resolution-independence-in-android-surfaceview
	// // BitmapFactory.Options options = new BitmapFactory.Options();
	// // options.outHeight = (int) (900 * currentScale);
	// // options.outWidth = (int) (900 * currentScale);
	// // backgroundImage =
	// // BitmapFactory.decodeResource(getResources(),R.drawable.ludoboard,
	// // options);
	// // boardImageX = backgroundImage.getWidth();
	// // boardImageY = backgroundImage.getHeight();
	//
	// // Se
	// //
	// http://android-er.blogspot.com/2010/07/scale-bitmap-image-using-matrix.html
	// Matrix matrix = new Matrix();
	// matrix.postScale(1.0f, 1.0f);
	// backgroundImage = BitmapFactory.decodeResource(getResources(),
	// R.drawable.ludoboard);
	// backgroundImage = Bitmap.createBitmap(backgroundImage, 0, 0,
	// backgroundImage.getWidth(), backgroundImage.getHeight(),
	// matrix, true);
	//
	// boardImageX = backgroundImage.getWidth();
	// boardImageY = backgroundImage.getHeight();
	//
	// // knapperDrawable = getResources().getDrawable(R.drawable.b4);
	// // Bitmap bb = BitmapFactory.decodeResource(getResources(),
	// R.drawable.b4);
	// // knapper = Bitmap.createBitmap(bb, 0, 0, bb.getWidth(), bb.getHeight(),
	// // matrix, true);
	//
	// // Lage knappView
	// // knappView = new ImageView(getContext());
	// // knappView.setImageResource(R.drawable.y4);
	// // knappView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
	// // LayoutParams.WRAP_CONTENT));
	// // ((SurfaceView)findViewById(R.id.surfView)).addTouchables(knappView);
	// }

	private void loadImage() {
		// backgroundImage = BitmapFactory.decodeResource(getResources(),
		// R.drawable.ludoboard);

		Matrix matrix = new Matrix();
		matrix.postScale(1.0f, 1.0f);

		// Hent navnet
		String boardName = GameHolder.getInstance().getGame().getGameImageName();
		int boardId = getResources().getIdentifier(boardName, "drawable", "com.ronny.ludo");

		backgroundImage = BitmapFactory.decodeResource(getResources(), boardId);
		// backgroundImage = Bitmap.createBitmap(backgroundImage, 0, 0,
		// backgroundImage.getWidth(), backgroundImage.getHeight(),
		// matrix, true);

		boardImageX = backgroundImage.getWidth();
		boardImageY = backgroundImage.getHeight();
		GameHolder.getInstance().getGame().getLudoBoard().setGraphicsResolution(boardImageX, boardImageY);

		Log.d(TAG, "Load image : " + boardImageX + ", " + boardImageY);
		// Først når vi vet størrelsen på bildet kan vi re-kalkulere...
		GameHolder.getInstance().getGame().getLudoBoard().recalcPositions();

		// knapperDrawable = getResources().getDrawable(R.drawable.b4);
	}

	// private OnTouchListener metroListener = new OnTouchListener() {
	// public boolean onTouch(View v, MotionEvent event) {
	// onTouchEvent(event);
	// return true;
	// }
	// };

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		screenWidth = width;
		screenHeight = height;

		// Litt hips om happ her..
		currentScale = (float) screenHeight / (float) boardImageY;
		// currentScale = (float) screenWidth / (float) boardImageX;

		recomputeMovementLimits();
		Canvas c = holder.lockCanvas(null);
		synchronized (holder) {
			onDraw(c);
		}
		holder.unlockCanvasAndPost(c);
		Log.d(TAG, "surfaceChanged");

	}

	/**
	 * Set scale for å se hele brettet.
	 */
	public void setScaleFullBoard() {

		currentScale = Math.min((float) screenWidth / (float) boardImageX, (float) screenHeight / (float) boardImageY);
		recomputeMovementLimits();
		Canvas c = holder.lockCanvas(null);
		synchronized (holder) {
			onDraw(c);
		}
		holder.unlockCanvasAndPost(c);
	}

	/**
	 * Skalering har skjedd - finn verdier for max pan
	 */
	private void recomputeMovementLimits() {
		minX = (int) (screenWidth / currentScale) - boardImageX;
		minY = (int) (screenHeight / currentScale) - boardImageY;

		maxX = 0;
		maxY = 0;

		checkViewLimits();
	}

	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.d(TAG, "surfaceCreated");
		mThread.setRunning(true);
		Log.d(TAG, "Starting drawThread");
		// TODO STARTER IKKE THREAD HER - dette er for debug tegning
		// mThread.start();

		// Tegn initielt bilde
		Canvas c = holder.lockCanvas(null);
		// synchronized (holder) {
		onDraw(c);
		// }
		holder.unlockCanvasAndPost(c);
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		boolean retry = true;
		mThread.setRunning(false);
		mThread.interrupt();

		while (retry) {
			try {
				mThread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}

	}

	@Override
	public void onDraw(Canvas canvas) {
		if (!mThread.isPaused()) {
		}
		Log.d(TAG, "onDraw");
		canvas.drawColor(Color.BLACK);
		// Tegn bilde...
		Matrix mx = new Matrix();
		mx.postScale(currentScale, currentScale);
		canvas.setMatrix(mx);
		canvas.drawBitmap(backgroundImage, current_X, current_Y, null);

		// Test denne
		// canvas.translate(50, 50);

		// plotPoints(canvas);
		// plotPoint(canvas, 0, 0);
		// plotPoint(canvas, boardImageX, boardImageY);

		placePlayerButtons(canvas);

		// Tegn b4 på canvas
		// knapperDrawable.setBounds(current_X + 100, current_Y + 100,
		// current_X + 150, current_Y + 150);
		// knapperDrawable.draw(canvas);
	}

	/**
	 * Plasser player buttons
	 */
	private void placePlayerButtons(Canvas canvas) {
		// Way home
		// for (PlayerColor pc : PlayerColor.values()) {
		for (PlayerColor pc : GameHolder.getInstance().getTurnManager().getPlayers()) {
			if (pc != PlayerColor.NONE) {
				// TODO KUN legg ut brikker for spillere som er med i spillet
				IPlayer p = GameHolder.getInstance().getGame().getLudoBoard().getPlayer(pc);

				// for(Coordinate co : p.getHomePositions()) {
				// plotPoint(canvas, co.x, co.y);
				// }
				// Tegner kun brikker for spillere som er med
				if (!GameHolder.getInstance().getTurnManager().isFree(p.getColor())) {
					for (IPiece brikke : p.getBrikker()) {
						if (brikke.isEnabled()) {
							plotBrikke(canvas, brikke);

						}
					}
				}
			}
		}
	}

	private void plotBrikke(Canvas c, IPiece b) {
		// IPiece farge
		String str = b.getId();
		int knappeid = getResources().getIdentifier(str, "drawable", "com.ronny.ludo");
		Bitmap brik = BitmapFactory.decodeResource(getResources(), knappeid);
		int w = brik.getWidth() / 2;
		brikSize = brik.getWidth();
		Drawable dr = getResources().getDrawable(knappeid);
		Coordinate co = b.getCurrentPosition();
		int x = co.x;
		int y = co.y;
		// Rect bnds = dr.getBounds();
		dr.setBounds(current_X + x - w, current_Y + y - w, current_X + x + w, current_Y + y + w);

		// TEST
		if (b.highLight()) {
			// TESTBILDE
			// Drawable highlightDrawable =
			// getResources().getDrawable(R.drawable.playeranim);
			// highlightDrawable.setBounds(current_X + x - w*2, current_Y + y -
			// w*2, current_X + x + w*2, current_Y + y + w*2);
			// final AnimationDrawable frameAnimation = (AnimationDrawable)
			// highlightDrawable;
			// highlightDrawable.draw(c);
			// frameAnimation.start();

			// AnimationDrawable highlightAnimationDrawable =
			// (AnimationDrawable)
			// getResources().getDrawable(R.drawable.playeranim);
			// highlightAnimationDrawable.setBounds(current_X + x - w*2,
			// current_Y + y - w*2, current_X + x + w*2, current_Y + y + w*2);
			// highlightAnimationDrawable.draw(c);

			Bitmap highlightBitMap = BitmapFactory.decodeResource(getResources(), R.drawable.highlightpiece);
			int with = highlightBitMap.getWidth();// / 2;
			Drawable highlightDrawable = getResources().getDrawable(R.drawable.highlightpiece);
			highlightDrawable.setBounds(current_X + x - with, current_Y + y - with, current_X + x + with, current_Y + y
					+ with);
			highlightDrawable.draw(c);
		}
		// SLUTT TEST

		dr.draw(c);
		// Paint foodPaint = new Paint();
		// foodPaint.setColor(Color.CYAN);
		// c.drawCircle(current_X + x, current_Y + y, 15, foodPaint);
	}

	private void plotPoint(Canvas c, int x, int y) {
		Paint foodPaint = new Paint();
		foodPaint.setColor(Color.CYAN);
		c.drawCircle(current_X + x, current_Y + y, 15, foodPaint);
	}

	// Debug metode
	@SuppressWarnings("unused")
	private void plotPoints(Canvas c) {
		// - delta 56

		plotPoint(c, 393, 787);
		plotPoint(c, 393, 731);
		plotPoint(c, 393, 675);
		plotPoint(c, 393, 619);
		plotPoint(c, 393, 563);
		plotPoint(c, 337, 505);
		plotPoint(c, 281, 505);
		plotPoint(c, 225, 505);
		plotPoint(c, 169, 505);
		plotPoint(c, 113, 505);
		plotPoint(c, 57, 505);
		plotPoint(c, 57, 449);
		plotPoint(c, 57, 393);
		plotPoint(c, 57, 393);
		plotPoint(c, 113, 393);
		plotPoint(c, 169, 393);
		plotPoint(c, 225, 393);
		plotPoint(c, 281, 393);
		plotPoint(c, 337, 393);
		plotPoint(c, 393, 337);
		plotPoint(c, 393, 281);
		plotPoint(c, 393, 225);
		plotPoint(c, 393, 169);
		plotPoint(c, 393, 113);
		plotPoint(c, 393, 57);
		plotPoint(c, 449, 57);
		plotPoint(c, 505, 57);
		plotPoint(c, 505, 57);
		plotPoint(c, 505, 113);
		plotPoint(c, 505, 169);
		plotPoint(c, 505, 225);
		plotPoint(c, 505, 281);
		plotPoint(c, 505, 337);
		plotPoint(c, 562, 393);
		plotPoint(c, 618, 393);
		plotPoint(c, 674, 393);
		plotPoint(c, 730, 393);
		plotPoint(c, 786, 393);
		plotPoint(c, 842, 393);
		plotPoint(c, 842, 449);
		plotPoint(c, 842, 505);
		plotPoint(c, 786, 505);
		plotPoint(c, 730, 505);
		plotPoint(c, 674, 505);
		plotPoint(c, 618, 505);
		plotPoint(c, 562, 505);
		plotPoint(c, 506, 562);
		plotPoint(c, 506, 618);
		plotPoint(c, 506, 674);
		plotPoint(c, 506, 730);
		plotPoint(c, 506, 786);
		plotPoint(c, 506, 842);
		plotPoint(c, 449, 842);
		plotPoint(c, 393, 842);

		plotPoint(c, 143, 750);
		plotPoint(c, 143, 653);
		plotPoint(c, 249, 653);
		plotPoint(c, 249, 750);

		plotPoint(c, 249, 147);
		plotPoint(c, 249, 246);
		plotPoint(c, 143, 147);
		plotPoint(c, 143, 246);

		plotPoint(c, 756, 147);
		plotPoint(c, 756, 246);
		plotPoint(c, 649, 147);
		plotPoint(c, 649, 246);

		plotPoint(c, 756, 750);
		plotPoint(c, 756, 653);
		plotPoint(c, 649, 653);
		plotPoint(c, 649, 750);

		// Homes
		plotPoint(c, 449, 787);
		plotPoint(c, 449, 731);
		plotPoint(c, 449, 675);
		plotPoint(c, 449, 619);
		plotPoint(c, 449, 563);

		plotPoint(c, 449, 113);
		plotPoint(c, 449, 169);
		plotPoint(c, 449, 225);
		plotPoint(c, 449, 281);
		plotPoint(c, 449, 337);

		plotPoint(c, 113, 449);
		plotPoint(c, 169, 449);
		plotPoint(c, 225, 449);
		plotPoint(c, 281, 449);
		plotPoint(c, 337, 449);

		plotPoint(c, 787, 449);
		plotPoint(c, 731, 449);
		plotPoint(c, 675, 449);
		plotPoint(c, 619, 449);
		plotPoint(c, 563, 449);

		// Goal
		plotPoint(c, 450, 506);
		plotPoint(c, 393, 451);
		plotPoint(c, 449, 393);
		plotPoint(c, 507, 449);

	}

	public void zoomIn() {
		currentScale += 0.2;
		if (currentScale > 3.5) {
			currentScale = 3.5f;
		}
		recomputeMovementLimits();
		Canvas c = holder.lockCanvas(null);
		synchronized (holder) {
			onDraw(c);
		}
		holder.unlockCanvasAndPost(c);

	}

	public void zoomOut() {
		currentScale -= 0.2;
		if (currentScale < 0.5) {
			currentScale = 0.5f;
		}
		recomputeMovementLimits();
		Canvas c = holder.lockCanvas(null);
		synchronized (holder) {
			onDraw(c);
		}
		holder.unlockCanvasAndPost(c);

	}

	public boolean setThrow(int eyes) {
		currentThrow = eyes;
		noOfThrows++;
		GameHolder.getInstance().getMessageBroker().dieThrowed(currentPlayer, currentThrow);
		// GameHolder.getInstance().getMessageBroker().distributeMessage("G,T,"+
		// currentPlayer + "," + currentThrow);
		if (!canMoove()) {
			whatNow();
			return false;
		} else {
			Canvas c = holder.lockCanvas(null);
			onDraw(c);
			holder.unlockCanvasAndPost(c);
			return true;
		}
	}

	private boolean canMoove() 
	{
		boolean retVal = false;
		for (PlayerColor pc : GameHolder.getInstance().getLocalClientColor()) 
		{
			if (pc == currentPlayer) 
			{
				IPlayer currentPlayer = GameHolder.getInstance().getGame().getPlayerInfo(pc);
				for (IPiece piece : currentPlayer.getBrikker()) 
				{
				    if (piece.isEnabled()){
    					if(GameHolder.getInstance().getRules().isLegalMove(piece, currentThrow))
    					{
    						piece.highLight(true);
    						retVal = true;
    					}
				    }
				}
			}
		}
		return retVal;
	}

	@SuppressWarnings("unused")
	public void debug(int teller) {
		// PlayerColor c = PlayerColor.BLUE;
		// PlayerColor c = PlayerColor.YELLOW;
		// PlayerColor c = PlayerColor.GREEN;
		// PlayerColor c = PlayerColor.RED;
		if (true) {
			if (teller == 0) {
				GameHolder.getInstance().getGame().playerMove(PlayerColor.RED, 0, LudoConstants.MOVE_FROM_HOUSE);
				GameHolder.getInstance().getGame().playerMove(PlayerColor.GREEN, 0, LudoConstants.MOVE_FROM_HOUSE);
				GameHolder.getInstance().getGame().playerMove(PlayerColor.YELLOW, 0, LudoConstants.MOVE_FROM_HOUSE);
				GameHolder.getInstance().getGame().playerMove(PlayerColor.BLUE, 0, LudoConstants.MOVE_FROM_HOUSE);
			}

			if (teller > 0) {
				GameHolder.getInstance().getGame().playerMove(PlayerColor.RED, 0, 1);
				GameHolder.getInstance().getGame().playerMove(PlayerColor.GREEN, 0, 1);
				GameHolder.getInstance().getGame().playerMove(PlayerColor.YELLOW, 0, 1);
				GameHolder.getInstance().getGame().playerMove(PlayerColor.BLUE, 0, 1);
			}
		}

		if (false) {
			if (teller == 0) {
				GameHolder.getInstance().getGame().playerMove(PlayerColor.RED, 0, LudoConstants.MOVE_FROM_HOUSE);
				GameHolder.getInstance().getGame().playerMove(PlayerColor.GREEN, 0, LudoConstants.MOVE_FROM_HOUSE);
				GameHolder.getInstance().getGame().playerMove(PlayerColor.YELLOW, 0, LudoConstants.MOVE_FROM_HOUSE);
				GameHolder.getInstance().getGame().playerMove(PlayerColor.BLUE, 0, LudoConstants.MOVE_FROM_HOUSE);
			}

			if (teller > 0) {
				GameHolder.getInstance().getGame()
						.playerMove(PlayerColor.RED, 0, GameHolder.getInstance().getGame().rollDie());
				GameHolder.getInstance().getGame()
						.playerMove(PlayerColor.GREEN, 0, GameHolder.getInstance().getGame().rollDie());
				GameHolder.getInstance().getGame()
						.playerMove(PlayerColor.YELLOW, 0, GameHolder.getInstance().getGame().rollDie());
				GameHolder.getInstance().getGame()
						.playerMove(PlayerColor.BLUE, 0, GameHolder.getInstance().getGame().rollDie());
			}

			if (teller > 30) {
				mThread.setRunning(false);
				try {
					mThread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
				}
			}
		}

		// if((teller>=1) && (teller<=7)) {
		// Game.getInstance().getLudoBoard().getPlayer(c).moveBrikke(0, 6);
		// } else if(teller>7) {
		// Game.getInstance().playerMove(c,0,1);
		// }
	}

	/**
	 * @return the pickingPiece
	 */
	public boolean isPickingPiece() {
		return pickingPiece;
	}

	/**
	 * @param pickingPiece
	 *            the pickingPiece to set
	 */
	public void setPickingPiece(boolean pickingPiece) {
		this.pickingPiece = pickingPiece;
	}

	/**
	 * @return the pickingColor
	 */
	public PlayerColor getPickingColor() {
		return pickingColor;
	}

	/**
	 * @param pickingColor
	 *            the pickingColor to set
	 */
	public void setPickingColor(PlayerColor pickingColor) {
		this.pickingColor = pickingColor;
	}
}
