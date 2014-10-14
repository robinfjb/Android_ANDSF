package com.chinamobile.android.connectionmanager.omadm;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;

import com.chinamobile.android.connectionmanager.omadm.protocol.Alert;
import com.chinamobile.android.connectionmanager.omadm.protocol.Data;
import com.chinamobile.android.connectionmanager.omadm.protocol.Item;
import com.chinamobile.android.connectionmanager.omadm.protocol.ItemizedCommand;
import com.chinamobile.android.connectionmanager.omadm.protocol.Meta;
import com.chinamobile.android.connectionmanager.omadm.protocol.Replace;
import com.chinamobile.android.connectionmanager.omadm.protocol.Source;
import com.chinamobile.android.connectionmanager.omadm.protocol.SourceRef;
import com.chinamobile.android.connectionmanager.omadm.protocol.Status;
import com.chinamobile.android.connectionmanager.omadm.protocol.SyncBody;
import com.chinamobile.android.connectionmanager.omadm.protocol.SyncHdr;
import com.chinamobile.android.connectionmanager.omadm.protocol.SyncML;
import com.chinamobile.android.connectionmanager.omadm.protocol.Target;
import com.chinamobile.android.connectionmanager.omadm.protocol.TargetRef;
import com.chinamobile.android.connectionmanager.omadm.protocol.VerDTD;

/**
 * Protocol Formatter class for bulid OMA-DM xml
 *
 */
public class ProtocolFormatter {
	private static final String TAG_LOG = "ProtocolFormatter";
	private ProtocolManager manager;
	private XmlSerializer serializer;
	private boolean webXml;

	public ProtocolFormatter(ProtocolManager manager)
			throws XmlPullParserException {
		this(manager, false);
	}
	
	public ProtocolFormatter(ProtocolManager manager, boolean webXml)
			throws XmlPullParserException {
		this.manager = manager;
//		if(webXml) {
//			serializer = com.funambol.org.kxml2.wap.syncml.SyncML.createSerializer();
//		} else {
			serializer = XmlPullParserFactory.newInstance().newSerializer();
//		}
	}

