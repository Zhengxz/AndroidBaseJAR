package com.zhengxizhen.tool;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

/**
 * @Title Tool
 * @package com.zhengxz_lib.tool
 * @Description 机器信息工具类
 * @author 郑锡真
 * @date 2015-03-17
 * @version 1.0.0
 * */
public class MachineUtil {

	/**
	 * 判断网络链接状态
	 * 
	 * @param context
	 *            当前句柄
	 * */
	public static boolean isNetWorkStatus(Context context) {
		ConnectivityManager cwjManager = (ConnectivityManager) context
				.getSystemService("connectivity");
		NetworkInfo netWrokInfo = cwjManager.getActiveNetworkInfo();

		return (netWrokInfo != null) && (netWrokInfo.isAvailable());
	}

	/**
	 * 跳转网络链接设置
	 * 
	 * @param fromPage
	 *            当前页面
	 * */
	public static final void goToSetNetwork(Activity fromPage) {
		Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
		fromPage.startActivity(intent);
	}

	/**
	 * 获取当前APP版本号
	 * 
	 * @param context
	 *            当前句柄
	 * @param packageName
	 *            程序包名
	 * */
	@SuppressWarnings("finally")
	public static int getAppVersionCode(Context context, String packageName) {
		int versionCode = 0;
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(packageName, 0);
			versionCode = pi.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return versionCode;
		}
	}

	/**
	 * 启动某个APP
	 * 
	 * @param activity
	 *            句柄
	 * @param packageName
	 *            APP包名
	 * */
	public static void RunApp(Activity activity, String packageName) {
		PackageInfo pi;
		try {
			pi = activity.getPackageManager().getPackageInfo(packageName, 0);
			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.setPackage(pi.packageName);
			PackageManager pManager = activity.getPackageManager();
			List<ResolveInfo> apps = pManager.queryIntentActivities(
					resolveIntent, 0);

			ResolveInfo ri = (ResolveInfo) apps.iterator().next();
			if (ri != null) {
				packageName = ri.activityInfo.packageName;
				String className = ri.activityInfo.name;
				Intent intent = new Intent(Intent.ACTION_MAIN);
				ComponentName cn = new ComponentName(packageName, className);
				intent.setComponent(cn);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activity.startActivity(intent);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取外置SD卡路径
	 * 
	 * @return
	 */
	public static String getSDCardPath() {

		return Environment.getExternalStorageDirectory().getPath();
	}

	/**
	 * 返回SD卡可用容量 --#
	 */
	@SuppressWarnings("deprecation")
	public static long getUsableStorage() {
		String sDcString = android.os.Environment.getExternalStorageState();

		if (sDcString.equals(android.os.Environment.MEDIA_MOUNTED)) {
			File pathFile = android.os.Environment
					.getExternalStorageDirectory();

			android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());

			long nAvailaBlock = statfs.getAvailableBlocks();

			long nBlocSize = statfs.getBlockSize();

			return nAvailaBlock * nBlocSize / 1024 / 1024;
		} else {
			return -1;
		}

	}

	/**
	 * 安装APK
	 * 
	 * @param activity
	 *            句柄
	 * @param apkpath
	 *            APK路径
	 * */
	public static boolean ReplaceLaunchApk(FragmentActivity activity,
			String apkpath) {
		File file = new File(apkpath);
		if (file.exists()) {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(android.content.Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file),
					"application/vnd.android.package-archive");
			activity.startActivity(intent);
			activity.finish();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断APK是否已安装
	 * */
	public static boolean checkApkExist(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName)) {
			return false;
		}
		try {
			ApplicationInfo info = context.getPackageManager()
					.getApplicationInfo(packageName,
							PackageManager.GET_UNINSTALLED_PACKAGES);
			if (info == null) {
				return false;
			} else {
				return true;
			}
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	/** 判断SD卡是否存在 */
	public static boolean SDCardExists() {
		return Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}
}