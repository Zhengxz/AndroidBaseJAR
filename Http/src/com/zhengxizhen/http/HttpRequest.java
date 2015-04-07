package com.zhengxizhen.http;

import com.zhengxizhen.tool.MachineUtil;

import android.content.Context;
import android.util.Log;

/**
 * @Title HttpRequest
 * @package com.zhengxizhen.http
 * @Description 网络请求类
 * @author 郑锡真
 * @date 2015-02-04
 * @version 1.0.0
 * */
public class HttpRequest extends BaseHttpRequest {

	public final static int ERRCODE_NETWORK = 1004;
	private Context mContext;
	private HttpRequestListener listener;

	/**
	 * 实例化网络请求
	 * 
	 * @param cxt
	 *            句柄
	 * @param url
	 *            接口地址
	 * */
	public HttpRequest(Context cxt, String url) {
		super(cxt, url);
		if (DEBUG && url != null) {
			Log.v("http", url);
		}
		this.mContext = cxt.getApplicationContext();
		/***************** 初始化 通用请求 Key－value ********************/
		// setPostValueForKey("key","value");
		/**************** end *********************/
	}

	/**
	 * 实例化网络请求
	 * 
	 * @param cxt
	 *            句柄
	 * @param url
	 *            接口地址
	 * @param duration
	 *            缓存期限
	 * @param getCache
	 *            是否存储缓存
	 * */
	public HttpRequest(Context cxt, String url, long duration, boolean getCache) {
		super(cxt, url, duration, getCache);
		if (DEBUG && url != null) {
			Log.v("http", url);
		}
		this.mContext = cxt.getApplicationContext();
		/***************** 初始化 通用请求 Key－value ********************/
		// setPostValueForKey("key","value");
		/**************** end *********************/
	}

	@Override
	public void startAsynchronous() {
		if (!checkCache(mCachKey) && checkNetwork()) {
			HttpRequest.super.startAsynchronous();
		}
	}

	/** 判断网络状态 */
	public boolean checkNetwork() {
		boolean hasNet = MachineUtil.isNetWorkStatus(mContext);
		if (!hasNet) {
			loadFailed(getThreadId(), ERRCODE_NETWORK);
		}
		return hasNet;
	}

	/**
	 * 设置回调事件
	 * */
	public void setOnHttpListener(int nThreadID, HttpRequestListener callBack) {
		setThreadId(nThreadID);
		this.listener = callBack;
	}

	@Override
	public final void setListener(int nThreadID,
			HttpBaseRequestListener callBack) {
		super.setListener(nThreadID, callBack);
	}

	/**
	 * 检查缓存
	 * */
	private boolean checkCache(String key) {
		if (CacheMrg.getInstance(mContext).checkLately(mCachKey)) {
			if (listener != null) {
				listener.onSussece(getThreadId(), CacheMrg
						.getInstance(mContext).getStringCache(mCachKey)
						.getBytes(), true);
				if (DEBUG) {
					Log.v("http", "----------->data is from cache<------------");
				}
			}
			return true;
		}
		return false;
	}

	@Override
	protected void loadFinished(int nThreadID, final byte[] b) {
		super.loadFinished(nThreadID, b);
		if (listener != null) {
			listener.onSussece(nThreadID, b, false);
		}
	}

	@Override
	protected void loadFailed(final int nThreadID, final int nErrorCode) {
		try {
			super.loadFailed(nThreadID, nErrorCode);
			if (listener != null) {
				listener.onFialed(nThreadID, nErrorCode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
