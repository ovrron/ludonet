package com.ronny.ludo.board;

// Sjekk ut 
/*
 * svn checkout http://snowservices.googlecode.com/svn/trunk/ snowservices-read-only
 * http://www.youtube.com/watch?v=7-62tRHLcHk&feature=player_embedded
 * svn checkout http://awesomeguy.googlecode.com/svn/trunk/ awesomeguy-read-only
 * 
 * http://www.youtube.com/watch?v=N6YdwzAvwOA&feature=player_embedded
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageButton;

import com.ronny.ludo.R;
import com.ronny.ludo.helper.LudoConstants;
import com.ronny.ludo.model.Die;
import com.ronny.ludo.model.Game;
import com.ronny.ludo.model.IPiece;
import com.ronny.ludo.model.Coordinate;
import com.ronny.ludo.model.IPlayer;
import com.ronny.ludo.model.PlayerColor;

public class LudoSurfaceView extends SurfaceView implements
		SurfaceHolder.Callback {

	private float lastTwoXMoves[] = new float[2];
	private float lastTwoYMoves[] = new float[2];
	private int moveHistorySize = 0;
	private int current_X = 0; // Dette er offset i X-retning for bildet i forhold til det vi ser på skjerm
	private int current_Y = 0; // Dette er offset i Y-retning for bildet i forhold til det vi ser på skjerm
	private int minX, maxX;
	private int minY, maxY;
	private int boardImageX, boardImageY; // bredde, høyde på bildet
	private SurfaceHolder holder;
	private Bitmap backgroundImage;
	// private Bitmap knapper;
	// private Drawable knapperDrawable;
	private float currentScale = 1.0f;

	// private ImageView knappView;

	private static String TAG = "SurfView";

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

				Log.d("TouchEvent", "--------- Action MOVE " + event.getX()
						+ ", " + event.getY() + " : min:" + minX + ", " + minY
						+ "   current:" + current_X + ", " + current_Y);

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
			Log.d("TouchEvent", "--------- Action DOWN " + event.getX() + ", "
					+ event.getY());
			moveHistorySize = 1;
		} else if ((event.getAction() == MotionEvent.ACTION_UP)
				&& (moveHistorySize >= 1)) {
			Log.d("TouchEvent", "--------- Action up - redraw " + current_X
					+ ", " + current_Y);
			Log.d("TouchEvent", "--------- ACTION_UP - redraw " + current_X + ", " + current_Y);
            boolean flyttetOK = handleMove(event.getX(), event.getY());
            Log.d("TouchEvent", "----------------- " + flyttetOK + " ----------------------------------");
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
     * Sjekker om et flytt skal hÃ¥ndteres og utfÃ¸rer dette.
     */
   
   private boolean handleMove(float currentX, float currentY) {

        boolean ret = false;
        Log.d("TouchEvent getBrikke", "currentX " + currentX + ", currentY " + currentY);
        Log.d("TouchEvent getBrikke", "current_X " + current_X + ", current_Y " + current_Y);
        Log.d("TouchEvent getBrikke", "boardImageX " + boardImageX + ", boardImageY " + boardImageY);
        
        Game.getInstance().getPlayerInfo(PlayerColor.RED).DumpGame();
        
        // Finn x på board - legg til offset
        double currentXBoard = (double) (-1*current_X) + currentX/currentScale;
        // Finn y på board - legg til offset
        double currentYBoard = (double) (-1*current_Y) + currentY/currentScale;
        
        double delta = 0.08 * boardImageX;
        Log.d("TouchEvent getBrikke", "currentXBoard " + currentXBoard + ", currentYBoard " + currentYBoard + ", delta " + delta);
        
        
        IPiece pp = Game.getInstance().getPieceNearPos((int) currentXBoard, (int) currentYBoard, delta);
        
        
        //TEST FLYTT
        if(pp != null) {
        	boolean bo = pp.canPieceMove(5);
        	if(bo) {
        		Coordinate cc = pp.getPositionAtBoardPosition(5);
        		Game.getInstance().playerMove(pp.getColor(), pp.getHousePosition(), 5);
        		debugRedrawBoard();
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
        c.drawCircle((int)currentXBoard, (int)currentYBoard, (int)delta, foodPaint);
        holder.unlockCanvasAndPost(c);
        // ****************************************** DEBUG END		 

//        ret = Game.getInstance().handleMove((int) currentXBoard, (int) currentYBoard, delta);
        return ret;
    }

	/**
	 * Sjekke pan limits for å se om disse er forenelig med definert
	 * skalering/størrelse
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
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
				"POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN
				|| actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid ").append(
					action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
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

	private int screenWidth;
	private int screenHeight;

	private DrawingThread mThread = null;

	@SuppressWarnings("unused")
	private ImageButton zoomInButton;
	@SuppressWarnings("unused")
	private ImageButton zoomOutButton;

	public LudoSurfaceView(Context context) {
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

		// setFocusable(true); // make sure we get key events
		// setOnTouchListener(metroListener);
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
		String boardName = Game.getInstance().getGameImageName();
		int boardId = getResources().getIdentifier(boardName, "drawable",
				"com.ronny.ludo");

		backgroundImage = BitmapFactory.decodeResource(getResources(), boardId);
		// backgroundImage = Bitmap.createBitmap(backgroundImage, 0, 0,
		// backgroundImage.getWidth(), backgroundImage.getHeight(),
		// matrix, true);

		boardImageX = backgroundImage.getWidth();
		boardImageY = backgroundImage.getHeight();
		Game.getInstance().getLudoBoard()
				.setGraphicsResolution(boardImageX, boardImageY);

		Log.d(TAG, "Load image : " + boardImageX + ", " + boardImageY);
		// FÃ¸rst nÃ¥r vi vet stÃ¸rrelsen pÃ¥ bildet kan vi re-kalkulere...
		Game.getInstance().getLudoBoard().recalcPositions();

		// knapperDrawable = getResources().getDrawable(R.drawable.b4);
	}

	// private OnTouchListener metroListener = new OnTouchListener() {
	// public boolean onTouch(View v, MotionEvent event) {
	// onTouchEvent(event);
	// return true;
	// }
	// };

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
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
	 * Set scale for Ã¥ se hele brettet.
	 */
	public void setScaleFullBoard() {

		currentScale = Math.min((float) screenWidth / (float) boardImageX,
				(float) screenHeight / (float) boardImageY);
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
		//mThread.start();

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

		// Tegn b4 pÃ¥ canvas
		// knapperDrawable.setBounds(current_X + 100, current_Y + 100,
		// current_X + 150, current_Y + 150);
		// knapperDrawable.draw(canvas);

	}

	/**
	 * Plasser player buttons
	 */
	private void placePlayerButtons(Canvas canvas) {
		// Way home
		for (PlayerColor pc : PlayerColor.values()) {
			IPlayer p = Game.getInstance().getLudoBoard().getPlayer(pc);

			// for(Coordinate co : p.getHomePositions()) {
			// plotPoint(canvas, co.x, co.y);
			// }

			for (IPiece brikke : p.getBrikker()) {
				plotBrikke(canvas, brikke);
			}

		}

	}

	private void plotBrikke(Canvas c, IPiece b) {
		// IPiece farge
		String str = b.getId();
		int knappeid = getResources().getIdentifier(str, "drawable",
				"com.ronny.ludo");
		Bitmap brik = BitmapFactory.decodeResource(getResources(), knappeid);
		int w = brik.getWidth() / 2;
		Drawable dr = getResources().getDrawable(knappeid);
		Coordinate co = b.getCurrentPosition();
		int x = co.x;
		int y = co.y;
		// Rect bnds = dr.getBounds();
		dr.setBounds(current_X + x - w, current_Y + y - w, current_X + x + w,
				current_Y + y + w);
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

	@SuppressWarnings("unused")
	public void debug(int teller) {
		// PlayerColor c = PlayerColor.BLUE;
		// PlayerColor c = PlayerColor.YELLOW;
		// PlayerColor c = PlayerColor.GREEN;
		// PlayerColor c = PlayerColor.RED;
		if (true) {
			if (teller == 0) {
				Game.getInstance().playerMove(PlayerColor.RED, 0,
						LudoConstants.MOVE_FROM_HOUSE);
				Game.getInstance().playerMove(PlayerColor.GREEN, 0,
						LudoConstants.MOVE_FROM_HOUSE);
				Game.getInstance().playerMove(PlayerColor.YELLOW, 0,
						LudoConstants.MOVE_FROM_HOUSE);
				Game.getInstance().playerMove(PlayerColor.BLUE, 0,
						LudoConstants.MOVE_FROM_HOUSE);
			}

			if (teller > 0) {
				Game.getInstance().playerMove(PlayerColor.RED, 0, 1);
				Game.getInstance().playerMove(PlayerColor.GREEN, 0, 1);
				Game.getInstance().playerMove(PlayerColor.YELLOW, 0, 1);
				Game.getInstance().playerMove(PlayerColor.BLUE, 0, 1);
			}
		}

		if (false) {
			if (teller == 0) {
				Game.getInstance().playerMove(PlayerColor.RED, 0,
						LudoConstants.MOVE_FROM_HOUSE);
				Game.getInstance().playerMove(PlayerColor.GREEN, 0,
						LudoConstants.MOVE_FROM_HOUSE);
				Game.getInstance().playerMove(PlayerColor.YELLOW, 0,
						LudoConstants.MOVE_FROM_HOUSE);
				Game.getInstance().playerMove(PlayerColor.BLUE, 0,
						LudoConstants.MOVE_FROM_HOUSE);
			}

			if (teller > 0) {
				Game.getInstance().playerMove(PlayerColor.RED, 0,
						Game.getInstance().rollDie());
				Game.getInstance().playerMove(PlayerColor.GREEN, 0,
						Game.getInstance().rollDie());
				Game.getInstance().playerMove(PlayerColor.YELLOW, 0,
						Game.getInstance().rollDie());
				Game.getInstance().playerMove(PlayerColor.BLUE, 0,
						Game.getInstance().rollDie());
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

}
