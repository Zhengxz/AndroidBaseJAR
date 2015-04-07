package com.comvee.voiceinteraction;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PlayImageView extends ImageView {
    private boolean isLeft = true;

    private AnimationDrawable aDrawable;

    public PlayImageView(Context context) {
	super(context);
	// TODO Auto-generated constructor stub
	init(context);
    }

    public PlayImageView(Context context, AttributeSet attrs) {
	super(context, attrs);
	// TODO Auto-generated constructor stub
	init(context);
    }

    public PlayImageView(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	// TODO Auto-generated constructor stub
	init(context);
    }

    /**
     * ����ͼƬ����
     * 
     * @param isleft
     *            true:��� false:�Ҳ�
     * */
    public void setIsLeft(boolean isleft) {
	this.isLeft = isleft;
	setStopImage();
    }

    private void init(Context cxt) {
	setStopImage();
	this.setScaleType(ScaleType.CENTER);
    }

    public void play() {
	setAnimImage();
	aDrawable = (AnimationDrawable) this.getDrawable();
	aDrawable.setOneShot(false);
	aDrawable.start();
    }

    public void stop() {
	if (aDrawable != null) {
	    aDrawable.stop();
	    setStopImage();
	}
    }

    public boolean isPlay() {
	if (aDrawable != null && aDrawable.isRunning()) {
	    return true;
	} else {
	    return false;
	}
    }

    private void setAnimImage() {
	int resId = R.anim.anim_voice_play_right;
	if (isLeft) {
	    resId = R.anim.anim_voice_play_left;
	}
	this.setImageResource(resId);
	this.postInvalidate();
    }

    private void setStopImage() {
	int resId = R.drawable.voice_stop_right;
	if (isLeft) {
	    resId = R.drawable.voice_play_left_03;
	}
	this.setImageResource(resId);
	this.postInvalidate();
    }

}