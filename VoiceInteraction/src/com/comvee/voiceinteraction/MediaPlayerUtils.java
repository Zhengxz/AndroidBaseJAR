package com.comvee.voiceinteraction;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import android.telephony.TelephonyManager;

import com.comvee.voiceinteraction.model.VoiceInfo;
import com.comvee.voiceinteraction.util.ParamsConfig;

public class MediaPlayerUtils
{
	
	private MediaPlayer mediaPlayer = null;
	private static Context context;
	private static MediaPlayerUtils instance = null;
	private String tempVoiceUrl = "";// �����������url��Ĭ�Ͽ�
	private VoiceInfo mCurVoiceInfo = null;// ��ǰ���ŵ���������
	private boolean playState = false; // ����״̬

	private MediaPlayerUtils()
	{
	}

	public static MediaPlayerUtils getInstance(Context context)
	{
		if (instance == null)
		{
			MediaPlayerUtils.context = context;
			instance = new MediaPlayerUtils();
			// TelephonyManager telephonyManager = (TelephonyManager) context
			// .getSystemService(Context.TELEPHONY_SERVICE);
			// telephonyManager.listen(new MyPhoneListener(),
			// PhoneStateListener.LISTEN_CALL_STATE);
		}
		return instance;
	}

	/**
	 * ����sdcard��Ƶ
	 * 
	 * @param voicePath
	 * @param voiceInfo
	 */
	public void playSDCardVoice(String voicePath, VoiceInfo voiceInfo, final Handler mHandler)
	{
		boolean isSame = false;
		if (mCurVoiceInfo != voiceInfo)
		{
			if (mCurVoiceInfo != null)
			{
				mCurVoiceInfo.setPlaying(false);
			}
			mCurVoiceInfo = voiceInfo;
			isSame = false;
		} else
		{
			isSame = true;
		}
		if (!playState || !isSame)
		{
			try
			{
				if (mediaPlayer != null)
				{
					stop(mCurVoiceInfo, mHandler);
				}

				mediaPlayer = new MediaPlayer();
				// mediaPlayer.reset();
				// ģ�����ﲥ�Ŵ�url��������Ŵ�getAmrPath()
				mediaPlayer.setDataSource(voicePath);
				// ѭ������
				mediaPlayer.setLooping(false);
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.prepareAsync();
				mediaPlayer.setOnPreparedListener(new OnPreparedListener()
				{

					@Override
					public void onPrepared(MediaPlayer mp)
					{
						// TODO Auto-generated method stub
						mediaPlayer.start();
					}
				});
				playState = true;
				mCurVoiceInfo.setPlaying(true);// ������
				mHandler.sendEmptyMessage(ParamsConfig.VOICE);
				// ���ò��Ž���ʱ����
				mediaPlayer.setOnCompletionListener(new OnCompletionListener()
				{

					@Override
					public void onCompletion(MediaPlayer mp)
					{
						playState = false;
						mediaPlayer.release();
						mediaPlayer = null;
						mCurVoiceInfo.setPlaying(false);
						mHandler.sendEmptyMessage(ParamsConfig.VOICE);
					}
				});
				mediaPlayer.setOnErrorListener(new OnErrorListener()
				{

					@Override
					public boolean onError(MediaPlayer mp, int what, int extra)
					{
						// TODO Auto-generated method stub
						stop(mCurVoiceInfo, mHandler);
						return true;
					}
				});
			} catch (IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
		{
			stop(voiceInfo, mHandler);
		}
	}

	/**
	 * ����������Ƶ
	 * 
	 * @param voiceUrl
	 * @param voiceInfo
	 */
	public void playNetVoice(String voiceUrl, VoiceInfo voiceInfo, final Handler mHandler)
	{
		boolean isSame = false;
		if (mCurVoiceInfo != voiceInfo)
		{
			if (mCurVoiceInfo != null)
			{
				mCurVoiceInfo.setPlaying(false);
			}
			mCurVoiceInfo = voiceInfo;
			isSame = false;
		} else
		{
			isSame = true;
		}
		if (!tempVoiceUrl.equals(voiceUrl) || !isSame)
		{
			try
			{
				if (mediaPlayer != null)
				{
					stop(mCurVoiceInfo, mHandler);
				}

				Uri uri = Uri.parse(voiceUrl);
				mediaPlayer = new MediaPlayer();
				// mediaPlayer.reset();
				mediaPlayer.setDataSource(context, uri);
				// ѭ������
				mediaPlayer.setLooping(false);
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mediaPlayer.prepareAsync();
				mediaPlayer.setOnPreparedListener(new OnPreparedListener()
				{

					@Override
					public void onPrepared(MediaPlayer mp)
					{
						// TODO Auto-generated method stub
						mediaPlayer.start();
					}
				});
				// ��ˢ�½���
				tempVoiceUrl = voiceUrl;
				mCurVoiceInfo.setPlaying(true);// ������
				mHandler.sendEmptyMessage(ParamsConfig.VOICE);
				// ���ò��Ž���ʱ����
				mediaPlayer.setOnCompletionListener(new OnCompletionListener()
				{

					@Override
					public void onCompletion(MediaPlayer mp)
					{
						mediaPlayer.release();
						mediaPlayer = null;
						mCurVoiceInfo.setPlaying(false);
						tempVoiceUrl = "";
						mHandler.sendEmptyMessage(ParamsConfig.VOICE);
					}
				});
				mediaPlayer.setOnErrorListener(new OnErrorListener()
				{

					@Override
					public boolean onError(MediaPlayer mp, int what, int extra)
					{
						// TODO Auto-generated method stub
						stop(mCurVoiceInfo, mHandler);
						return true;
					}
				});
			} catch (IllegalArgumentException e)
			{
				e.printStackTrace();
			} catch (IllegalStateException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		} else
		{
			stop(mCurVoiceInfo, mHandler);
		}
	}

	private void stop(VoiceInfo voiceInfo, Handler mHandler)
	{
		try
		{
			if (mediaPlayer != null && mediaPlayer.isPlaying())
			{
				if (voiceInfo != null)
				{
					voiceInfo.setPlaying(false);
				}
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			} else
			{
				if (voiceInfo != null)
				{
					voiceInfo.setPlaying(false);
				}
				mediaPlayer.release();
				mediaPlayer = null;
			}
			tempVoiceUrl = "";
			playState = false;
			mHandler.sendEmptyMessage(ParamsConfig.VOICE);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void releasePlayer()
	{
		tempVoiceUrl = "";
		playState = false;
		if (mCurVoiceInfo != null)
		{
			mCurVoiceInfo.setPlaying(false);
			mCurVoiceInfo = null;
		}
		if (mediaPlayer != null)
		{
			if (mediaPlayer.isPlaying())
			{
				mediaPlayer.stop();
			}
			mediaPlayer.release();
			mediaPlayer = null;
		}
		context = null;
		instance = null;
	}

	public void pause()
	{
		try
		{
			if (mediaPlayer != null && mediaPlayer.isPlaying())
			{
				mediaPlayer.pause();
			}
		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void release()
	{
		try
		{
			if (mediaPlayer != null)
			{
				if (mediaPlayer.isPlaying())
				{
					mediaPlayer.stop();
				}
				mediaPlayer.release();
				mediaPlayer = null;
			} else
			{
				mediaPlayer = null;
			}
		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * ֻ�е绰����֮�����ͣ���ֵĲ���
	 */
	static class MyPhoneListener extends android.telephony.PhoneStateListener
	{
		@Override
		public void onCallStateChanged(int state, String incomingNumber)
		{
			switch (state)
			{
			case TelephonyManager.CALL_STATE_RINGING:// �绰����
				if (instance != null)
				{
					instance.callIsComing();
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE: // ͨ������
				if (instance != null)
				{
					instance.callIsDown();
				}
				break;
			}
		}
	}

	/**
	 * ���绰��
	 */
	public void callIsComing()
	{
		if (mediaPlayer != null)
		{
			if (mediaPlayer.isPlaying())
			{
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			}
		}
	}

	/**
	 * ͨ������
	 */
	public void callIsDown()
	{

	}

}