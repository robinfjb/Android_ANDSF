package com.chinamobile.android.connectionmanager.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.R;

/**
 * use BaiduMap location
 *
 */
public class LocationUtil {
	private static final String TAG = "LocationUtil";
	private static BufferedReader br;
	private static LocationUtil instance;
	private Context context;
	private Location mLocation;
	private static int retry;
	private LocationManager locationManager;
	private String bestProviders;
	private MyloccationListener baiduListener;
	
	public LocationUtil(Context context) {
		this.context = context;
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public static LocationUtil getInstance(Context context) {
		if(instance == null) {
			instance = new LocationUtil(context);
		}
		
		retry = 0;
		return instance;
	}
	
	/**
	 * start <b>BaiduMap</b> listener to get the updated location
	 * <br>the max interval of listener is 10 and the minimum interval is 5
	 */
	public void start() {
//		if(locationManager == null) {
//			locationManager = (LocationManager) context
//					.getSystemService(Context.LOCATION_SERVICE);
//		}
//		Criteria criteria = new Criteria();
//		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
//		criteria.setPowerRequirement(Criteria.POWER_LOW);
//		criteria.setAltitudeRequired(false);
//		criteria.setBearingRequired(false);
//		criteria.setSpeedRequired(false);
//		criteria.setCostAllowed(false);
//		bestProviders = locationManager.getBestProvider(criteria, false);
//		mLocation = locationManager.getLastKnownLocation(bestProviders);
//		locationManager.requestLocationUpdates(bestProviders, 6000, 1,
//				mLocationListener);
		AppApplication.getBMapManager().stop();
		AppApplication.getBMapManager().start();
		baiduListener = new MyloccationListener();
		
		AppApplication.getBMapManager().getLocationManager().requestLocationUpdates(baiduListener);
		AppApplication.getBMapManager().getLocationManager().setNotifyInternal(Constants.MAX_LOCTIONCHANGE_INTERVAL,
				Constants.MIN_LOCTIONCHANGE_INTERVAL);
	}
	
	/**
	 * stop the listener to get the updated location
	 */
	public void stop(){
		try {
			mLocation = null;
			bestProviders = null;
			AppApplication.getBMapManager().getLocationManager().removeUpdates(baiduListener);
			AppApplication.getBMapManager().stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * The listener to monitor the location change
	 *
	 */
	private class MyloccationListener implements com.baidu.mapapi.LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			mLocation = location;
			
			// to modify the error compare with Google Geo
//			mLocation.setLatitude(location.getLatitude() - 0.0060);
//			mLocation.setLongitude(location.getLongitude() - 0.0065);
			
			if(location != null) {
				LogUtil.add(location.getLatitude() + "||"
						+ location.getLongitude());
			}
		}
	}
	
	@Deprecated
	private final LocationListener mLocationListener=new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			mLocation = location;
			if(mLocation != null) {
				LogUtil.add(mLocation.getLatitude() + "\n"
						+ mLocation.getLongitude());
			} else {
				LogUtil.add("-------GEO get nothing--------");
			}
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
	 };
	
	 /**
	  * return the {@link Location}
	  * @return
	  */
	public Location getLocation() {
		if(mLocation == null) {
			if(locationManager == null) {
				locationManager = (LocationManager) context
						.getSystemService(Context.LOCATION_SERVICE);
			}
			
			try {
				mLocation = locationManager.getLastKnownLocation(bestProviders);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return mLocation;
	}
	
	@Deprecated
	public static void checkLocationEnable(final Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		boolean boolGPS = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean boolNet = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if(!boolGPS && !boolNet) {
			new AlertDialog.Builder(context)
            .setMessage(R.string.navigate_to_location_setting)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        			context.startActivity(intent);
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            })
            .create().show();
		}
	}

