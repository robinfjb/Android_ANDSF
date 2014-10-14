package com.chinamobile.android.connectionmanager.util;

public class Constants {
		public static final String VERSION = "1.0";
		public static final String NAME = "cmri_andsf_cm";
	// the time of signal strength check cycle
		public static final int SIGNAL_TIME_INTERVAL = 10000;//10 second
		// the time of cell id change stable time
		public static int INTERVAL_TIME = 30000;//30 seconds//for test==============================================================
		// the time of WiFi scan time out
		public static final int TIMEOUT_SCAN = 30000;//30 seconds
		// the time of WiFi connection time out
		public static final int TIMEOUT_WIFI = 60000;//60 seconds
		// the time of 3G connection time out
		public static final int TIMEOUT_3G = 10000;//10 seconds
		// the minimum WiFi strength level
		public static int minDBMLevel = 2;
//		public static int minDBMLevel2 = 2;//for test==============================================================
		
		// the time of policy pick track task
		public static final int POLICY_PICK_TIME_INTERVAL = 5000;// 5 seconds
		// the time of restart connection task
		public static final int RESTART_CONNECTION_INTEVAL = 15000;// 15 seconds
		
		public static final int LOCATION_CHECK_INTEVAL = 5000;// 5 seconds
		
		public static final String LOCAL_XML_NAME = "local_static_policy.xml";
		public static final String XML_NAME = "policy.xml";
		
		// for policy
		public static final int MSG_HAS_NEW_POLICY = 0x01; //cell id changed message
		public static final int MSG_REQUEST_FAIL = 0x02; //http request fail message
		public static final int MSG_REQUEST_SUCCESS = 0x03; //http request success message
		public static final int MSG_STATIC_POLICY = 0x04;
		public static final int MSG_STATIC_POLICY_APPLY = 0x22;
		public static final int MSG_REQUEST_NO_ACTIVE_POLICY = 0x23;
		public static final int MSG_CLEAN_UP = 0x21;
		public static final int MSG_APP_NET = 0x24;
		// for connection
		public static final int MSG_CONNECTION_FAILED = 0x05;
		public static final int MSG_CONNECTION_SUCCESS = 0x06;
		public static final int MSG_CONNECTION_RADIO_CHANGE = 0x07;
		public static final int MSG_CONNECTION_OPEN_3G_FAILED = 0x08;
		public static final int MSG_CONNECTION_BREAK = 0x09;
		public static final int MSG_CONNECTION_ALREDAY_CONNECTED = 0x10;
		public static final int MSG_CONNECTION_SCAN_COMPLETE = 0x11;
		public static final int MSG_CONNECTION_SET_CMCC_ACCOUNT = 0x12;
		public static final int MSG_CONNECTION_SET_AUTO_ACCOUNT = 0x13;
		public static final int MSG_CONNECTION_HOTSPOT_FAILED = 0x14;
		public static final int MSG_CONNECTION_HOTSPOT_SUCCESS = 0x15;
		public static final int MSG_CONNECTION_HOTSPOT_ALREDAY_CONNECTED = 0x16;
		public static final int MSG_CONNECTION_HOTSPOT_BREAK = 0x17;
		public static final int MSG_CONNECTION_SET_CMCC_ACCOUNT_HOTSPOT = 0x18;
		public static final int MSG_CONNECTION_SET_AUTO_ACCOUNT_HOTSPOT = 0x19;
		public static final int MSG_CONNECTION_HOTSPOT_SCAN_COMPLETE = 0x20;
		
		public static final String mStrKey = "5D78C4DEEE93201F05B99638BD60A914C65AD672";//Baidu maps key
		public static final long MIN_DATE = 19700101;
		public static final int RETRY_TIME = 1;
		
