package com.comvee.voiceinteraction;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;

public class CusMediaPlay implements OnCompletionListener, OnErrorListener
{
	public static final int IDLE_STATE = 0;
	public static final int PLAYING_STATE = 1;
	public static final int PLAYING_PAUSED_STATE = 2;
	private int mState = IDLE_STATE;

	public static final int NO_ERROR = 0;
	public static final int STORAGE_ACCESS_ERROR = 1;
	public static final int INTERNAL_ERROR = 2;
	public static final int IN_CALL_RECORD_ERROR = 3;

	private MediaPlayer mPlayer = null;
	private Context mContext;
	private OnStateChangedListener mOnStateChangedListener = null;
	private long mSampleStart = 0;// shijian

	public interface OnStateChangedListener
	{
		public void onStateChanged(int state);

		public void onError(int error);
	}

	public CusMediaPlay(Context context)
	{
		mContext = context;
	}

	public float playProgress()
	{
		if (mPlayer != null)
		{
			return ((float) mPlayer.getCurrentPosition()) / mPlayer.getDuration();
		}
		return 0.0f;
	}

	public void startPlayback(String url, float percentage)
	{
		if (state() == PLAYING_PAUSED_STATE)
		{
			mSampleStart = System.currentTimeMillis() - mPlayer.getCurrentPosition();
			mPlayer.seekTo((int) (percentage * mPlayer.getDuration()));
			mPlayer.start();
			setState(PLAYING_STATE);
		} else
		{
			stop();

			mPlayer = new MediaPlayer();
			try
			{
				mPlayer.setDataSource(url);
				mPlayer.setOnCompletionListener(this);
				mPlayer.setOnErrorListener(this);
				mPlayer.prepare();
				mPlayer.seekTo((int) (percentage * mPlayer.getDuration()));
				mPlayer.start();
			} catch (IllegalArgumentException e)
			{
				e.printStackTrace();
				setError(INTERNAL_ERROR);
				mPlayer = null;
				return;
			} catch (IllegalStateException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
				setError(STORAGE_ACCESS_ERROR);
				mPlayer = null;
				return;
			}

			mSampleStart = System.currentTimeMillis();
			setState(PLAYING_STATE);
		}
	}

	public void pausePlayback()
	{
		if (mPlayer == null)
		{
			return;
		}
		mPlayer.pause();
		setState(PLAYING_PAUSED_STATE);
	}

	public void stopPlayback()
	{
		if (mPlayer == null) // we were not in playback
			return;

		mPlayer.stop();
		mPlayer.release();
		mPlayer = null;
		setState(IDLE_STATE);
	}

	public void stop()
	{
		stopPlayback();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra)
	{
		// TODO Auto-generated method stub
		stop();
		setError(STORAGE_ACCESS_ERROR);
		return true;
	}

	@Override
	public void onCompletion(MediaPlayer mp)
	{
		// TODO Auto-generated method stub
		stop();
	}

	public int state()
	{
		return mState;
	}

	public void setState(int state)
	{
		Log.d("MediaPlayer", "state is " + state);
		if (state == mState)
			return;
		mState = state;
		signalStateChanged(mState);
	}

	public void setOnStateChangedListener(OnStateChangedListener listener)
	{
		mOnStateChangedListener = listener;
	}

	private void signalStateChanged(int state)
	{
		if (mOnStateChangedListener != null)
			mOnStateChangedListener.onStateChanged(state);
	}

	public void setError(int error)
	{
		if (mOnStateChangedListener != null)
			mOnStateChangedListener.onError(error);
	}

}