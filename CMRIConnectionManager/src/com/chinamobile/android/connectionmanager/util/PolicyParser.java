package com.chinamobile.android.connectionmanager.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.model.NetworkModel;
import com.chinamobile.android.connectionmanager.model.PolicyModel;
import com.chinamobile.android.connectionmanager.model.PolicyModel.GeoLocation;
import com.chinamobile.android.connectionmanager.model.PolicyModel.TimeAndDate;
import com.chinamobile.android.connectionmanager.model.WifiModel;
import com.chinamobile.android.connectionmanager.model._3GModel;

public class PolicyParser {
	private static final String RESULTS = "RESULTS";
	private static final String MSGREF = "MSGREF";
	private static final String CMDREF = "CMDREF";
	private static final String CMDID = "CMDID";
	private static final String ITEM = "ITEM";
	private static final String RESPONSETYPE = "RESPONSETYPE";
	private static final String DATA = "DATA";
	private static final String POLICY = "POLICY";
	private static final String	RULEPRIORITY = "RULEPRIORITY";
	private static final String PRIORITIZEDACCESS = "PRIORITIZEDACCESS";
	private static final String ACCESSTECHNOLOGY = "ACCESSTECHNOLOGY";
	private static final String ACCESSID = "ACCESSID";
	private static final String ACCESSNETWORKPRIORITY = "ACCESSNETWORKPRIORITY";
	private static final String VALIDITYAREA = "VALIDITYAREA";
	private static final String _3GPP_LOCATION = "_3GPP_LOCATION";
	private static final String PLMN = "PLMN";
	private static final String TAC = "TAC";
	private static final String LAC = "LAC";
	private static final String GERAN_CI = "GERAN_CI";
	private static final String WLAN_LOCATION = "WLAN_LOCATION";
	private static final String SSID = "SSID";
	private static final String BSSID = "BSSID";
	private static final String GEO_LOCATION = "GEO_LOCATION";
	private static final String ANCHORLATITUDE = "ANCHORLATITUDE";
	private static final String ANCHORLONGITUDE = "ANCHORLONGITUDE";
	private static final String RADIUS = "RADIUS";
	private static final String TIMEOFDAY = "TIMEOFDAY";
	private static final String TIMESTART = "TIMESTART";
	private static final String TIMESTOP = "TIMESTOP";
	private static final String DATESTART = "DATESTART";
	private static final String DATESTOP = "DATESTOP";
	private static final String NAME = "NAME";
	private static final String ANDSF = "ANDSF";
	
	private XmlPullParser pullParser;
	private String responseType;
	private Context context;
	
