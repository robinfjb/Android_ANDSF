package com.chinamobile.android.connectionmanager.omadm;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

import com.chinamobile.android.connectionmanager.omadm.protocol.Alert;
import com.chinamobile.android.connectionmanager.omadm.protocol.Data;
import com.chinamobile.android.connectionmanager.omadm.protocol.Get;
import com.chinamobile.android.connectionmanager.omadm.protocol.Item;
import com.chinamobile.android.connectionmanager.omadm.protocol.ItemizedCommand;
import com.chinamobile.android.connectionmanager.omadm.protocol.MetInf;
import com.chinamobile.android.connectionmanager.omadm.protocol.Meta;
import com.chinamobile.android.connectionmanager.omadm.protocol.Put;
import com.chinamobile.android.connectionmanager.omadm.protocol.Replace;
import com.chinamobile.android.connectionmanager.omadm.protocol.Source;
import com.chinamobile.android.connectionmanager.omadm.protocol.Status;
import com.chinamobile.android.connectionmanager.omadm.protocol.SyncBody;
import com.chinamobile.android.connectionmanager.omadm.protocol.SyncHdr;
import com.chinamobile.android.connectionmanager.omadm.protocol.SyncML;
import com.chinamobile.android.connectionmanager.omadm.protocol.Target;
import com.chinamobile.android.connectionmanager.omadm.protocol.VerDTD;
import com.chinamobile.android.connectionmanager.util.StringUtil;
//import com.funambol.org.kxml2.wap.Wbxml;
//import com.funambol.org.kxml2.wap.WbxmlParser;

/**
 * Protocol Parser
 *
 */
public class ProtocolParser {
	private static final String TAG_LOG = "ProtocolParser";

	// SyncML tags
	public static final String TAG_ALERT = "Alert";
	public static final String TAG_ADD = "Add";
	public static final String TAG_CMD = "Cmd";
	public static final String TAG_CMDID = "CmdID";
	public static final String TAG_CMDREF = "CmdRef";
	public static final String TAG_DATA = "Data";
	public static final String TAG_MORE_DATA = "MoreData";
	public static final String TAG_DELETE = "Delete";
	public static final String TAG_FORMAT = "Format";
	public static final String TAG_ITEM = "Item";
	public static final String TAG_LOC_URI = "LocURI";
	public static final String TAG_LOC_NAME = "LocName";
	public static final String TAG_MSGID = "MsgID";
	public static final String TAG_MSGREF = "MsgRef";
	public static final String TAG_REPLACE = "Replace";
	public static final String TAG_MAP = "Map";
	public static final String TAG_PUT = "Put";
	public static final String TAG_SOURCE = "Source";
	public static final String TAG_SOURCE_PARENT = "SourceParent";
	public static final String TAG_SOURCEREF = "SourceRef";
	public static final String TAG_STATUS = "Status";
	public static final String TAG_SYNC = "Sync";
	public static final String TAG_SYNCBODY = "SyncBody";
	public static final String TAG_SYNCHDR = "SyncHdr";
	public static final String TAG_SYNCML = "SyncML";
	public static final String TAG_TARGET = "Target";
	public static final String TAG_TARGETREF = "TargetRef";
	public static final String TAG_TARGET_PARENT = "TargetParent";
	public static final String TAG_TYPE = "Type";
	public static final String TAG_META = "Meta";
	public static final String TAG_METAINF = "MetaInf";
	public static final String TAG_LOCURI = "LocURI";
	public static final String TAG_LOCNAME = "LocName";
	public static final String TAG_DEVINF = "DevInf";
	public static final String TAG_VERDTD = "VerDTD";
	public static final String TAG_DEVINFMAN = "Man";
	public static final String TAG_DEVINFMOD = "Mod";
	public static final String TAG_DEVINFOEM = "OEM";
	public static final String TAG_DEVINFFWV = "FwV";
	public static final String TAG_DEVINFSWV = "SwV";
	public static final String TAG_DEVINFHWV = "HwV";
	public static final String TAG_DEVINFDEVID = "DevID";
	public static final String TAG_DEVINFDEVTYP = "DevTyp";
	public static final String TAG_DEVINFUTC = "UTC";
	public static final String TAG_DEVINFLO = "SupportLargeObjs";
	public static final String TAG_DEVINFNC = "SupportNumberOfChanges";
	public static final String TAG_DEVINFDATASTORE = "DataStore";
	public static final String TAG_DATASTOREHS = "SupportHierarchicalSync";
	public static final String TAG_DISPLAYNAME = "DisplayName";
	public static final String TAG_MAXGUIDSIZE = "MaxGUIDSize";
	public static final String TAG_RX = "Rx";
	public static final String TAG_RXPREF = "Rx-Pref";
	public static final String TAG_TX = "Tx";
	public static final String TAG_TXPREF = "Tx-Pref";
	public static final String TAG_CTTYPE = "CTType";
	public static final String TAG_VERCT = "VerCT";
	public static final String TAG_SYNCCAP = "SyncCap";
	public static final String TAG_SYNCTYPE = "SyncType";
	public static final String TAG_EXT = "Ext";
	public static final String TAG_XNAM = "XNam";
	public static final String TAG_XVAL = "XVal";
	public static final String TAG_RESULTS = "Results";
	public static final String TAG_CTCAP = "CTCap";
	public static final String TAG_PROPERTY = "Property";
	public static final String TAG_PROPNAME = "PropName";
	public static final String TAG_MAXSIZE = "MaxSize";
	public static final String TAG_MAXOCCUR = "MaxOccur";
	public static final String TAG_DATATYPE = "DataType";
	public static final String TAG_VALENUM = "ValEnum";
	public static final String TAG_PROPPARAM = "PropParam";
	public static final String TAG_PARAMNAME = "ParamName";
	public static final String TAG_DSMEM = "DSMem";
	public static final String TAG_SHAREDMEM = "SharedMem";
	public static final String TAG_MAXMEM = "MaxMem";
	public static final String TAG_MAXID = "MaxID";
	public static final String TAG_VERPROTO = "VerProto";
	public static final String TAG_SESSIONID = "SessionID";
	public static final String TAG_LAST = "Last";
	public static final String TAG_NEXT = "Next";
	public static final String TAG_ANCHOR = "Anchor";
	public static final String TAG_NUMBEROFCHANGES = "NumberOfChanges";
	public static final String TAG_FINAL = "Final";
	public static final String TAG_NEXTNONCE = "NextNonce";
	public static final String TAG_CHAL = "Chal";
	public static final String TAG_RESPURI = "RespURI";
	public static final String TAG_SIZE = "Size";
	public static final String TAG_NORESP = "NoResp";
	public static final String TAG_GET = "Get";
	public static final String TAG_CRED = "Cred";
	public static final String TAG_LANG = "Lang";
	public static final String TAG_MAXMSGSIZE = "MaxMsgSize";
	public static final String TAG_MAXOBJSIZE = "MaxObjSize";
	public static final String TAG_MAPITEM = "MapItem";
	public static final String TAG_NEXT_NONCE = "NextNonce";
	public static final String TAG_VERSION = "Version";

