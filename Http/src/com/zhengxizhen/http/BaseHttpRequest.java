package com.zhengxizhen.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.zhengxizhen.tool.LogUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

/**
 * @Title BaseHttpRequest
 * @package com.zhengxizhen.http
 * @Description Http请求基类
 * @author 郑锡真
 * @date 2015-02-04
 * @version 1.0.0
 * */
@SuppressLint("DefaultLocale")
public class BaseHttpRequest {

	private Context mContext;
	private String strURL = "";

	private HashMap<String, String> headers = new HashMap<String, String>();
	private HashMap<String, String> postData = new HashMap<String, String>();

	private HttpBaseRequestListener callBack;
	private int nThreadID = 0;

	public static int ERRCODE_NETWORK = 1001;
	public static int ERRCODE_READ = 1002;
	public static int ERRCODE_HTTP = 1003;
	public static int SUCCESS = 1000;

	public static boolean DEBUG;
	public String mCachKey;
	private boolean mNeedGetCach;
	private long mCacheDuration = 60000l;

	private RequestTask mTask;

	public int getThreadId() {
		return nThreadID;
	}

	public void setThreadId(int what) {
		nThreadID = what;
		mCachKey = String.format("%d", what);
	}

	public HttpBaseRequestListener getCallBack() {
		return callBack;
	}

	@SuppressWarnings("deprecation")
	private static String concatParams(NameValuePair[] params) {
		if (params == null || params.length <= 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < params.length; i++) {
			NameValuePair param = params[i];
			if (i == 0) {
				sb.append("?");
			} else {
				sb.append("&");
			}
			sb.append(URLEncoder.encode(param.getName()) + "="
					+ URLEncoder.encode(param.getValue()));
		}
		return sb.toString();
	}

	public void setProxy(HttpClient httpclient, String proxy, int port) {
		if ((proxy != null) && (!"".equals(proxy))) {
			System.out.println("proxy:" + proxy + "--------" + "port:" + port);
			HttpHost host = new HttpHost(proxy, port, "http");
			httpclient.getParams().setParameter("http.route.default-proxy",
					host);
		}
	}

	private void checkAPN(HttpClient client) {
		WifiManager wifiManager = (WifiManager) this.mContext
				.getSystemService("wifi");
		if (!wifiManager.isWifiEnabled()) {
			String proxy = null;
			int port = 0;
			ApnObject apn = APNManager.getCurrentAPN(this.mContext);
			if ((apn != null) && (!TextUtils.isEmpty(apn.getProxy()))) {
				proxy = apn.getProxy();
				port = apn.getPort();
				setProxy(client, proxy, port);
			}
		}
	}

	public static BaseHttpRequest requestWithURL(Context context,
			String baseUrl, NameValuePair... params) {
		String url = baseUrl + concatParams(params);
		BaseHttpRequest request = new BaseHttpRequest(context, url);
		return request;
	}

	public static BaseHttpRequest requestWithURL(Context context, String url) {
		BaseHttpRequest request = new BaseHttpRequest(context, url);
		return request;
	}

	public BaseHttpRequest(Context context, String url) {
		this.mContext = context;
		setURL(url);
		postData.clear();
		setCachDuration(0l, false);
	}

	public BaseHttpRequest(Context context, String url, long duration,
			boolean getCache) {
		this.mContext = context;
		setURL(url);
		postData.clear();
		setCachDuration(duration, getCache);
	}

	public BaseHttpRequest(Context context) {
		this.mContext = context;
		setCachDuration(0l, false);
		postData.clear();
		headers.clear();
	}

	// 链接地址设置
	public void setURL(String url) {
		strURL = url;
	}

	// 头部设置
	public void setHeaderValueForKey(String key, String value) {
		if (value == null) {
			headers.put(key, "");
		} else {
			headers.put(key, value);
		}
	}

	// POST参数数据键值
	public void setPostValueForKey(String key, String value) {
		if (value == null) {
			postData.put(key, "");
		} else {
			postData.put(key, value);
			if (DEBUG) {
				Log.i("lib_http", key + ":" + value);
			}
		}
	}

	public void setHeaders(HttpUriRequest request) {
		if (headers.size() <= 0) {
			return;
		}

		Set<String> headerKey = headers.keySet();
		for (String key : headerKey) {
			String value = headers.get(key);
			request.addHeader(key, value);
		}
		request.setHeader("User-Agent",
				System.getProperties().getProperty("http.agent")
						+ " SurfingClub");
	}

	public String encodeParameters(HashMap<String, String> postParams) {
		if (postParams.size() <= 0) {
			return "";
		}

		StringBuilder buf = new StringBuilder();
		int j = 0;
		Set<String> keyset = postParams.keySet();
		for (String key : keyset) {
			String value = postParams.get(key);
			if (j != 0) {
				buf.append("&");
			}
			try {
				buf.append(URLEncoder.encode(key, "UTF-8")).append("=")
						.append(URLEncoder.encode(value, "UTF-8"));
			} catch (java.io.UnsupportedEncodingException neverHappen) {
			}
			j++;
		}
		return buf.toString();
	}

	private HttpResponse requestHttp() {
		HttpResponse response = null;
		try {
			HttpUriRequest request = null;
			HttpClient client = HttpClientController.getNewInstance(mContext);
			checkAPN(client);

			if (postData.size() > 0) // POST信息
			{
				HttpPost httpPost = new HttpPost(strURL);
				request = httpPost;

				String postParam = encodeParameters(postData);

				if (DEBUG) {
					Log.v("http", "请求数据--->" + postParam);
				}
				byte[] data = postParam.getBytes("UTF-8");
				ByteArrayEntity formEntity = new ByteArrayEntity(data);
				httpPost.setEntity(formEntity);

				request.setHeader("Content-Type",
						"application/x-www-form-urlencoded");
				setHeaders(request);
				response = client.execute(request);
			} else // GET信息
			{
				HttpGet httpGet = new HttpGet(strURL);
				request = httpGet;
				setHeaders(request);
				response = client.execute(request);// MyHttpClient.execute(mContext,
				// request);
			}

		} catch (Exception e) {
			LogUtil.e(new Throwable().getStackTrace()[0].toString()
					+ " Exception ", e);
		}
		return response;
	}

