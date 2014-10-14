package com.chinamobile.android.connectionmanager.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * OMA-DM Response class
 *
 */
public class OMAResponse implements CMResponse{
	public InputStream content;
	public long uid;
	@Override
	public void setData(HttpURLConnection conn, InputStream inputStream,
			CMRequest request, boolean isCmwap) throws Exception {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte buffer[] = new byte[1024];
		int len = -1;
		while ((len = inputStream.read(buffer)) != -1) {
			os.write(buffer, 0, len);
		}
		String s = new String(os.toByteArray());
		content = new ByteArrayInputStream(os.toByteArray());
	}
	@Override
	public long getUid() {
		return uid;
	}
	
	public InputStream getContent() {
		return content;
	}
	
	public void setUid(long uid) {
		this.uid = uid;
	}

}
