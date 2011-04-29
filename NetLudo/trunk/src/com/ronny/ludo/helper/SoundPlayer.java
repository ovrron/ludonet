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
	public static final int SPLASH = R.raw.viacom2;
	public static final int DISCONNECT = R.raw.disconnect;
	public static final int MOVE = R.raw.move;
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
			try
			{
				mp = MediaPlayer.create(context, soundToPlay);
				mp.start();
				return mp.getDuration();
			}
			catch(Exception e)
			{
				return 0;
			}
		}
		return 0;
	}

	public int getDuration(int soundToPlay)
	{
		try
		{
			mp = MediaPlayer.create(context, soundToPlay);
			return mp.getDuration();
		}
		catch(Exception e)
		{
			return 0;
		}
	}
}
