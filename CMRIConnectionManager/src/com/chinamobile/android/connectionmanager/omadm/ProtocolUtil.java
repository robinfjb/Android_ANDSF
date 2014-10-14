package com.chinamobile.android.connectionmanager.omadm;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import com.chinamobile.android.connectionmanager.omadm.protocol.Alert;
import com.chinamobile.android.connectionmanager.omadm.protocol.CmdId;
import com.chinamobile.android.connectionmanager.omadm.protocol.Data;
import com.chinamobile.android.connectionmanager.omadm.protocol.Item;
import com.chinamobile.android.connectionmanager.omadm.protocol.ItemizedCommand;
import com.chinamobile.android.connectionmanager.omadm.protocol.MetInf;
import com.chinamobile.android.connectionmanager.omadm.protocol.Meta;
import com.chinamobile.android.connectionmanager.omadm.protocol.Get;
import com.chinamobile.android.connectionmanager.omadm.protocol.Put;
import com.chinamobile.android.connectionmanager.omadm.protocol.Replace;
import com.chinamobile.android.connectionmanager.omadm.protocol.Source;
import com.chinamobile.android.connectionmanager.omadm.protocol.Status;
import com.chinamobile.android.connectionmanager.omadm.protocol.SyncBody;
import com.chinamobile.android.connectionmanager.omadm.protocol.SyncHdr;
import com.chinamobile.android.connectionmanager.omadm.protocol.SyncML;
import com.chinamobile.android.connectionmanager.omadm.protocol.Target;
import com.chinamobile.android.connectionmanager.omadm.protocol.VerDTD;
import com.chinamobile.android.connectionmanager.util.CommonUtil;
import com.chinamobile.android.connectionmanager.util.Constants;

import android.content.Context;
import android.util.Log;

/**
 * protocol util
 *
 */
public class ProtocolUtil {
	// Alert Codes
	public static final int ALERT_CODE_NONE = 0;
	public static final int ALERT_CODE_FAST = 200;
	public static final int ALERT_CODE_SLOW = 201;
	public static final int ALERT_CODE_ONE_WAY_FROM_CLIENT = 202;
	public static final int ALERT_CODE_REFRESH_FROM_CLIENT = 203;
	public static final int ALERT_CODE_ONE_WAY_FROM_SERVER = 204;
	public static final int ALERT_CODE_REFRESH_FROM_SERVER = 205;
	public static final int ALERT_CODE_TWO_WAY_BY_SERVER = 206;
	public static final int ALERT_CODE_ONE_WAY_FROM_CLIENT_BY_SERVER = 207;
	public static final int ALERT_CODE_REFRESH_FROM_CLIENT_BY_SERVER = 208;
	public static final int ALERT_CODE_ONE_WAY_FROM_SERVER_BY_SERVER = 209;
	public static final int ALERT_CODE_REFRESH_FROM_SERVER_BY_SERVER = 210;
	public static final int ALERT_CODE_NEXT_MESSAGE = 222;
	public static final int ALERT_CODE_RESUME = 225;
	public static final int ALERT_CODE_SUSPEND = 224;

	public static final int ALERT_CODE_ONE_WAY_FROM_CLIENT_NO_SLOW = 250;
	public static final int ALERT_CODE_ONE_WAY_FROM_CLIENT_SLOW = 251;
	public static final int ALERT_CODE_ONE_WAY_FROM_SERVER_SLOW = 252;

	// Authentication types
	public static final String AUTH_TYPE_MD5 = "syncml:auth-md5";
	public static final String AUTH_TYPE_BASIC = "syncml:auth-basic";
	public static final String AUTH_TYPE_HMAC = "syncml:auth-MAC";
	public static final String AUTH_NONE = "none";
	public static final String DEVINF12 = "./devinf12";
	
	//-- Status----------------------------------------------------
	public static final int SUCCESS = 200;
	public static final int AUTHENTICATION_ACCEPTED = 212;
	public static final int CHUNKED_ITEM_ACCEPTED = 213;
	public static final int INVALID_CREDENTIALS = 401;
	public static final int FORBIDDEN = 403;
	public static final int NOT_FOUND = 404;
	public static final int ALREADY_EXISTS = 418;
	public static final int DEVICE_FULL = 420;
	public static final int GENERIC_ERROR = 500;
	public static final int SERVER_BUSY = 503;
	public static final int PROCESSING_ERROR = 506;
	public static final int REFRESH_REQUIRED = 508;
	public static final int BACKEND_AUTH_ERROR = 511;

