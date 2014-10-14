package com.chinamobile.android.connectionmanager.http;

import java.util.Hashtable;

/**
 * OMA-DM Request class
 *
 */
public class OMARequest implements CMRequest{
	public String url;
	public String postParams;
	public Hashtable requestProperties;
	public long uid;
	
	public OMARequest(String url) {
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
		// TODO Auto-generated method stub
		return url;
	}

	@Override
	public int getContentLength() {
		// TODO Auto-generated method stub
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
		return CMRequest.TYPE_OMA;
	}

	@Override
	public long getUid() {
		// TODO Auto-generated method stub
		return uid;
	}

}
