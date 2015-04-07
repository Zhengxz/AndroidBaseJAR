package com.zhengxizhen.http;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

/**
 * @Title APNManager
 * @package com.zhengxz_lib.http
 * @Description APN管理类
 * @author 郑锡真
 * @date 2015-02-04
 * @version 1.0.0
 * */
@SuppressLint("DefaultLocale")
public class APNManager {
    private static final Uri CURRENT_APN_URI = Uri
	    .parse("content://telephony/carriers/preferapn");

    public static final Uri APN_LIST_URI = Uri
	    .parse("content://telephony/carriers");
    private static ApnObject apn;

    public static ApnObject getCurrentAPN(Context context) {
	ConnectivityManager conManager = (ConnectivityManager) context
		.getSystemService("connectivity");
	NetworkInfo info = conManager.getNetworkInfo(0);
	String currentAPN = info.getExtraInfo();
	if (currentAPN != null)
	    try {
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(APN_LIST_URI, new String[] {
			"_id", "proxy", "port", "apn" },
			" apn = ? and current = 1",
			new String[] { currentAPN.toLowerCase() }, null);
		apn = new ApnObject();
		cursor.moveToNext();
		apn.setId(cursor.getInt(0));
		apn.setProxy(cursor.getString(1));
		apn.setPort(cursor.getInt(2));
		apn.apn = cursor.getString(3);
		return apn;
	    } catch (Exception localException) {
	    }
	else
	    return null;

	return null;
    }

    public static int updatePreferAPN(Context context, int apnid) {
	ContentValues value = new ContentValues();

	value.put("apn_id", Integer.valueOf(apnid));

	int updaterow = context.getContentResolver().update(CURRENT_APN_URI,
		value, null, null);
	return updaterow;
    }

    public static int updateCurrentAPN(ContentResolver resolver, String newAPN) {
	Cursor cursor = null;
	try {
	    cursor = resolver.query(APN_LIST_URI, null,
		    " apn = ? and current = 1",
		    new String[] { newAPN.toLowerCase() }, null);
	    String apnId = null;
	    if ((cursor != null) && (cursor.moveToFirst())) {
		apnId = cursor.getString(cursor.getColumnIndex("_id"));
	    }
	    cursor.close();

	    if (apnId != null) {
		ContentValues values = new ContentValues();
		values.put("apn_id", apnId);
		resolver.update(CURRENT_APN_URI, values, null, null);
	    } else {
		return 0;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    if (cursor != null)
		cursor.close();
	}
	if (cursor != null) {
	    cursor.close();
	}
	return 1;
    }
}
