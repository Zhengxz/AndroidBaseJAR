package com.comvee.voiceinteraction.util;

public class VoiceTools {

	public static String getVoiceTime(int time)
	{
		String tempTime = null;
		int n = time / 60;
		int j = (time - 60 * n);
		if (j <= 0)
		{
			tempTime = n == 0 ? "00\"" : n + "'00\"";
		} else
		{
			if (j < 10)
			{
				tempTime = n == 0 ? j + "\"" : n + "'0" + j + "\"";
			} else
			{
				tempTime = n == 0 ? j + "\"" : n + "'" + j + "\"";
			}
		}
		return tempTime;
	}
}