	public static final String SUCCESS_STR = "200";
	private Context context;
	/**
	 * This member is used to store the current message ID. It is sent to the
	 * server in the MsgID tag.
	 */
	private int msgID = 0;
	/**
	 * This member is used to store the current command ID. It is sent to the
	 * server in the CmdID tag.
	 */
	private CmdId cmdID = new CmdId(0);

	public ProtocolUtil(Context context) {
		this.context = context;
	}
	
	/**
	 * get the request1 Object {@link SyncML}
	 * @param sessionID
	 * @param command
	 * @return {@link SyncML}
	 * @see {@link SyncML}
	 */
	public SyncML prepareInitMessage(ItemizedCommand... command) {
		SyncML msg = new SyncML();
		// Prepare the header
		SyncHdr syncHdr = new SyncHdr();
//		MetInf hdrMetInf = new MetInf();
//		Meta hdrMeta = new Meta();
//		hdrMeta.setMetInf(hdrMetInf);
//		syncHdr.setMeta(hdrMeta);

		VerDTD verDTD = new VerDTD("1.2");
		syncHdr.setVerDTD(verDTD);
		syncHdr.setVerProto("DM/1.2");
		// Set the session ID
		syncHdr.setSessionID(ProtocolManager.sessionID);
		// Set the message ID
		resetMsgID();
		syncHdr.setMsgID(getNextMsgID());

		Target hdrTarget = new Target();
		hdrTarget.setLocURI(Constants.POLICY_SERVER_URL);
		syncHdr.setTarget(hdrTarget);

		Source hdrSource = new Source();
		hdrSource.setLocURI("IMEI:" + CommonUtil.getIMEI(context));
		syncHdr.setSource(hdrSource);

		// Now create the sync header and add it to the msg
		msg.setSyncHdr(syncHdr);

		// Prepare the body
		SyncBody syncBody = new SyncBody();
		// -------------------------------for Huawei Server Start-------------------------
		syncBody.setFinalMsg(true);
		// -------------------------------for Huawei Server End-------------------------
		Vector<ItemizedCommand> commands = new Vector<ItemizedCommand>();
		syncBody.setCommands(commands);
		resetCmdID();

		Alert alert = new Alert();
		alert.setCmdID(getNextCmdID());
		alert.setData(((Alert) command[0]).getData());
		commands.add(alert);

		Replace replace = new Replace();
		replace.setCmdID(getNextCmdID());
		Item alertItem = new Item();
		Item commadItem = ((Replace) command[1]).getItem();
		Source alertSource = new Source();
		alertSource.setLocURI(commadItem.getSource().getLocURI());
		alertItem.setSource(alertSource);
		Meta alertItemMeta = new Meta();
		MetInf alertItemMeInf = new MetInf();
		alertItemMeInf.setType(commadItem.getMeta().getMetInf().getType());
		alertItemMeInf.setFormat(commadItem.getMeta().getMetInf().getFormat());
		alertItemMeta.setMetInf(alertItemMeInf);
		alertItem.setMeta(alertItemMeta);
		Data alertData = new Data("IMEI:" + CommonUtil.getIMEI(context));
		alertItem.setData(alertData);
		replace.setItem(alertItem);
		commands.add(replace);

		Alert alertDisc = new Alert();
		alertDisc.setCmdID(getNextCmdID());
		alertDisc.setData(((Alert) command[2]).getData());
		Item discItem = new Item();
		Item commad2Item = ((Alert) command[2]).getItem();
		Source discSource = new Source();
		discSource.setLocURI(commad2Item.getSource().getLocURI());
		discItem.setSource(discSource);
		Meta discItemMeta = new Meta();
		MetInf discItemMeInf = new MetInf();
		discItemMeInf.setType(commad2Item.getMeta().getMetInf().getType());
		discItemMeInf.setFormat(commad2Item.getMeta().getMetInf().getFormat());
		discItemMeta.setMetInf(discItemMeInf);
		discItem.setMeta(discItemMeta);
		Data disData = new Data(commad2Item.getData().getData());
		discItem.setData(disData);
		alertDisc.setItem(discItem);
		commands.add(alertDisc);

		Alert policyAlert = new Alert();
		policyAlert.setCmdID(getNextCmdID());
		policyAlert.setData(((Alert) command[3]).getData());
		Item policyItem = new Item();
		Item commad3Item = ((Alert) command[3]).getItem();
		Source policySource = new Source();
		policySource.setLocURI(commad3Item.getSource().getLocURI());
		policyItem.setSource(policySource);
		Meta policyItemMeta = new Meta();
		MetInf policyItemMeInf = new MetInf();
		policyItemMeInf.setType(commad3Item.getMeta().getMetInf().getType());
		policyItemMeInf
				.setFormat(commad3Item.getMeta().getMetInf().getFormat());
		policyItemMeta.setMetInf(policyItemMeInf);
		policyItem.setMeta(policyItemMeta);
		Data policyData = new Data(commad3Item.getData().getData());
		policyItem.setData(policyData);
		policyAlert.setItem(policyItem);
		commands.add(policyAlert);

		// syncBody.setFinalMsg(true);
		msg.setSyncBody(syncBody);
		return msg;
	}
	