	@Deprecated
	private static void getLocationByNet(final Context context) {

		final TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					GsmCellLocation gcl = (GsmCellLocation) tm
							.getCellLocation();
					if (null == gcl) {
						Toast.makeText(context, "null", Toast.LENGTH_LONG)
								.show();
					}
					int cid = gcl.getCid();
					int lac = gcl.getLac();
					int mcc = Integer.valueOf(tm.getNetworkOperator()
							.substring(0, 3));
					int mnc = Integer.valueOf(tm.getNetworkOperator()
							.substring(3, 5));
					JSONObject holder = new JSONObject();
					holder.put("version", "1.1.0");
					holder.put("host", "maps.google.com");
					holder.put("request_address", true);

					JSONArray array = new JSONArray();
					JSONObject data = new JSONObject();

					data.put("cell_id", cid);
					data.put("location_area_code", lac);
					data.put("mobile_country_code", mcc);
					data.put("mobile_network_code", mnc);
					array.put(data);
					holder.put("cell_towers", array);
					DefaultHttpClient client = new DefaultHttpClient();
					HttpPost post = new HttpPost(
							"http://www.google.com/loc/json");
					StringEntity se = new StringEntity(holder.toString());
					post.setEntity(se);
					HttpResponse resp = client.execute(post);
					if (resp.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = resp.getEntity();
						br = new BufferedReader(new InputStreamReader(entity
								.getContent()));
						StringBuffer sb = new StringBuffer();
						String result = br.readLine();
						while (result != null) {
							sb.append(result);
							result = br.readLine();
						}

						JSONObject data_ = new JSONObject(sb.toString());
						data_ = (JSONObject) data_.get("location");
						Location loc = new Location(
								LocationManager.NETWORK_PROVIDER);
						loc.setLatitude((Double) data_.get("latitude"));
						loc.setLongitude((Double) data_.get("longitude"));
						Log.i(TAG, "latitude : " + loc.getLatitude()
								+ "  longitude : " + loc.getLongitude());
					}

				} catch (JSONException e) {
					Log.e(TAG,
							"network get the latitude and longitude ocurr JSONException error",
							e);
				} catch (ClientProtocolException e) {
					Log.e(TAG,
							"network get the latitude and longitude ocurr ClientProtocolException error",
							e);
				} catch (IOException e) {
					Log.e(TAG,
							"network get the latitude and longitude ocurr IOException error",
							e);
				} catch (Exception e) {
					Log.e(TAG,
							"network get the latitude and longitude ocurr Exception error",
							e);
				} finally {
					if (null != br) {
						try {
							br.close();
						} catch (IOException e) {
							Log.e(TAG,
									"network get the latitude and longitude when closed BufferedReader ocurr IOException error",
									e);
						}
					}
				}
			}
		}).start();

	}

	private static final double PI = 3.14159265;
	private static final double EARTH_RADIUS = 6378137;
	private static final double RAD = Math.PI / 180.0;

	/**
	 * get the rectangle around of inputting parameters
	 * 
	 * @param raidus
	 *	return minLat,minLng,maxLat,maxLng
	 */
	public static double[] getAround(double lat, double lon, int raidus) {

		final Double latitude = lat;
		final Double longitude = lon;

		final Double degree = (24901 * 1609) / 360.0;
		final double raidusMile = raidus;

		final Double dpmLat = 1 / degree;
		final Double radiusLat = dpmLat * raidusMile;
		final Double minLat = latitude - radiusLat;
		final Double maxLat = latitude + radiusLat;

		final Double mpdLng = degree * Math.cos(latitude * (PI / 180));
		final Double dpmLng = 1 / mpdLng;
		final Double radiusLng = dpmLng * raidusMile;
		final Double minLng = longitude - radiusLng;
		final Double maxLng = longitude + radiusLng;
		
		return new double[] { minLat, minLng, maxLat, maxLng };
	}

	/**
	 * get the distance format in meter between lat1&lng1 and lat2&lng2
	 * @param lng1
	 * @param lat1
	 * @param lng2
	 * @param lat2
	 * @return
	 */
	public static double getDistance(double lng1, double lat1, double lng2,
			double lat2) {
		final double radLat1 = lat1 * RAD;
		final double radLat2 = lat2 * RAD;
		final double a = radLat1 - radLat2;
		final double b = (lng1 - lng2) * RAD;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}
	
	/**
	 * return if lat1&lng1 is in the range of lat2&lng2, the range id a circle
	 * @param lng1
	 * @param lat1
	 * @param lng2
	 * @param lat2
	 * @param radius
	 * @return
	 */
	public static boolean isInRange(double lng1, double lat1, double lng2,
			double lat2, long radius) {
		if(lat1 < 0 || lng1 < 0 || lng2 < 0 || lat2 < 0) {
			return false;
		}
		double distance = getDistance(lng1, lat1, lng2, lat2);
		if(radius >= distance) {
			return true;
		}
		return false;
	}
	
	/**
	 * convert latitude string format into double format
	 * <br>According to 3GPP TS 23.032 6.1
	 * @param code
	 * @return
	 */
	public static double latitudeConverter(String code) {
		final double NUM = 90.0;
		long MAX_LAT = Integer.parseInt("100000000000000000000000", 2);// 2^23
		double latitude = 0;
		try {
			int head = Integer.parseInt(String.valueOf(code.charAt(0)));
			long content = Long.parseLong(code.substring(1), 2);
			double x = content * NUM;
			latitude = (double) (x / MAX_LAT);
			if(head == 0) {
				latitude *= 1;
			} else if(head == 1) {
				latitude *= -1;
			} else {
				return 0;
			}
			if(latitude >= 90 || latitude < -90) {
				return 0;
			}
		} catch (Exception e) {
			Log.e(TAG, "latitudeConverter->" + e.getMessage());
		}
		return latitude;
	}
	
	/**
	 * convert longitude string format into double format
	 * <br>According to 3GPP TS 23.032 6.1
	 * @param code
	 * @return
	 */
	public static double longtitudeConverter(String code) {
		final double NUM = 360;
		long MAX_LAT = Integer.parseInt("1000000000000000000000000", 2);// 2^24
		double longtitude = 0;
		try {
			int head = Integer.parseInt(String.valueOf(code.charAt(0)));
			long content = Long.parseLong(code.substring(1), 2);
			double x = content * NUM;
			longtitude = (x / MAX_LAT);
			if(head == 0) {
				longtitude *= 1;
			} else if(head == 1) {
				longtitude *= -1;
			} else {
				return 0;
			}
			if(longtitude > 180 || longtitude < -180) {
				return 0;
			}
		} catch (Exception e) {
			Log.e(TAG, "longtitudeConverter->" + e.getMessage());
		}
		return longtitude;
	}
	
	/**
	 * convert radius string format into long format
	 * <br>According to 3GPP TS 23.032 6.5
	 * @param code
	 * @return
	 */
	public static long radiusConverter(String code) {
		final int NUM = 5;
		long radius = -1;
		try {
			radius = Integer.parseInt(code, 2);
			radius *= NUM;
			if(radius > 327675) {
				return -1;
			}
		} catch (Exception e) {
			Log.e(TAG, "radiusConverter->" + e.getMessage());
		}
		return radius;
	}
}