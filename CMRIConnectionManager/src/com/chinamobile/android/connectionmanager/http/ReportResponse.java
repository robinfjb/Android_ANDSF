package com.chinamobile.android.connectionmanager.http;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Report Response class
 *
 */
public class ReportResponse implements CMResponse {
	private String content;
	private long uid;
	@Override
	public void setData(HttpURLConnection conn, InputStream inputStream,
			CMRequest request, boolean isCmwap) throws Exception {
		StringBuffer buffer = new StringBuffer();
		InputStreamReader inputStreamReader;

		inputStreamReader = new InputStreamReader(inputStream);

		int oneChar;
		while ((oneChar = inputStreamReader.read()) > -1) {
			buffer.append((char) oneChar);
		}
		content = buffer.toString();
		uid = ((ReportRequest)request).uid;
		if(inputStreamReader != null) {
			inputStreamReader.close();
		}
	}

	public String getContent() {
		return content;
	}

	@Override
	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}
}