	/**
	 * build package1 into xml type
	 * <p> see OMA-DM protocol guide
	 * @return
	 * @throws RuntimeException
	 */
	public String buildPacket1Xml() throws RuntimeException {

		StringWriter writer = new StringWriter();
		SyncML msg = manager.initalPacket1Data();
		try {
			serializer.setOutput(writer);
			serializer.startDocument(null, null);
			startTag(ProtocolParser.TAG_SYNCML);
			serializer.attribute(null, "xmlns", ProtocolManager.XML_TITLE);
			
			SyncHdr hdr = msg.getSyncHdr();
			SyncBody body = msg.getSyncBody();

			if (hdr != null) {
				formatSyncHdr(hdr);
			}

			if (body != null) {
				formatSyncBody(body);
			}
			endTag(ProtocolParser.TAG_SYNCML);

			serializer.endDocument();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return writer.toString();
	}

	/**
	 * build package3 into xml type
	 * <p> see OMA-DM protocol guide
	 * @return
	 * @throws RuntimeException
	 */
	public String buildPacket3Xml(SyncML packet2) throws RuntimeException {

		StringWriter writer = new StringWriter();
		SyncML msg = manager.initalPacket3Data(packet2);
		try {
			serializer.setOutput(writer);
			serializer.startDocument(null, null);
			startTag(ProtocolParser.TAG_SYNCML);
			serializer.attribute(null, "xmlns", ProtocolManager.XML_TITLE);

			SyncHdr hdr = msg.getSyncHdr();
			SyncBody body = msg.getSyncBody();

			if (hdr != null) {
				formatSyncHdr(hdr);
			}

			if (body != null) {
				formatSyncBody(body);
			}
			endTag(ProtocolParser.TAG_SYNCML);

			serializer.endDocument();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return writer.toString();
	}
	
	/**
	 * format SyncML header
	 * @param header
	 * @throws IllegalArgumentException
	 * @throws RuntimeException
	 * @throws IOException
	 */
	private void formatSyncHdr(SyncHdr header) throws IllegalArgumentException,
			RuntimeException, IOException {
		startTag(ProtocolParser.TAG_SYNCHDR);
		VerDTD verDTD = header.getVerDTD();
		if (verDTD != null) {
			formatSimpleTag(ProtocolParser.TAG_VERDTD, verDTD.getValue());
		}

		String verProto = header.getVerProto();
		formatSimpleTag(ProtocolParser.TAG_VERPROTO, verProto);

		String sessionId = header.getSessionID();
		formatSimpleTag(ProtocolParser.TAG_SESSIONID, sessionId);

		String msgId = header.getMsgID();
		formatSimpleTag(ProtocolParser.TAG_MSGID, msgId);

		Target target = header.getTarget();
		formatTarget(target);

		Source source = header.getSource();
		formatSource(source);

		endTag(ProtocolParser.TAG_SYNCHDR);
	}

	/**
	 * format SyncML body
	 * @param body
	 * @throws IOException
	 */
	private void formatSyncBody(SyncBody body) throws IOException {
		startTag(ProtocolParser.TAG_SYNCBODY);

		Vector<ItemizedCommand> commands = body.getCommands();
		Boolean finalMsg = body.getFinalMsg();

		for (int i = 0; i < commands.size(); ++i) {
			ItemizedCommand command = commands.elementAt(i);
			if (command instanceof Alert) {
				Alert alert = (Alert) command;
				formatAlert(alert);
			} else if (command instanceof Replace) {
				Replace replace = (Replace) command;
				formatReplace(replace);
			} else if (command instanceof Status) {
				Status status = (Status) command;
				formatStatus(status);
			} else {
				Log.e(TAG_LOG, "Cannot format sync body command " + command);
			}
		}

		if (finalMsg != null && finalMsg.booleanValue()) {
			formatSimpleTag(ProtocolParser.TAG_FINAL, "");
		}

		endTag(ProtocolParser.TAG_SYNCBODY);
	}

	/**
	 * format simple tag
	 * @param tagName
	 * @param value
	 * @throws IOException
	 */
	private void formatSimpleTag(String tagName, String value)
			throws IOException {
		formatSimpleTagWithNamespace(tagName, value, null);
	}

	/**
	 * format simple tag with namespace
	 * @param tagName
	 * @param value
	 * @param namespace
	 * @throws IOException
	 */
	private void formatSimpleTagWithNamespace(String tagName, String value,
			String namespace) throws IOException {

		if (value != null) {

			startTag(tagName);

			if (namespace != null) {
				serializer.attribute(null, "xmlns", namespace);
			}

			// For performance reason we always use CDATA because it does
			// not require escaping. kxml2 is really ineffecient at escaping
			// text, so we try to avoid it as much as possible.
			if (value.length() == 0) {
				serializer.text("");
			} else {
				serializer.text(value);
				// formatter.cdsect(value);
			}
			endTag(tagName);
		}
	}

	/**
	 * format {@link Target}
	 * @param target
	 * @throws IOException
	 */
	private void formatTarget(Target target) throws IOException {
		if (target != null) {
			String locURI = target.getLocURI();
			startTag(ProtocolParser.TAG_TARGET);
			formatSimpleTag(ProtocolParser.TAG_LOC_URI, locURI);
			endTag(ProtocolParser.TAG_TARGET);
		}
	}

	/**
	 * format {@link Source}
	 * @param source
	 * @throws IOException
	 */
	private void formatSource(Source source) throws IOException {

		if (source != null) {
			String locURI = source.getLocURI();
			startTag(ProtocolParser.TAG_SOURCE);
			formatSimpleTag(ProtocolParser.TAG_LOC_URI, locURI);
			endTag(ProtocolParser.TAG_SOURCE);
		}
	}

	/**
	 * format {@link Status}
	 * @param status
	 * @throws IOException
	 */
	private void formatStatus(Status status) throws IOException {
		if (status != null) {
			startTag(ProtocolParser.TAG_STATUS);

			String cmdId = status.getCmdID();
			formatSimpleTag(ProtocolParser.TAG_CMDID, cmdId);

			String msgRef = status.getMsgRef();
			formatSimpleTag(ProtocolParser.TAG_MSGREF, msgRef);

			String cmdRef = status.getCmdRef();
			formatSimpleTag(ProtocolParser.TAG_CMDREF, cmdRef);

			String cmd = status.getCmd();
			formatSimpleTag(ProtocolParser.TAG_CMD, cmd);

			Data data = status.getData();
			formatData(data);

			Vector<Item> items = status.getItems();
			if (items != null) {
				for (int i = 0; i < items.size(); ++i) {
					Item item = (Item) items.elementAt(i);
					formatItem(item);
				}
			}

			endTag(ProtocolParser.TAG_STATUS);
		}
	}
	
	/**
	 * format {@link Replace}
	 * @param replace
	 * @throws IOException
	 */
	private void formatReplace(Replace replace) throws IOException {
		if (replace != null) {
			startTag(ProtocolParser.TAG_REPLACE);
			formatItemizedCommand(replace);
			endTag(ProtocolParser.TAG_REPLACE);
		}
	}

	/**
	 * format {@link Alert}
	 * @param alert
	 * @throws IOException
	 */
	private void formatAlert(Alert alert) throws IOException {
		if (alert != null) {
			startTag(ProtocolParser.TAG_ALERT);
			formatItemizedCommand(alert);
			formatSimpleTag(ProtocolParser.TAG_DATA, "" + alert.getData());
			endTag(ProtocolParser.TAG_ALERT);
		}
	}

	/**
	 * format {@link ItemizedCommand}
	 * @param icommand
	 * @throws IOException
	 */
	private void formatItemizedCommand(ItemizedCommand icommand)
			throws IOException {

		if (icommand != null) {
			String cmdId = icommand.getCmdID();
			formatSimpleTag(ProtocolParser.TAG_CMDID, cmdId);

			Vector<Item> items = icommand.getItems();
			for (int i = 0; i < items.size(); ++i) {
				Item item = (Item) items.elementAt(i);
				formatItem(item);
			}
		}
	}

	/**
	 * format {@link Item}
	 * @param item
	 * @throws IOException
	 */
	private void formatItem(Item item) throws IOException {

		startTag(ProtocolParser.TAG_ITEM);

		Source source = item.getSource();
		formatSource(source);

		Target target = item.getTarget();
		formatTarget(target);

		Meta meta = item.getMeta();
		formatMeta(meta);

		Data data = item.getData();
		formatData(data);

		endTag(ProtocolParser.TAG_ITEM);
	}

	/**
	 * format {@link Meta}
	 * @param meta
	 * @throws IOException
	 */
	private void formatMeta(Meta meta) throws IOException {

		// We only print the field supported by the parser
		if (meta != null) {
			startTag(ProtocolParser.TAG_META);

			String format = meta.getFormat();
			String type = meta.getType();

			formatSimpleTagWithNamespace(ProtocolParser.TAG_FORMAT, format,
					ProtocolManager.METINF);
			formatSimpleTagWithNamespace(ProtocolParser.TAG_TYPE, type,
					ProtocolManager.METINF);

			endTag(ProtocolParser.TAG_META);
		}
	}
	
	/**
	 * format {@link Data}
	 * @param data
	 * @throws IOException
	 */
	private void formatData(Data data) throws IOException {
		if (data != null) {
			// We expect only of these four possibilities to have a valid value
			String str = data.getData();
			byte binData[] = data.getBinData();
			if (str != null) {
				formatSimpleTag(ProtocolParser.TAG_DATA, str);
			} else if (binData != null) {
				formatBinData(binData);
			}
		}
	}

	/**
	 * format {@link Data} in <code>byte[]</code>
	 * @param binData
	 * @throws IOException
	 */
	private void formatBinData(byte binData[]) throws IOException {
		if (binData != null) {
//			if (serializer instanceof WbxmlSerializer) {
//				WbxmlSerializer wbxmlFormatter = (WbxmlSerializer) serializer;
//				wbxmlFormatter.writeWapExtension(Wbxml.OPAQUE, binData);
//			} else {
				String textData = new String(binData, "UTF-8");
				serializer.text(textData);
//			}
		}
	}
	 
	/**
	 * start tag
	 * @param tagName
	 * @throws IOException
	 */
	private void startTag(String tagName) throws IOException {
		serializer.startTag(null, tagName);
//		serializer.text("\n");
	}

	/**
	 * end tag
	 * @param tagName
	 * @throws IOException
	 */
	private void endTag(String tagName) throws IOException {
		serializer.endTag(null, tagName);
//		serializer.text("\n");
	}
}
