package com.comvee.voiceinteraction;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RecorderDialog implements DialogInterface.OnShowListener, DialogInterface.OnDismissListener,
		OnClickListener
{
	private static int MAX_TIME = 15; // �¼��ʱ�䣬��λ�룬0Ϊ��ʱ������
	private static int MIX_TIME = 1; // ���¼��ʱ�䣬��λ�룬0Ϊ��ʱ�����ƣ�������Ϊ1

	private static int RECORD_NO = 0; // ����¼��
	private static int RECORD_ING = 1; // ����¼��
	private static int RECODE_ED = 2; // ���¼��

	private static int RECODE_STATE = 0; // ¼����״̬

	private static float recodeTime = 0.0f; // ¼����ʱ��
	private static double voiceValue = 0.0; // ��˷��ȡ������ֵ

	private ImageView dialog_img;
	private static boolean playState = false; // ����״̬
	private AudioRecorder mr;
	private Thread recordThread;

	private Dialog Dialog;
	private Activity mActivity;
	private String strPath = "";// ¼���ļ�·��
	private TextView tvRecord;// ��ʾ�İ�

	public RecorderDialog(Activity mActivity, TextView tView, String mPath)
	{
		this.mActivity = mActivity;
		this.strPath = mPath;
		this.tvRecord = tView;

		initDialog(mActivity);
	}

	/**
	 * ʵ�����Ի���
	 * 
	 * @param mActivity
	 */
	private void initDialog(Activity mActivity)
	{
		// TODO Auto-generated method stub
		Dialog = new Dialog(mActivity, R.style.DialogStyle);
		Dialog.setOnDismissListener(this);
		Dialog.setOnShowListener(this);
		Dialog.setCanceledOnTouchOutside(true);
		Dialog.setContentView(R.layout.my_record_dialog);
		dialog_img = (ImageView) Dialog.findViewById(R.id.dialog_img);

		Window win = Dialog.getWindow();
		WindowManager.LayoutParams wl = win.getAttributes();
		int width = mActivity.getWindow().getWindowManager().getDefaultDisplay().getWidth();
		int height = mActivity.getWindow().getWindowManager().getDefaultDisplay().getHeight();
		wl.width = width / 2 + 50;
		wl.height = height / 2;
		// wl.alpha = 0.8f;
		wl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP; // �����õĻ�Ĭ���Ǿ���
		wl.y = 90;
		win.setAttributes(wl);
		// Dialog.show();
	}

	public void showRecorderDialog()
	{
		if (Dialog != null)
		{
			Dialog.show();
		}
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
			showRecorderDialog();
			try
			{
				mr.start();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tvRecord.setText("�ɿ��֣�ֹͣ¼��");
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
			if (Dialog.isShowing())
			{
				Dialog.dismiss();
			}
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
				tvRecord.setText("��ס��ʼ¼��");
				RECODE_STATE = RECORD_NO;
			} else
			{
				tvRecord.setText("¼�����!�������¼��");
				System.out.println("¼��ʱ�䣺" + ((int) recodeTime));
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
		return ((int) recodeTime);
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

	@Override
	public void onDismiss(DialogInterface dialog)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onShow(DialogInterface dialog)
	{
		// TODO Auto-generated method stub

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

	// ¼��DialogͼƬ��������С�л�
	void setDialogImage()
	{
		if (voiceValue < 200.0)
		{
			dialog_img.setImageResource(R.drawable.record_animate_01);
		} else if (voiceValue > 200.0 && voiceValue < 400)
		{
			dialog_img.setImageResource(R.drawable.record_animate_02);
		} else if (voiceValue > 400.0 && voiceValue < 800)
		{
			dialog_img.setImageResource(R.drawable.record_animate_03);
		} else if (voiceValue > 800.0 && voiceValue < 1600)
		{
			dialog_img.setImageResource(R.drawable.record_animate_04);
		} else if (voiceValue > 1600.0 && voiceValue < 3200)
		{
			dialog_img.setImageResource(R.drawable.record_animate_05);
		} else if (voiceValue > 3200.0 && voiceValue < 5000)
		{
			dialog_img.setImageResource(R.drawable.record_animate_06);
		} else if (voiceValue > 5000.0 && voiceValue < 7000)
		{
			dialog_img.setImageResource(R.drawable.record_animate_07);
		} else if (voiceValue > 7000.0 && voiceValue < 10000.0)
		{
			dialog_img.setImageResource(R.drawable.record_animate_08);
		} else if (voiceValue > 10000.0 && voiceValue < 14000.0)
		{
			dialog_img.setImageResource(R.drawable.record_animate_09);
		} else if (voiceValue > 14000.0 && voiceValue < 17000.0)
		{
			dialog_img.setImageResource(R.drawable.record_animate_10);
		} else if (voiceValue > 17000.0 && voiceValue < 20000.0)
		{
			dialog_img.setImageResource(R.drawable.record_animate_11);
		} else if (voiceValue > 20000.0 && voiceValue < 24000.0)
		{
			dialog_img.setImageResource(R.drawable.record_animate_12);
		} else if (voiceValue > 24000.0 && voiceValue < 28000.0)
		{
			dialog_img.setImageResource(R.drawable.record_animate_13);
		} else if (voiceValue > 28000.0)
		{
			dialog_img.setImageResource(R.drawable.record_animate_14);
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
					// ¼������15���Զ�ֹͣ
					if (RECODE_STATE == RECORD_ING)
					{
						RECODE_STATE = RECODE_ED;

						// if (Dialog.isShowing())
						// {
						// Dialog.dismiss();
						// }

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
							System.out.println("¼��ʱ�䣺" + ((int) recodeTime));
						}
					}
					break;
				case 1:
					setDialogImage();
					break;
				default:
					break;
				}

			}
		};
	};

}