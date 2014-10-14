package com.chinamobile.android.connectionmanager.http;

import java.io.InputStream;

/**
 * Http listener
 *
 */
public interface CMHttpListener {
	/**
	 * actions that should be taken when a request has completed.
	 * 
	 * @param data
	 */
	public void completed(CMResponse resp);

	/**
	 * actions that should be taken when a <code>Request</code> object is
	 * indicating that the request cannot be carried out due to thrown
	 * exceptions.
	 * 
	 * @param e
	 * @param handler
	 */
	public void exception(Exception e, CMRequest request);

	/**
	 * 
	 * @param request
	 * @return
	 */
	public boolean acceptResponse(CMRequest request);//
}
