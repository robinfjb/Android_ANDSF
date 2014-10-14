package com.chinamobile.android.connectionmanager.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.KeyguardManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.PowerManager;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.R;
import com.chinamobile.android.connectionmanager.database.PolicyDB.ProfileType;
import com.chinamobile.android.connectionmanager.model.HotspotModel;
import com.chinamobile.android.connectionmanager.model.HotspotModel.WlanInfo;
import com.chinamobile.android.connectionmanager.model.NetworkModel;
import com.chinamobile.android.connectionmanager.model.PolicyModel;
import com.chinamobile.android.connectionmanager.model.PolicyModel.TimeAndDate;
import com.chinamobile.android.connectionmanager.model.ReportModel;
import com.chinamobile.android.connectionmanager.model.ReportModel.ReportData;
import com.chinamobile.android.connectionmanager.model.WifiModel;
import com.chinamobile.android.connectionmanager.model._3GModel;

public class CommonUtil {
	private static final String TAG = "CommonUtil";
	
	/**
	 * write list into local xml file
	 * @param context
	 * @param list
	 */
	@Deprecated
	public static void writeIntoLocalXml(Context context, final List<NetworkModel> list) {
		final PolicyParser pp = new PolicyParser(context);
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
				try {
					pp.bulidPolicyXml(list);
				} catch (XmlPullParserException e) {
					Log.e(TAG, "writeIntoLocalXml--->XmlPullParserException---->" + e.getMessage());
				} catch (RuntimeException e) {
					Log.e(TAG, "writeIntoLocalXml--->RuntimeException---->" + e.getMessage());
				} catch (IOException e) {
					Log.e(TAG, "writeIntoLocalXml--->IOException---->" + e.getMessage());
				}
//			}
//		}).start();
	}
	
	/**
	 * check whether the two parameter match each other. 
	 * <p>If parameter 1 is null/-1, parameter 2 should be null/-1. And if parameter 1 is not null/-1,
	 * parameter 2 should not be null/-1; Otherwise, it will throw an exception
	 * @param p1
	 * @param p2
	 * @throws Throwable
	 */
	public static <T> void checkPair(T p1, T p2) throws Throwable{
		Throwable exception = new Throwable("Not Pairing!");
		if(p1 == null) {
			if(p2 != null) {
				throw exception;
			} else {
				return;
			}
		} else {
			if(p2 == null) {
				throw exception;
			}
		}
		if(!p1.getClass().getName().equals(p2.getClass().getName())) {
			throw exception;
		}
		if(p1 instanceof Integer) {
			if((Integer) p1 == -1) {
				if((Integer) p2 != -1) {
					throw exception;
				}
			} else {
				if((Integer) p2 == -1) {
					throw exception;
				}
			}
		}
		
		if(p1 instanceof Long) {
			if((Long) p1 == -1) {
				if((Long) p2 != -1) {
					throw exception;
				}
			} else {
				if((Long) p2 == -1) {
					throw exception;
				}
			}
		}
		
	}
	/**
	 * get the final result via checking list of sampling results
	 * @param results
	 * @return  {@link WifiModel#SIGNAL_STATE_VALID},
	 * {@link WifiModel#SIGNAL_STATE_INVALID},
	 * {@link WifiModel#SIGNAL_STATE_NONE}
	 */
	public static int getFinalSignalResult(int[] results) {
		if(results.length == 0) {
			return -1;
		}
		int[] newResults = getValidArray(results);
		int size = newResults.length;
		if(size == 0) {
			return -1;
		}
		
		boolean isAllValid = true;
		boolean isAllNone = true;
		for (int i = 0; i < size; i++) {
			if(newResults[i] == WifiModel.SIGNAL_STATE_VALID) {
				isAllNone = false;
			} else if(newResults[i] == WifiModel.SIGNAL_STATE_NONE) {
				isAllValid = false;
			} else if(newResults[i] == WifiModel.SIGNAL_STATE_INVALID) {
				isAllNone = false;
				isAllValid = false;
			} 
		}
		if(isAllValid) {
			return WifiModel.SIGNAL_STATE_VALID;
		} else if(isAllNone) {
			return WifiModel.SIGNAL_STATE_NONE;
		} else {
			return WifiModel.SIGNAL_STATE_INVALID;
		}
	}
	
	/**
	 * get length of int array which > 0 form target int array
	 * @param target
	 * @return
	 */
	public static int getArrayValidLength(int[] target) {
		int len = 0;
		for (int i : target) {
			if(i > 0) {
				len ++;
			}
		}
		return len;
	}
	
	/**
	 * get new int array which > 0 form target int array
	 * @param target
	 * @return
	 */
	public static int[] getValidArray(int[] target) {
		int[] newArray = new int[getArrayValidLength(target)];
		int j = 0;
		for (int i = 0; i < target.length; i++) {
			if (target[i] > 0) {
				newArray[j] = target[i];
				j++;
			}
		}
		return newArray;
	}
	
