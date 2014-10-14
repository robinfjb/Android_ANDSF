package com.chinamobile.android.connectionmanager.http;

import java.util.Hashtable;

/**
 * Report Request class
 *
 */
public class ReportRequest implements CMRequest {
	public String url;
	public String postParams;
	public Hashtable requestProperties;
	public long uid;
	public ReportRequest(String url) {
		this.url = url;
	}

	@Override
	public byte[] getContent() {
		if (postParams != null)
            return postParams.getBytes();
		return null;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public int getContentLength() {
		return postParams == null ? 0 : postParams.length();
	}

	@Override
	public Hashtable getRequestProperty() {
		// TODO Auto-generated method stub
		return requestProperties;
	}

	@Override
	public byte getType() {
		// TODO Auto-generated method stub
		return CMRequest.TYPE_REPORT;
	}

	@Override
	public long getUid() {
		// TODO Auto-generated method stub
		return uid;
	}

}
