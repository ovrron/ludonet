package com.ronny.ludo;

import java.io.IOException;
import java.util.Timer;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ronny.ludo.board.Animation;
import com.ronny.ludo.board.AnimationCallBack;
import com.ronny.ludo.board.BoardView;
import com.ronny.ludo.board.SizeCallBack;
import com.ronny.ludo.model.Coordinate;
import com.ronny.ludo.model.Game;

public class LudoActivity extends LudoCommon {
	private String TAG = "-Ludo-:";
	private BoardView backgroundImageView;
	private ImageButton zoomInButton;
	private ImageButton zoomOutButton;
	private ImageButton bugButton;
	private Matrix matrix;
	private RectF sourceRect;
	private RectF destinationRect;
	private Bitmap bitmap;
	private Timer timer;
	private Animation animation;
	private Handler handle = new Handler();

	private int imageSizeX = 900;
	private int imageSizeY = 900;
	private static final float INITIAL_SCALE = (float) 1;
	private static final float MAGNIFY_SCALE = (float) 1.9;

	private float current_scale = INITIAL_SCALE;
	private int current_centerX = imageSizeX / 2;
	private int current_centerY = imageSizeY / 2;
	private int current_drawable = R.drawable.ludoboard;

	private int moveHistorySize;
	private float lastTwoXMoves[] = new float[2];
	private float lastTwoYMoves[] = new float[2];
	private long downTimer;

	// RHA
	RelativeLayout rl2;

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

		setContentView(R.layout.main);
		backgroundImageView = (BoardView) findViewById(R.id.image);
		zoomInButton = (ImageButton) findViewById(R.id.zoomIn);
		zoomOutButton = (ImageButton) findViewById(R.id.zoomOut);
		bugButton = (ImageButton) findViewById(R.id.debug);

		sourceRect = new RectF();
		destinationRect = new RectF();
		matrix = new Matrix();

		if (savedInstanceState != null) {
			current_centerX = savedInstanceState.getInt("centerX");
			current_centerY = savedInstanceState.getInt("centerY");
			current_scale = savedInstanceState.getFloat("scale");
			current_drawable = savedInstanceState.getInt("drawable");
			imageSizeX = savedInstanceState.getInt("sizeX");
			imageSizeY = savedInstanceState.getInt("sizeY");
		}

		timer = new Timer();
		animation = new Animation(handle, current_centerX, current_centerY,
				current_scale);

		backgroundImageView.setHandle(handle);
		backgroundImageView.setCallBack(sizeCallback);

		// Background relativelayout
		// XXX
		rl2 = new RelativeLayout(backgroundImageView.getContext());
		rl2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		animation.stopProcess();
		animation.setCallBack(animationCallBack);
		timer.scheduleAtFixedRate(animation, 200, 30);

		backgroundImageView.setOnTouchListener(metroListener);
		zoomInButton.setOnClickListener(zoomInListener);
		zoomOutButton.setOnClickListener(zoomOutListener);
		bugButton.setOnClickListener(bugListener);

		bitmap = BitmapFactory.decodeResource(getResources(), current_drawable);

		imageSizeX = bitmap.getWidth();
		imageSizeY = bitmap.getHeight();

		backgroundImageView.setImageBitmap(bitmap);
		backgroundImageView.getDrawable().setFilterBitmap(true);
		backgroundImageView.setImageMatrix(matrix);