	/**
	 * get IMSI
	 * @param context
	 * @return
	 */
	public static String getIMSI(Context context) {
		TelephonyManager mTelManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return mTelManager.getSubscriberId();
	}
	
	/**
	 * get IMEI
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
		TelephonyManager mTelManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return mTelManager.getDeviceId();
	}
	
	/**
	 * return if target service started
	 * @param context
	 * @param serviceName
	 * @return
	 */
	public static boolean isServiceStarted(Context context, String serviceName) {
		ActivityManager am = (ActivityManager)context.
				getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = am.getRunningServices(30);
		for (RunningServiceInfo info : infos) {
			if (info.service.getClassName().equals(serviceName)) {
				return info.started;
			}
		}
		return false;
	}
	
	/**
	 * return whether wifi1 and wifi2 is the same wifi
	 * <p> Compare by ssid and authentication type
	 * @param w1
	 * @param w2
	 * @return
	 */
	public static boolean isSameWifi(WifiModel w1, WifiModel w2) {
		if (w1.getSSID() == null || w1.getAuthenticationType() == null
				|| w2.getSSID() == null || w2.getAuthenticationType() == null) {
			return false;
		}
		return (w1.getSSID().equals(w2.getSSID()))
				&& (w1.getAuthenticationType().equals(w2.getAuthenticationType()));
	}
	
	@Deprecated
	public static <T> void switchElements(List<T> list) {
		T obj = list.get(0);
		list.remove(0);
		list.add(obj);
	}
	
