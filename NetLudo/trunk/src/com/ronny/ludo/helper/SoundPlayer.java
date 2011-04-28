package com.ronny.ludo.helper;

import com.ronny.ludo.R;
import com.ronny.ludo.model.GameHolder;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundPlayer
{
	public static final int ROLL6 = R.raw.shake_and_roll_6;
	public static final int ROLL = R.raw.shake_and_roll;
	public static final int NO_LEGAL_MOVE = R.raw.crowd_groan;
	private MediaPlayer mp = null;
	private Context context = null;
	
	public SoundPlayer(Context context)
	{
		this.context = context;
	}

	public int PlaySound(int soundToPlay)
	{
		if(GameHolder.getInstance().isSoundOn())
		{
//			mp = MediaPlayer.create(context, soundToPlay);
//			mp.start();
//			return mp.getDuration();			
		}
		return 0;
	}

	public int getDuration(int soundToPlay)
	{
//		mp = MediaPlayer.create(context, soundToPlay);
//		return mp.getDuration();
	    return 0;
	}
	
	//mp = MediaPlayer.create(getBaseContext(),R.raw.shake_and_roll_6);
}
