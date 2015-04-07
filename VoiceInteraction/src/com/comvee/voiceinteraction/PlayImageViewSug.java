package com.comvee.voiceinteraction;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PlayImageViewSug extends ImageView {
    private AnimationDrawable aDrawable;

    public PlayImageViewSug(Context context) {
	super(context);
	init(context);
    }

    public PlayImageViewSug(Context context, AttributeSet attrs) {
	super(context, attrs);
	init(context);
    }

    public PlayImageViewSug(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	init(context);
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
	int resId = R.anim.anim_voice_play_left_sug;
	this.setImageResource(resId);
	this.postInvalidate();
    }

    private void setStopImage() {
	int resId = R.drawable.voice_play_left_03;
	this.setImageResource(resId);
	this.postInvalidate();
    }

}