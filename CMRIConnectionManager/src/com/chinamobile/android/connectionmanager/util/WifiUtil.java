package com.chinamobile.android.connectionmanager.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import com.chinamobile.android.connectionmanager.AppApplication;
import com.chinamobile.android.connectionmanager.model.WifiModel;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.PairwiseCipher;
import android.net.wifi.WifiConfiguration.Protocol;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Parcel;
import android.util.Log;

public class WifiUtil {
	private static final String INT_PRIVATE_KEY = "private_key";
    private static final String INT_PHASE2 = "phase2";
    private static final String INT_PASSWORD = "password";
    private static final String INT_IDENTITY = "identity";
    private static final String INT_EAP = "eap";
    private static final String INT_CLIENT_CERT = "client_cert";
    private static final String INT_CA_CERT = "ca_cert";
    private static final String INT_ANONYMOUS_IDENTITY = "anonymous_identity";
    private static final String INT_ENTERPRISEFIELD_NAME = "android.net.wifi.WifiConfiguration$EnterpriseField";
    
    /**
     * update eap-sim type {@link WifiConfiguration}
     * @param config
     */
    public static void updateEapSimConfig(WifiConfiguration config) {
		config.allowedKeyManagement.clear();
		config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
		config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
		config.allowedGroupCiphers.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		setWifiConfigurationField(config, "eap", "SIM");
		setWifiConfigurationField(config, "phase2", "");
		setWifiConfigurationField(config, "anonymous_identity", "");
		setWifiConfigurationField(config, "identity", "");
		setWifiConfigurationField(config, "password", "");
		setWifiConfigurationField(config, "client_cert", "");
		setWifiConfigurationField(config, "private_key", "");
		setWifiConfigurationField(config, "ca_cert", "");
    }
    
