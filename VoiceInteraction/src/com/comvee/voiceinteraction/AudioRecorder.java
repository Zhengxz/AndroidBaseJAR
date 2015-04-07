package com.comvee.voiceinteraction;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;

public class AudioRecorder
{
	private static int SAMPLE_RATE_IN_HZ = 8000;

	final MediaRecorder recorder = new MediaRecorder();
	final String path;

	public AudioRecorder(String filePath, String fileName)
	{
		this.path = sanitizePath(filePath, fileName);
	}

	private String sanitizePath(String fpath, String fname)
	{
		if (!fname.startsWith("/"))
		{
			fname = "/" + fname;
		}
		if (!fname.contains("."))
		{
			fname += ".amr";
		}
		return fpath + "/voice" + fname;
	}

	public void start() throws IOException
	{
		String state = android.os.Environment.getExternalStorageState();
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED))
		{
			throw new IOException("SD Card is not mounted,It is  " + state + ".");
		}
		File directory = new File(path).getParentFile();
		if (!directory.exists() && !directory.mkdirs())
		{
			throw new IOException("Path to file could not be created");
		}
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);
		recorder.setOutputFile(path);
		recorder.prepare();
		recorder.start();
	}

	public void stop() throws IOException
	{
		recorder.stop();
		recorder.release();
	}

	public double getAmplitude()
	{
		if (recorder != null)
		{
			return (recorder.getMaxAmplitude());
		} else
			return 0;
	}

	public String getFilePath()
	{
		return this.path;
	}
}