	/**
	 * get the local static policy according to user setting
	 * @return policy List
	 */
	@Deprecated
	public static List<PolicyModel> getLocalPolicyList(Context context) {
		InputStream inputStream = null;
		List<PolicyModel> list;
		try {
			inputStream = context.getApplicationContext().openFileInput(
					Constants.LOCAL_XML_NAME);
		} catch (FileNotFoundException e) {
			Log.w(TAG, "file not found in ./data/data/...");
		}
		if (inputStream == null) {
			inputStream = context.getResources().openRawResource(
					R.raw.local_static_policy);
			list = parseXml(inputStream);
			inputStream = context.getResources().openRawResource(
					R.raw.local_static_policy);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));
			StringBuffer sbuffer = new StringBuffer();
			String line = null;
			try {
				while ((line = reader.readLine()) != null) {
					sbuffer.append(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			FileUtil.saveFile2InternalStorage(context,
					Constants.LOCAL_XML_NAME, sbuffer.toString());
		} else {
			list = parseXml(inputStream);
		}
		
		if (inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	/**
	 * parse <code>Policy</code> xml
	 * @param str
	 * @return
	 */
	public static List<PolicyModel> parseXml(String str) {
		InputStream inputStream = new ByteArrayInputStream(str.getBytes());
		return parseXml(inputStream);
	}
	
	/**
	 * parse <code>Policy</code> xml
	 * @param inputStream
	 * @return
	 */
	public static List<PolicyModel> parseXml(InputStream inputStream) {
		PolicyParser parser = new PolicyParser(null, inputStream);
		try {
			 return parser.parse();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	/**
	 * parse <code>Hotspot</code> xml
	 * @param str
	 * @return
	 */
	public static List<HotspotModel> parseHotspot(String str) {
		InputStream inputStream = new ByteArrayInputStream(str.getBytes());
		HotspotParser parser = new HotspotParser(null, inputStream);
		try {
			 return parser.parseHotspot();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	/**
	 * parse <code>WlanInfo</code> xml
	 * @param str
	 * @return
	 */
	public static WlanInfo parseWlan(String str) {
		InputStream inputStream = new ByteArrayInputStream(str.getBytes());
		HotspotParser parser = new HotspotParser(null, inputStream);
		try {
			 return parser.parseWlanInfo();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	/**
	 * return if is OMS, only work for OMS 2.0
	 * @return
	 */
	public static boolean isOphone() {
		try {
			Class<?> c = Class.forName("oms.home.HomeIntents");
			Object.class.isAssignableFrom(c);
			return true;
		} catch (Exception e) {
		}
		return false;
	}
	
	/**
	 * split one policy into one or more policy on the basis of {@link TimeAndDate}
	 * @param policy
	 * @return
	 */
	public static List<PolicyModel> splitPolicy(PolicyModel policy) {
		List<PolicyModel> list = new ArrayList<PolicyModel>();
		final List<TimeAndDate> timeList = policy.getTimeList();
		for (int i = 0; i < timeList.size(); i++) {
			PolicyModel eachPolicy = policy.clone();
			List<TimeAndDate> eachTimeList = new ArrayList<TimeAndDate>();
			eachTimeList.add(timeList.get(i));
			eachPolicy.setTimeList(eachTimeList);
			list.add(eachPolicy);
		}
		return list;
	}
	
	/**
	 * get current cell id
	 * @param context
	 * @return
	 */
	public static int getCellId(Context context) {
		TelephonyManager telManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		CellLocation cid = telManager.getCellLocation();
		if(cid == null) {
			return -1;
		}
		int cellId = 0;
		if (telManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
			cellId = ((GsmCellLocation) cid).getCid();
		} else if (telManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
			cellId = ((CdmaCellLocation) cid).getBaseStationId();
		}
		return cellId;
	}
	
	/**
	 * get cell id format in binary
	 * @param context
	 * @return
	 */
	public static String getCellIdBinary(Context context) {
		TelephonyManager telManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		CellLocation cid = telManager.getCellLocation();
		if(cid == null) {
			return "";
		}
		String cellId = "";
		if (telManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
			cellId = Integer.toBinaryString(((GsmCellLocation) cid).getCid());
			int len = cellId.length();
			StringBuffer sb = new StringBuffer(cellId);
			for(int i = 0; i < 16 - len; i++) {
				sb.insert(0, "0");
			}
			cellId = sb.toString();
		} else if (telManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
//			cellId = ((CdmaCellLocation) cid).getBaseStationId();
		}
		return cellId;
	}
	
	/**
	 * get lac
	 * @param context
	 * @return
	 */
	public static int getLac(Context context) {
		TelephonyManager telManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		CellLocation cid = telManager.getCellLocation();
		if(cid == null) {
			return -1;
		}
		int lac = -1;
		if (telManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
			lac = ((GsmCellLocation) cid).getLac();
		} else if (telManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
			lac = -1;
		}
		return lac;
	}
	
	/**
	 * get lac format in Hex
	 * @param context
	 * @return
	 */
	public static String getLacHex(Context context) {
		TelephonyManager telManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		CellLocation cid = telManager.getCellLocation();
		if(cid == null) {
			return "";
		}
		String lac = "";
		if (telManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
			lac = Integer.toHexString(((GsmCellLocation) cid).getLac());
			int len = lac.length();
			StringBuffer sb = new StringBuffer(lac);
			for(int i = 0; i < 4 - len; i++) {
				sb.insert(0, "0");
			}
			lac = sb.toString();
		} else if (telManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
		}
		return lac;
	}
	
	/**
	 * get plmn
	 * @param context
	 * @return
	 */
	public static int getPlmn(Context context) {
		TelephonyManager telManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String mccMnc = telManager.getNetworkOperator();
		int plmn = -1;
		try {
			plmn = Integer.parseInt(mccMnc.substring(0, 3) +  mccMnc.substring(3, 5));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return plmn;
	}
	
	/**
	 * get device id
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager telManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = telManager.getDeviceId();
		if(deviceId == null) {
			deviceId = new String("123456789012345");
		}
		
		return deviceId;
	}
	
	/**
	 * To some SIM card, can not retrieve the phone number
	 * @param context
	 * @return
	 */
	public static String getPhoneNumber(Context context) {
		TelephonyManager telManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telManager.getLine1Number();
	}
	
	/**
	 * get network traffic data, this method is use for OS below 2.2
	 * @param device
	 * @param recvSentBytes
	 * @return
	 */
	public static boolean getDeviceSentRecv(String device,
			Integer[] recvSentBytes) {
		FileReader file = null;
		try {
			file = new FileReader("/proc/self/net/dev");
		} catch (FileNotFoundException e) {
		}
		if (file == null) {
			return false;
		}
		boolean bFind = false;
		BufferedReader buffer = new BufferedReader(file);
		try {
			String line = buffer.readLine();

			while (line != null && bFind == false) {
				if (line.length() > 0) {
					String data = line.trim();
					if (data.indexOf(device) > -1) {
						bFind = true;
						int nBegin = data.indexOf(':');
						data = data.substring(nBegin + 1, data.length()).trim();
						// get the recv bytes
						nBegin = data.indexOf(' ');
						int value = 0;
						if (nBegin > 0) {
							value = Integer.parseInt(data.substring(0, nBegin)
									.trim());
							recvSentBytes[0] = value;
						}
						int number = 1;
						while (nBegin > 0) {
							data = data.substring(nBegin + 1, data.length())
									.trim();
							nBegin = data.indexOf(' ');
							if (number == 8) {
								value = Integer.parseInt(data.substring(0,
										nBegin).trim());
								recvSentBytes[1] = value;
								break;
							}
							number++;
						}
					}
				}
				line = buffer.readLine();
			}
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return bFind;
	}
	
	
	/**
	 * set default SSID value to CMCC-AUTO and CMCC
	 * @param policy
	 */
	public static void handleDefaultSSID(PolicyModel policy) {
		if(policy == null) {
			return;
		}
		List<NetworkModel> list = policy.getAccessNetworkList();
		int size = list.size();
		boolean addPriority = false;
		for(int i = 0; i < size; i++) {
			NetworkModel networkModel = list.get(i);
			if(networkModel.getType() == NetworkModel.TYPE_WIFI) {
				if(((WifiModel) networkModel).getSSID() == null || ((WifiModel) networkModel).getSSID().equals("")) {
					list.remove(i);
					list.add(i, new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC_AUTO, 
							((WifiModel) networkModel).getPriority()));
					list.add(i + 1, new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC, 
							((WifiModel) networkModel).getPriority() + 1));
					size ++;
					addPriority = true;
				}
			}  else if(networkModel.getType() == NetworkModel.TYPE_3G) {
				if(addPriority) {
					addPriority = !addPriority;
					int priority = networkModel.getPriority();
					networkModel.setPriority(priority + 1);
				}
			}
		}
	}
	
	/**
	 * turn into CMCC, CMCCAUTO, G3,
		CMCC_CMCCAUTO, CMCCAUTO_CMCC, CMCC_G3, CMCCAUTO_G3, G3_CMCC, G3_CMCCAUTO,
		CMCC_CMCCAUTO_G3, CMCC_G3_CMCCAUTO, CMCCAUTO_CMCC_G3, CMCCAUTO_G3_CMCC, G3_CMCC_CMCCAUTO, G3_CMCCAUTO_CMCC
	 * @param list
	 * @return
	 */
	public static ProfileType encodeProfileType(List<NetworkModel> list) {
		if(list == null) {
			Log.e(TAG, "the profile is null!!!!");
			return null;
		}
		int size = list.size();
		if(size > 3 || size <= 0) {
			Log.e(TAG, "the profile capatity > 3 or empty!!!!");
			return null;
		}
		
		try {
			if (size == 1) {
				NetworkModel net = list.get(0);
				if (net.getType() == NetworkModel.TYPE_3G) {
					return ProfileType.G3;
				} else if (net.getType() == NetworkModel.TYPE_WIFI) {
					if (((WifiModel) net).getSSID()
							.equalsIgnoreCase(Constants.CMCC)) {
						return ProfileType.CMCC;
					} else if (((WifiModel) net).getSSID().equalsIgnoreCase(
							Constants.CMCC_AUTO)) {
						return ProfileType.CMCCAUTO;
					}
				}
			} 
			
			else if(size == 2) {
				NetworkModel net1 = list.get(0);
				NetworkModel net2 = list.get(1);
				if (net1.getType() == NetworkModel.TYPE_3G) {
					if (net2.getType() == NetworkModel.TYPE_WIFI) {
						if (((WifiModel) net2).getSSID()
								.equalsIgnoreCase(Constants.CMCC)) {
							return ProfileType.G3_CMCC;
						} else if (((WifiModel) net2).getSSID().equalsIgnoreCase(
								Constants.CMCC_AUTO)) {
							return ProfileType.G3_CMCCAUTO;
						}
					}
				}  else if (net1.getType() == NetworkModel.TYPE_WIFI) {
					if (((WifiModel) net1).getSSID()
							.equalsIgnoreCase(Constants.CMCC)) {
						if (net2.getType() == NetworkModel.TYPE_3G) {
							return ProfileType.CMCC_G3;
						} else if (net2.getType() == NetworkModel.TYPE_WIFI) {
							if (((WifiModel) net2).getSSID()
									.equalsIgnoreCase(Constants.CMCC_AUTO)) {
								return ProfileType.CMCC_CMCCAUTO;
							} 
						}
					} else if (((WifiModel) net1).getSSID().equalsIgnoreCase(
							Constants.CMCC_AUTO)) {
						if (net2.getType() == NetworkModel.TYPE_3G) {
							return ProfileType.CMCCAUTO_G3;
						} else if (net2.getType() == NetworkModel.TYPE_WIFI) {
							if (((WifiModel) net2).getSSID()
									.equalsIgnoreCase(Constants.CMCC)) {
								return ProfileType.CMCCAUTO_CMCC;
							} 
						}
					}
				}
			} else if(size == 3) {
				NetworkModel net1 = list.get(0);
				NetworkModel net2 = list.get(1);
				NetworkModel net3 = list.get(2);
				
				if (net1.getType() == NetworkModel.TYPE_3G) {
					if (net2.getType() == NetworkModel.TYPE_WIFI) {
						if (((WifiModel) net2).getSSID()
								.equalsIgnoreCase(Constants.CMCC)) {
							if (((WifiModel) net3).getSSID()
									.equalsIgnoreCase(Constants.CMCC_AUTO)) {
								return ProfileType.G3_CMCC_CMCCAUTO;
							} 
						} else if (((WifiModel) net2).getSSID().equalsIgnoreCase(
								Constants.CMCC_AUTO)) {
							if (((WifiModel) net3).getSSID()
									.equalsIgnoreCase(Constants.CMCC)) {
								return ProfileType.G3_CMCCAUTO_CMCC;
							}
						}
					}
				}  else if (net1.getType() == NetworkModel.TYPE_WIFI) {
					if (((WifiModel) net1).getSSID()
							.equalsIgnoreCase(Constants.CMCC)) {
						if (net2.getType() == NetworkModel.TYPE_3G) {
							if (((WifiModel) net3).getSSID()
									.equalsIgnoreCase(Constants.CMCC_AUTO)) {
								return ProfileType.CMCC_G3_CMCCAUTO;
							}
						} else if (net2.getType() == NetworkModel.TYPE_WIFI) {
							if (((WifiModel) net2).getSSID()
									.equalsIgnoreCase(Constants.CMCC_AUTO)
									&& net3.getType() == NetworkModel.TYPE_3G) {
								return ProfileType.CMCC_CMCCAUTO_G3;
							}
						}
					} else if (((WifiModel) net1).getSSID()
							.equalsIgnoreCase(Constants.CMCC_AUTO)) {
						if (net2.getType() == NetworkModel.TYPE_3G) {
							if (((WifiModel) net3).getSSID()
									.equalsIgnoreCase(Constants.CMCC)) {
								return ProfileType.CMCCAUTO_G3_CMCC;
							}
						} else if (net2.getType() == NetworkModel.TYPE_WIFI) {
							if (((WifiModel) net2).getSSID()
									.equalsIgnoreCase(Constants.CMCC)
									&& net3.getType() == NetworkModel.TYPE_3G) {
								return ProfileType.CMCCAUTO_CMCC_G3;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * decode CMCC, CMCCAUTO, G3,
		CMCC_CMCCAUTO, CMCCAUTO_CMCC, CMCC_G3, CMCCAUTO_G3, G3_CMCC, G3_CMCCAUTO,
		CMCC_CMCCAUTO_G3, CMCC_G3_CMCCAUTO, CMCCAUTO_CMCC_G3, CMCCAUTO_G3_CMCC, G3_CMCC_CMCCAUTO, G3_CMCCAUTO_CMCC
		as list
	 * @param type
	 * @return
	 */
	public static List<NetworkModel> decodeProfileType(ProfileType type) {
		List<NetworkModel> list = new ArrayList<NetworkModel>();
		switch (type) {
		case CMCC:
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC, 0));
			break;
		case CMCCAUTO:
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC_AUTO, 0));
			break;
		case G3:
			list.add(new _3GModel(NetworkModel.NAME_3G, 0));
			break;
		case CMCC_CMCCAUTO:
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC, 0));
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC_AUTO, 1));
			break;
		case CMCCAUTO_CMCC:
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC, 1));
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC_AUTO, 0));
			break;
		case CMCC_G3:
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC, 0));
			list.add(new _3GModel(NetworkModel.NAME_3G, 1));
			break;
		case CMCCAUTO_G3:
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC_AUTO, 0));
			list.add(new _3GModel(NetworkModel.NAME_3G, 1));
			break;
		case G3_CMCC:
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC, 1));
			list.add(new _3GModel(NetworkModel.NAME_3G, 0));
			break;
		case G3_CMCCAUTO:
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC_AUTO, 1));
			list.add(new _3GModel(NetworkModel.NAME_3G, 0));
			break;
		case CMCC_CMCCAUTO_G3:
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC, 0));
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC_AUTO, 1));
			list.add(new _3GModel(NetworkModel.NAME_3G, 2));
			break;
		case CMCC_G3_CMCCAUTO:
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC, 0));
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC_AUTO, 2));
			list.add(new _3GModel(NetworkModel.NAME_3G, 1));
			break;
		case CMCCAUTO_CMCC_G3:
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC, 1));
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC_AUTO, 0));
			list.add(new _3GModel(NetworkModel.NAME_3G, 2));
			break;
		case CMCCAUTO_G3_CMCC:
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC, 2));
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC_AUTO, 0));
			list.add(new _3GModel(NetworkModel.NAME_3G, 1));
			break;
		case G3_CMCC_CMCCAUTO:
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC, 1));
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC_AUTO, 2));
			list.add(new _3GModel(NetworkModel.NAME_3G, 0));
			break;
		case G3_CMCCAUTO_CMCC:
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC, 2));
			list.add(new WifiModel(NetworkModel.NAME_WLAN, Constants.CMCC_AUTO, 1));
			list.add(new _3GModel(NetworkModel.NAME_3G, 0));
			break;
		}
		
		return list;
	}
	
	/**
	 * build report XML
	 * @param report
	 * @return
	 * @throws XmlPullParserException
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static String buildReportXml(ReportModel report)
			throws XmlPullParserException, IllegalArgumentException, IllegalStateException, IOException {
		final String DEVICE_ID = "DeviceId";
		final String OS_VERSION = "OsVersion";
		final String APP_NAME = "AppName";
		final String APP_VERSION = "AppVersion";
		final String NETWORK_TYPE = "NetworkType";
		final String NETWORK_NAME = "NetworkName";
		final String TIME = "Time";
		final String CELL_ID = "CellId";
		final String PLMN = "Plmn";
		final String LAC = "Lac";
		final String LATITUDE = "Latitude";
		final String LOGITUDE = "Longitude";
		final String WLAN_UP = "WlanTrafficUpload";
		final String WLAN_DOWN = "WlanTrafficDownload";
		final String G3_UP = "3GTrafficUpload";
		final String G3_DOWN = "3GTrafficDownload";
		
		
		String deviceId = report.getDeviceId();
		String androidOsVersion = report.getAndroidOsVersion();
		String andsfAppVersion = report.getAndsfAppVersion();
		String andsfAppName = report.getAndsfAppName();
		List<ReportData> list = report.getData();
		
		StringWriter writer = new StringWriter();
		
		XmlSerializer serializer = XmlPullParserFactory.newInstance()
				.newSerializer();
		serializer.setOutput(writer);
		serializer.startTag(null, DEVICE_ID);
		serializer.text(deviceId);
		serializer.endTag(null, DEVICE_ID);
		serializer.startTag(null, OS_VERSION);
		serializer.text(androidOsVersion);
		serializer.endTag(null, OS_VERSION);
		serializer.startTag(null, APP_VERSION);
		serializer.text(andsfAppVersion);
		serializer.endTag(null, APP_VERSION);
		serializer.startTag(null, APP_NAME);
		serializer.text(andsfAppName);
		serializer.endTag(null, APP_NAME);
		for (int i = 0; i < list.size(); i++) {
			serializer.startTag(null, "X" +	String.valueOf(i));
			ReportData data = list.get(i);
			
			serializer.startTag(null, NETWORK_TYPE);
			serializer.text(data.networkType);
			serializer.endTag(null, NETWORK_TYPE);
			serializer.startTag(null, NETWORK_NAME);
			serializer.text(data.networkName);
			serializer.endTag(null, NETWORK_NAME);
			serializer.startTag(null, TIME);
			serializer.text(data.time);
			serializer.endTag(null, TIME);
			serializer.startTag(null, PLMN);
			serializer.text(String.valueOf(data.plmn));
			serializer.endTag(null, PLMN);
			serializer.startTag(null, LAC);
			serializer.text(String.valueOf(data.lac));
			serializer.endTag(null, LAC);
			serializer.startTag(null, CELL_ID);
			serializer.text(String.valueOf(data.cellId));
			serializer.endTag(null, CELL_ID);
			serializer.startTag(null, LATITUDE);
			serializer.text(String.valueOf(data.latitude));
			serializer.endTag(null, LATITUDE);
			serializer.startTag(null, LOGITUDE);
			serializer.text(String.valueOf(data.longitude));
			serializer.endTag(null, LOGITUDE);
			serializer.startTag(null, WLAN_UP);
			serializer.text(String.valueOf(data.wifiTrafficUpload));
			serializer.endTag(null, WLAN_UP);
			serializer.startTag(null, WLAN_DOWN);
			serializer.text(String.valueOf(data.wifiTrafficDownload));
			serializer.endTag(null, WLAN_DOWN);
			serializer.startTag(null, G3_UP);
			serializer.text(String.valueOf(data.g3TrafficUpload));
			serializer.endTag(null, G3_UP);
			serializer.startTag(null, G3_DOWN);
			serializer.text(String.valueOf(data.g3TrafficDownload));
			serializer.endTag(null, G3_DOWN);

			serializer.endTag(null, "X" + String.valueOf(i));
		}
		serializer.endDocument();
		
		XmlSerializer serializer2 = XmlPullParserFactory.newInstance()
				.newSerializer();
		StringWriter writer2 = new StringWriter();
		serializer2.setOutput(writer2);
		serializer2.startDocument("utf-8", true);
		serializer2.startTag(null, "DATA");
		serializer2.cdsect(writer.toString());
		serializer2.endTag(null, "DATA");
		serializer2.endDocument();
		
		String ss = writer2.getBuffer().toString();
		writer2.flush();
		writer2.close();
		return ss;
	}
	
	/**
	 * remove duplicated CMCC/CMCC-AUTO SSID
	 */
	private static List<WifiConfiguration> clearDuplicatedSSID(List<WifiConfiguration> configList, WifiManager mWifiManager) {
		List<WifiConfiguration> returnConfigList = new ArrayList<WifiConfiguration>();
		List<String> ssidList = new ArrayList<String>();
		for (int i = 0; i < configList.size(); i++) {
			WifiConfiguration config = configList.get(i);
			String ssid = config.SSID.substring(1, config.SSID.length() - 1);
			if(ssid.equals(Constants.CMCC)) {
				if(ssidList.contains(Constants.CMCC)) {
					mWifiManager.removeNetwork(i);
					mWifiManager.saveConfiguration();
				} else {
					returnConfigList.add(config);
					ssidList.add(ssid);
				}
			} else if(ssid.equals(Constants.CMCC_AUTO)) {
				if(ssidList.contains(Constants.CMCC_AUTO)) {
					mWifiManager.removeNetwork(i);
					mWifiManager.saveConfiguration();
				} else {
					returnConfigList.add(config);
					ssidList.add(ssid);
				}
			} else {
				returnConfigList.add(config);
				ssidList.add(ssid);
			}
		}
		return returnConfigList;
	}
	
	/**
	 * add or update CMCC CMCC-AUTO configuration into OS
	 */
	public static void addWiFiConfig2OS(Context context) throws Exception{
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> configList = clearDuplicatedSSID(mWifiManager.getConfiguredNetworks(), mWifiManager);
		List<String> ssidList = new ArrayList<String>();
		for (WifiConfiguration config : configList) {
			ssidList.add(config.SSID.substring(1, config.SSID.length() - 1));//remove ""
		}
		if (ssidList.contains(Constants.CMCC)) {
			int index = ssidList.indexOf(Constants.CMCC);
			WifiConfiguration wiCon = configList.get(index);
			WifiUtil.updateWepConfig(wiCon);
			
			int updateIndex = mWifiManager.updateNetwork(wiCon);
//			if(updateIndex < 0) {
//				boolean removed = mWifiManager.removeNetwork(index);
//				int addIndex = mWifiManager.addNetwork(wiCon);
//				mWifiManager.saveConfiguration();
//				if(!removed || addIndex < 0)
//					Log.e(TAG, "wifiConnect---->update CMCC failed");;
//			} else {
				boolean result = mWifiManager.saveConfiguration();
				if(!result) {
					Log.e(TAG, "wifiConnect---->save CMCC failed");
				}
//			}
				
		} else {
//			Log.i(TAG, "add CMCC configuration");
			WifiConfiguration wc = WifiUtil.getNewWepConfig(Constants.CMCC);
			int NetId = mWifiManager.addNetwork(wc);
			boolean es = mWifiManager.saveConfiguration();
			if(!es || NetId < 0)
				Log.e(TAG, "wifiConnect---->add CMCC failed");
		}
		
		if (ssidList.contains(Constants.CMCC_AUTO)) {
//			Log.i(TAG, "update CMCC_AUTO configured");
			int index = ssidList.indexOf(Constants.CMCC_AUTO);
			WifiConfiguration wiCon = configList.get(index);
			if(AppApplication.isEapSim) {
				WifiUtil.updateEapSimConfig(wiCon);
			} else {
				WifiUtil.updatePeapConfig(wiCon, AppApplication.peap_username, AppApplication.peap_password);
			}
			
			int updateIndex = mWifiManager.updateNetwork(wiCon);
//			if(updateIndex < 0) {
//				boolean removed = mWifiManager.removeNetwork(index);
//				int addIndex = mWifiManager.addNetwork(wiCon);
//				mWifiManager.saveConfiguration();
//				if(!removed || addIndex < 0)
//					Log.e(TAG, "wifiConnect---->update CMCC_AUTO failed");
//			} else {
				boolean result = mWifiManager.saveConfiguration();
				if(!result) {
					Log.e(TAG, "wifiConnect---->save CMCC_AUTO failed");
				}
//			}
		} else {
//			Log.i(TAG, "add CMCC_AUTO configuration");
			WifiConfiguration wc = null;
			if(AppApplication.isEapSim) {
				wc = WifiUtil.getEapSimConfig(Constants.CMCC_AUTO);
			} else {
				wc = WifiUtil.getPeapConfig(Constants.CMCC_AUTO, AppApplication.peap_username, AppApplication.peap_password);
			}
			
			int NetId = mWifiManager.addNetwork(wc);
			boolean es = mWifiManager.saveConfiguration();
			if(!es || NetId < 0)
				Log.e(TAG, "wifiConnect---->add CMCC_AUTO failed");
		}
	}
	
	/**
	 * return if device support eap-sim, the support devices are list on R.raw.device
	 * @param context
	 * @return
	 */
	public static boolean isDeviceSupportEapSim(Context context) {
		StringBuffer buffer = new StringBuffer();
		InputStreamReader inputStreamReader = new 
				InputStreamReader(context.getResources().openRawResource(R.raw.device));
		int oneChar;
		try {
			while ((oneChar = inputStreamReader.read()) > -1) {
				buffer.append((char) oneChar);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] devices = buffer.toString().split(";");
		if(Arrays.binarySearch(devices, Build.MODEL) > -1) {
			return true;
		}
		
		return false;
	}
	
	public static final int SIGNAL_VALID = 0;
	public static final int SIGNAL_INVALID = 1;
	public static final int SIGNAL_NONE = 2;
	public static final int SIGNAL_NO_RECORD = -1;
	/**
	 * called to get the valid signal strength WiFi network
	 * @param wifi
	 * @return 1 invalid; 0: valid; -1 no record; 2 none
	 */
	public static int validateSignalStrengthNetwork(WifiModel wifi) {
		wifi.signalStrengthStatus = CommonUtil.getFinalSignalResult(wifi.signalRecord);
		switch (wifi.signalStrengthStatus) {
		case WifiModel.SIGNAL_STATE_VALID:
			return SIGNAL_VALID;
		case WifiModel.SIGNAL_STATE_NONE:
			return SIGNAL_NONE;
		case WifiModel.SIGNAL_STATE_INVALID:
			return SIGNAL_INVALID;
		case -1:
			Log.w(TAG, "signalRecord is null");
			break;
		}
		return SIGNAL_NO_RECORD;
	}
	
	/**
	 * choose a valid WiFi to connect
	 * @return
	 */
	public static int pickValidWifi(List<NetworkModel> list) {
		for (int i = 0; i < list.size(); i++) {
			NetworkModel net = list.get(i);
			if(net.getType() == NetworkModel.TYPE_WIFI) {
				WifiModel wifi = (WifiModel) net;
				if ((wifi.signalStrengthStatus != WifiModel.SIGNAL_STATE_NONE)
						&& !wifi.isAbandoned()) {
					return i;
				}
			}
		}
		return -1;
	}
	
	/**
	 * return if target network connected or not
	 * @param context
	 * @param net
	 * @return
	 */
	public static boolean isNetworkConnected(Context context, NetworkModel net) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State netState = null;
		if(net.getType() == NetworkModel.TYPE_WIFI) {
			netState = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
			WifiModel wifiInfo = getCurrentConnectionWifi(context);
			if(wifiInfo != null && netState.toString().equals(State.CONNECTED.toString()) 
					&& CommonUtil.isSameWifi(wifiInfo, (WifiModel) net)) {
				return true;
			} else {
				return false;
			}
		} else if(net.getType() == NetworkModel.TYPE_3G) {
			netState = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		}
		 
		return netState.toString().equals(State.CONNECTED.toString());
	}
	
	/**
	 * get current connected wifi
	 * @param context
	 * @return
	 */
	private static WifiModel getCurrentConnectionWifi(Context context) {
		WifiManager mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo currentInfo = mWifiManager.getConnectionInfo();
		if(currentInfo == null || currentInfo.getSSID() == null) {
			return null;
		}
		if(currentInfo.getSSID().equals(Constants.CMCC)) {
			return WifiUtil.readWepConfig(context, currentInfo.getSSID());
		} else if(currentInfo.getSSID().equals(Constants.CMCC_AUTO)) {
			if(AppApplication.isEapSim) {
				return WifiUtil.readEapSimConfig(context, currentInfo.getSSID());
			} else {
				return WifiUtil.readEapConfig(context, currentInfo.getSSID());
			}
		}
		return null;
	}
	
	/**
	 * return if screen locked
	 * @param c
	 * @return
	 */
	public final static boolean isScreenLocked(Context c) {
		android.app.KeyguardManager mKeyguardManager = (KeyguardManager) c
				.getSystemService(c.KEYGUARD_SERVICE);
		return !mKeyguardManager.inKeyguardRestrictedInputMode();
	}
	
	/**
	 * return if screen on
	 * @param context
	 * @return
	 */
	public static boolean isScreenOn(Context context) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);   
		return pm.isScreenOn();
	}
	
	/**
	 * get current connected network
	 * @param context
	 * @return
	 */
	public static int getCurrentNetworkInfo(Context context) {
		ConnectivityManager mag = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mag.getActiveNetworkInfo();
		if(info == null) {
			return -1;
		} else {
			if(info.getType() == ConnectivityManager.TYPE_WIFI) {
				return 0;
			} else if(info.getType() == ConnectivityManager.TYPE_MOBILE) {
				return 1;
			}
		}
		return -1;
	}
}
