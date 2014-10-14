package com.chinamobile.android.connectionmanager.http;

import java.util.Hashtable;

public interface CMRequest {
	public static final byte TYPE_REPORT = 1;
    public static final byte TYPE_OMA = 2;
    /**
     * convert into <code>byte[]</code>
     * @return
     */
	public byte[] getContent();
	/**
	 * get url
	 * @return
	 */
	public String getUrl();
	/**
	 * get content length
	 * @return
	 */
	public int getContentLength();
	/**
	 * get reuqest parameters
	 * @return
	 */
	public Hashtable getRequestProperty();
	/**
	 * get request type
	 * @return
	 */
	public byte getType();
	/**
	 * get uid
	 * @return
	 */
	public long getUid();
}
