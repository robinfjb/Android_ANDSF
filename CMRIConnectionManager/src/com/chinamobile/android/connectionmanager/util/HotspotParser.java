package com.chinamobile.android.connectionmanager.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

import com.chinamobile.android.connectionmanager.model.HotspotModel;
import com.chinamobile.android.connectionmanager.model.HotspotModel.WlanInfo;
import com.chinamobile.android.connectionmanager.model.NetworkModel;
import com.chinamobile.android.connectionmanager.model.WifiModel;

public class HotspotParser {
	private static final String DISCOVERY_INFORMATION = "DiscoveryInformation";
	private static final String ACCESS_NETWORK_TYPE = "AccessNetworkType";
	private static final String ACCESS_NETWORK_AREA = "AccessNetworkArea";
	private static final String G3PP_LOCATION = "_3GPP_Location";
	private static final String WLAN_LOCATION = "WLAN_Location";
	private static final String PLMN = "PLMN";
	private static final String LAC = "LAC";
	private static final String GERAN_CI = "GERAN_CI";
	private static final String GEO_LCATION = "Geo_Location";
	private static final String CIRCULAR = "Circular";
	private static final String ANCHORLATITUDE = "AnchorLatitude";
	private static final String ANCHORLONGITUDE = "AnchorLongitude";
	private static final String RADIUS = "Radius";
	private static final String ACCESS_NETWORK_INFORMATION_REF = "AccessNetworkInformationRef";
	
	private static final String ID = "ID";
	private static final String ADDR = "Addr";
	private static final String ADDR_TYPE = "AddrType";
	private static final String BEARER_TYPE = "BearerType";
	private static final String BEARER_PARAM = "BearerParams";
	private static final String WLAN = "WLAN";
	private static final String SSID_LIST = "SSIDList";
	private static final String SSID = "SSID";
	private static final String BSSID = "BSSID";
	
	private XmlPullParser pullParser;
	private Context context;
	