	// ////////////////////////////////////////////////////////////////////////
	//
	//
	// 同步读取文件
	//
	//
	// ////////////////////////////////////////////////////////////////////////
	public InputStream startSynchronous() {
		InputStream is = null;
		HttpResponse response = requestHttp();
		if (response == null) {
			return null;
		}

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) // OK
			{
				is = new BufferedInputStream(response.getEntity().getContent());
				return is;
			}
		} catch (Exception e) {
			LogUtil.e(new Throwable().getStackTrace()[0].toString()
					+ " Exception ", e);
		}
		return is;
	}

	public byte[] startSyncRequestString() {
		HttpResponse response = requestHttp();

		if (response == null) {
			return null;
		}

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				byte bResult[] = read(response);
				return bResult;
			} else {
				return null;
			}

		} catch (Exception e) {
			LogUtil.e(new Throwable().getStackTrace()[0].toString()
					+ " Exception ", e);
			return null;

		}
	}

	public Bitmap startSyncRequestBitmap() {
		InputStream is = startSynchronous();
		if (is == null) {
			return null;
		}
		Bitmap bitmap = BitmapFactory.decodeStream(is);
		return bitmap;
	}

	// ////////////////////////////////////////////////////////////////////////
	//
	//
	// 异步读取
	//
	//
	// ////////////////////////////////////////////////////////////////////////

	public void setListener(int nThreadID, HttpBaseRequestListener callBack) {
		this.callBack = callBack;
		this.nThreadID = nThreadID;
	}

	protected void loadFinished(final int nThreadID, final byte[] b) {
		if (callBack != null) {
			callBack.loadFinished(nThreadID, b);
			if (getNeedGetCach() && getmCacheDuration() != 0) {
				CacheMrg.getInstance(mContext).putStringCache(mCachKey,
						new String(b), getmCacheDuration());
				if (DEBUG) {
					Log.v("http", "----------->data to cache<------------");
				}
			}
		}
	}

	protected void loadFailed(final int nThreadID, final int nErrorCode) {
		if (callBack != null)
			callBack.loadFailed(nThreadID, nErrorCode);
	}

	class RequestTask extends AsyncTask<String, String, Message> {

		@Override
		protected Message doInBackground(String... arg0) {
			HttpResponse response = requestHttp();
			Message msg = new Message();
			msg.what = nThreadID;

			if (response == null) {
				Log.e("lib_http", "ERRCODE_NETWORK");
				msg.arg1 = ERRCODE_NETWORK;
				return msg;
			}

			try {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					byte bResult[] = read(response);
					msg.arg1 = SUCCESS;
					msg.obj = bResult;
					return msg;
				} else {
					String result = new String(read(response));
					int errCode = statusCode;
					try {
						JSONObject json = new JSONObject(result);
						errCode = json.getInt("error_code");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Log.e("lib_http", "ERRCODE_NETWORK");
					msg.arg1 = errCode;
					return msg;
				}

			} catch (Exception e) {
				msg.arg1 = ERRCODE_READ;
				LogUtil.e(new Throwable().getStackTrace()[0].toString()
						+ " Exception ", e);
				return msg;

			}

		}

		@Override
		protected void onPostExecute(Message result) {
			if (result.arg1 == SUCCESS) {
				loadFinished(result.what, (byte[]) result.obj);
			} else {
				loadFailed(result.what, result.arg1);
			}
			super.onPostExecute(result);
		}

	}

	// 异步读取数据
	public void startAsynchronous() {
		mTask = new RequestTask();
		mTask.execute();
	}

	private byte[] read(HttpResponse response) throws Exception {
		HttpEntity entity = response.getEntity();
		InputStream inputStream;
		try {
			inputStream = entity.getContent();
			ByteArrayOutputStream content = new ByteArrayOutputStream();

			Header header = response.getFirstHeader("Content-Encoding");
			if (header != null
					&& header.getValue().toLowerCase().indexOf("gzip") > -1) {
				inputStream = new GZIPInputStream(inputStream);
			}

			int readBytes = 0;
			byte[] sBuffer = new byte[512];
			while ((readBytes = inputStream.read(sBuffer)) != -1) {
				content.write(sBuffer, 0, readBytes);
			}
			return content.toByteArray();
		} catch (IllegalStateException e) {
			throw new Exception(e);
		} catch (IOException e) {
			throw new Exception(e);
		}
	}

	public void shutdown() {
		if (null != mTask) {
			try {
				mTask.cancel(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// executor.shutdownNow();
	}

	/**
	 * 设置缓存时间
	 * 
	 * @param duration
	 */
	public void setCachDuration(long duration, boolean getCache) {
		setmCacheDuration(duration);
		setNeedGetCach(getCache);
	}

	/**
	 * 设置是否需要缓存
	 * */
	public void setNeedGetCach(boolean need) {
		mNeedGetCach = need;
	}

	public boolean getNeedGetCach() {
		return mNeedGetCach;
	}

	public long getmCacheDuration() {
		return mCacheDuration;
	}

	public void setmCacheDuration(long mCacheDuration) {
		this.mCacheDuration = mCacheDuration;
	}

	public interface HttpBaseRequestListener {
		public void loadFinished(int nThreadID, byte[] b);

		public void loadFailed(int nThreadID, int nErrorCode);
	}
}
