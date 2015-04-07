package com.comvee.voiceinteraction;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import com.comvee.voiceinteraction.util.ParamsConfig;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class RecordService extends Service
{
	private String status = Environment.getExternalStorageState();
	private String fileName;
	private MediaRecorder mediaRecorder = null;
	private String dirPath = ParamsConfig.strImgPath;
	private static int SAMPLE_RATE_IN_HZ = 8000;

	@Override
	public IBinder onBind(Intent arg0)
	{
		// TODO Auto-generated method stub
		Log.d("Service", "onBind");
		return null;
	}

	@Override
	public void onDestroy()
	{
		// TODO Auto-generated method stub
		Log.d("Service", "onDestroy");
		localStopRecording();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		// TODO Auto-generated method stub
		Log.d("Service", "onStartCommand");
		if (intent.getAction().equals("com.myly.record.record_start"))
		{
			localStartRecording();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate()
	{
		// TODO Auto-generated method stub
		Log.d("Service", "onCreate");
		super.onCreate();

	}

	private void localStopRecording()
	{
		if (mediaRecorder != null)
		{
			try
			{
				mediaRecorder.stop();
			} catch (RuntimeException e)
			{
				e.printStackTrace();
			}
			mediaRecorder.reset();
			mediaRecorder.release();
			mediaRecorder = null;
			Toast.makeText(getApplicationContext(), "End Record.", Toast.LENGTH_SHORT).show();
		}
	}

	private void localStartRecording()
	{
		File file;
		dirPath = dirPath + "/recorder";
		fileName = this.currenttime();
		if (recordDri(dirPath))
		{
			file = new File(dirPath, fileName + ".amr");
			mediaRecorder = new MediaRecorder();
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mediaRecorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);
			mediaRecorder.setOutputFile(file.getAbsolutePath());
			Log.i("file.getAbsolutePath", "" + file.getAbsolutePath());
			try
			{
				mediaRecorder.prepare();
			} catch (IOException exception)
			{
				mediaRecorder.reset();
				mediaRecorder.release();
				mediaRecorder = null;
				return;
			}

			try
			{
				mediaRecorder.start();
			} catch (RuntimeException exception)
			{
				mediaRecorder.reset();
				mediaRecorder.release();
				mediaRecorder = null;
				return;
			}
			Toast.makeText(getApplicationContext(), "Start Record.", Toast.LENGTH_SHORT).show();
		} else
		{
			Toast.makeText(getApplicationContext(), "SD卡不存在", Toast.LENGTH_SHORT).show();
		}
	}

	public double getAmplitude()
	{
		if (mediaRecorder != null)
		{
			return (mediaRecorder.getMaxAmplitude());
		} else
			return 0;
	}

	public String getTime()
	{
		return fileName;
	}

	public void setTime(String time)
	{
		this.fileName = time;
	}

	public String getPath()
	{
		return dirPath;
	}

	public void setPath(String path)
	{
		this.dirPath = path;
	}

	private String currenttime()
	{
		Calendar ca = Calendar.getInstance();
		int year = ca.get(Calendar.YEAR);// 获取年份
		int month = ca.get(Calendar.MONTH) + 1;// 获取月份
		int day = ca.get(Calendar.DATE);// 获取日
		int minute = ca.get(Calendar.MINUTE);// 分
		int hour = ca.get(Calendar.HOUR);// 小时
		int second = ca.get(Calendar.SECOND);// 秒
		String times = "" + year + month + day + hour + minute + second;
		return times;
	}

	private boolean recordDri(String path)
	{
		boolean b = false;
		if (status.equals(Environment.MEDIA_MOUNTED))
		{
			File dir = new File(path);
			if (!dir.exists())
			{
				try
				{
					dir.mkdirs();
				} catch (Exception e)
				{
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			b = true;
		} else if (status.equals("android.intent.action.MEDIA_REMOVED")
				|| status.equals("android.intent.action.ACTION_MEDIA_UNMOUNTED")
				|| status.equals("android.intent.action.ACTION_MEDIA_BAD_REMOVAL"))
		{
			b = false;
		}
		return b;
	}

}