	/**
	 * get the request3 Object {@link SyncML}
	 * @param command
	 * @return
	 */
	public SyncML preparePacket3Message(SyncML packet2, boolean increaseMsgId) {
		// Prepare the body
		SyncML packet3 = new SyncML();
		Vector<ItemizedCommand> statusList = new Vector<ItemizedCommand>();
		
		SyncBody p2Body = packet2.getSyncBody();
		if(p2Body == null) {
			return null;
		}
		
		// Prepare the header
		SyncHdr syncHdr = new SyncHdr();

		VerDTD verDTD = new VerDTD("1.2");
		syncHdr.setVerDTD(verDTD);
		syncHdr.setVerProto("DM/1.2");
		// Set the session ID
		syncHdr.setSessionID(ProtocolManager.sessionID);
		// Set the message ID
		if(increaseMsgId) {
			syncHdr.setMsgID(getNextMsgID());
		} else {
			syncHdr.setMsgID(getMsgID());
		}
		

		Target hdrTarget = new Target();
		hdrTarget.setLocURI(Constants.POLICY_SERVER_URL);
		syncHdr.setTarget(hdrTarget);

		Source hdrSource = new Source();
		hdrSource.setLocURI("IMEI:" + CommonUtil.getIMEI(context));
		syncHdr.setSource(hdrSource);

		// Now create the sync header and add it to the msg
		packet3.setSyncHdr(syncHdr);

		resetCmdID();
		Status hdrStatus = new Status();
		hdrStatus.setCmdRef("0");
		hdrStatus.setCmd(ProtocolParser.TAG_SYNCHDR);
		hdrStatus.setData(new Data("" + ProtocolUtil.SUCCESS));
		hdrStatus.setMsgRef("" + msgID);
		hdrStatus.setCmdID(getNextCmdID());
		statusList.addElement(hdrStatus);
		
		//
		Vector<ItemizedCommand> commands = p2Body.getCommands();

		boolean hdrStats = false;
		boolean alertStats = true;
		for(int i=0;i<commands.size();++i) {
			ItemizedCommand command = commands.elementAt(i);
			try {
				if (command instanceof Alert) {
					Alert alert = (Alert) command;

					Status aStatus = new Status();
					aStatus.setMsgRef("" + msgID);
					aStatus.setCmdRef(alert.getCmdID());
					aStatus.setCmd(ProtocolParser.TAG_ALERT);
					aStatus.setCmdID(getNextCmdID());
					aStatus.setData(new Data("" + ProtocolUtil.SUCCESS));
					// Add the status to the list
					statusList.addElement(aStatus);
				} else if (command instanceof Replace) {
					Replace replace = (Replace) command;
					
					Status rStatus = new Status();
					rStatus.setMsgRef("" + msgID);
					rStatus.setCmdRef(replace.getCmdID());
					rStatus.setCmdID(getNextCmdID());
					rStatus.setCmd(ProtocolParser.TAG_REPLACE);
					rStatus.setData(new Data("" + ProtocolUtil.SUCCESS));
					statusList.addElement(rStatus);
				} else if (command instanceof Status) {
					Status status = (Status) command;
					String cmd = status.getCmd();

					if (ProtocolParser.TAG_SYNCHDR.equals(cmd)) {
						checkStatusCode(status);
						hdrStats = true;
					} else if (ProtocolParser.TAG_ALERT.equals(cmd)) {
						checkStatusCode(status);
						int statusCode = getStatusCode(status);
						if (statusCode == REFRESH_REQUIRED) {
							// The server refused our resume attempt. At this point
							// we can wipe the SyncStatus info
						} else if(statusCode == SUCCESS) {
							
						}
						alertStats = true;
					} else if (ProtocolParser.TAG_PUT.equals(cmd)) {
					} else if (ProtocolParser.TAG_GET.equals(cmd)) {
					} else if (ProtocolParser.TAG_REPLACE.equals(cmd)) {
					}

				} else if (command instanceof Put) {
				} else if (command instanceof Get) {
				}
			} catch (ProtocolException e) {
				Log.w("ProtocolUtil", "ProtocolException: " + e.getCode() + " / "
						+ e.getMessage());
				continue;
			}
		}
		
		SyncBody body = new SyncBody();
		// -------------------------------for Huawei Server Start-------------------------
		body.setFinalMsg(true);
		// -------------------------------for Huawei Server End-------------------------
		body.setCommands(statusList);
		packet3.setSyncBody(body);
		
		return packet3;
	}
	/**
	 *  Reset the message ID counter.
	 */
	private void resetMsgID() {
		msgID = 0;
	}