	private XmlPullParser pullParser;
	private Context context;

	public ProtocolParser(Context context) {
		this.context = context;
	}

	public ProtocolParser(String namespace, InputStream input) {
		try {
			pullParser = XmlPullParserFactory.newInstance().newPullParser();
			pullParser.setInput(new InputStreamReader(input));
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * parse packet2
	 * <p> see OMA-DM protocol guide
	 * @return {@link SyncML}
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ProtocolParserException
	 */
	public SyncML parsePacket2() throws XmlPullParserException, IOException,
			ProtocolParserException {
		SyncHdr header = null;
		SyncBody body = null;

		// Begin parsingO
		nextSkipSpaces(pullParser);
		// If the first tag is not the SyncML start tag, then this is an
		// invalid message
		require(pullParser, XmlPullParser.START_TAG, null, TAG_SYNCML);
		nextSkipSpaces(pullParser);

		while (pullParser.getEventType() == XmlPullParser.START_TAG) {
			String tagName = pullParser.getName();
			if (TAG_SYNCHDR.equals(tagName)) {
				header = parseHeader(pullParser);
			} else if (TAG_SYNCBODY.equals(tagName)) {
				body = parseSyncBody(pullParser);
			} else {
				String msg = "Error parsing. Skipping unexpected token: "
						+ tagName;
				Log.e(TAG_LOG, msg);
				skipUnknownToken(pullParser, tagName);
			}
			nextSkipSpaces(pullParser);
		}

		SyncML syncML = new SyncML();
		if (header != null) {
			syncML.setSyncHdr(header);
		}
		if (body != null) {
			syncML.setSyncBody(body);
		}
		return syncML;
	}
	
	/**
	 * parse packet4
	 * <p> see OMA-DM protocol guide
	 * @return {@link SyncML}
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ProtocolParserException
	 */
	public SyncML parsePacket4() throws XmlPullParserException, IOException,
			ProtocolParserException {
		SyncHdr header = null;
		SyncBody body = null;

		// Begin parsingO
		nextSkipSpaces(pullParser);
		// If the first tag is not the SyncML start tag, then this is an
		// invalid message
		require(pullParser, XmlPullParser.START_TAG, null, TAG_SYNCML);
		nextSkipSpaces(pullParser);

		while (pullParser.getEventType() == XmlPullParser.START_TAG) {
			String tagName = pullParser.getName();
			if (TAG_SYNCHDR.equals(tagName)) {
				header = parseHeader(pullParser);
			} else if (TAG_SYNCBODY.equals(tagName)) {
				body = parseSyncBody(pullParser);
			} else {
				String msg = "Error parsing. Skipping unexpected token: "
						+ tagName;
				Log.e(TAG_LOG, msg);
				skipUnknownToken(pullParser, tagName);
			}
			nextSkipSpaces(pullParser);
		}

		SyncML syncML = new SyncML();
		if (header != null) {
			syncML.setSyncHdr(header);
		}
		if (body != null) {
			syncML.setSyncBody(body);
		}
		return syncML;
	}

	/**
	 * skip space
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ProtocolParserException
	 */
	private void nextSkipSpaces(XmlPullParser parser)
			throws XmlPullParserException, IOException, ProtocolParserException {
		int eventType = parser.next();

		if (eventType == XmlPullParser.TEXT) {
			if (!parser.isWhitespace()) {
				String t = parser.getText();

				if (t.length() > 0) {
					Log.e(TAG_LOG, "Unexpected text: " + t);
					throw new ProtocolParserException("Unexpected text: " + t);
				}
			}
			parser.next();
		}
	}

	/**
	 * parse {@link SyncML} header
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ProtocolParserException
	 */
	private SyncHdr parseHeader(XmlPullParser parser)
			throws XmlPullParserException, IOException, ProtocolParserException {

		VerDTD verDTD = null;
		String verProto = null;
		String sessionID = null;
		String msgId = null;
		Source source = null;
		Target target = null;
		Meta meta = null;
		String respUri = null;
		boolean noResp = false;

		nextSkipSpaces(parser);
		while (parser.getEventType() == XmlPullParser.START_TAG) {
			String tagName = parser.getName();
			if (TAG_VERDTD.equals(tagName)) {
				String verDTDvalue = parseSimpleStringTag(parser, TAG_VERDTD);
				verDTD = new VerDTD(verDTDvalue);
			} else if (TAG_VERPROTO.equals(tagName)) {
				verProto = parseSimpleStringTag(parser, TAG_VERPROTO);
			} else if (TAG_SESSIONID.equals(tagName)) {
				sessionID = parseSimpleStringTag(parser, TAG_SESSIONID);
			} else if (TAG_MSGID.equals(tagName)) {
				msgId = parseSimpleStringTag(parser, TAG_MSGID);
			} else if (TAG_SOURCE.equals(tagName)) {
				source = parseSource(parser);
			} else if (TAG_TARGET.equals(tagName)) {
				target = parseTarget(parser);
			} else if (TAG_META.equals(tagName)) {
				meta = parseMeta(parser);
			} else if (TAG_RESPURI.equals(tagName)) {
				respUri = parseSimpleStringTag(parser, TAG_RESPURI);
			} else {
				String msg = "Error parsing header tag. Skipping unexpected token: "
						+ tagName;
				Log.e(TAG_LOG, msg);
				skipUnknownToken(parser, tagName);
			}
			nextSkipSpaces(parser);
		}
		require(parser, XmlPullParser.END_TAG, null, TAG_SYNCHDR);

		SyncHdr hdr = new SyncHdr();
		if (verDTD != null) {
			hdr.setVerDTD(verDTD);
		}
		if (verProto != null) {
			hdr.setVerProto(verProto);
		}
		if (sessionID != null) {
			hdr.setSessionID(sessionID);
		}
		if (msgId != null) {
			hdr.setMsgID(msgId);
		}
		if (source != null) {
			hdr.setSource(source);
		}
		if (target != null) {
			hdr.setTarget(target);
		}
		if (respUri != null) {
			hdr.setRespURI(respUri);
		}
		if (noResp) {
			hdr.setNoResp(true);
		}
		if (meta != null) {
			hdr.setMeta(meta);
		}
		return hdr;
	}

	/**
	 * parse {@link SyncML} body
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ProtocolParserException
	 */
	private SyncBody parseSyncBody(XmlPullParser parser)
			throws XmlPullParserException, IOException, ProtocolParserException {

		Vector<ItemizedCommand> commands = new Vector<ItemizedCommand>();
		boolean lastMsg = false;

		nextSkipSpaces(parser);
		while (parser.getEventType() == XmlPullParser.START_TAG) {
			String tagName = parser.getName();
			if (TAG_STATUS.equals(tagName)) {
				Status status = parseStatus(parser);
				commands.addElement(status);
			} else if (TAG_ALERT.equals(tagName)) {
				Alert alert = parseAlert(parser);
				commands.addElement(alert);
			} else if (TAG_GET.equals(tagName)) {
				Get get = parseGet(parser);
				commands.addElement(get);
			} else if (TAG_PUT.equals(tagName)) {
				Put put = parsePut(parser);
				commands.addElement(put);
			} else if (TAG_REPLACE.equals(tagName)) {
				try {
					Replace replace = parseReplace(parser);
					commands.addElement(replace);
				} catch (NumberFormatException e) {
					String msg = "Error format number in Data tag. Skipping unexpected token: "
							+ tagName;
					Log.e(TAG_LOG, msg);
				}
			} else if (TAG_FINAL.equals(tagName)) {
				parseFinal(parser);
				lastMsg = true;
			} else {
				String msg = "Error parsing sync item tag. Skipping unexpected token: "
						+ tagName;
				Log.e(TAG_LOG, msg);
				skipUnknownToken(parser, tagName);
			}
			nextSkipSpaces(parser);
		}
		require(parser, XmlPullParser.END_TAG, null, TAG_SYNCBODY);

		SyncBody syncBody = new SyncBody();
		syncBody.setCommands(commands);
		syncBody.setFinalMsg(lastMsg);
		return syncBody;
	}

	/**
	 * parse simple string tag
	 * @param parser
	 * @param tag
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ProtocolParserException
	 */
	private String parseSimpleStringTag(XmlPullParser parser, String tag)
			throws XmlPullParserException, IOException, ProtocolParserException {
		String value = "";
		parser.next();
		if (parser.getEventType() == XmlPullParser.TEXT) {
			value = parser.getText();
			// We expect text plain data. Since this text is not contained
			// in CDATA section, we must unescape it
			value = StringUtil.unescapeXml(value);
			parser.next();
		} else if (parser.getEventType() == XmlPullParser.CDSECT) {
			value = parser.getText();
			parser.next();
		}

		require(parser, XmlPullParser.END_TAG, null, tag);

		return value;
	}

	/**
	 * parse {@link Source}
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ProtocolParserException
	 */
	private Source parseSource(XmlPullParser parser)
			throws XmlPullParserException, IOException, ProtocolParserException {
		nextSkipSpaces(parser);
		// TODO handle filter
		String locUri = null;
		String locName = null;
		while (parser.getEventType() == XmlPullParser.START_TAG) {
			String tagName = parser.getName();
			if (TAG_LOC_URI.equals(tagName)) {
				locUri = parseSimpleStringTag(parser, TAG_LOC_URI);
			} else if (TAG_LOC_NAME.equals(tagName)) {
				locName = parseSimpleStringTag(parser, TAG_LOC_NAME);
			} else {
				String msg = "Error parsing target item tag. Skipping unexpected token: "
						+ tagName;
				Log.e(TAG_LOG, msg);
				skipUnknownToken(parser, tagName);
			}
			nextSkipSpaces(parser);
		}
		require(parser, XmlPullParser.END_TAG, null, TAG_SOURCE);

		Source source = new Source();
		if (locUri != null) {
			source.setLocURI(locUri);
		}
		if (locName != null) {
			source.setLocName(locName);
		}
		return source;
	}

	/**
	 * parse {@link Target}
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ProtocolParserException
	 */
	private Target parseTarget(XmlPullParser parser)
			throws XmlPullParserException, IOException, ProtocolParserException {
		nextSkipSpaces(parser);
		// TODO handle filter
		String locUri = null;
		String locName = null;
		while (parser.getEventType() == XmlPullParser.START_TAG) {
			String tagName = parser.getName();
			if (TAG_LOC_URI.equals(tagName)) {
				locUri = parseSimpleStringTag(parser, TAG_LOC_URI);
			} else if (TAG_LOC_NAME.equals(tagName)) {
				locName = parseSimpleStringTag(parser, TAG_LOC_NAME);
			} else {
				String msg = "Error parsing target item tag. Skipping unexpected token: "
						+ tagName;
				Log.e(TAG_LOG, msg);
				skipUnknownToken(parser, tagName);
			}
			nextSkipSpaces(parser);
		}
		require(parser, XmlPullParser.END_TAG, null, TAG_TARGET);

		Target target = new Target();
		if (locUri != null) {
			target.setLocURI(locUri);
		}
		if (locName != null) {
			target.setLocName(locName);
		}
		return target;
	}

	/**
	 * parse {@link Meta}
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ProtocolParserException
	 */
	private Meta parseMeta(XmlPullParser parser) throws XmlPullParserException,
			IOException, ProtocolParserException {
		String type = null;
		String format = null;

		Long size = null;
		Long maxMsgSize = null;
		Long maxObjSize = null;
		MetInf metInf = null;
		String version = null;

		nextSkipSpaces(parser);
		while (parser.getEventType() == XmlPullParser.START_TAG) {
			String tagName = parser.getName();
			if (TAG_TYPE.equals(tagName)) {
				type = parseSimpleStringTag(parser, TAG_TYPE);
			} else if (TAG_FORMAT.equals(tagName)) {
				format = parseSimpleStringTag(parser, TAG_FORMAT);
			} else if (TAG_SIZE.equals(tagName)) {
				long sizeVal = parseSimpleLongTag(parser, TAG_SIZE);
				size = sizeVal;
			} else if (TAG_MAXMSGSIZE.equals(tagName)) {
				long sizeVal = parseSimpleLongTag(parser, TAG_MAXMSGSIZE);
				maxMsgSize = sizeVal;
			} else if (TAG_MAXOBJSIZE.equals(tagName)) {
				long sizeVal = parseSimpleLongTag(parser, TAG_MAXOBJSIZE);
				maxObjSize = sizeVal;
			} else if (TAG_METAINF.equals(tagName)) {
				metInf = parseMetaInf(parser);
			} else if (TAG_VERSION.equals(tagName)) {
				version = parseSimpleStringTag(parser, TAG_VERSION);
			} else {
				String msg = "Error parsing META tag. Skipping unexpected token: "
						+ tagName;
				Log.e(TAG_LOG, msg);
				skipUnknownToken(parser, tagName);
			}
			nextSkipSpaces(parser);
		}
		require(parser, XmlPullParser.END_TAG, null, TAG_META);

		Meta meta = new Meta();

		// If the metainf is specified as an attribute, then this object is
		// allowed to be null
		if (metInf == null) {
			metInf = new MetInf();
		}

		if (type != null) {
			metInf.setType(type);
		}
		if (format != null) {
			metInf.setFormat(format);
		}
		meta.setMetInf(metInf);

		return meta;
	}

	/**
	 * parse {@link MetInf}
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ProtocolParserException
	 */
	private MetInf parseMetaInf(XmlPullParser parser)
			throws XmlPullParserException, IOException, ProtocolParserException {
		String type = null;
		String format = null;

		nextSkipSpaces(parser);
		while (parser.getEventType() == XmlPullParser.START_TAG) {
			String tagName = parser.getName();
			if (TAG_TYPE.equals(tagName)) {
				type = parseSimpleStringTag(parser, TAG_TYPE);
			} else if (TAG_FORMAT.equals(tagName)) {
				format = parseSimpleStringTag(parser, TAG_FORMAT);
			} else {
				String msg = "Error parsing META tag. Skipping unexpected token: "
						+ tagName;
				Log.e(TAG_LOG, msg);
				skipUnknownToken(parser, tagName);
			}
			nextSkipSpaces(parser);
		}
		require(parser, XmlPullParser.END_TAG, null, TAG_METAINF);

		MetInf metaInf = new MetInf();
		if (format != null) {
			metaInf.setFormat(format);
		}
		if (type != null) {
			metaInf.setType(type);
		}
		return metaInf;
	}

	/**
	 * parse simple long tag
	 * @param parser
	 * @param tag
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ProtocolParserException
	 */
	private long parseSimpleLongTag(XmlPullParser parser, String tag)
			throws XmlPullParserException, IOException, ProtocolParserException {
		String value = parseSimpleStringTag(parser, tag);
		try {
			long l = Long.parseLong(value);
			return l;
		} catch (Exception e) {
			String msg = "Error while parsing long " + e.toString();
			ProtocolParserException pe = new ProtocolParserException(msg);
			throw pe;
		}
	}

	private void require(XmlPullParser parser, int type, String namespace,
			String name) throws XmlPullParserException {
		if (type != parser.getEventType()
				|| (namespace != null && !namespace.equals(parser
						.getNamespace()))
				|| (name != null && !name.equals(parser.getName()))) {
			StringBuffer desc = new StringBuffer();
			desc.append("Expected ").append(XmlPullParser.TYPES[type])
					.append(parser.getPositionDescription())
					.append(" -- Found ")
					.append(XmlPullParser.TYPES[parser.getEventType()]);
			throw new XmlPullParserException(desc.toString());
		}
	}

	private void skipUnknownToken(XmlPullParser parser, String tagName)
			throws ProtocolParserException, XmlPullParserException, IOException {
		/*
		 * // Skip this subtree parser.skipSubTree(); // Now we are positioned
		 * on the end tag require(parser, parser.END_TAG, null, tagName);
		 * parser.next();
		 */

		do {
			parser.next();
		} while (parser.getEventType() != XmlPullParser.END_TAG
				|| !tagName.equals(parser.getName()));
	}

	private Status parseStatus(XmlPullParser parser)
			throws XmlPullParserException, IOException, ProtocolParserException {
		String cmdId = null;
		String msgRef = null;
		String cmdRef = null;
		String cmd = null;
		Data data = null;
		// Vector targetRefs = new Vector();
		// Vector sourceRefs = new Vector();
		Vector<Item> items = new Vector<Item>();

		nextSkipSpaces(parser);
		while (parser.getEventType() == XmlPullParser.START_TAG) {
			String tagName = parser.getName();
			if (TAG_CMDID.equals(tagName)) {
				cmdId = parseSimpleStringTag(parser, TAG_CMDID);
			} else if (TAG_MSGREF.equals(tagName)) {
				msgRef = parseSimpleStringTag(parser, TAG_MSGREF);
			} else if (TAG_ITEM.equals(tagName)) {
				Item item = parseSyncItem(parser);
				items.addElement(item);
			} else if (TAG_CMDREF.equals(tagName)) {
				cmdRef = parseSimpleStringTag(parser, TAG_CMDREF);
			} else if (TAG_CMD.equals(tagName)) {
				cmd = parseSimpleStringTag(parser, TAG_CMD);
			} else if (TAG_DATA.equals(tagName)) {
				String dataVal = parseSimpleStringTag(parser, TAG_DATA);
				data = new Data(dataVal);
			} else {
				String msg = "Error parsing sync item tag. Skipping unexpected token: "
						+ tagName;
				Log.e(TAG_LOG, msg);
				skipUnknownToken(parser, tagName);
			}
			nextSkipSpaces(parser);
		}
		require(parser, XmlPullParser.END_TAG, null, TAG_STATUS);

		Status status = new Status();
		if (cmdId != null) {
			status.setCmdID(cmdId);
		}
		if (msgRef != null) {
			status.setMsgRef(msgRef);
		}
		// if (sourceRefs.size() > 0) {
		// status.setSourceRef(sourceRefs);
		// }
		// if (targetRefs.size() > 0) {
		// status.setTargetRef(targetRefs);
		// }
		if (items.size() > 0) {
			status.setItems(items);
		}
		if (cmdRef != null) {
			status.setCmdRef(cmdRef);
		}
		if (cmd != null) {
			status.setCmd(cmd);
		}
		if (data != null) {
			status.setData(data);
		}
		return status;
	}

	private Item parseSyncItem(XmlPullParser parser)
			throws XmlPullParserException, IOException, ProtocolParserException {

		Source source = null;
		Target target = null;
		Data data = null;
		boolean hasMoreData = false;
		Meta meta = null;

		nextSkipSpaces(parser);
		while (parser.getEventType() == XmlPullParser.START_TAG) {
			String tagName = parser.getName();
			if (TAG_TARGET.equals(tagName)) {
				target = parseTarget(parser);
			} else if (TAG_SOURCE.equals(tagName)) {
				source = parseSource(parser);
			} else if (TAG_MORE_DATA.equals(tagName)) {
				parseSimpleStringTag(parser, TAG_MORE_DATA);
				hasMoreData = true;
			} else if (TAG_DATA.equals(tagName)) {
				data = parseItemData(parser);
			} else if (TAG_META.equals(tagName)) {
				meta = parseMeta(parser);
			} else {
				String msg = "Error parsing sync item tag. Skipping unexpected token: "
						+ tagName;
				Log.e(TAG_LOG, msg);
				skipUnknownToken(parser, tagName);
			}
			nextSkipSpaces(parser);
		}
		require(parser, XmlPullParser.END_TAG, null, TAG_ITEM);

		Item item = new Item();
		if (source != null) {
			item.setSource(source);
		}
		if (target != null) {
			item.setTarget(target);
		}
		if (data != null) {
			item.setData(data);
		}
		if (hasMoreData) {
			item.setMoreData(hasMoreData);
		}

		if (meta != null) {
			item.setMeta(meta);
		}
		return item;
	}

	private Data parseItemData(XmlPullParser parser)
			throws XmlPullParserException, IOException, ProtocolParserException {

		boolean done = false;
		String textData = null;
		byte binData[] = null;
		StringBuffer preamble = new StringBuffer();

		// Use the nextToken here to catch binary data (Wbxml OPAQUE)
		parser.nextToken();
		if (parser.getEventType() == XmlPullParser.CDSECT) {
			// This can only happen in XML, so we can ignore binary data and
			// grab the content directly (note that this can only be a leaf, so
			// this must be textData)
			textData = parser.getText().trim();
			// Advance to the next token
			parser.nextToken();
			if(parser.getEventType() != XmlPullParser.END_TAG) {
				parser.nextTag();
			}
			
			require(parser, XmlPullParser.END_TAG, null, TAG_DATA);
		} else {
			while (parser.getEventType() == XmlPullParser.IGNORABLE_WHITESPACE
					|| parser.getEventType() == XmlPullParser.TEXT
					|| parser.getEventType() == XmlPullParser.ENTITY_REF) {
				preamble.append(parser.getText());
				parser.nextToken();
			}
			textData = preamble.toString();
//			if (parser.getEventType() == WbxmlParser.WAP_EXTENSION) {
//				// This is binary data
//				binData = parseBinaryData(parser);
			if (parser.getEventType() == XmlPullParser.TEXT) {
				textData = parseTextData(parser, textData);
				// We expect text plain data. Since this text is not contained
				// in CDATA section, we must unescape it
				textData = StringUtil.unescapeXml(textData);
			}
			
			require(parser, XmlPullParser.END_TAG, null, TAG_DATA);
		}

		// We require the END DATA tag here
		

		Data data;
		if (binData != null) {
			data = new Data(binData);
		} else {
			data = new Data(textData);
		}
		return data;
	}

//	private byte[] parseBinaryData(XmlPullParser parser)
//			throws XmlPullParserException, ProtocolParserException, IOException {
//
//		byte binData[] = null;
//		// We support the OPAQUE as binary data
//		if (parser instanceof WbxmlParser) {
//			WbxmlParser wbxmlParser = (WbxmlParser) parser;
//			int wapId = wbxmlParser.getWapCode();
//			if (wapId == Wbxml.OPAQUE) {
//				binData = (byte[]) wbxmlParser.getWapExtensionData();
//			} else {
//				throw new ProtocolParserException("Cannot parse WAP EXTENSION "
//						+ wapId);
//			}
//		} else {
//			throw new ProtocolParserException("Cannot parse binary data in XML");
//		}
//		// Advance to the next token
//		nextSkipSpaces(parser);
//		return binData;
//	}

	private String parseTextData(XmlPullParser parser, String preamble)
			throws XmlPullParserException, ProtocolParserException, IOException {
		StringBuffer value = new StringBuffer(preamble);

		String v = null;

		while (parser.getEventType() != XmlPullParser.END_TAG) {
			// Now fetch the rest of the data tag
			parser.nextToken();
			if (parser.getEventType() == XmlPullParser.TEXT
					|| parser.getEventType() == XmlPullParser.IGNORABLE_WHITESPACE
					|| parser.getEventType() == XmlPullParser.ENTITY_REF) {
				v = parser.getText();
				value.append(v);
			} else if (parser.getEventType() != XmlPullParser.END_TAG) {
				throw new ProtocolParserException("Unexpected event: "
						+ parser.getEventType());
			}
		}
		// Try to avoid redundant memory usage.
		if (v != null && v.length() == value.length()) {
			return v;
		} else if (v == null) {
			return preamble;
		} else {
			return value.toString();
		}
	}

	/**
	 * check the start tag and end tag
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ProtocolParserException
	 */
	private Alert parseAlert(XmlPullParser parser)
			throws XmlPullParserException, IOException, ProtocolParserException {
		String cmdId = null;
		int alertCode = 0;
		Vector<Item> items = new Vector<Item>();

		nextSkipSpaces(parser);
		while (parser.getEventType() == XmlPullParser.START_TAG) {
			String tagName = parser.getName();
			if (TAG_CMDID.equals(tagName)) {
				cmdId = parseSimpleStringTag(parser, TAG_CMDID);
			} else if (TAG_DATA.equals(tagName)) {
				Data data = parseItemData(parser);
				String dataVal = null;

				if (data.getData() != null) {
					dataVal = data.getData();
				} else if (data.getBinData() != null) {
					// This is not a real binary data
					dataVal = new String(data.getBinData());
				}

				try {
					alertCode = Integer.parseInt(dataVal);
				} catch (Exception e) {
					throw new ProtocolParserException("Invalid alert code: "
							+ dataVal);
				}
			} else if (TAG_ITEM.equals(tagName)) {
				Item item = parseAlertItem(parser);
				items.addElement(item);
			} else {
				String msg = "Error parsing sync item tag. Skipping unexpected token: "
						+ tagName;
				Log.e(TAG_LOG, msg);
				skipUnknownToken(parser, tagName);
			}
			nextSkipSpaces(parser);
		}
		require(parser, XmlPullParser.END_TAG, null, TAG_ALERT);

		Alert alert = new Alert();
		if (cmdId != null) {
			alert.setCmdID(cmdId);
		}
		alert.setData(alertCode);
		if (items.size() > 0) {
			alert.setItems(items);
		}
		return alert;
	}

	/**
	 * parse {@link Item}
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ProtocolParserException
	 */
	private Item parseAlertItem(XmlPullParser parser)
			throws XmlPullParserException, IOException, ProtocolParserException {
		Target target = null;
		Source source = null;
		Meta meta = null;

		nextSkipSpaces(parser);
		while (parser.getEventType() == XmlPullParser.START_TAG) {
			String tagName = parser.getName();
			if (TAG_TARGET.equals(tagName)) {
				target = parseTarget(parser);
			} else if (TAG_SOURCE.equals(tagName)) {
				source = parseSource(parser);
			} else if (TAG_META.equals(tagName)) {
				meta = parseMeta(parser);
			} else {
				String msg = "Error parsing sync item tag. Skipping unexpected token: "
						+ tagName;
				Log.e(TAG_LOG, msg);
				skipUnknownToken(parser, tagName);
			}
			nextSkipSpaces(parser);
		}
		require(parser, XmlPullParser.END_TAG, null, TAG_ITEM);

		Item item = new Item();
		if (target != null) {
			item.setTarget(target);
		}
		if (source != null) {
			item.setSource(source);
		}
		if (meta != null) {
			item.setMeta(meta);
		}
		return item;
	}

	/**
	 * parse {@link Get}
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ProtocolParserException
	 */
	private Get parseGet(XmlPullParser parser) throws XmlPullParserException,
			IOException, ProtocolParserException {
		Meta meta = null;
		String lang = null;
		boolean noResp = false;
		String cmdId = null;
		Vector<Item> items = new Vector<Item>();

		nextSkipSpaces(parser);
		while (parser.getEventType() == XmlPullParser.START_TAG) {
			String tagName = parser.getName();
			if (TAG_META.equals(tagName)) {
				meta = parseMeta(parser);
			} else if (TAG_LANG.equals(tagName)) {
				lang = parseSimpleStringTag(parser, TAG_LANG);
			} else if (TAG_CMDID.equals(tagName)) {
				cmdId = parseSimpleStringTag(parser, TAG_CMDID);
			} else if (TAG_ITEM.equals(tagName)) {
				Item item = parseSyncItem(parser);
				items.addElement(item);
			} else {
				String msg = "Error parsing get element. Skipping unexpected token: "
						+ tagName;
				Log.e(TAG_LOG, msg);
				skipUnknownToken(parser, tagName);
			}
			nextSkipSpaces(parser);
		}

		Get get = new Get();
		if (meta != null) {
			get.setMeta(meta);
		}
		if (lang != null) {
			get.setLang(lang);
		}
		if (cmdId != null) {
			get.setCmdID(cmdId);
		}
		get.setNoResp(noResp);
		if (items.size() > 0) {
			get.setItems(items);
		}
		return get;
	}

	/**
	 * parse {@link Put}
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ProtocolParserException
	 */
	private Put parsePut(XmlPullParser parser) throws XmlPullParserException,
			IOException, ProtocolParserException {
		Meta meta = null;
		String lang = null;
		boolean noResp = false;
		String cmdId = null;
		Vector<Item> items = new Vector<Item>();

		nextSkipSpaces(parser);
		while (parser.getEventType() == XmlPullParser.START_TAG) {
			String tagName = parser.getName();
			if (TAG_META.equals(tagName)) {
				meta = parseMeta(parser);
			} else if (TAG_LANG.equals(tagName)) {
				lang = parseSimpleStringTag(parser, TAG_LANG);
			} else if (TAG_CMDID.equals(tagName)) {
				cmdId = parseSimpleStringTag(parser, TAG_CMDID);
			} else if (TAG_ITEM.equals(tagName)) {
				Item item = parseSyncItem(parser);
				items.addElement(item);
			} else {
				String msg = "Error parsing get element. Skipping unexpected token: "
						+ tagName;
				Log.e(TAG_LOG, msg);
				skipUnknownToken(parser, tagName);
			}
			nextSkipSpaces(parser);
		}

		Put put = new Put();
		if (meta != null) {
			put.setMeta(meta);
		}
		if (lang != null) {
			put.setLang(lang);
		}
		if (cmdId != null) {
			put.setCmdID(cmdId);
		}
		if (items.size() > 0) {
			put.setItems(items);
		}
		put.setNoResp(noResp);
		return put;
	}

	/**
	 * parse {@link Replace}
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ProtocolParserException
	 * @throws NumberFormatException
	 */
	private Replace parseReplace(XmlPullParser parser) throws XmlPullParserException,
			IOException, ProtocolParserException, NumberFormatException {
		Meta meta = null;
		String lang = null;
		int data = 0;
		boolean noResp = false;
		String cmdId = null;
		Vector<Item> items = new Vector<Item>();

		nextSkipSpaces(parser);
		while (parser.getEventType() == XmlPullParser.START_TAG) {
			String tagName = parser.getName();
			if (TAG_META.equals(tagName)) {
				meta = parseMeta(parser);
			} else if (TAG_LANG.equals(tagName)) {
				lang = parseSimpleStringTag(parser, TAG_LANG);
			} else if (TAG_CMDID.equals(tagName)) {
				cmdId = parseSimpleStringTag(parser, TAG_CMDID);
			} else if (TAG_DATA.equals(tagName)) {
				data = Integer.parseInt(parseSimpleStringTag(parser, TAG_DATA));
			} else if (TAG_ITEM.equals(tagName)) {
				Item item = parseSyncItem(parser);
				items.addElement(item);
			} else {
				String msg = "Error parsing get element. Skipping unexpected token: "
						+ tagName;
				Log.e(TAG_LOG, msg);
				skipUnknownToken(parser, tagName);
			}
			nextSkipSpaces(parser);
		}

		Replace replace = new Replace();
		if (meta != null) {
			replace.setMeta(meta);
		}
		if (lang != null) {
			replace.setLang(lang);
		}
		if (cmdId != null) {
			replace.setCmdID(cmdId);
		}
		if (data != 0) {
			replace.setData(data);
		}
		if (items.size() > 0) {
			replace.setItems(items);
		}
		replace.setNoResp(noResp);
		return replace;
	}

	/**
	 * parse final tag
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 * @throws ProtocolParserException
	 */
	private void parseFinal(XmlPullParser parser)
			throws XmlPullParserException, IOException, ProtocolParserException {
		nextSkipSpaces(parser);
		require(parser, XmlPullParser.END_TAG, null, TAG_FINAL);
	}
}
