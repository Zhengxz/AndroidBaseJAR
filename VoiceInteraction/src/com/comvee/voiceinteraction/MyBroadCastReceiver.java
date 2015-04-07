package com.comvee.voiceinteraction;

import android.content.Context;
import android.content.Intent;

public class MyBroadCastReceiver extends android.content.BroadcastReceiver
{

	@Override
	public void onReceive(Context arg0, Intent arg1)
	{
		if (arg1.getAction().equals("com.myly.record.ServiceStart"))
		{
			Intent service = new Intent("com.myly.record.record_start");
			service.setClass(arg0, RecordService.class);
			arg0.startService(service);
		} else if (arg1.getAction().equals("com.myly.record.ServiceStop"))
		{
			Intent service = new Intent();
			service.setClass(arg0, RecordService.class);
			arg0.stopService(service);
		}
	}

}