	/**
	 *  Return the next message ID to use.
	 * @return
	 */
	private String getNextMsgID() {
		return String.valueOf(++msgID);
	}
	/**
	 *  Return the message ID to use.
	 * @return
	 */
	private String getMsgID() {
		return String.valueOf(msgID);
	}
	/**
	 *  Reset the command ID counter.
	 */
	private void resetCmdID() {
		cmdID.setValue(0);
	}

	/** Return the next message ID to use.
	 * 
	 * @return
	 */
	public String getNextCmdID() {
		return String.valueOf(cmdID.next());
	}
	
	/**
	 * check {@link Status}
	 * @param status
	 * @throws ProtocolException
	 */
	private void checkStatusCode(Status status) throws ProtocolException {
		int code = getStatusCode(status);

		Data data = status.getData();
		String msg = data.getData();

		switch (code) {
		case ProtocolUtil.SUCCESS: // 200
			return;
		case ProtocolUtil.REFRESH_REQUIRED: // 508
			throw new ProtocolException(ProtocolException.REFRESH_REQUIRED,
					"Refresh required by serve");
		case ProtocolUtil.AUTHENTICATION_ACCEPTED: // 212
		{
			// ....

			return;
		}
		case ProtocolUtil.INVALID_CREDENTIALS: // 401
		{
			throw new ProtocolException(
					// SyncException.AUTH_ERROR,
					ProtocolException.AUTH_ERROR,
					"Authentication error from remote server");
		}
		case ProtocolUtil.FORBIDDEN: // 403
			throw new ProtocolException(
			// SyncException.AUTH_ERROR,
					ProtocolException.FORBIDDEN_ERROR, "User not authorized: "
							+ "" + " for source: " + "");
		case ProtocolUtil.NOT_FOUND: // 404
			throw new ProtocolException(
					// SyncException.ACCESS_ERROR,
					ProtocolException.NOT_FOUND_URI_ERROR,
					"Source URI not found on server: " + "");
		case ProtocolUtil.SERVER_BUSY: // 503
			throw new ProtocolException(ProtocolException.SERVER_BUSY,
					"Server busy, another sync in progress for " + "");
		case ProtocolUtil.PROCESSING_ERROR: // 506
			throw new ProtocolException(ProtocolException.BACKEND_ERROR,
					"Error processing source: " + "" + "," + msg);
		case ProtocolUtil.BACKEND_AUTH_ERROR: // 511
			throw new ProtocolException(ProtocolException.BACKEND_AUTH_ERROR,
					"Error processing source: " + "" + "," + msg);
		default:
			// Unhandled status code
			throw new ProtocolException(ProtocolException.SERVER_ERROR,
					"Error from server: " + code);
		}
	}

	/**
	 * get {@link Status} code
	 * @param status
	 * @return
	 * @throws ProtocolException
	 */
	private int getStatusCode(Status status) throws ProtocolException {
		Data data = status.getData();

		if (data == null) {
			String msg = "Status from server has no data";
			throw new ProtocolException(ProtocolException.SERVER_ERROR, msg);
		}

		String codeVal = data.getData();

		try {
			int code = Integer.parseInt(codeVal);
			return code;
		} catch (Exception e) {
			String msg = "Status code from server is not a valid number "
					+ codeVal;
			throw new ProtocolException(ProtocolException.SERVER_ERROR, msg);
		}
	}
	
