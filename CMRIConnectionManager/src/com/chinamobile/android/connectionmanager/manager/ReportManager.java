package com.chinamobile.android.connectionmanager.manager;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.chinamobile.android.connectionmanager.database.DBAdpter;
import com.chinamobile.android.connectionmanager.http.CMHttpHandler;
import com.chinamobile.android.connectionmanager.http.CMHttpListener;
import com.chinamobile.android.connectionmanager.http.CMRequest;
import com.chinamobile.android.connectionmanager.http.CMResponse;
import com.chinamobile.android.connectionmanager.http.ReportRequest;
import com.chinamobile.android.connectionmanager.http.ReportResponse;
import com.chinamobile.android.connectionmanager.model.NetworkModel;
import com.chinamobile.android.connectionmanager.model.ReportModel;
import com.chinamobile.android.connectionmanager.model.ReportModel.ReportData;
import com.chinamobile.android.connectionmanager.model.WifiModel;
import com.chinamobile.android.connectionmanager.util.CommonUtil;
import com.chinamobile.android.connectionmanager.util.Constants;
import com.chinamobile.android.connectionmanager.util.LocationUtil;
import com.chinamobile.android.connectionmanager.util.TimeUtil;
import com.chinamobile.android.connectionmanager.util.TrafficStatistic;
import com.chinamobile.android.connectionmanager.util.TrafficStatistic.TrafficData;

/**
 * the manager to manage report  action
 * <p> it provide create a report, update report data, send report data to server and receive response
	from report server
 *
 */
public class ReportManager extends BaseManager implements CMHttpListener{
	private static final String TAG = "ReportManager";
	private DBAdpter mDbAdapter;
	private String timeKey;
	
	private CMHttpHandler mHttpHandler;

	public ReportManager(Context context, Handler handler, DBAdpter mDbAdapter) {
		super(context, handler);
		this.mDbAdapter = mDbAdapter;
	}

	@Override
	public void onStart() {
		TrafficStatistic.getInstance().reset();
		TrafficData.reset();
		timeKey = null;
		
		mHttpHandler = new CMHttpHandler(this);
	}

	@Override
	public void onStop() {
		timeKey = null;
		TrafficStatistic.getInstance().reset();
		TrafficData.reset();
		
		if(mHttpHandler != null) {
			mHttpHandler.stop();
			mHttpHandler = null;
		}
	}
	
	/**
	 * update the network traffic data into database
	 */
	public void updateReport() {
		//get the previous network traffic data use
		TrafficStatistic.getInstance().stop();
		final ReportData data = new ReportData();
		data.g3TrafficDownload = TrafficData.g3_down_total;
		data.g3TrafficUpload = TrafficData.g3_up_total;
		data.wifiTrafficDownload = TrafficData.wifi_down_total;
		data.wifiTrafficUpload = TrafficData.wifi_up_total;
		try {
			mDbAdapter.openDatabase();
			mDbAdapter.updateReportData(data, timeKey);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mDbAdapter.close();
		}
		timeKey = null;
	}
	
	/**
	 * creat a report to database without traffic data
	 * @param net
	 */
	public void creatReport(NetworkModel net) {
		
		// start to collect new network traffic data use
		TrafficStatistic.getInstance().start();
		
		Location location = LocationUtil.getInstance(context).getLocation();
		final ReportData data = new ReportData();
		data.cellId = CommonUtil.getCellId(context);
		data.plmn = CommonUtil.getPlmn(context);
		data.lac = CommonUtil.getLac(context);
		
		if(location != null) {
			data.latitude = location.getLatitude();
			data.longitude = location.getLongitude();
		}
		if(net.getType() == NetworkModel.TYPE_3G) {
			data.networkType = data.networkName = "3G";
		} else if(net.getType() == NetworkModel.TYPE_WIFI) {
			data.networkType = "Wlan";
			data.networkName = ((WifiModel)net).getSSID();
		}
		data.time = String.valueOf(TimeUtil.getNowFullTime());
		timeKey = data.time;
		try {			
			mDbAdapter.openDatabase();
			mDbAdapter.addReportData(data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mDbAdapter.close();
		}
		
	}
	
	/**
	 * delete previous report
	 */
	private void deletePreviousReport() {
		try {	
			mDbAdapter.openDatabase();
			mDbAdapter.clearPreviouReportData();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			mDbAdapter.close();
		}
	}
	
	/**
	 * get list of {@link ReportData}
	 * @return
	 */
	private List<ReportData> getReport() {
		List<ReportData> list = null;
		try {	
			mDbAdapter.openDatabase();
			list = mDbAdapter.getReportData();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			mDbAdapter.close();
		}
		return list;
	}
	
	/**
	 * send report to server
	 */
	public void sendReport() {
		List<ReportData> list = getReport();
		if(list == null || list.isEmpty()) {
			return;
		}
		ReportModel report = new ReportModel();
		report.setAndroidOsVersion(Build.VERSION.RELEASE);
		report.setAndsfAppName(Constants.NAME);
		report.setAndsfAppVersion(Constants.VERSION);
		report.setDeviceId(CommonUtil.getDeviceId(context));
		report.setData(list);
		String results = null;
		try {
			results = CommonUtil.buildReportXml(report);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(results != null) {
			ReportRequest request = new ReportRequest(Constants.REPROT_SERVER_URL);
			request.postParams = results;
			Hashtable<String, String> requestProperties = new Hashtable<String, String>(2);
			requestProperties.put("Content-Length",
					String.valueOf(results.getBytes().length));
			requestProperties.put("Content-Type", "text/xml; charset=utf-8");
			request.requestProperties = requestProperties;
			request.uid = System.currentTimeMillis();
			mHttpHandler.sendRequest(request);
		}
	}

	@Override
	public void completed(CMResponse resp) {
		ReportResponse response = (ReportResponse) resp;
		String result = response.getContent();
		if(result.equalsIgnoreCase("OK")) {
			deletePreviousReport();
		} else {
			Log.w(TAG, "result:" + result);
		}
	}

	@Override
	public void exception(Exception e, CMRequest request) {
		Log.w(TAG, "exception:" + e);
	}

	@Override
	public boolean acceptResponse(CMRequest request) {
		// TODO Auto-generated method stub
		return true;
	}

}
