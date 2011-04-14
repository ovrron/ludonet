package com.ronny.ludo.board;

import android.graphics.Canvas;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;

public class DrawingThread extends Thread {
	private static String TAG = "DrawingThread";
	
	private final static int sleepInterval = 30000;
	private SurfaceHolder mSurfaceHolder;
	private LudoSurfaceView mLudoView;
	private boolean mRun = false;
//	private int mHeading = 0;
	private boolean mPause = false;
	public DrawingThread(SurfaceHolder holder, LudoSurfaceView parentView){
		   mSurfaceHolder = holder;
		   mLudoView = parentView;
	}
	
   public boolean doKeyDown(int keyCode, KeyEvent msg){
//	   synchronized(mSurfaceHolder) {
//		   if (keyCode==KeyEvent.KEYCODE_DPAD_LEFT || keyCode==KeyEvent.KEYCODE_DPAD_RIGHT ) {
//			   if (keyCode==KeyEvent.KEYCODE_DPAD_LEFT) mHeading = (mHeading+1) % 4; 
//			   else if (keyCode==KeyEvent.KEYCODE_DPAD_RIGHT)mHeading = (mHeading-1+4)%4; 
////			   mSnakeView.changeDirection(mHeading);
//		   	   return true;
//		   	}
//		   else if (keyCode==KeyEvent.KEYCODE_MENU) {
//			   setPaused(true);
//			   return false;
//		   }
//	   return false;
//	   }
	   return false;
   }

	@Override
	public void run() {
		   Canvas c;
		   Log.d("DRAWINGTHREAD", "----STARTER");
		   mySleep(18000);
		   int teller = 0;
		   while (isRunning()) {
				c=null;
				try {
					c=mSurfaceHolder.lockCanvas(null);
					synchronized(mSurfaceHolder) {
						//Log.d(TAG, "--Call on draw");
						//mLudoView.debug(teller);
						teller++;
						mLudoView.onDraw(c);
					}
				} finally {
					if (c!=null) {
						mSurfaceHolder.unlockCanvasAndPost(c);
					}
				}
				mySleep(sleepInterval);
			}
	   }
	   public void mySleep(int length) {
		   try {
			   sleep(length);
		   }
		   catch (InterruptedException e) {
			   Log.i("Mytaggg","mySleep interrupted");
		   }
	   }
	   public void setRunning(boolean run) {
		   mRun=run;
	   }
	   public boolean isRunning() {
		   return mRun;
	   }
	   public void setPaused(boolean p) {
		   synchronized(mSurfaceHolder) {
		   mPause = p;
		   }
	   }
	   public boolean isPaused() {
		   synchronized(mSurfaceHolder) {
		   return mPause;
		   }
	   }
}