		public static class Action {
			private static final String ACTION_HOST = "com.chinamobile.android.connectionmanager.action";
			public static final String ACTION_START = ACTION_HOST + ".start";
			public static final String ACTION_STOP = ACTION_HOST + ".stop";
			public static final String ACTION_PREFERED = ACTION_HOST + ".preferred";
			public static final String ACTION_HOTSPOT = ACTION_HOST + ".hotspot";
//			public static final String ACTION_GENERATE_POLICY = ACTION_HOST + ".generate.policy";
			public static final String ACTION_POLICY_APPLY = ACTION_HOST + ".policy.apply";
			public static final String ACTION_CONTINUE_CONNECTION = ACTION_HOST + ".connection";
			public static final String ACTION_OPEN_MOBILE_DATA = ACTION_HOST + ".mobiledata.open";
			public static final String ACTION_CLEAN_UP = ACTION_HOST + ".cleanup";
			
			public static final String ACTION_SETTING_DYNAMIC = ACTION_HOST + "setting.dynamic";
			public static final String ACTION_SETTING_SIGNAL = ACTION_HOST + "setting.signal";
			public static final String ACTION_SETTING_3GVALID = ACTION_HOST + "setting.3gvalid";
			public static final String ACTION_SETTING_AUTO_BREAK = ACTION_HOST + "setting.auto.break";
			public static final String ACTION_SETTING_POLICY_APPLY = ACTION_HOST + "setting.policy.apply";
			public static final String ACTION_SETTING_AUTO_RUN = ACTION_HOST + "setting.autorun";
			public static final String ACTION_SETTING_PREFERED = ACTION_HOST + "setting.preferred";
			public static final String ACTION_SETTING_HOTSPOT_NOTIFY = ACTION_HOST + "setting.hotspot.notify";
			
			public static final String ACTION_SCREEN_ON = ACTION_HOST + "screen.on";
			public static final String ACTION_SCREEN_OFF = ACTION_HOST + "screen.off";
			
			public static final String ACTION_APP_UPDATE = ACTION_HOST + "app.update";
		}
		
		public static final String CMCC_AUTO = "PCCW1x";//"CMCC-AUTO";
		public static final String CMCC = "CoovaChilli";//"CMCC";
		
		public static String POLICY_SERVER_URL = "219.143.2.217:8181/ANDSF_Server/ANDSF";//"195.53.58.173:8686/ANDSF_Server/ANDSF";////"58.251.159.129:8888/ANDSF_Server/ANDSF";//;//;//"10.185.3.109:8080/ANDSF_Server/poll?i=493005100592800";
		public static final String REPROT_SERVER_URL = "";
		
		//Http Constants
		public static final int HTTP_OVER_TIME = 30000;
		public static final int HTTP_RETRY = 1;
		
		//SMS Constants
		public static final String SMS_ADDRESS = "10086";
		public static final String SMS_CONTENT = "KTWLANZD";
		public static final String SMS_MODIFY_CONTENT = "XGWLANZDMM";
		public static final String SMS_CMCC_NORMAL = "KTWLAN";
		public static final String SMS_CMCC_5 = "KTWLAN5";
		public static final String SMS_CMCC_10 = "KTWLAN10";
		public static final String SMS_CMCC_20 = "KTWLAN20";
		public static final String SMS_CMCC_30 = "KTWLAN30";
		public static final String SMS_CMCC_50 = "KTWLAN50";
		public static final String SMS_CMCC_100 = "KTWLAN100";
		public static final String SMS_CMCC_200 = "KTWLAN200";
		public static final String SMS_MODIFY_CMCC_CONTENT = "XGWLANMM";
		
		//HOTSPOT
		public static final int ROUTE_TIMEOUT = 40000;
		public static final int GEO_TIMEOUT = 30000;
		public static final int SCAN_INTERVAL = 5000;
		public static final int MAX_LOCTIONCHANGE_INTERVAL = 10;// unit of s
		public static final int MIN_LOCTIONCHANGE_INTERVAL = 5;// unit of s
		
		public static final int TOP_APP_INTERVAL = 2000;
}
