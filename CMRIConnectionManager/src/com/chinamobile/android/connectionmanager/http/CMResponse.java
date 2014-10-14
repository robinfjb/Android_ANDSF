package com.chinamobile.android.connectionmanager.http;

import java.io.InputStream;
import java.net.HttpURLConnection;

public interface CMResponse {
	/**
	 * set inputStream data into own object
	 * @param conn
	 * @param inputStream
	 * @param request
	 * @param isCmwap
	 * @throws Exception
	 */
	public void setData(HttpURLConnection conn, InputStream inputStream, CMRequest request, boolean isCmwap)
			throws Exception;
	/**
	 * get uid
	 * @return
	 */
	public long getUid();
}
