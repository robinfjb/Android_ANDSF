package com.chinamobile.android.connectionmanager.omadm;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParserException;

import com.chinamobile.android.connectionmanager.omadm.protocol.Alert;
import com.chinamobile.android.connectionmanager.omadm.protocol.Data;
import com.chinamobile.android.connectionmanager.omadm.protocol.Item;
import com.chinamobile.android.connectionmanager.omadm.protocol.ItemizedCommand;
import com.chinamobile.android.connectionmanager.omadm.protocol.MetInf;
import com.chinamobile.android.connectionmanager.omadm.protocol.Meta;
import com.chinamobile.android.connectionmanager.omadm.protocol.Replace;
import com.chinamobile.android.connectionmanager.omadm.protocol.Put;
import com.chinamobile.android.connectionmanager.omadm.protocol.Get;
import com.chinamobile.android.connectionmanager.omadm.protocol.Source;
import com.chinamobile.android.connectionmanager.omadm.protocol.Status;
import com.chinamobile.android.connectionmanager.omadm.protocol.SyncBody;
import com.chinamobile.android.connectionmanager.omadm.protocol.SyncML;
import com.chinamobile.android.connectionmanager.omadm.protocol.TargetRef;
import com.chinamobile.android.connectionmanager.util.CommonUtil;

import android.content.Context;

/**
 * protocol manager
 *
 */
public class ProtocolManager {
	public static final String FORMAT_XML = "xml";
	public static final String FORMAT_CHR = "chr";
	public static final String TYPE_TEXT = "text/plain";
	public static final String TYPE_DISCOVERY = "urn:oma:at:ext-3gpp-andsf:1.0:provision-disc-info";
	public static final String TYPE_POLICY = "urn:oma:at:ext-3gpp-andsf:1.0:provision-single-if";
	public static final String TYPE_WLAN = "urn:oma:mo:oma-connmo-nap:1.0";
	public static final String URL_DISCOVERY = "./ANDSF/DiscoveryInformation";
	public static final String URL_POLICY = "./ANDSF/Policy";
	public static final String METINF = "syncml:metinf";
	public static final String XML_TITLE = "SYNCML:SYNCML1.2";
	public static String sessionID;

	private Context context;
	private ProtocolUtil util;
	private ProtocolFormatter formatter;

	public ProtocolManager(Context context) {
		this.context = context;
		util = new ProtocolUtil(context);
	}
	
	/**
	 * start and initial
	 */
	public void start() {
		
		if(formatter == null) {
			try {
				formatter = new ProtocolFormatter(this);
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		sessionID = String.valueOf(System.currentTimeMillis());
	}
	
	/**
	 * get {@link ProtocolFormatter} instance
	 * @return
	 */
	public ProtocolFormatter getFormatter() {
		return formatter;
	}
	
	/**
	 * get current location
	 * @return
	 */
	private String initalUELocationData() {

		String ss = "<![CDATA[<_3GPP_Location><C1>" +
				"<PLMN>" + CommonUtil.getPlmn(context) + "</PLMN>" +
				"<LAC>" + CommonUtil.getLacHex(context) + "</LAC>" +
				"<GERAN_CI>" + CommonUtil.getCellIdBinary(context)+ "</GERAN_CI>"
				+ "</C1></_3GPP_Location>]]>";
		return ss;
	}

	/**
	 * prepare packet1 data
	 * @return {@link SyncML}
	 */
	public SyncML initalPacket1Data() {
		ItemizedCommand[] items = new ItemizedCommand[4];
		Alert alert = new Alert();
		alert.setData(1201);
		items[0] = alert;

		Replace replace = new Replace();
		Item item = new Item();
		replace.setItem(item);
		Source source = new Source();
		source.setLocURI("./DevInfo/DevId");
		item.setSource(source);
		Meta meta = new Meta();
		MetInf inf = new MetInf();
		inf.setFormat(FORMAT_CHR);
		inf.setType(TYPE_TEXT);
		meta.setMetInf(inf);
		item.setMeta(meta);
		items[1] = replace;

		Alert alert2 = new Alert();
		Item item2 = new Item();
		alert2.setItem(item2);
		alert2.setData(1226);
		Source source2 = new Source();
		source2.setLocURI("./ANDSF/UE_Location");
		item2.setSource(source2);
		Meta meta2 = new Meta();
		MetInf inf2 = new MetInf();
		inf2.setFormat(FORMAT_XML);
		inf2.setType(TYPE_DISCOVERY);
		meta2.setMetInf(inf2);
		item2.setMeta(meta2);
		item2.setData(new Data(initalUELocationData()));
		items[2] = alert2;

		Alert alert3 = new Alert();
		Item item3 = new Item();
		alert3.setItem(item3);
		alert3.setData(1226);
		Source source3 = new Source();
		source3.setLocURI("./ANDSF/UE_Location");
		item3.setSource(source3);
		Meta meta3 = new Meta();
		MetInf inf3 = new MetInf();
		inf3.setFormat(FORMAT_XML);
		inf3.setType(TYPE_POLICY);
		meta3.setMetInf(inf3);
		item3.setMeta(meta3);
		item3.setData(new Data(initalUELocationData()));
		items[3] = alert3;

		return util.prepareInitMessage(items);
	}
	
	/**
	 * prepare packet3 data
	 * @return {@link SyncML}
	 */
	public SyncML initalPacket3Data(SyncML packet2) {
		return util.preparePacket3Message(packet2, true);
	}
	
	/**
	 * prepare packet3 data
	 * @return {@link SyncML}
	 */
	public SyncML initalPacket3Data(SyncML packet2, boolean increaseMsgId) {
		return util.preparePacket3Message(packet2, increaseMsgId);
	}
}
