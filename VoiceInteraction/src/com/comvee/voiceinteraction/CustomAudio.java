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
	private static int MAX_TIME = 120; // �¼��ʱ�䣬��λ�룬0Ϊ��ʱ������
	private static int MIX_TIME = 1; // ���¼��ʱ�䣬��λ�룬0Ϊ��ʱ�����ƣ�������Ϊ1

	private static int RECORD_NO = 0; // ����¼��
	private static int RECORD_ING = 1; // ����¼��
	private static int RECODE_ED = 2; // ���¼��

	private static int RECODE_STATE = 0; // ¼����״̬

	private static float recodeTime = 0.0f; // ¼����ʱ��
	private static double voiceValue = 0.0; // ��˷��ȡ������ֵ

	private AudioRecorder mr;
	private Thread recordThread;

	private Activity mActivity;
	private String strPath = "";// ¼���ļ�·��
	private TextView tvRecord;// ��ʾ״̬
	private TextView tvTime;// ��ʾʣ��ʱ��

	public CustomAudio(Activity mActivity, TextView tvTime, TextView tvStatus, String mPath)
	{
		this.mActivity = mActivity;
		this.strPath = mPath;
		this.tvRecord = tvStatus;
		this.tvTime = tvTime;
	}

	/**
	 * ��ʼ¼��
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
			tvRecord.setText("�ɿ�ֹͣ¼��");
			mythread();
		}
	}

	/**
	 * ֹͣ¼��
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
				tvRecord.setText("������ʼ¼��");
				RECODE_STATE = RECORD_NO;
			} else
			{
				tvRecord.setText("¼�����!��������¼��");
				BigDecimal b = new BigDecimal(recodeTime);
				if (recodeTime >= 120.0)
				{
					tvTime.setText("¼��ʱ����2'00\"");
				} else
				{
					tvTime.setText("¼��ʱ����"
							+ VoiceTools.getVoiceTime((b.setScale(0, BigDecimal.ROUND_HALF_UP).intValue())));
				}
			}
		}
	}

	/**
	 * ��ȡ¼��ʱ��
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
			return 0;// С��1�룬�൱��û��
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

	// ɾ�����ļ�
	void scanOldFile(String path)
	{
		File file = new File(path);
		if (file.exists())
		{
			file.delete();
		}
	}

	// ¼��ʱ��̫��ʱToast��ʾ
	void showWarnToast()
	{
		Toast toast = new Toast(mActivity);
		LinearLayout linearLayout = new LinearLayout(mActivity);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setPadding(20, 20, 20, 20);

		// ����һ��ImageView
		ImageView imageView = new ImageView(mActivity);
		imageView.setImageResource(R.drawable.voice_to_short); // ͼ��

		TextView mTv = new TextView(mActivity);
		mTv.setText("ʱ��̫��   ¼��ʧ�ܣ�");
		mTv.setTextSize(14);
		mTv.setTextColor(Color.WHITE);// ������ɫ
		// mTv.setPadding(0, 10, 0, 0);

		// ��ImageView��ToastView�ϲ���Layout��
		linearLayout.addView(imageView);
		linearLayout.addView(mTv);
		linearLayout.setGravity(Gravity.CENTER);// ���ݾ���
		linearLayout.setBackgroundResource(R.drawable.record_bg);// �����Զ���toast�ı���

		toast.setView(linearLayout);
		toast.setGravity(Gravity.CENTER, 0, 0);// ���λ��Ϊ�м� 100Ϊ������100dp
		toast.show();
	}

	// ��ȡ�ļ��ֻ�·��
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

	// ¼����ʱ�߳�
	void mythread()
	{
		recordThread = new Thread(ImgThread);
		recordThread.start();
	}

	// ¼���߳�
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
					// ¼������120���Զ�ֹͣ
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
							tvRecord.setText("��ס��ʼ¼��");
							RECODE_STATE = RECORD_NO;
						} else
						{
							tvRecord.setText("¼�����!�������¼��");
							BigDecimal b = new BigDecimal(recodeTime);
							if (recodeTime >= 120.0)
							{
								tvTime.setText("¼��ʱ����2'00\"");
							} else
							{
								tvTime.setText("¼��ʱ����"
										+ VoiceTools.getVoiceTime((b.setScale(0, BigDecimal.ROUND_HALF_UP)
												.intValue())));
							}
						}
					}
					break;
				case 1:
					// ��ʾ������С
					break;
				case 2:
					int time = 120 - (int) msg.arg1;
					if (time <= 10)
					{
						tvTime.setText(String.format("¼��ʱ�仹ʣ%s��", time));
					}
					break;

				default:
					break;
				}
			}
		};
	};

}