	public PolicyParser(String namespace, InputStream input) {
		try {
			pullParser = XmlPullParserFactory.newInstance().newPullParser();
			pullParser.setInput(new InputStreamReader(input));
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public PolicyParser(Context context) {
		this.context = context;
	}

	/**
	 * parse the Policy stream to list of {@link PolicyModel}
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public List<PolicyModel> parse() throws XmlPullParserException, IOException {
		List<PolicyModel> listResult = new ArrayList<PolicyModel>();
		PolicyModel policyModel = null;
		int eventType = pullParser.getEventType();
		try {
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
//					if (ANDSF.equalsIgnoreCase(pullParser.getName())) {
//						while (true) {// children of categories
//							if (eventType == XmlPullParser.END_DOCUMENT
//									|| (eventType == XmlPullParser.END_TAG && ANDSF
//											.equalsIgnoreCase(pullParser.getName()))) {
//								break;
//							}
//							eventType = pullParser.next();

//							if (eventType == XmlPullParser.START_TAG
//									&& POLICY.equalsIgnoreCase(pullParser
//											.getName())) { //POLICY
								
//								while (true) {
//									if (eventType == XmlPullParser.END_DOCUMENT
//											|| (eventType == XmlPullParser.END_TAG && POLICY
//													.equalsIgnoreCase(pullParser.getName()))) {
//										if(policyModel != null) {
//											listResult.add(policyModel);
//										}
//										break;
//									}
//									eventType = pullParser.next();
									
									//RULEPRIORITY
									if(eventType == XmlPullParser.START_TAG
											&& RULEPRIORITY.equalsIgnoreCase(pullParser.getName())) {
										try {
											policyModel = new PolicyModel();
											listResult.add(policyModel);
											policyModel.setRulePriority(Integer.parseInt(pullParser.nextText().trim()));
										} catch (Exception e) {
											continue;
										}
									}
									
									// PRIORITIZEDACCESS
									else if(eventType == XmlPullParser.START_TAG
											&& PRIORITIZEDACCESS.equalsIgnoreCase(pullParser.getName())) {
										NetworkModel net = null;
										List<NetworkModel> accessNetworkList = new ArrayList<NetworkModel>();
										while (true) {
											if (eventType == XmlPullParser.END_DOCUMENT
													|| (eventType == XmlPullParser.END_TAG && PRIORITIZEDACCESS
															.equalsIgnoreCase(pullParser.getName()))) {
												break;
											}
											eventType = pullParser.next();
											
											if(eventType == XmlPullParser.START_TAG
													&& ACCESSTECHNOLOGY.equalsIgnoreCase(pullParser.getName())) {
												String text = pullParser.nextText().trim();
												try {
													if(Integer.parseInt(text) == 3) {
														net = new WifiModel(text, null, 0);
													} else if(Integer.parseInt(text) == 1) {
														net = new _3GModel(text, 0);
													} else {
														throw new Exception();
													}
												} catch (Exception e) {
													accessNetworkList.clear();
													break;
												}
											} else if(eventType == XmlPullParser.START_TAG
													&& ACCESSID.equalsIgnoreCase(pullParser.getName())) {
												if(net != null && net.getType() == NetworkModel.TYPE_WIFI) {
													((WifiModel) net).setSSID(pullParser.nextText().trim());
												}
											} else if(eventType == XmlPullParser.START_TAG
													&& ACCESSNETWORKPRIORITY.equalsIgnoreCase(pullParser.getName())) {
												if(net != null) {
													try {
														net.setPriority(Integer.parseInt(pullParser.nextText().trim()));
													} catch (Exception e) {
														continue;
													}
													accessNetworkList.add(net);
												}
											} 
										}
										policyModel.setAccessNetworkList(accessNetworkList);	
									} 
									
									//_3GPP_LOCATION
									else if(eventType == XmlPullParser.START_TAG
											&& _3GPP_LOCATION.equalsIgnoreCase(pullParser.getName())) {
										List<_3GModel> location3GList = new ArrayList<_3GModel>();
										_3GModel _3gM = null;
										while (true) {
											if (eventType == XmlPullParser.END_DOCUMENT
													|| (eventType == XmlPullParser.END_TAG && _3GPP_LOCATION
															.equalsIgnoreCase(pullParser.getName()))) {
												break;
											}
											eventType = pullParser.next();
											try {
											if(eventType == XmlPullParser.START_TAG
													&& PLMN.equalsIgnoreCase(pullParser.getName())) {
												_3gM = new _3GModel();
												
												_3gM.setPlmn(Integer.parseInt(pullParser.nextText().trim()));
												
												location3GList.add(_3gM);
											} else if(eventType == XmlPullParser.START_TAG
													&& TAC.equalsIgnoreCase(pullParser.getName())) {
												if(_3gM != null) {
													_3gM.setTac(Long.parseLong(pullParser.nextText().trim()));
												}
											} else if(eventType == XmlPullParser.START_TAG
													&& LAC.equalsIgnoreCase(pullParser.getName())) {
												if(_3gM != null) {
													_3gM.setLac(Long.parseLong(Integer.valueOf(pullParser.nextText().trim(),16).toString()));
												}
											} else if(eventType == XmlPullParser.START_TAG
													&& GERAN_CI.equalsIgnoreCase(pullParser.getName())) {
												if(_3gM != null) {
													_3gM.setCid(Long.parseLong(Integer.valueOf(pullParser.nextText().trim(),2).toString()));
												}
											}
											} catch (Exception e) {
												continue;
											}
										}
										policyModel.setLocation3GList(location3GList);
									} 
									
									//WLAN_LOCATION
									else if(eventType == XmlPullParser.START_TAG
											&& WLAN_LOCATION.equalsIgnoreCase(pullParser.getName())) {
										List<WifiModel> locationWlanList = new ArrayList<WifiModel>();
										WifiModel wifiInfo = null;
										while (true) {
											if (eventType == XmlPullParser.END_DOCUMENT
													|| (eventType == XmlPullParser.END_TAG && WLAN_LOCATION
															.equalsIgnoreCase(pullParser.getName()))) {
												break;
											}
											eventType = pullParser.next();
											
											if(eventType == XmlPullParser.START_TAG
													&& SSID.equalsIgnoreCase(pullParser.getName())) {
												wifiInfo = new WifiModel(pullParser.nextText().trim());
												locationWlanList.add(wifiInfo);
											} else if(eventType == XmlPullParser.START_TAG
													&& BSSID.equalsIgnoreCase(pullParser.getName())) {
												if(wifiInfo != null)
													wifiInfo.setBSSID(pullParser.nextText().trim());
											}
										}
										policyModel.setLocationWlanList(locationWlanList);
										
									} 
									
									//GEO_LOCATION
									else if(eventType == XmlPullParser.START_TAG
											&& GEO_LOCATION.equalsIgnoreCase(pullParser.getName())) {
										List<GeoLocation> geoLocationList = new ArrayList<GeoLocation>();
										GeoLocation geoInfo = null;
										while (true) {
											if (eventType == XmlPullParser.END_DOCUMENT
													|| (eventType == XmlPullParser.END_TAG && GEO_LOCATION
															.equalsIgnoreCase(pullParser.getName()))) {
												break;
											}
											eventType = pullParser.next();
											
											try{
											if(eventType == XmlPullParser.START_TAG
													&& ANCHORLATITUDE.equalsIgnoreCase(pullParser.getName())) {
												geoInfo = new GeoLocation();
												double latitude = LocationUtil.latitudeConverter(pullParser.nextText().trim());
												geoInfo.setLatitude(latitude);
												geoLocationList.add(geoInfo);
											} else if(eventType == XmlPullParser.START_TAG
													&& ANCHORLONGITUDE.equalsIgnoreCase(pullParser.getName())) {
												if(geoInfo != null) {
													double longtitude = LocationUtil.longtitudeConverter(pullParser.nextText().trim());
													geoInfo.setLongtitude(longtitude);
												}
											} else if(eventType == XmlPullParser.START_TAG
													&& RADIUS.equalsIgnoreCase(pullParser.getName())) {
												if(geoInfo != null) {
													long radius = LocationUtil.radiusConverter(pullParser.nextText().trim());
													geoInfo.setRadius(radius);
												}
											}
											}catch (Exception e) {
												continue;
											}
										}
										policyModel.setGeoLocationList(geoLocationList);
										
									}
									
									//TIMEOFDAY
									else if(eventType == XmlPullParser.START_TAG
											&& TIMEOFDAY.equalsIgnoreCase(pullParser.getName())) {
										List<TimeAndDate> timeList = new ArrayList<TimeAndDate>();
										TimeAndDate timeDate = null;
										while (true) {
											if (eventType == XmlPullParser.END_DOCUMENT
													|| (eventType == XmlPullParser.END_TAG && TIMEOFDAY
															.equalsIgnoreCase(pullParser.getName()))) {
												break;
											}
											eventType = pullParser.next();
											try{
											if(eventType == XmlPullParser.START_TAG
													&& TIMESTART.equalsIgnoreCase(pullParser.getName())) {
												timeDate = new TimeAndDate();
												timeDate.setStartTime(Integer.parseInt(pullParser.nextText().trim()));
												timeList.add(timeDate);
											} else if(eventType == XmlPullParser.START_TAG
													&& TIMESTOP.equalsIgnoreCase(pullParser.getName())) {
												if(timeDate != null) {
													timeDate.setEndTime(Integer.parseInt(pullParser.nextText().trim()));
												}
											} else if(eventType == XmlPullParser.START_TAG
													&& DATESTART.equalsIgnoreCase(pullParser.getName())) {
												if(timeDate != null) {
													timeDate.setStartDate(Integer.parseInt(pullParser.nextText().trim()));
												}
											} else if(eventType == XmlPullParser.START_TAG
													&& DATESTOP.equalsIgnoreCase(pullParser.getName())) {
												if(timeDate != null) {
													timeDate.setEndDate(Integer.parseInt(pullParser.nextText().trim()));
												}
											} 
											}catch (Exception e) {
												continue;
											}
										}
										policyModel.setTimeList(timeList);
									}
									
//									else if(eventType == XmlPullParser.START_TAG
//											&& PLMN.equalsIgnoreCase(pullParser.getName())) {
//										if(policyModel != null) {
//											listResult.add(policyModel);
//										}
//									}
									
//								}
								
								
//							}
						}
//					}
//				}
				eventType = pullParser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return listResult;
	}
	
	/**
	 * build the default policy
	 * @param netList
	 * @throws XmlPullParserException
	 * @throws RuntimeException
	 * @throws IOException
	 */
	@Deprecated
	public void bulidPolicyXml(List<NetworkModel> netList)
			throws XmlPullParserException, RuntimeException, IOException {
		XmlSerializer serializer = null;
		StringWriter writer = null;
		try {
			serializer = XmlPullParserFactory.newInstance()
					.newSerializer();
			writer = new StringWriter();
			serializer.setOutput(writer);
			serializer.startDocument("utf-8", true);
			serializer.startTag(null, ANDSF);
//			serializer.startTag(null, NAME);
//			serializer.text("Default");
//			serializer.endTag(null, NAME);
//			serializer.startTag(null, POLICY);
			serializer.startTag(null, RULEPRIORITY);
			serializer.text("0");
			serializer.endTag(null, RULEPRIORITY);
			// PRIORITIZEDACCESS
			serializer.startTag(null, PRIORITIZEDACCESS);
			for (NetworkModel networkMode : netList) {
				
				
				
				serializer.startTag(null, ACCESSTECHNOLOGY);
				serializer.text(networkMode.getType() == NetworkModel.TYPE_WIFI ? "3" : "1");
				serializer.endTag(null, ACCESSTECHNOLOGY);
				if (networkMode.getType() == NetworkModel.TYPE_WIFI) {
					serializer.startTag(null, ACCESSID);
					if(((WifiModel) networkMode).getSSID() != null)
						serializer.text(((WifiModel) networkMode).getSSID());
					serializer.endTag(null, ACCESSID);
				}
				serializer.startTag(null, ACCESSNETWORKPRIORITY);
				serializer.text(networkMode.getPriority().toString());
				serializer.endTag(null, ACCESSNETWORKPRIORITY);
				
				
			}
			serializer.endTag(null, PRIORITIZEDACCESS);
			serializer.startTag(null, PLMN);
			serializer.text("46000");
			serializer.endTag(null, PLMN);
//			serializer.endTag(null, POLICY);
			serializer.endTag(null, ANDSF);
			serializer.endDocument();
			
			FileUtil.saveFile2InternalStorage(context, Constants.LOCAL_XML_NAME, writer.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				if(serializer != null)
					serializer.flush();
				if(writer != null)
					writer.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}
