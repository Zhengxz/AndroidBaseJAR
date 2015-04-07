package com.comvee.voiceinteraction;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import com.comvee.voiceinteraction.util.VoiceTools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CustomAudio implements OnClickListener
{
	private static int MAX_TIME = 120; // 最长录制时间，单位秒，0为无时间限制
	private static int MIX_TIME = 1; // 最短录制时间，单位秒，0为无时间限制，建议设为1

	private static int RECORD_NO = 0; // 不在录音
	private static int RECORD_ING = 1; // 正在录音
	private static int RECODE_ED = 2; // 完成录音

	private static int RECODE_STATE = 0; // 录音的状态

	private static float recodeTime = 0.0f; // 录音的时间
	private static double voiceValue = 0.0; // 麦克风获取的音量值

	private AudioRecorder mr;
	private Thread recordThread;

	private Activity mActivity;
	private String strPath = "";// 录音文件路径
	private TextView tvRecord;// 显示状态
	private TextView tvTime;// 显示剩余时间

	public CustomAudio(Activity mActivity, TextView tvTime, TextView tvStatus, String mPath)
	{
		this.mActivity = mActivity;
		this.strPath = mPath;
		this.tvRecord = tvStatus;
		this.tvTime = tvTime;
	}

	/**
	 * 开始录音
	 */
	public void startRecord()
	{
		if (RECODE_STATE != RECORD_ING && !TextUtils.isEmpty(strPath))
		{
			mr = new AudioRecorder(strPath, "voice");
			scanOldFile(mr.getFilePath());
			RECODE_STATE = RECORD_ING;
			try
			{
				mr.start();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tvRecord.setText("松开停止录音");
			mythread();
		}
	}

	/**
	 * 停止录音
	 */
	public void stopRecord()
	{
		if (RECODE_STATE == RECORD_ING)
		{
			RECODE_STATE = RECODE_ED;
			try
			{
				mr.stop();
				voiceValue = 0.0;
			} catch (IOException e)
			{
				e.printStackTrace();
			}

			if (recodeTime < MIX_TIME)
			{
				showWarnToast();
				tvRecord.setText("长按开始录音");
				RECODE_STATE = RECORD_NO;
			} else
			{
				tvRecord.setText("录音完成!长按重新录音");
				BigDecimal b = new BigDecimal(recodeTime);
				if (recodeTime >= 120.0)
				{
					tvTime.setText("录音时长：2'00\"");
				} else
				{
					tvTime.setText("录音时长："
							+ VoiceTools.getVoiceTime((b.setScale(0, BigDecimal.ROUND_HALF_UP).intValue())));
				}
			}
		}
	}

	/**
	 * 获取录音时长
	 * 
	 * @return
	 */
	public int getRecordTime()
	{
		BigDecimal b = new BigDecimal(recodeTime);
		if (recodeTime >= 120.0)
		{
			return 120;
		} else if (recodeTime < 1.0)
		{
			return 0;// 小于1秒，相当于没有
		} else
		{
			return b.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		}
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case 1:

			break;

		default:
			break;
		}
	}

	// 删除老文件
	void scanOldFile(String path)
	{
		File file = new File(path);
		if (file.exists())
		{
			file.delete();
		}
	}

	// 录音时间太短时Toast显示
	void showWarnToast()
	{
		Toast toast = new Toast(mActivity);
		LinearLayout linearLayout = new LinearLayout(mActivity);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setPadding(20, 20, 20, 20);

		// 定义一个ImageView
		ImageView imageView = new ImageView(mActivity);
		imageView.setImageResource(R.drawable.voice_to_short); // 图标

		TextView mTv = new TextView(mActivity);
		mTv.setText("时间太短   录音失败！");
		mTv.setTextSize(14);
		mTv.setTextColor(Color.WHITE);// 字体颜色
		// mTv.setPadding(0, 10, 0, 0);

		// 将ImageView和ToastView合并到Layout中
		linearLayout.addView(imageView);
		linearLayout.addView(mTv);
		linearLayout.setGravity(Gravity.CENTER);// 内容居中
		linearLayout.setBackgroundResource(R.drawable.record_bg);// 设置自定义toast的背景

		toast.setView(linearLayout);
		toast.setGravity(Gravity.CENTER, 0, 0);// 起点位置为中间 100为向下移100dp
		toast.show();
	}

	// 获取文件手机路径
	public String getAmrPath()
	{
		if (mr != null)
		{
			return mr.getFilePath();
		} else
		{
			return "";
		}
	}

	// 录音计时线程
	void mythread()
	{
		recordThread = new Thread(ImgThread);
		recordThread.start();
	}

	// 录音线程
	private Runnable ImgThread = new Runnable()
	{

		@Override
		public void run()
		{
			recodeTime = 0.0f;
			while (RECODE_STATE == RECORD_ING)
			{
				if (recodeTime >= MAX_TIME && MAX_TIME != 0)
				{
					imgHandle.sendEmptyMessage(0);
				} else
				{
					try
					{
						Thread.sleep(200);
						recodeTime += 0.2;
						if (RECODE_STATE == RECORD_ING)
						{
							voiceValue = mr.getAmplitude();
							imgHandle.sendEmptyMessage(1);
						}
						if (RECODE_STATE == RECORD_ING)
						{
							Message msg = imgHandle.obtainMessage();
							msg.arg1 = (int) recodeTime;
							msg.what = 2;
							imgHandle.sendMessage(msg);
						}
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		}

		@SuppressLint("HandlerLeak")
		Handler imgHandle = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				switch (msg.what)
				{
				case 0:
					// 录音超过120秒自动停止
					if (RECODE_STATE == RECORD_ING)
					{
						RECODE_STATE = RECODE_ED;

						try
						{
							mr.stop();
							voiceValue = 0.0;
						} catch (IOException e)
						{
							e.printStackTrace();
						}

						if (recodeTime < 1.0)
						{
							showWarnToast();
							tvRecord.setText("按住开始录音");
							RECODE_STATE = RECORD_NO;
						} else
						{
							tvRecord.setText("录音完成!点击重新录音");
							BigDecimal b = new BigDecimal(recodeTime);
							if (recodeTime >= 120.0)
							{
								tvTime.setText("录音时长：2'00\"");
							} else
							{
								tvTime.setText("录音时长："
										+ VoiceTools.getVoiceTime((b.setScale(0, BigDecimal.ROUND_HALF_UP)
												.intValue())));
							}
						}
					}
					break;
				case 1:
					// 显示音量大小
					break;
				case 2:
					int time = 120 - (int) msg.arg1;
					if (time <= 10)
					{
						tvTime.setText(String.format("录音时间还剩%s秒", time));
					}
					break;

				default:
					break;
				}
			}
		};
	};

}