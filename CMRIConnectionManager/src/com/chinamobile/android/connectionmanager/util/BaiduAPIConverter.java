/*package com.chinamobile.android.connectionmanager.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import android.annotation.TargetApi;
import android.util.Base64;

@TargetApi(8)
public class BaiduAPIConverter {

	public static void testPost(String x, String y) throws IOException {
		
		URL url = new URL(
				"http://api.map.baidu.com/ag/coord/convert?from=0&to=4&x=" + x
						+ "&y=" + y);
		URLConnection connection = url.openConnection();

		connection.setDoOutput(true);
		OutputStreamWriter out = new OutputStreamWriter(
				connection.getOutputStream(), "utf-8");
		// remember to clean up
		out.flush();
		out.close();

		String sCurrentLine;
		String sTotalString;
		sCurrentLine = "";
		sTotalString = "";
		InputStream l_urlStream;
		l_urlStream = connection.getInputStream();
		BufferedReader l_reader = new BufferedReader(new InputStreamReader(
				l_urlStream));
		while ((sCurrentLine = l_reader.readLine()) != null) {
			if (!sCurrentLine.equals(""))
				sTotalString += sCurrentLine;
		}
		System.out.println(sTotalString);
		sTotalString = sTotalString.substring(1, sTotalString.length() - 1);
		System.out.println(sTotalString);
		String[] results = sTotalString.split("\\,");
		if (results.length == 3) {
			if (results[0].split("\\:")[1].equals("0")) {
				String mapX = results[1].split("\\:")[1];
				String mapY = results[2].split("\\:")[1];
				mapX = mapX.substring(1, mapX.length() - 1);
				mapY = mapY.substring(1, mapY.length() - 1);
				mapX = new String(Base64.decode(mapX, 0));
				mapY = new String(Base64.decode(mapY, 0));
				System.out.println(mapX);
				System.out.println(mapY);
			}
		}

	}
}
*/