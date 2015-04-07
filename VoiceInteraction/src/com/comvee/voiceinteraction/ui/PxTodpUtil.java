package com.comvee.voiceinteraction.ui;

import android.content.Context;

public class PxTodpUtil {
	/**
	 * ¢Ù dip×ªpx
	 * */
	public static int dip2px(Context c, int dipValue) {
		float scale = c.getResources().getDisplayMetrics().density;
		if (scale == 0.0F)
			System.out.println("[scale] : 0");
			System.out.println("[scale] :  " + scale);
			System.out.println("[0.5f] :  0.5");
			System.out.println("[dipValue * scale + 0.5f] :  " + dipValue * scale + 0.5F);
			return (int)(dipValue * scale + 0.5F);
	}

}
