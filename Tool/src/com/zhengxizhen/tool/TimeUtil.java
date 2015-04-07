package com.zhengxizhen.tool;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author zhengxizhen
 * @version 1.0.0
 * @Title Tool
 * @package com.zhengxizhen.tool
 * @Description 工具类
 * @date 2015-03-17
 * */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {
	/**
	 * 时间格式：yyyy-MM-dd
	 * */
	public final static String FORMAT_YMD = "yyyy-MM-dd";
	/**
	 * 时间格式：HH:mm［HH 24小时制 hh 12小时制度］
	 * */
	public final static String FORMAT_HM = "HH:mm";
	/**
	 * 时间格式：yyyy-MM-dd HH:mm［HH 24小时制 hh 12小时制度］
	 * */
	public final static String FORMAT_YMD_HM = "yyyy-MM-dd HH:mm";
	/**
	 * 时间格式：yyyy-MM-dd HH:mm:ss［HH 24小时制 hh 12小时制度］
	 * */
	public final static String FORMAT_YMD_HMS = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 时间格式：MM月dd日 HH:mm［HH 24小时制 hh 12小时制度］
	 * */
	public final static String FORMAT_MONTH_DAY_TIME = "MM月dd日 HH:mm";
	/**
	 * 时间格式：MM-dd
	 * */
	public final static String FORMAT_MONTH_DAY = "MM-dd";

	private static SimpleDateFormat sdf = new SimpleDateFormat();
	private static final int YEAR = 365 * 24 * 60 * 60;
	private static final int MONTH = 30 * 24 * 60 * 60;
	private static final int DAY = 24 * 60 * 60;
	private static final int HOUR = 60 * 60;
	private static final int MINUTE = 60;

	/**
	 * 对比当前时间差多少
	 * 
	 * @param timestamp
	 *            节点时间（long）
	 * */
	public static String getDescriptionTimeFromTimestamp(long timestamp) {
		long currentTime = System.currentTimeMillis();
		long timeGap = (currentTime - timestamp) / 1000;
		String timeStr = null;
		if (currentTime > timestamp) {
			if (timeGap > YEAR) {
				timeStr = timeGap / YEAR + "年前";
			} else if (timeGap > MONTH) {
				timeStr = timeGap / MONTH + "个月前";
			} else if (timeGap > DAY) {
				timeStr = timeGap / DAY + "天前";
			} else if (timeGap > HOUR) {
				timeStr = timeGap / HOUR + "小时前";
			} else if (timeGap > MINUTE) {
				timeStr = timeGap / MINUTE + "分钟前";
			} else {
				timeStr = "刚刚";
			}
		} else {
			if (timeGap < 0 - YEAR) {
				timeStr = timeGap / YEAR + "年后";
			} else if (timeGap < 0 - MONTH) {
				timeStr = timeGap / MONTH + "个月后";
			} else if (timeGap < 0 - DAY) {
				timeStr = timeGap / DAY + "天后";
			} else if (timeGap < 0 - HOUR) {
				timeStr = timeGap / HOUR + "小时后";
			} else if (timeGap < 0 - MINUTE) {
				timeStr = timeGap / MINUTE + "分钟后";
			} else {
				timeStr = "刚刚";
			}
		}
		return timeStr;
	}

	/**
	 * 时间格式转化
	 * 
	 * @param timestamp
	 *            节点时间（long）
	 * @param format
	 *            时间格式
	 * */
	public static String getFormatTimeFromTimestamp(long timestamp,
			String format) {
		if (format == null || format.trim().equals("")) {
			sdf.applyPattern(FORMAT_YMD);
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			int year = Integer.valueOf(sdf.format(new Date(timestamp))
					.substring(0, 4));
			if (currentYear == year) {
				sdf.applyPattern(FORMAT_MONTH_DAY_TIME);
			} else {
				sdf.applyPattern(FORMAT_YMD_HM);
			}
		} else {
			sdf.applyPattern(format);
		}
		Date date = new Date(timestamp);
		return sdf.format(date);
	}

	/**
	 * 获取最小时间差
	 * 
	 * @param timestamp
	 *            节点时间（long）
	 * @param partionSeconds
	 *            最小基数
	 * @param format
	 *            时间格式
	 * @return 1、getDescriptionTimeFromTimestamp 2、getFormatTimeFromTimestamp
	 * */
	public static String getMixTimeFromTimestamp(long timestamp,
			long partionSeconds, String format) {
		long currentTime = System.currentTimeMillis();
		long timeGap = (currentTime - timestamp) / 1000;
		if (timeGap <= partionSeconds) {
			return getDescriptionTimeFromTimestamp(timestamp);
		} else {
			return getFormatTimeFromTimestamp(timestamp, format);
		}
	}

	/**
	 * 获取当前时间字符串
	 * 
	 * @param format
	 *            时间格式
	 * */
	public static String getCurrentTime(String format) {
		if (format == null || format.trim().equals("")) {
			sdf.applyPattern(FORMAT_YMD_HM);
		} else {
			sdf.applyPattern(format);
		}
		return sdf.format(new Date());
	}

	/**
	 * 时间转化 ( String to Calendar)
	 * 
	 * @param timeStr
	 *            节点时间（String）
	 * @param format
	 *            时间格式
	 * */
	public static Calendar getCalendarFromString(String timeStr, String format)
			throws ParseException {
		if (format == null || format.trim().equals("")) {
			sdf.applyPattern(FORMAT_YMD_HM);
		} else {
			sdf.applyPattern(format);
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(sdf.parse(timeStr));
		return calendar;
	}

	/**
	 * 时间转化（java.util.Date to String）
	 * 
	 * @param time
	 *            节点时间（java.util.Date）
	 * @param format
	 *            时间格式
	 * */
	public static String getStringFromTime(Date time, String format) {
		if (format == null || format.trim().equals("")) {
			sdf.applyPattern(FORMAT_YMD_HM);
		} else {
			sdf.applyPattern(format);
		}
		return sdf.format(time);
	}

	/**
	 * 时间转化（long to String）
	 * 
	 * @param utd
	 *            节点时间（long）
	 * @param format
	 *            时间格式
	 * */
	public static final String fomateTime(long utd, String fomate) {
		SimpleDateFormat sdf = new SimpleDateFormat(fomate);
		return sdf.format(new Date(utd));
	}

	/**
	 * 时间格式转化
	 * 
	 * @param fomateFrom
	 *            时间格式1
	 * @param fomateTo
	 *            时间格式2
	 * @param time
	 *            时间节点（String）
	 * */
	public static final String fomateTime(String fomateFrom, String fomateTo,
			String time) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(fomateFrom);
		Date date = sdf.parse(time);
		sdf = new SimpleDateFormat(fomateTo);
		return sdf.format(date);
	}

	/**
	 * 时间对比(Calendar)[是否同一天]
	 * 
	 * @param c1
	 * @param c2
	 * */
	public static final boolean isSameDay(Calendar c1, Calendar c2) {
		int m1 = c1.get(2);
		int m2 = c2.get(2);
		int y1 = c1.get(1);
		int y2 = c2.get(1);
		int d1 = c1.get(5);
		int d2 = c2.get(5);
		return (m1 == m2) && (y1 == y2) && (d1 == d2);
	}

	/**
	 * 时间对比(long)[是否同一天]
	 * 
	 * @param utc1
	 * @param utc2
	 * */
	public static final boolean isOffer24hour(long utc1, long utc2) {
		return Math.abs(utc1 - utc2) / 3600000L < 24L;
	}

	/**
	 * 时间对比(long)[是否同一天]
	 * 
	 * @param utc1
	 * @param utc2
	 * */
	public static final boolean isSameDay(long utc1, long utc2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(utc1);
		Calendar c2 = Calendar.getInstance();
		c2.setTimeInMillis(utc2);
		return isSameDay(c1, c2);
	}

	/**
	 * 相差天数
	 * 
	 * @param utc1
	 * @param utc2
	 * @return 天数（int）
	 * */
	public static final int getOfferDay(long utc1, long utc2) {
		return (int) Math.ceil((float) Math.abs(utc1 - utc2) / 86400000.0F);
	}

	/**
	 * 相差天数
	 * 
	 * @param pointTime
	 * @param startTime
	 * @return 天数（float）
	 * */
	public static final float getOfferDayReFloat(long pointTime, long startTime) {
		float time = 0.0f;
		long l = pointTime - startTime;
		long day = l / (24 * 60 * 60 * 1000);

		if (day > 0d) {
			time += day;
		}
		return time;
	}

	/**
	 * 是否同一个月(Calendar)
	 * 
	 * @param c1
	 * @param c2
	 * */
	public static final boolean isSameMouth(Calendar c1, Calendar c2) {
		int m1 = c1.get(2);
		int m2 = c2.get(2);
		int y1 = c1.get(1);
		int y2 = c2.get(1);
		return (m1 == m2) && (y1 == y2);
	}

	/**
	 * 转化UTC时间
	 * 
	 * @param time
	 *            时间节点（String）
	 * @param format
	 *            时间格式
	 * */
	public static final long getUTC(String time, String format)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = sdf.parse(time);
		return date.getTime();
	}

	/**
	 * 对比时间大小
	 * 
	 * @param time1
	 *            时间节点（long）
	 * @param time2
	 *            时间节点（long）
	 * @return time1 < time2 = [true] 或者 time1 >= time2 = [false]
	 * */
	public static final boolean isBefore(long time1, long time2) {
		return time1 < time2;
	}
}
