/**
 * �ļ���  VolumeViewer
 * ������   VolumeViewer
 * �ļ�����   ����ָʾ��
 * ��������  2013-5-13
 * �汾��Ϣ V1.0  
 * ����    ����֥
 * ��Ȩ���� Copyright (c) 2007��2013 Wootion.Co.Ltd. All rights reserved.
 */
package com.comvee.voiceinteraction.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
//import android.util.Log;
import android.view.View;

/**
 * 
 * ���� VolumeViewer
 * ���� ����ָʾ��
 * ����  toney
 * �������� 2013-10-13
 * �޸ļ�¼��
 *  ����          �汾      �޸���      ����
 *
 */
public class VolumeViewer extends View
{

//	private final static String TAG = "VolumeViewer";
	private Paint mPaint;
	private int mVolumeValue = 0;
	private boolean mIsFresh = true;

	public VolumeViewer(Context context) 
	{
		super(context);
		init(context);
	}
	
	public VolumeViewer(Context context, AttributeSet attrs) 
	{
		super(context,attrs);
		init(context);
	}

	//��ʼ��
	private void init(Context context) 
	{             
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);     
        mVolumeValue = 0;        
    }
	
	/**
	 * 
	 * ����������С��������UI
	 * <p>����������С��������UI</p>
	 * @param volume ������С
	 * @throws
	 */
	public void setVolumeValue(int volume)
	{
//		Log.d("VolumeViewer", "volume is "+volume);
		this.mVolumeValue = volume;
		if(!mIsFresh)  mIsFresh = true;
		
    }
	/**
	 * 
	 * ֹͣ����ͼ��
	 * <p>ֹͣ����ͼ��</p>
	 * @throws
	 */
	public void stopFresh()
	{
		mIsFresh = false;
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
	
		//����Ӧ��ʾ�ĸ�����ָʾ��
		final int height = getHeight();
//		Log.d(TAG, "height:"+height);
		for (int i = 1; i <= mVolumeValue; i++)
		{
			int top = height - i * 20;//���μ�ľ��� 20-12
			canvas.drawRect(0, top, 10+i*5, top + 12, mPaint);//���ο�Ȳ�����
		}
		
		if(mIsFresh) 
		{
			postInvalidateDelayed(10);
		}
		
	}	
}
