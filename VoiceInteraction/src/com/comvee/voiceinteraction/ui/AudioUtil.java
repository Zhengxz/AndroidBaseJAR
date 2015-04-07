package com.comvee.voiceinteraction.ui;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.text.TextUtils;
import android.util.Log;

public class AudioUtil {
	private final static String TAG = "AudioUtil";

	private String mAudioPath;
	private boolean mIsRecording;
	private boolean mIsPlaying;
	private MediaRecorder mRecorder;
	private MediaPlayer mPlayer;

	// ���ñ���·��
	public void setAudioPath(String path) {
		this.mAudioPath = path;
	}

	// ��ʼ¼���������浽�ļ���
	public void recordAudio() {
		if (mAudioPath != null && !mAudioPath.equals("")) {
			initRecorder();
			try {
				mRecorder.prepare();
			} catch (IOException e) {
				e.printStackTrace();
			}

			mRecorder.start();
		}

		else {
			Log.d(TAG, "AudioUtil ����¼��ʧ�ܣ�");
		}

	}

	// ��ȡ����ֵ��ֻ�����¼������
	public int getVolumn() {
		int volumn = 0;

		// ¼��
		if (mRecorder != null && mIsRecording) {
			volumn = mRecorder.getMaxAmplitude();
			if (volumn != 0)
				volumn = (int) (10 * Math.log(volumn) / Math.log(10)) / 7;
		}

		return volumn;
	}

	// ��ʼ�� ¼����
	private void initRecorder() {

		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		mRecorder.setOutputFile(mAudioPath);
		mIsRecording = true;

	}

	// ֹͣ¼��
	public void stopRecord() {
		if (mRecorder != null) {
			try {
				mRecorder.stop();
				mRecorder.release();
				mRecorder = null;
				mIsRecording = false;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mRecorder = null;
				mIsRecording = false;
			}
		}
	}

	// ��ʼ����
	public void startPlay() {
		if (TextUtils.isEmpty(mAudioPath)) {
			mPlayer = new MediaPlayer();
			try {
				mPlayer.setDataSource(mAudioPath);
				mPlayer.prepare();
				mPlayer.start();
				mIsPlaying = true;
				mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						mp.release();
						mPlayer = null;
						mIsPlaying = false;
					}
				});

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Log.d(TAG, "AudioUtil ����¼����Ƶʧ�ܣ�");
		}
	}

	public boolean isPlaying() {
		return mIsPlaying;
	}

}