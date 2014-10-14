package com.chinamobile.android.connectionmanager.http;

import java.util.Vector;

import com.chinamobile.android.connectionmanager.util.Constants;

/**
 * the handler to control the whole HTTP connection
 *
 */
public class CMHttpHandler implements Runnable {

	private boolean isRunning = false;
	private CMHttpClient httpClient = null;
	private final int timeout;
	private final int retries;
	// HttpListener
	private CMHttpListener httpListener = null;
	private Vector<CMRequest> vctRequests = new Vector<CMRequest>();

	public CMHttpHandler(CMHttpListener httpListener) {
		this(Constants.HTTP_OVER_TIME, Constants.HTTP_RETRY, httpListener);
	}

	public CMHttpHandler(int timeout, int retries, CMHttpListener httpListener) {
		this.retries = retries;
		this.timeout = timeout;
		this.httpListener = httpListener;
		this.isRunning = true;
		httpClient = new CMHttpClient();
		Thread t = new Thread(this);
		t.start();
	}

	public void setHttpListener(CMHttpListener httpListener) {
		this.httpListener = httpListener;
	}

	@Override
	public void run() {
		while (isRunning) {
			try {
				if (vctRequests.size() > 0) {
					CMRequest request = null;
					synchronized (vctRequests) {
						if (vctRequests.size() > 0) {
							request = (CMRequest) vctRequests.elementAt(0);
							vctRequests.removeElementAt(0);
						}
					}

					CMResponse resp = getResponse(request);

					if (resp != null && httpListener != null
							&& httpListener.acceptResponse(request)
							&& resp.getUid() == request.getUid()) {
						httpListener.completed(resp);
					}
					resp = null;
				}
				Thread.sleep(500);
			} catch (Exception ex) {
//				ex.printStackTrace();
			}
		}
	}

	/**
	 * call {@link CMHttpClient} to get {@link CMResponse}
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public CMResponse getResponse(CMRequest request) throws Exception {
		CMResponse resp = null;

		int thisTry = 1;
		while (thisTry <= retries) {
			try {
				resp = httpClient.getResponse(request);
			} catch (Exception e) {
				if (thisTry >= retries) {
					cleanup();
					if (httpListener != null
							&& httpListener.acceptResponse(request)
							&& (resp == null || resp.getUid() == request.getUid())) {
						 httpListener.exception(e, request);
					}
				}
			} finally {
				thisTry++;
			}
		}
		return resp;
	}

	/**
	 * send request
	 * @param request
	 */
	public void sendRequest(CMRequest request) {
		sendRequest(request, true);
	}

	/**
	 * send request
	 * @param request
	 * @param removeOldRequests
	 */
	public void sendRequest(CMRequest request, final boolean removeOldRequests) {
		if (request == null) {
			return;
		}
		synchronized (vctRequests) {
			if (removeOldRequests)
				vctRequests.removeAllElements();
			vctRequests.addElement(request);
		}
	}

	/**
	 * stop the runnable
	 */
	public void stop() {
		synchronized (vctRequests) {
			vctRequests.removeAllElements();
		}

		httpListener = null;
		cleanup();
		isRunning = false;
		httpClient = null;

	}

	/**
	 * clean up
	 */
	private void cleanup() {
		try {
			httpClient.cleanup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