	/**
	 * check package2 {@link SyncML} is valid
	 * @param context
	 * @param requestSyncML
	 * @param responseSyncML
	 * @return
	 */
	public static boolean checkSyncMLValid(Context context, SyncML requestSyncML, SyncML responseSyncML) {
		try {
			if(!checkSyncHdrValid(context, requestSyncML.getSyncHdr(), responseSyncML.getSyncHdr())) {
				return false;
			}
			SyncBody requestBody = requestSyncML.getSyncBody();
			SyncBody responseBody = responseSyncML.getSyncBody();
			String requestMsgId = requestSyncML.getSyncHdr().getMsgID();
			if(!responseBody.getFinalMsg().booleanValue()) {
				return false;
			}
			
			Vector<Status> statuses = new Vector<Status>();
			Vector<ItemizedCommand> responseCommands = responseBody.getCommands();
			for (ItemizedCommand itemizedCommand : responseCommands) {
				if(itemizedCommand instanceof Status) {
					statuses.add((Status) itemizedCommand);
				}
			}
			
			Status first = statuses.get(0);
			if(first.getMsgRef().compareTo(requestMsgId) != 0
					|| first.getCmdRef().compareTo("0") != 0
					|| !first.getCmd().equalsIgnoreCase("SyncHdr")
					|| first.getData().getData().compareTo(SUCCESS_STR) != 0) {
				return false;
			}
			
			Vector<ItemizedCommand> requestCommands = requestBody.getCommands();
			Map<String, String> commandMap = new HashMap<String, String>();
			for (ItemizedCommand itemizedCommand : requestCommands) {
				commandMap.put(itemizedCommand.getCmdID(), itemizedCommand.getName());
			}
			
			int size = statuses.size();
			for (Entry<String, String> entity : commandMap.entrySet()) {
				String key = entity.getKey();
				String value = entity.getValue();
				for (int i = 0; i < size; i++) {
					Status status = statuses.get(i);
					if(status.getMsgRef().compareTo(requestMsgId) == 0
							&& status.getCmdRef().compareTo(key) == 0
							&& status.getCmd().equalsIgnoreCase(value)
							&& status.getData().getData().compareTo(SUCCESS_STR) == 0) {
						break;
					}
					if(i == size - 1) {
						return false;
					}
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * check package4 {@link SyncML} is valid
	 * @param context
	 * @param requestSyncML
	 * @param responseSyncML
	 * @return
	 */
	public static boolean checkFinalSyncMLValid(Context context, SyncML requestSyncML, SyncML responseSyncML) {
		try {
			if(!checkSyncHdrValid(context, requestSyncML.getSyncHdr(), responseSyncML.getSyncHdr())) {
				return false;
			}
			String requestMsgId = requestSyncML.getSyncHdr().getMsgID();
			SyncBody responseBody = responseSyncML.getSyncBody();
			if(!responseBody.getFinalMsg().booleanValue()) {
				return false;
			}
			
			Vector<ItemizedCommand> responseCommands = responseBody.getCommands();
			Status first = (Status) responseCommands.get(0);
			if(first.getMsgRef().compareTo(requestMsgId) != 0
					|| first.getCmdRef().compareTo("0") != 0
					|| !first.getCmd().equalsIgnoreCase("SyncHdr")
					|| first.getData().getData().compareTo(SUCCESS_STR) != 0) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * check Sync header in {@link SyncML} is valid
	 * @param context
	 * @param requestHeader
	 * @param responseHeader
	 * @return
	 */
	private static boolean checkSyncHdrValid(Context context, SyncHdr requestHeader, SyncHdr responseHeader) {
		try {
			if(!responseHeader.getVerDTD().getValue().equalsIgnoreCase("1.2")) {
				return false;
			}
			
			if(!responseHeader.getVerProto().equalsIgnoreCase("DM/1.2")) {
				return false;
			}
			
			if(!responseHeader.getSessionID().equalsIgnoreCase(requestHeader.getSessionID())) {
				return false;
			}
			
			String imei = "IMEI:" + CommonUtil.getIMEI(context);
			if(!responseHeader.getTarget().getLocURI().equalsIgnoreCase(imei)) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