	public HotspotParser(String namespace, InputStream input) {
		try {
			pullParser = XmlPullParserFactory.newInstance().newPullParser();
			pullParser.setInput(new InputStreamReader(input));
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * parse the Discovery Information stream to list of {@link HotspotModel}
	 * @return list of {@link HotspotModel}
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public List<HotspotModel> parseHotspot() throws XmlPullParserException, IOException {
		List<HotspotModel> listResult = new ArrayList<HotspotModel>();
		HotspotModel hotspot = null;
		HotspotModel anotherHot = null;
		int eventType = pullParser.getEventType();
//		int geoCount = 0;
		List<HotspotModel> oneItemList = new ArrayList<HotspotModel>();
		try {
			while (eventType != XmlPullParser.END_DOCUMENT) {
//				if (eventType == XmlPullParser.START_TAG
//						&& DISCOVERY_INFORMATION.equalsIgnoreCase(pullParser
//								.getName())) {
//					while (true) {
//						if (eventType == XmlPullParser.END_DOCUMENT
//								|| (eventType == XmlPullParser.END_TAG && DISCOVERY_INFORMATION
//										.equalsIgnoreCase(pullParser.getName()))) {
//							break;
//						}
//						eventType = pullParser.next();
						
						if (eventType == XmlPullParser.START_TAG
								&& ACCESS_NETWORK_TYPE.equalsIgnoreCase(pullParser
										.getName())) {
							String type = pullParser.nextText().trim();
							if(type.compareTo(NetworkModel.NAME_WLAN) == 0) {
								hotspot = new HotspotModel();
								anotherHot = null;
								oneItemList = new ArrayList<HotspotModel>();
								try {
									hotspot.setAccessNetworkType(Integer.parseInt(type));
								} catch (Exception e) {
									e.printStackTrace();
								}
//								listResult.add(hotspot);
							}
//						} else if(eventType == XmlPullParser.START_TAG
//								&& ACCESS_NETWORK_AREA.equalsIgnoreCase(pullParser
//										.getName())) {
//							while (true) {
//								if (eventType == XmlPullParser.END_DOCUMENT
//										|| (eventType == XmlPullParser.END_TAG && ACCESS_NETWORK_AREA
//												.equalsIgnoreCase(pullParser.getName()))) {
//									break;
//								}
//								eventType = pullParser.next();
								
						} else if (eventType == XmlPullParser.START_TAG
										&& G3PP_LOCATION.equalsIgnoreCase(pullParser
												.getName())) {
									while (true) {
										if (eventType == XmlPullParser.END_DOCUMENT
												|| (eventType == XmlPullParser.END_TAG && G3PP_LOCATION
														.equalsIgnoreCase(pullParser.getName()))) {
											break;
										}
										eventType = pullParser.next();
										
										try {
											if (eventType == XmlPullParser.START_TAG
													&& PLMN.equalsIgnoreCase(pullParser.getName())) {
												if(hotspot != null) {
													hotspot.setG3_plmn(Integer.parseInt(pullParser.nextText().trim()));
												}
											} else if (eventType == XmlPullParser.START_TAG
													&& LAC.equalsIgnoreCase(pullParser.getName())) {
												if(hotspot != null) {
													hotspot.setG3_lac(Integer.valueOf(pullParser.nextText().trim(),16));
												}
											} else if (eventType == XmlPullParser.START_TAG
													&& GERAN_CI.equalsIgnoreCase(pullParser.getName())) {
												if(hotspot != null) {
													hotspot.setG3_cid(Long.parseLong(Integer.valueOf(pullParser.nextText().trim(),2).toString()));
												}
											}
										} catch (Exception e) {
											continue;
										}
										
									}
								} 
						//WLAN_LOCATION
						else if(eventType == XmlPullParser.START_TAG
								&& WLAN_LOCATION.equalsIgnoreCase(pullParser.getName())) {
							while (true) {
								if (eventType == XmlPullParser.END_DOCUMENT
										|| (eventType == XmlPullParser.END_TAG && WLAN_LOCATION
												.equalsIgnoreCase(pullParser.getName()))) {
									break;
								}
								eventType = pullParser.next();
								
								if(eventType == XmlPullParser.START_TAG
										&& SSID.equalsIgnoreCase(pullParser.getName())) {
									hotspot.wlanSsid = pullParser.nextText().trim();
									break;
								}
							}
						} 
						
						else if (eventType == XmlPullParser.START_TAG
										&& GEO_LCATION.equalsIgnoreCase(pullParser
												.getName())) {
									while (true) {
										if (eventType == XmlPullParser.END_DOCUMENT
												|| (eventType == XmlPullParser.END_TAG && GEO_LCATION
														.equalsIgnoreCase(pullParser.getName()))) {
											break;
										}
										eventType = pullParser.next();
										try {
											if (eventType == XmlPullParser.START_TAG
													&& ANCHORLATITUDE.equalsIgnoreCase(pullParser.getName())) {
												if(hotspot != null) {
													String content = pullParser.nextText().trim();
													if(content != null && !content.equals("")) {
														anotherHot = hotspot.clone();
														double latitude = LocationUtil.latitudeConverter(content);
														anotherHot.setLatitude(latitude);
														oneItemList.add(anotherHot);
													}
												}
											} else if (eventType == XmlPullParser.START_TAG
													&& ANCHORLONGITUDE.equalsIgnoreCase(pullParser.getName())) {
												if(hotspot != null && anotherHot != null) {
													String content = pullParser.nextText().trim();
													if(content != null && !content.equals("")) {
														double longtitude = LocationUtil.longtitudeConverter(content);
														anotherHot.setLongitude(longtitude);
													}
												}
											} else if (eventType == XmlPullParser.START_TAG
													&& RADIUS.equalsIgnoreCase(pullParser.getName())) {
//												if(hotspot != null) {
//													long radius = LocationUtil.radiusConverter(pullParser.nextText().trim());
//													hotspot.setRadius(radius);
//												}
											}
										} catch (Exception e) {
											continue;
										}
									}
								} else if (eventType == XmlPullParser.START_TAG
										&& ACCESS_NETWORK_INFORMATION_REF.equalsIgnoreCase(pullParser
												.getName())) {
									if(hotspot != null) {
//										hotspot.setAccessNetworkInformationRef();
										String accessNetworkInformationRef = pullParser.nextText().trim();
										for (HotspotModel hotspotModel : oneItemList) {
											hotspotModel.setAccessNetworkInformationRef(accessNetworkInformationRef);
										}
									}
								} else if (eventType == XmlPullParser.START_TAG
										&& PLMN.equalsIgnoreCase(pullParser
												.getName())) {
									if(hotspot != null) {
										String plmn = pullParser.nextText().trim();
										for (HotspotModel hotspotModel : oneItemList) {
											try {
												hotspotModel.setPlmn(Integer.parseInt(plmn));
											} catch (Exception e) {
												continue;
											}
										}
									}
									listResult.addAll(oneItemList);
								}
//							}
//						}
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
	 * parse the Wlan Mo stream to {@link WlanInfo}
	 * @return {@link WlanInfo}
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public WlanInfo parseWlanInfo() throws XmlPullParserException, IOException {
		WlanInfo wlan = new WlanInfo();
		int eventType = pullParser.getEventType();
		try {
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG
						&& ACCESS_NETWORK_INFORMATION_REF.equalsIgnoreCase(pullParser
								.getName())) {
					while (true) {
						if (eventType == XmlPullParser.END_DOCUMENT
								|| (eventType == XmlPullParser.END_TAG && ACCESS_NETWORK_INFORMATION_REF
										.equalsIgnoreCase(pullParser.getName()))) {
							break;
						}
						eventType = pullParser.next();
						
						if (eventType == XmlPullParser.START_TAG
								&& ID.equalsIgnoreCase(pullParser
										.getName())) {
							wlan.id = pullParser.nextText();
						} else if(eventType == XmlPullParser.START_TAG
								&& ADDR.equalsIgnoreCase(pullParser
										.getName())) {
							wlan.addr = pullParser.nextText();
						} else if(eventType == XmlPullParser.START_TAG
								&& ADDR_TYPE.equalsIgnoreCase(pullParser
										.getName())) {
							wlan.addr_type = pullParser.nextText();
						} else if(eventType == XmlPullParser.START_TAG
								&& BEARER_TYPE.equalsIgnoreCase(pullParser
										.getName())) {
							wlan.bearer_type = pullParser.nextText();
						} else if(eventType == XmlPullParser.START_TAG
								&& BEARER_PARAM.equalsIgnoreCase(pullParser
										.getName())) {
							while (true) {
								if (eventType == XmlPullParser.END_DOCUMENT
										|| (eventType == XmlPullParser.END_TAG && BEARER_PARAM
												.equalsIgnoreCase(pullParser.getName()))) {
									break;
								}
								eventType = pullParser.next();
								if (eventType == XmlPullParser.START_TAG
										&& SSID.equalsIgnoreCase(pullParser
												.getName())) {
									wlan.ssid = pullParser.nextText();
								}
							}
						}
					}
				}
				eventType = pullParser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return wlan;
	}
}