		parseXmlDefs();
		
	}

	private void parseXmlDefs() {
		// Xml parse
		XmlResourceParser defs = getResources().getXml(R.xml.boarddefinition);
		int eventType = -1;
		// Find Score records from XML
		try {
			while (eventType != XmlResourceParser.END_DOCUMENT) {
				if (eventType == XmlResourceParser.START_TAG) {
					// Get the name of the tag (eg scores or score)
					String strName = defs.getName();
					if (strName.equals("commonfields")) {
						behandleCommons(defs);
					}
					if (strName.equals("itemdef")) {
						behandleSpillerData(defs);
					}

				}
				eventType = defs.next();
			}
		} catch (Exception e) {
			Log.e("ERROR", "Failed to load defs", e);
		}

	}

	private void behandleSpillerData(XmlResourceParser defs) {
		boolean ferdig = false;
		String theText = new String();
		int eventType = -1;
		String strName = null;
		String col = "unknown"; // Player color
		int whatToParse = 0; // 1 is base, 2=way home
		Vector<Coordinate> wayHome = new Vector<Coordinate>();
		Vector<Coordinate> baseHome = new Vector<Coordinate>();

		// Alle felles felter
		try {
			eventType = defs.getEventType();
			while (!ferdig) {
				strName = defs.getName();
				if (eventType == XmlResourceParser.START_TAG) {
					if (strName.equals("itemdef")) {
						col = defs.getAttributeValue(null, "col");
						int pos = Integer.parseInt(defs.getAttributeValue(null,
								"firstmove"));
						Game.getInstance().getLudoBoard()
								.addPlayerInfo(col, pos);
					}
					if (strName.equals("path")) {
						int pos = Integer.parseInt(defs.getAttributeValue(null,
								"pos"));
						int x = Integer.parseInt(defs.getAttributeValue(null,
								"x"));
						int y = Integer.parseInt(defs.getAttributeValue(null,
								"y"));
						Coordinate co = new Coordinate();
						co.pos = pos;
						co.x = x;
						co.y = y;

						switch (whatToParse) {
						case 1:
							wayHome.add(co); // Last track home
							break;
						case 2:
							baseHome.add(co); // Home base
							break;
						}
					}
					if (strName.equals("base")) {
						whatToParse = 1;
					}
					if (strName.equals("wayhome")) {
						whatToParse = 2;
						int pos = Integer.parseInt(defs.getAttributeValue(null,
								"start"));
						Game.getInstance().getLudoBoard()
								.setWayHomePosition(col, pos);
					}
				} else if (defs.getEventType() == XmlResourceParser.END_TAG) {
					if (defs.getName().equals("itemdef")) {
						ferdig = true;
					}
				}
				if (!ferdig) {
					eventType = defs.next();
				}
			}
			Game.getInstance().getLudoBoard().addBaseHomeDefs(col, baseHome);
			Game.getInstance().getLudoBoard().addWayHomeDefs(col, wayHome);

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void behandleCommons(XmlResourceParser defs) {
		boolean ferdig = false;
		String theText = new String();
		int eventType = -1;
		String strName = null;

		// Alle felles felter
		try {
			while (!ferdig) {
				strName = defs.getName();
				if (eventType == XmlResourceParser.START_TAG) {
					if (strName.equals("common")) {
						int pos = Integer.parseInt(defs.getAttributeValue(null,
								"pos"));
						int x = Integer.parseInt(defs.getAttributeValue(null,
								"x"));
						int y = Integer.parseInt(defs.getAttributeValue(null,
								"y"));
						Game.getInstance().getLudoBoard().addCommon(pos, x, y);
						Log.d("Xml load", "Board pos " + x + ", " + y);
					}
				} else if (defs.getEventType() == XmlResourceParser.END_TAG) {
					if (defs.getName().equals("commonfields")) {
						ferdig = true;
					}
				}
				if (!ferdig) {
					eventType = defs.next();
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onRestoreInstanceState(Bundle inState) {
		current_centerX = inState.getInt("centerX");
		current_centerY = inState.getInt("centerY");
		current_scale = inState.getFloat("scale");
		current_drawable = inState.getInt("drawable");
		imageSizeX = inState.getInt("sizeX");
		imageSizeY = inState.getInt("sizeY");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("centerX", current_centerX);
		outState.putInt("centerY", current_centerY);
		outState.putFloat("scale", current_scale);
		outState.putInt("drawable", current_drawable);
		outState.putInt("sizeX", imageSizeX);
		outState.putInt("sizeY", imageSizeY);
	}

	public void onDestroy() {
		if (!bitmap.isRecycled())
			bitmap.recycle();
		super.onDestroy();
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

	// private float spacing(MotionEvent event) {
	// float x = event.getX(0) - event.getX(1);
	// float y = event.getY(0) - event.getY(1);
	// return FloatMath.sqrt(x * x + y * y);
	// }
	//
	// private void midPoint(PointF point, MotionEvent event) {
	// float x = event.getX(0) + event.getX(1);
	// float y = event.getY(0) + event.getY(1);
	// point.set(x / 2, y / 2);
	// }

	private OnTouchListener metroListener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			dumpEvent(event);

			// int action = event.getAction();
			// int actionCode = action & MotionEvent.ACTION_MASK;

			// RHA
			// if ((actionCode == MotionEvent.ACTION_POINTER_DOWN)) {
			// currentTouchtype = Moves.ZOOM;
			// Log.d("TouchEvent",
			// "-------------------- POINTER --------------------------");
			// oldDist = spacing(event);
			//
			// } else if ((actionCode == MotionEvent.ACTION_POINTER_UP)) {
			// }

			if ((event.getAction() == MotionEvent.ACTION_MOVE)) {

				// if (currentTouchtype == Moves.ZOOM) {
				// float newDist = spacing(event);
				// Log.d("TouchEvent", "---- Zooming : dist = " + newDist
				// + "   scale = " + current_scale);
				// if (newDist > oldDist) {
				// current_scale += 0.1f;
				// } else {
				// current_scale -= 0.1f;
				// }
				// animation.stopProcess();
				// animation.setScaleInfo(current_scale, current_scale
				// / MAGNIFY_SCALE);
				// updateDisplay();
				// } else {
				// Log.d("TouchEvent", "---- Moving");
				// currentTouchtype = Moves.DRAG;
				moveHistorySize++;
				lastTwoXMoves[1] = lastTwoXMoves[0];
				lastTwoXMoves[0] = event.getX();
				lastTwoYMoves[1] = lastTwoYMoves[0];
				lastTwoYMoves[0] = event.getY();

				if (moveHistorySize >= 2) {
					current_centerX += (int) ((lastTwoXMoves[1] - lastTwoXMoves[0])
							* (imageSizeX / current_scale) / backgroundImageView
							.getWidth());
					current_centerY += (int) ((lastTwoYMoves[1] - lastTwoYMoves[0])
							* (imageSizeY / current_scale) / backgroundImageView
							.getHeight());

					updateDisplay();
				}
				// }
			} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
				animation.stopProcess();
				lastTwoXMoves[0] = event.getX();
				lastTwoYMoves[0] = event.getY();
				downTimer = event.getEventTime();
				moveHistorySize = 1;
			} else if ((event.getAction() == MotionEvent.ACTION_UP)
					&& (moveHistorySize >= 1)) {
				// if (currentTouchtype == Moves.ZOOM) {
				// currentTouchtype = Moves.NONE;
				// Log.d("TouchEvent", "--------- Action up - nozoom");
				// } else {
				Log.d("TouchEvent", "--------- Action up - doSomething");
				if (event.getEventTime() != downTimer) {
					float speedX = (lastTwoXMoves[1] - lastTwoXMoves[0])
							* (imageSizeX / current_scale)
							/ backgroundImageView.getWidth();
					float speedY = (lastTwoYMoves[1] - lastTwoYMoves[0])
							* (imageSizeY / current_scale)
							/ backgroundImageView.getHeight();

					speedX /= event.getEventTime() - downTimer;
					speedY /= event.getEventTime() - downTimer;

					speedX *= 30;
					speedY *= 30;

					animation.setInfo(speedX, speedY, current_centerX,
							current_centerY);
				}
				// }
			}

			return true;
		}
	};

	private OnClickListener zoomInListener = new OnClickListener() {
		public void onClick(View v) {
			Log.d(TAG, "Zoom in");
			animation.stopProcess();

			if (current_scale <= 5) {
				animation.setScaleInfo(current_scale, current_scale
						* MAGNIFY_SCALE);
			}

		}
	};

	private OnClickListener bugListener = new OnClickListener() {
		public void onClick(View v) {
			// XXX
			Log.d(TAG, "Bug click");
			
			
			// Test av GAME
			
			int oyne = Game.getInstance().rollDie();
			// End test
			
			ImageView imx = new ImageView(backgroundImageView.getContext());
			imx.setImageResource(R.drawable.bug);
			imx.setLayoutParams(new Gallery.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			// RelativeLayout rl =
			// (RelativeLayout)findViewById(R.id.mainlayout);
			rl2.addView(imx);
			// Toast.makeText(rl.getContext(), "Bug pressed",
			// Toast.LENGTH_LONG);
			Toast.makeText(((RelativeLayout) findViewById(R.id.mainlayout))
					.getContext(), "Bug pressed", Toast.LENGTH_LONG);
		}
	};

	private OnClickListener zoomOutListener = new OnClickListener() {
		public void onClick(View v) {
			Log.d(TAG, "Zoom out");
			animation.stopProcess();

			if (current_scale >= MAGNIFY_SCALE * INITIAL_SCALE) {
				animation.setScaleInfo(current_scale, current_scale
						/ MAGNIFY_SCALE);
			} else if ((current_scale > INITIAL_SCALE)) {
				animation.setScaleInfo(current_scale, INITIAL_SCALE);
			}
		}
	};

	private AnimationCallBack animationCallBack = new AnimationCallBack() {
		public void onTimer(int centerX, int centerY, float scale) {
			current_centerX = centerX;
			current_centerY = centerY;
			current_scale = scale;
			updateDisplay();
		}
	};

	private SizeCallBack sizeCallback = new SizeCallBack() {
		public void onSizeChanged(int w, int h) {
			destinationRect.set((float) 0, (float) 0, (float) w, (float) h);
			updateDisplay();
		}
	};

	private void updateDisplay() {
		calculateSourceRect(current_centerX, current_centerY, current_scale);
		matrix.setRectToRect(sourceRect, destinationRect,
				Matrix.ScaleToFit.FILL);
		backgroundImageView.setImageMatrix(matrix);
	}

	private void calculateSourceRect(int centerX, int centerY, float scale) {
		int xSubValue;
		int ySubValue;

		if (destinationRect.bottom >= destinationRect.right) {
			ySubValue = (int) ((imageSizeY / 2) / scale);
			xSubValue = ySubValue;

			xSubValue = (int) (xSubValue * ((float) backgroundImageView
					.getWidth() / (float) backgroundImageView.getHeight()));
		} else {
			xSubValue = (int) ((imageSizeX / 2) / scale);
			ySubValue = xSubValue;

			ySubValue = (int) (ySubValue * ((float) backgroundImageView
					.getHeight() / (float) backgroundImageView.getWidth()));
		}

		if (centerX - xSubValue < 0) {
			animation.stopProcess();
			centerX = xSubValue;
		}
		if (centerY - ySubValue < 0) {
			animation.stopProcess();
			centerY = ySubValue;
		}
		if (centerX + xSubValue >= imageSizeX) {
			animation.stopProcess();
			centerX = imageSizeX - xSubValue - 1;
		}
		if (centerY + ySubValue >= imageSizeY) {
			animation.stopProcess();
			centerY = imageSizeY - ySubValue - 1;
		}

		current_centerX = centerX;
		current_centerY = centerY;

		sourceRect.set(centerX - xSubValue, centerY - ySubValue, centerX
				+ xSubValue, centerY + ySubValue);
	}

	public void setNewDrawable(int resId) {
		current_drawable = resId;
		bitmap.recycle();
		bitmap = BitmapFactory.decodeResource(getResources(), resId);
		backgroundImageView.setImageBitmap(bitmap);
		backgroundImageView.getDrawable().setFilterBitmap(true);

		current_scale = INITIAL_SCALE;
		imageSizeX = bitmap.getWidth();
		imageSizeY = bitmap.getHeight();
		current_centerX = imageSizeX / 2;
		current_centerY = imageSizeY / 2;

		animation.setInfo(0, 0, current_centerX, current_centerY);
		animation.setScaleInfo(current_scale, current_scale);

		updateDisplay();
	}
}