package com.zhengxizhen.http;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;

/**
 * @Title HttpClientController
 * @package com.zhengxz_lib.http
 * @Description 网络链接控制器
 * @author 郑锡真
 * @date 2015-02-04
 * @version 1.0.0
 * */
public class HttpClientController {
    private static final int TIMEOUT = 15000;

    private HttpClientController() {
    }

    public static HttpClient getNewInstance(Context mContext) {
	SSLSocketFactory sf = null;
	try {
	    KeyStore trustStore = KeyStore.getInstance(KeyStore
		    .getDefaultType());
	    trustStore.load(null, null);

	    sf = new MySSLSocketFactory(trustStore);
	    sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	HttpClient newInstance;
	HttpParams params = new BasicHttpParams();
	HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	HttpProtocolParams.setContentCharset(params,
		HTTP.DEFAULT_CONTENT_CHARSET);

	ConnManagerParams.setTimeout(params, TIMEOUT);
	HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
	HttpConnectionParams.setSoTimeout(params, TIMEOUT);

	SchemeRegistry schReg = new SchemeRegistry();
	schReg.register(new Scheme("http", PlainSocketFactory
		.getSocketFactory(), 80));
	if (sf != null) {
	    schReg.register(new Scheme("https", sf, 443));
	} else {
	    schReg.register(new Scheme("https", SSLSocketFactory
		    .getSocketFactory(), 443));
	}

	ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
		params, schReg);
	newInstance = new DefaultHttpClient(conMgr, params);
	return newInstance;
    }

    public static HttpResponse execute(Context context,
	    HttpUriRequest paramHttpUriRequest) throws ClientProtocolException,
	    IOException {
	HttpResponse response = getNewInstance(context).execute(
		paramHttpUriRequest);

	return response;
    }

    public static class MySSLSocketFactory extends SSLSocketFactory {
	SSLContext sslContext = SSLContext.getInstance("TLS");

	public MySSLSocketFactory(KeyStore truststore)
		throws NoSuchAlgorithmException, KeyManagementException,
		KeyStoreException, UnrecoverableKeyException {
	    super(truststore);

	    TrustManager tm = new X509TrustManager() {
		public void checkClientTrusted(X509Certificate[] chain,
			String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain,
			String authType) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
		    return null;
		}
	    };
	    sslContext.init(null, new TrustManager[] { tm }, null);
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port,
		boolean autoClose) throws IOException, UnknownHostException {
	    return sslContext.getSocketFactory().createSocket(socket, host,
		    port, autoClose);
	}

	@Override
	public Socket createSocket() throws IOException {
	    return sslContext.getSocketFactory().createSocket();
	}
    }
}
