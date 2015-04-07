package com.zhengxizhen.http;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @Title CacheMrg
 * @package com.zhengxz_lib.http
 * @Description 缓存管理
 * @author 郑锡真
 * @date 2015-02-04
 * @version 1.0.0
 * */
public class CacheMrg {
    /** 单一实例 */
    private static CacheMrg mrg;
    /** 本地缓存 */
    private SharedPreferences sp;

    private CacheMrg(Context cxt) {
	sp = cxt.getSharedPreferences("cache", 0);
    }

    /** 获取实例 */
    public static CacheMrg getInstance(Context cxt) {
	if (null == mrg) {
	    mrg = new CacheMrg(cxt.getApplicationContext());
	}
	return mrg;
    }

    /**
     * 获取缓存－－验证过期
     * */
    public String tryGetCache(String key) {
	if (checkLately(key)) {
	    return sp.getString(key, null);
	} else {
	    return null;
	}
    }

    /**
     * 获取缓存－－不在乎是否过期
     * */
    public String getStringCache(String key) {
	return sp.getString(key, null);
    }

    /**
     * 记录缓存
     * 
     * @param key
     *            ID
     * @param value
     *            值
     * @param duration
     *            缓存保持时长
     * */
    public boolean putStringCache(String key, String value, long duration) {
	sp.edit().putString(key, value)
		.putLong(key + "_duration_time", duration)
		.putLong(key + "_store_time", System.currentTimeMillis())
		.commit();
	return true;
    }

    /**
     * 删除缓存
     * */
    public void deleteCache(String key) {
	sp.edit().remove(key).remove(key + "_duration_time")
		.remove(key + "_store_time").commit();
    }

    /**
     * 清空所有缓存
     * */
    public void clear() {
	sp.edit().clear().commit();
    }

    /**
     * 判断缓存是否存在或过期
     * */
    public boolean checkLately(String key, long time) {

	if (!sp.contains(key)) {
	    return false;
	}

	long storeTime = sp.getLong(key + "_store_time", 0);
	final long duration = sp.getLong(key + "_duration_time", 0);

	if (storeTime != 0 && duration != 0 && time > storeTime + duration) {
	    return false;
	}

	return true;
    }

    /**
     * 判断缓存是否存在或过期
     * */
    public boolean checkLately(String key) {
	return checkLately(key, System.currentTimeMillis());
    }
}