    /**
     * read eap-sim type {@link WifiModel}
     * @param ssid
     * @return
     */
    public static WifiModel readEapSimConfig(Context context, String ssid) {
    	WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> configList = mWifiManager.getConfiguredNetworks();
		WifiModel wifi = new WifiModel(ssid);
		for (int i = 0; i < configList.size(); i++) {
			if (configList.get(i).SSID.contentEquals("\"" + ssid + "\"")) {
				WifiConfiguration config = configList.get(i);
				try {
					Class[] wcClasses = WifiConfiguration.class.getClasses();
					Class wcEnterpriseField = null;
					for (Class wcClass : wcClasses)
						if (wcClass.getName().equals(INT_ENTERPRISEFIELD_NAME)) {
							wcEnterpriseField = wcClass;
							break;
						}
                    boolean noEnterpriseFieldType = false; 
                    if(wcEnterpriseField == null)
                        noEnterpriseFieldType = true;

					Field wcefAnonymousId = null, wcefCaCert = null, wcefClientCert = null, wcefEap = null, wcefIdentity = null, wcefPassword = null, wcefPhase2 = null, wcefPrivateKey = null;
					Field[] wcefFields = WifiConfiguration.class.getFields();
					for (Field wcefField : wcefFields) {
						if (wcefField.getName().trim()
								.equals(INT_ANONYMOUS_IDENTITY))
							wcefAnonymousId = wcefField;
						else if (wcefField.getName().trim().equals(INT_CA_CERT))
							wcefCaCert = wcefField;
						else if (wcefField.getName().trim()
								.equals(INT_CLIENT_CERT))
							wcefClientCert = wcefField;
						else if (wcefField.getName().trim().equals(INT_EAP))
							wcefEap = wcefField;
						else if (wcefField.getName().trim()
								.equals(INT_IDENTITY))
							wcefIdentity = wcefField;
						else if (wcefField.getName().trim()
								.equals(INT_PASSWORD))
							wcefPassword = wcefField;
						else if (wcefField.getName().trim().equals(INT_PHASE2))
							wcefPhase2 = wcefField;
						else if (wcefField.getName().trim()
								.equals(INT_PRIVATE_KEY))
							wcefPrivateKey = wcefField;
					}
	                Method wcefSetValue = null;
					if (!noEnterpriseFieldType) {
						for (Method m : wcEnterpriseField.getMethods())
							// System.out.println(m.getName());
							if (m.getName().trim().equals("value")) {
								wcefSetValue = m;
								break;
							}
					}

					/* EAP Method */
					String result = null;
					Object obj = null;
					if (!noEnterpriseFieldType) {
						obj = wcefSetValue.invoke(wcefEap.get(config), null);
						wifi.setAuthenticationType((String) obj);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
    	return wifi;
    }
    
    /**
     * get eap-sim type {@link WifiConfiguration}
     * @param ssid
     * @return
     */
    public static WifiConfiguration getEapSimConfig(String ssid) {
    	WifiConfiguration wc = new WifiConfiguration();
    	wc.SSID = "\"" + ssid + "\"";
    	wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
    	wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
    	setWifiConfigurationField(wc,"eap","SIM");
    	return wc;
    }
    
    /**
     * update peap type {@link WifiConfiguration}
     * @param config
     * @param username
     * @param psw
     */
    public static void updatePeapConfig(WifiConfiguration config, String username, String psw) {
		String osVersion = Build.VERSION.RELEASE;
        config.allowedKeyManagement.clear();
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        config.allowedGroupCiphers.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
//        config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//        config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
//        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
//        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        
        try {
        	
			if (osVersion.compareTo("2.2") >= 0) {
				setWifiConfigurationField(config,"eap","PEAP");
				setWifiConfigurationField(config,"phase2", "auth=MSCHAPV2");
				if(CommonUtil.isOphone()) {
					setWifiConfigurationField(config,"anonymous_identity",null);
				} else {
					setWifiConfigurationField(config,"anonymous_identity","");
				}
				setWifiConfigurationField(config,"identity",username);
				setWifiConfigurationField(config,"password",psw);
			} else if (osVersion.compareTo("2.1") >= 0) {
				setWifiConfigurationField(config,"eap","PEAP");
				setWifiConfigurationField(config,"phase2",String.format("\"%s\"","auth=MSCHAPV2"));
				if(CommonUtil.isOphone()) {
					setWifiConfigurationField(config,"anonymous_identity",null);
				} else {
					setWifiConfigurationField(config,"anonymous_identity","");
				}
				setWifiConfigurationField(config,"identity",String.format("\"%s\"",username));
				setWifiConfigurationField(config,"password",String.format("\"%s\"",psw));
			}
			if(CommonUtil.isOphone()) {
				setWifiConfigurationField(config, "client_cert", null);
				setWifiConfigurationField(config, "private_key", null);
				setWifiConfigurationField(config, "ca_cert", null);
			} else {
				setWifiConfigurationField(config, "client_cert", "");
				setWifiConfigurationField(config, "private_key", "");
				setWifiConfigurationField(config, "ca_cert", "");
			}
        } catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    /**
     * get peap type {@link WifiModel}
     * @param context
     * @param ssid
     * @return
     */
	public static WifiModel readEapConfig(Context context, String ssid) {
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> configList = mWifiManager.getConfiguredNetworks();
		WifiModel wifi = new WifiModel(ssid);
        /*Now we need to search appropriate configuration i.e. with name SSID_Name*/
		for (int i = 0; i < configList.size(); i++) {
			if (configList.get(i).SSID.contentEquals("\"" + ssid + "\"")) {
				WifiConfiguration config = configList.get(i);

              
                
				try {
					Class[] wcClasses = WifiConfiguration.class.getClasses();
					Class wcEnterpriseField = null;
					for (Class wcClass : wcClasses)
						if (wcClass.getName().equals(INT_ENTERPRISEFIELD_NAME)) {
							wcEnterpriseField = wcClass;
							break;
						}
                    boolean noEnterpriseFieldType = false; 
                    if(wcEnterpriseField == null)
                        noEnterpriseFieldType = true;

					Field wcefAnonymousId = null, wcefCaCert = null, wcefClientCert = null, wcefEap = null, wcefIdentity = null, wcefPassword = null, wcefPhase2 = null, wcefPrivateKey = null;
					Field[] wcefFields = WifiConfiguration.class.getFields();
					for (Field wcefField : wcefFields) {
						if (wcefField.getName().trim()
								.equals(INT_ANONYMOUS_IDENTITY))
							wcefAnonymousId = wcefField;
						else if (wcefField.getName().trim().equals(INT_CA_CERT))
							wcefCaCert = wcefField;
						else if (wcefField.getName().trim()
								.equals(INT_CLIENT_CERT))
							wcefClientCert = wcefField;
						else if (wcefField.getName().trim().equals(INT_EAP))
							wcefEap = wcefField;
						else if (wcefField.getName().trim()
								.equals(INT_IDENTITY))
							wcefIdentity = wcefField;
						else if (wcefField.getName().trim()
								.equals(INT_PASSWORD))
							wcefPassword = wcefField;
						else if (wcefField.getName().trim().equals(INT_PHASE2))
							wcefPhase2 = wcefField;
						else if (wcefField.getName().trim()
								.equals(INT_PRIVATE_KEY))
							wcefPrivateKey = wcefField;
					}
	                Method wcefSetValue = null;
					if (!noEnterpriseFieldType) {
						for (Method m : wcEnterpriseField.getMethods())
							// System.out.println(m.getName());
							if (m.getName().trim().equals("value")) {
								wcefSetValue = m;
								break;
							}
					}

					/* EAP Method */
					String result = null;
					Object obj = null;
					if (!noEnterpriseFieldType) {
						obj = wcefSetValue.invoke(wcefEap.get(config), null);
						wifi.setAuthenticationType((String) obj);
					}

					/* phase 2 */
					if (!noEnterpriseFieldType) {
						result = (String) wcefSetValue.invoke(wcefPhase2.get(config), null);
						wifi.phase2 = result;
					}

					/* Anonymous Identity */
					if (!noEnterpriseFieldType) {
						result = (String) wcefSetValue.invoke(wcefAnonymousId.get(config), null);
						wifi.anonymous = result;
					}
		
		            /*CA certificate*/
					if (!noEnterpriseFieldType) {
		                result = (String) wcefSetValue.invoke(wcefCaCert.get(config), null);
//		                Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>", "[EAP CA CERTIFICATE]" + result);
		            }
		
		            /*private key*/
					if (!noEnterpriseFieldType) {
		                result = (String) wcefSetValue.invoke(wcefPrivateKey.get(config),null);
//		                Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>", "[EAP PRIVATE KEY]" + result);
		            }
		
		            /*Identity*/
					if (!noEnterpriseFieldType) {
		                result = (String) wcefSetValue.invoke(wcefIdentity.get(config), null);
		                wifi.identity = result;
		            }
		
		            /*Password*/
					if (!noEnterpriseFieldType) {
		                result = (String) wcefSetValue.invoke(wcefPassword.get(config), null);
		                wifi.password = result;
		            }
		
		            /*client certificate*/
					if (!noEnterpriseFieldType) {
		                result = (String) wcefSetValue.invoke(wcefClientCert.get(config), null);
//		                Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>", "[EAP CLIENT CERT]" + result);
		            }
		
	            }
	            catch(Exception e)
	            {
	                e.printStackTrace();
	            }
				return wifi;
            }
        }
		return null;
	}
	
	/**
	 * get peap type {@link WifiConfiguration}
	 * @param ssid
	 * @param username
	 * @param psw
	 * @return
	 */
	public static WifiConfiguration getPeapConfig(String ssid, String username, String psw) {
		 /********************************Configuration Strings****************************************************/
       final String ENTERPRISE_EAP = "PEAP";
//       final String ENTERPRISE_CLIENT_CERT = "keystore://USRCERT_CertificateName";
//       final String ENTERPRISE_PRIV_KEY = "keystore://USRPKEY_CertificateName";

       /*Optional Params- My wireless Doesn't use these*/
       final String ENTERPRISE_PHASE2 = "auth=MSCHAPV2";
       final String ENTERPRISE_ANON_IDENT = "";
       final String ENTERPRISE_CA_CERT = "";
       /********************************Configuration Strings****************************************************/

       /*Create a WifiConfig*/
       WifiConfiguration selectedConfig = new WifiConfiguration();
       String osVersion = Build.VERSION.RELEASE;
//       Log.i("WifiPreference", osVersion);
       
       /*AP Name*/
       selectedConfig.SSID = "\"" + ssid + "\"";
       /*Priority*/
       /*Enable Hidden SSID*/
//       selectedConfig.hiddenSSID = true;
       /*Key Mgmnt*/
       selectedConfig.allowedKeyManagement.clear();
       selectedConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
       selectedConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
       
       /*Group Ciphers*/
       selectedConfig.allowedGroupCiphers.clear();
//       selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//       selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//       selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
//       selectedConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
       /*Pairwise ciphers*/
       selectedConfig.allowedPairwiseCiphers.clear();
//       selectedConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//       selectedConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
       /*Protocols*/
       selectedConfig.allowedProtocols.clear();
//       selectedConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
//       selectedConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

       // Enterprise Settings
       // Reflection magic here too, need access to non-public APIs
       try {
       	
    	   
    	   if (osVersion.compareTo("2.2") >= 0) {
				setWifiConfigurationField(selectedConfig,"eap","PEAP");
				setWifiConfigurationField(selectedConfig,"phase2", "auth=MSCHAPV2");
				setWifiConfigurationField(selectedConfig,"identity",username);
				setWifiConfigurationField(selectedConfig,"password",psw);
			} else if (osVersion.compareTo("2.1") >= 0) {
				setWifiConfigurationField(selectedConfig,"eap","PEAP");
				setWifiConfigurationField(selectedConfig,"phase2",String.format("\"%s\"","auth=MSCHAPV2"));
				setWifiConfigurationField(selectedConfig,"identity",String.format("\"%s\"",username));
				setWifiConfigurationField(selectedConfig,"password",String.format("\"%s\"",psw));
			}
			
          /* // Let the magic start
           Class[] wcClasses = WifiConfiguration.class.getClasses();
           // null for overzealous java compiler
           Class wcEnterpriseField = null;

           for (Class wcClass : wcClasses)
               if (wcClass.getName().equals(INT_ENTERPRISEFIELD_NAME)) 
               {
                   wcEnterpriseField = wcClass;
                   break;
               }
           boolean noEnterpriseFieldType = false; 
           if(wcEnterpriseField == null)
               noEnterpriseFieldType = true; // Cupcake/Donut access enterprise settings directly

           Field wcefAnonymousId = null, wcefCaCert = null, wcefClientCert = null, wcefEap = null, wcefIdentity = null, wcefPassword = null, wcefPhase2 = null, wcefPrivateKey = null;
           Field[] wcefFields = WifiConfiguration.class.getFields();
           for (Field wcefField : wcefFields) 
           {
               if (wcefField.getName().equals(INT_ANONYMOUS_IDENTITY))
                   wcefAnonymousId = wcefField;
               else if (wcefField.getName().equals(INT_CA_CERT))
                   wcefCaCert = wcefField;
               else if (wcefField.getName().equals(INT_CLIENT_CERT))
                   wcefClientCert = wcefField;
               else if (wcefField.getName().equals(INT_EAP))
                   wcefEap = wcefField;
               else if (wcefField.getName().equals(INT_IDENTITY))
                   wcefIdentity = wcefField;
               else if (wcefField.getName().equals(INT_PASSWORD))
                   wcefPassword = wcefField;
               else if (wcefField.getName().equalsIgnoreCase(INT_PHASE2))
                   wcefPhase2 = wcefField;
               else if (wcefField.getName().equals(INT_PRIVATE_KEY))
                   wcefPrivateKey = wcefField;
           }


           Method wcefSetValue = null;
           if(!noEnterpriseFieldType){
           for(Method m: wcEnterpriseField.getMethods())
               //System.out.println(m.getName());
               if(m.getName().trim().equals("setValue"))
                   wcefSetValue = m;
           }
           
           EAP Method
           if(!noEnterpriseFieldType)
               wcefSetValue.invoke(wcefEap.get(selectedConfig), ENTERPRISE_EAP);

           EAP Phase 2 Authentication
           if(!noEnterpriseFieldType)
        	   if (osVersion.compareTo("2.2") >= 0) {
        		   wcefSetValue.invoke(wcefPhase2.get(selectedConfig), ENTERPRISE_PHASE2);
        	   } else if (osVersion.compareTo("2.1") >= 0) {
        		   wcefSetValue.invoke(wcefPhase2.get(selectedConfig), String.format("\"%s\"",ENTERPRISE_PHASE2));
        	   }
        	   

           EAP Anonymous Identity
           if(!noEnterpriseFieldType)
        	   if (osVersion.compareTo("2.2") >= 0) {
        		   wcefSetValue.invoke(wcefAnonymousId.get(selectedConfig), ENTERPRISE_ANON_IDENT);
        	   } else if (osVersion.compareTo("2.1") >= 0) {
        		   wcefSetValue.invoke(wcefPhase2.get(selectedConfig), String.format("\"%s\"",ENTERPRISE_ANON_IDENT));
        	   }
              

           EAP CA Certificate
//           if(!noEnterpriseFieldType)
//               wcefSetValue.invoke(wcefCaCert.get(selectedConfig), ENTERPRISE_CA_CERT);

           EAP Private key
//           if(!noEnterpriseFieldType)
//               wcefSetValue.invoke(wcefPrivateKey.get(selectedConfig), ENTERPRISE_PRIV_KEY);

           EAP Identity
           if(!noEnterpriseFieldType)
        	   if (osVersion.compareTo("2.2") >= 0) {
        		   wcefSetValue.invoke(wcefIdentity.get(selectedConfig), username);
        	   } else if (osVersion.compareTo("2.1") >= 0) {
        		   wcefSetValue.invoke(wcefPhase2.get(selectedConfig), String.format("\"%s\"",username));
        	   }
               

           EAP Password
           if(!noEnterpriseFieldType)
        	   if (osVersion.compareTo("2.2") >= 0) {
        		   wcefSetValue.invoke(wcefPassword.get(selectedConfig), psw);
        	   } else if (osVersion.compareTo("2.1") >= 0) {
        		   wcefSetValue.invoke(wcefPhase2.get(selectedConfig), String.format("\"%s\"",psw));
        	   }

           EAp Client certificate
//           if(!noEnterpriseFieldType)
//               wcefSetValue.invoke(wcefClientCert.get(selectedConfig), ENTERPRISE_CLIENT_CERT);

           // Adhoc for CM6
           // if non-CM6 fails gracefully thanks to nested try-catch

//          try{
//           Field wcAdhoc = WifiConfiguration.class.getField("adhocSSID");
//           Field wcAdhocFreq = WifiConfiguration.class.getField("frequency");
//           //wcAdhoc.setBoolean(selectedConfig, prefs.getBoolean(PREF_ADHOC,
//           //      false));
//           wcAdhoc.setBoolean(selectedConfig, false);
//           int freq = 2462;    // default to channel 11
//           wcAdhocFreq.setInt(selectedConfig, freq); 
//           } catch (Exception e)
//           {
//               e.printStackTrace();
//           }
       	
       	*/

       } catch (Exception e)
       {
           e.printStackTrace();
       }

       return selectedConfig;
	}
	
	/**
	 * read wep open type {@link WifiModel}
	 * @param context
	 * @param ssid
	 * @return
	 */
	public static WifiModel readWepConfig(Context context, String ssid) {
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> item = mWifiManager.getConfiguredNetworks();
        int i = item.size();
        Iterator<WifiConfiguration> iter =  item.iterator();
        WifiModel wifi = new WifiModel(ssid);
        while(iter.hasNext()) {
        	WifiConfiguration config = iter.next();
        	if (config.SSID.contentEquals("\"" + ssid + "\"")) {
        		if(config.allowedAuthAlgorithms.get(AuthAlgorithm.OPEN)) {
        			wifi.setAuthenticationType("OPEN");
        		}
        		
        	}
        	
//        	Log.i("WifiPreference", "------------------");
// 	        Log.d("WifiPreference", "SSID" + config.SSID);
// 	        Log.d("WifiPreference", "PASSWORD" + config.preSharedKey);
// 	        Log.d("WifiPreference", "ALLOWED ALGORITHMS");
// 	        Log.d("WifiPreference", "LEAP" + config.allowedAuthAlgorithms.get(AuthAlgorithm.LEAP));
// 	        Log.d("WifiPreference", "OPEN" + config.allowedAuthAlgorithms.get(AuthAlgorithm.OPEN));
// 	        Log.d("WifiPreference", "SHARED" + config.allowedAuthAlgorithms.get(AuthAlgorithm.SHARED));
// 	        Log.d("WifiPreference", "GROUP CIPHERS");
// 	        Log.d("WifiPreference", "CCMP" + config.allowedGroupCiphers.get(GroupCipher.CCMP));
// 	        Log.d("WifiPreference", "TKIP" + config.allowedGroupCiphers.get(GroupCipher.TKIP));
// 	        Log.d("WifiPreference", "WEP104" + config.allowedGroupCiphers.get(GroupCipher.WEP104));
// 	        Log.d("WifiPreference", "WEP40" + config.allowedGroupCiphers.get(GroupCipher.WEP40));
// 	        Log.d("WifiPreference", "KEYMGMT");
// 	        Log.d("WifiPreference", "IEEE8021X" + config.allowedKeyManagement.get(KeyMgmt.IEEE8021X));
// 	        Log.d("WifiPreference", "NONE" + config.allowedKeyManagement.get(KeyMgmt.NONE));
// 	        Log.d("WifiPreference", "WPA_EAP" + config.allowedKeyManagement.get(KeyMgmt.WPA_EAP));
// 	        Log.d("WifiPreference", "WPA_PSK" + config.allowedKeyManagement.get(KeyMgmt.WPA_PSK));
// 	        Log.d("WifiPreference", "PairWiseCipher");
// 	        Log.d("WifiPreference", "CCMP" + config.allowedPairwiseCiphers.get(PairwiseCipher.CCMP));
// 	        Log.d("WifiPreference", "NONE" + config.allowedPairwiseCiphers.get(PairwiseCipher.NONE));
// 	        Log.d("WifiPreference", "TKIP" + config.allowedPairwiseCiphers.get(PairwiseCipher.TKIP));
// 	        Log.d("WifiPreference", "Protocols");
// 	        Log.d("WifiPreference", "RSN" + config.allowedProtocols.get(Protocol.RSN));
// 	        Log.d("WifiPreference", "WPA" + config.allowedProtocols.get(Protocol.WPA));
// 	        Log.d("WifiPreference", "WEP Key Strings");
// 	        String[] wepKeys = config.wepKeys;
// 	        Log.d("WifiPreference", "WEP KEY 0" + wepKeys[0]);
// 	        Log.d("WifiPreference", "WEP KEY 1" + wepKeys[1]);
// 	        Log.d("WifiPreference", "WEP KEY 2" + wepKeys[2]);
// 	        Log.d("WifiPreference", "WEP KEY 3" + wepKeys[3]);
        }
        
        return wifi;
	}
	
	/**
	 * update wep open type {@link WifiConfiguration}
	 * @param wc
	 */
	public static void updateWepConfig(WifiConfiguration wc) {
	    wc.hiddenSSID = true;
//	    wc.status = WifiConfiguration.Status.DISABLED;
	    wc.allowedGroupCiphers.clear();
	    wc.allowedPairwiseCiphers.clear();
	    wc.allowedProtocols.clear();
	    wc.allowedKeyManagement.clear();
	    wc.allowedAuthAlgorithms.clear();
	    
	    wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//	    wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN); 
//	    wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//	    wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//	    wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
//	    wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//	    wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//	    wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//	    wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
	    wc.wepKeys[0] = "";
	    wc.wepTxKeyIndex = 0;
	}
	
	/**
	 * get wep open type {@link WifiConfiguration}
	 * @param ssid
	 * @return
	 */
	public static WifiConfiguration getNewWepConfig(String ssid) {
	    WifiConfiguration wc = new WifiConfiguration(); 
	    wc.SSID = "\"" + ssid + "\""; //IMP! This should be in Quotes!!
	    wc.hiddenSSID = true;
//	    wc.status = WifiConfiguration.Status.DISABLED;  
	    wc.allowedGroupCiphers.clear();
	    wc.allowedPairwiseCiphers.clear();
	    wc.allowedProtocols.clear();
	    wc.allowedKeyManagement.clear();
	    wc.allowedAuthAlgorithms.clear();
	    
	    wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//	    wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN); 
//	    wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//	    wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//	    wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
//	    wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//	    wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//	    wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//	    wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

	    wc.wepKeys[0] = ""; //This is the WEP Password
	    wc.wepTxKeyIndex = 0;
	    
	    return wc;
	}
	
	/**
	 * set class field
	 * @param wifiConfig
	 * @param fName
	 * @param fValue
	 */
	private static void setWifiConfigurationField(WifiConfiguration wifiConfig,String fName,String fValue)
    {
   		try {
   	   		Field mField = null;
   			mField = wifiConfig.getClass().getField(fName);
    		if (mField!=null){
    			Object o = mField.get(wifiConfig);
    			if (o!=null){
    		   		Method mMethod = o.getClass().getMethod("setValue", new Class[]{String.class});
        			if (mMethod!=null) {
        				mMethod.invoke(o,fValue);
        			}
    			}
    		}
   		} catch (Exception e) {
   			e.printStackTrace();
   		}    			
    }
	
	public static enum WifiRadioStats {
		WIFI_RADIO_DISABLED, WIFI_RADIO_DISABLING, WIFI_RADIO_ENABLED, WIFI_RADIO_ENABLING, UNKNOWN
	}
	/**
	 * return wifi radio states
	 * @return
	 */
    public static WifiRadioStats checkWifiRadioState(Context context) {
    	WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    	switch (mWifiManager.getWifiState()) {
		case WifiManager.WIFI_STATE_DISABLED:
			return WifiRadioStats.WIFI_RADIO_DISABLED;
		case WifiManager.WIFI_STATE_DISABLING:
			return WifiRadioStats.WIFI_RADIO_DISABLING;
		case WifiManager.WIFI_STATE_ENABLED:
			return WifiRadioStats.WIFI_RADIO_ENABLED;
		case WifiManager.WIFI_STATE_ENABLING:
			return WifiRadioStats.WIFI_RADIO_ENABLING;
		default:
			return WifiRadioStats.UNKNOWN;
		}
    }
    
    /**
	 * return if WiFi radio opened
	 * @return
	 */
	public static boolean isWifiRadioOpen(Context context) {
		WifiRadioStats result = checkWifiRadioState(context);
		return result == WifiRadioStats.WIFI_RADIO_ENABLED;
	}
	
	/**
	 * return if WiFi radio opened or opening
	 * @param context
	 * @return
	 */
	public static boolean isWifiRadioOpenOrOpening(Context context) {
		WifiRadioStats result = checkWifiRadioState(context);
		if(result == WifiRadioStats.WIFI_RADIO_ENABLED || result == WifiRadioStats.WIFI_RADIO_ENABLING) {
			return true;
		}
		return false;
	}
	/**
	 * return the current wifi ssid {@link WifiInfo}
	 * @param context
	 * @return
	 */
	
	public static String getCurrentSSID(Context context) {
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = mWifiManager.getConnectionInfo();
		if(info != null) {
			return info.getSSID();
		}
		return null;
	}
	
	/**
	 * return the current wifi bssid {@link WifiInfo}
	 * @param context
	 * @return
	 */
	public static String getCurrentBSSID(Context context) {
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = mWifiManager.getConnectionInfo();
		if(info != null) {
			return info.getBSSID();
		}
		return null;
	}
	
	/**
	 * disable all wifi in OS wifi config
	 * @param context
	 * @param mWifiManager
	 */
	public static void disableAllWifi(Context context, WifiManager mWifiManager) {
		if (WifiUtil.isWifiRadioOpen(context)) {
			List<WifiConfiguration> configList = mWifiManager.getConfiguredNetworks();
			for (int i = 0; i < configList.size(); i++) {
				mWifiManager.disableNetwork(i);
			}
		}
	}
	
	/**
	 * get scanned result
	 * @param context
	 * @return
	 */
	public static List<ScanResult> getScanResult(Context context) {
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return mWifiManager.getScanResults();
	}
	
	/**
	 * open wifi radio
	 * @param context
	 */
	public static void openWifiRadio(Context context) {
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mWifiManager.setWifiEnabled(true);
	}
	
	/**
	 * close wifi radio
	 * @param context
	 */
	public static void closeWifiRadio(Context context) {
		WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		mWifiManager.setWifiEnabled(false);
	}
	
	/**
	 * get max priority of configured network
	 * @param context
	 * @return
	 */
	public static int getMaxPriority(Context context) {
		WifiManager paramWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> localList = paramWifiManager
				.getConfiguredNetworks();
		int MAX = 0;
		for (int i = 0; i < localList.size(); i++) {
			WifiConfiguration localWifiConfiguration = localList.get(i);
			if (MAX < localWifiConfiguration.priority) {
				MAX = localWifiConfiguration.priority;
			}
		}
		return MAX;
	}
}
