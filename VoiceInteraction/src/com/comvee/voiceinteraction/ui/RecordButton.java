package com.comvee.voiceinteraction.ui;

import java.io.File;

import com.comvee.voiceinteraction.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class RecordButton extends Button {
	private String AUDOI_DIR = null;// ¼����Ƶ�����·��
	private static final int MIN_INTERVAL_TIME = 2000;// 2s
	public final static int VOLUME_MAX = 8;

	private VolumeViewer mVolumeViewer;
	private RelativeLayout mVolumeLay, mExitLay;
	private boolean mIsCancel = false;

	private String mFileName = null;
	private OnFinishedRecordListener mFinishedListerer;
	private long mStartTime;
	private Dialog mRecordDialog;

	private AudioUtil mAudioUtil;
	private ObtainDecibelThread mThread;
	private Handler mVolumeHandler;
	private int mYpositon = 0;
	private int recodeTime = 0;

	public RecordButton(Context context) {
		super(context);
		init(context);
	}

	public RecordButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public RecordButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void setSavePath(String path) {
		mFileName = AUDOI_DIR + "/" + path;
		File file = new File(mFileName);
		if (!file.exists()) {
			file.mkdirs();
		}
		mFileName += "/" + System.currentTimeMillis() + ".amr";
	}

	public void setOnFinishedRecordListener(OnFinishedRecordListener listener) {
		mFinishedListerer = listener;
	}

	private void init(Context context) {
		if (context.getExternalCacheDir() == null) {
			AUDOI_DIR = context.getDir("video", 0).getAbsolutePath()
					+ "/comvee/audio";
		} else {
			AUDOI_DIR = context.getExternalCacheDir() + "/comvee/audio";
		}
		mVolumeHandler = new ShowVolumeHandler();
		mAudioUtil = new AudioUtil();
		int[] location = new int[2];
		getLocationOnScreen(location);
		mYpositon = location[1];
		setnomal();
	}

	private void setnomal() {
		setText(R.string.ask_voice_normal);
		setTextColor(Color.rgb(153, 153, 153));
		setBackgroundResource(R.drawable.btn_edit_way_voice_whrite);
		// ֪ͨ����
		postInvalidate();
	}

	private void setpress() {
		setText(R.string.ask_voice_press);
		setTextColor(Color.rgb(255, 255, 255));
		setBackgroundResource(R.drawable.btn_edit_way_voice_blue);
		// ֪ͨ����
		postInvalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (mFileName == null) {
			return false;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			initDialogAndStartRecord();
			mVolumeLay.setVisibility(View.VISIBLE);
			mExitLay.setVisibility(View.GONE);
			break;

		case MotionEvent.ACTION_UP:
			if (!mIsCancel) {
				finishRecord();
			} else {
				cancelRecord();
			}

			mIsCancel = false;
			break;

		case MotionEvent.ACTION_MOVE:// ����ָ�ƶ���view���棬��cancel

			if (event.getY() < mYpositon) {
				mIsCancel = true;
				mExitLay.setVisibility(View.VISIBLE);
				mVolumeLay.setVisibility(View.GONE);
			}
			break;
		}

		return true;
	}

	// ���� dialog ��contentView
	private View createContentView() {
		View dialog = LayoutInflater.from(getContext()).inflate(
				R.layout.my_btnrecord_dialog, null);

		mVolumeLay = (RelativeLayout) dialog.findViewById(R.id.mVolumeLay);
		mVolumeViewer = (VolumeViewer) dialog.findViewById(R.id.mVolumeViewer);
		mExitLay = (RelativeLayout) dialog.findViewById(R.id.mExitLay);
		return dialog;
	}

	// ��ʼ��dialog ��¼����
	private void initDialogAndStartRecord() {

		mStartTime = System.currentTimeMillis();
		if (mRecordDialog == null) {
			mRecordDialog = new Dialog(getContext(), R.style.mydialog);
			mRecordDialog.setContentView(createContentView());

			mRecordDialog.setOnDismissListener(onDismiss);
		}

		startRecording();
		mRecordDialog.show();
	}

	// ����¼��
	private void finishRecord() {
		stopRecording();
		mRecordDialog.dismiss();

		long intervalTime = System.currentTimeMillis() - mStartTime;
		if (intervalTime < MIN_INTERVAL_TIME) {
			Toast.makeText(getContext(), "ʱ��̫�̣�", Toast.LENGTH_SHORT).show();
			File file = new File(mFileName);
			file.delete();
			return;
		}

		recodeTime = (int) ((System.currentTimeMillis() - mStartTime) / 1000);
		if (mFinishedListerer != null) {
			mFinishedListerer.onFinishedRecord(mFileName, recodeTime);
		}
	}

	// ȡ��¼��
	private void cancelRecord() {
		stopRecording();
		mRecordDialog.dismiss();

		Toast.makeText(getContext(), "ȡ��¼����", Toast.LENGTH_SHORT).show();
		File file = new File(mFileName);
		file.delete();
	}

	// ��ʼ¼��
	private void startRecording() {
		try {
			mAudioUtil.setAudioPath(mFileName);
			mAudioUtil.recordAudio();
			mThread = new ObtainDecibelThread();
			mThread.start();
			setpress();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void stopRecording() {
		if (mThread != null) {
			mThread.exit();
			mThread = null;
		}
		if (mAudioUtil != null) {
			mAudioUtil.stopRecord();
		}
		setnomal();
	}

	private class ObtainDecibelThread extends Thread {

		private volatile boolean running = true;

		public void exit() {
			running = false;
		}

		@Override
		public void run() {
			while (running) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (mAudioUtil == null || !running) {
					break;
				}

				int volumn = mAudioUtil.getVolumn();

				if (volumn != 0) {
					mVolumeHandler.sendEmptyMessage(volumn);
				}

			}
		}

	}

	private OnDismissListener onDismiss = new OnDismissListener() {

		@Override
		public void onDismiss(DialogInterface dialog) {
			stopRecording();
		}
	};

	class ShowVolumeHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			mVolumeViewer.setVolumeValue(msg.what);
		}
	}

	public interface OnFinishedRecordListener {
		public void onFinishedRecord(String audioPath, int recordTime);
	}

}
