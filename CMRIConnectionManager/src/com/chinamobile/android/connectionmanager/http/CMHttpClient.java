package com.chinamobile.android.connectionmanager.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.http.client.methods.HttpPost;

import android.content.Context;
import android.util.Log;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.util.Constants;
import com.chinamobile.android.connectionmanager.util.LogUtil;
import com.chinamobile.android.connectionmanager.util._3GUtil;

/**
 * the client to do HTTP connection
 *
 */
public class CMHttpClient {
	private static final String TAG = "HttpClient";
	private HttpURLConnection conn = null;
	private InputStream inputStream = null;
	private OutputStream outputStream = null;
	private Context context;
	
	public CMHttpClient() {
		context = AppApplication.getApp();
	}
	
	/**
	 * get {@link CMResponse}
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public CMResponse getResponse(CMRequest request)
			throws Exception {
		if (request == null) {
			return null;
		}

		CMResponse resp = createResponse(request);
		
		if (resp == null) {
			return null;
		}
		
		if (!getResponse(request, resp)) {
			return null;
		}
		
		
		return resp;
	}
	
	/**
	 * create the {@link CMResponse}
	 * @param request
	 * @return
	 */
	private CMResponse createResponse(CMRequest request) {
		final byte type = request.getType();
		if (type == CMRequest.TYPE_REPORT) {
			ReportResponse report = new ReportResponse();
			report.setUid(request.getUid());
			return report;
		} else if (type == CMRequest.TYPE_OMA) {
			OMAResponse oma = new OMAResponse();
			oma.setUid(request.getUid());
			return oma;
		}
		return null;
	}
	
	/**
	 * get {@link CMResponse}
	 * @param subRequest
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private boolean getResponse(CMRequest subRequest, CMResponse response) throws Exception {
		try {
			makeConnection(subRequest.getUrl());
			setProperty(subRequest);
			setContent(subRequest);
			final int responseCode = conn.getResponseCode();
//			if (responseCode != HttpURLConnection.HTTP_OK) {
//				return false;
//			} else {
				inputStream = conn.getInputStream();

				final boolean isCmwap;
				final String netType = _3GUtil.NetType(context);
				if (netType != null
						&& (netType.equalsIgnoreCase("cmwap") 
								|| netType.equalsIgnoreCase("uniwap")
								|| netType.equalsIgnoreCase("ctwap"))) {
					isCmwap = true;
				} else {
					isCmwap = false;
				}

				response.setData(conn, inputStream, subRequest, isCmwap);
				return true;
//			}
		} catch (Exception e) {
//			e.printStackTrace();
			throw e;
		} finally {
			cleanup();
		}
	}
	
	/**
	 * inital the {@link HttpURLConnection}
	 * @param requestUrl
	 */
	private void makeConnection(final String requestUrl) {
		String tmpUrl = requestUrl;
		if (requestUrl == null) {
			Log.e(TAG, "No url!!!!!");
			return;
		}
		URL url = null;
		try {
			url = new URL("http://" + tmpUrl);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		conn = openConnection(url);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setConnectTimeout(Constants.HTTP_OVER_TIME);
	}
	
	/**
	 * open connection
	 * <br>add proxy to 3G
	 * @param url
	 * @return
	 */
	public HttpURLConnection openConnection(URL url) {
		final String netType = _3GUtil.NetType(context);
		if (netType != null
				&& (netType.equalsIgnoreCase("cmwap") 
						|| netType.equalsIgnoreCase("uniwap")
						|| netType.equalsIgnoreCase("ctwap"))) {
			String[] proxy = _3GUtil.getHostAndProxy(context);
			if (proxy == null) {
				return null;
			}

			try {
				Proxy p = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
						proxy[0], Integer.parseInt(proxy[1])));
				return (HttpURLConnection) url.openConnection(p);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} else {
			try {
				return (HttpURLConnection) url.openConnection();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}
	
	/**
	 * set parameters into request head
	 * @param request
	 * @throws Exception
	 */
	private void setProperty(CMRequest request) throws Exception {
		final Hashtable table = request.getRequestProperty();
		if (table != null) {
			Enumeration keys = table.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				String value = (String) table.get(key);
				conn.setRequestProperty(key, value);
			}
		}
	}
	
	/**
	 * set data as parameter in request data
	 * @param request
	 * @throws Exception
	 */
	public void setContent(CMRequest request) throws Exception {
		final byte[] content = request.getContent();
		if (content != null) {
			if (conn.getRequestProperty("Content-Type") == null) {
				final int contentLength = request.getContentLength();
				conn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				conn.setRequestProperty("Content-Length",
						Integer.toString(contentLength));
			}
			conn.setRequestMethod(HttpPost.METHOD_NAME);

			outputStream = conn.getOutputStream();
			outputStream.write(content);
		}
	}
	
	/**
	 * disconnect
	 */
	public void disConnect() {
		if (conn != null) {
			conn.disconnect();
			conn = null;
		}
	}
	
	/**
	 * clean up the stream
	 */
	public void cleanup() {
		try {
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
			if (outputStream != null) {
				outputStream.close();
				outputStream = null;
			}
			disConnect();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}
}
