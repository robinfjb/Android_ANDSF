package com.chinamobile.android.connectionmanager.model;

/**
 * 3G mode
 */
public class _3GModel extends NetworkModel{
	private static final long serialVersionUID = -8053338181280713956L;
	private int plmn = -1;
	private long tac = -1;
	private long lac = -1;
	private long cid = -1;
	private long utran_cid = -1;
	private long eutra_cid = -1;
	private boolean needOpenWifiRadio = false;

	public _3GModel() {
		this.name = NAME_3G;
	}
	
	public _3GModel(String name, Integer priority) {
		this.name = name;
		this.priority = priority;
	}
	public int getPlmn() {
		return plmn;
	}

	public void setPlmn(int plmn) {
		this.plmn = plmn;
	}

	public long getTac() {
		return tac;
	}

	public void setTac(long tac) {
		this.tac = tac;
	}

	public long getLac() {
		return lac;
	}

	public void setLac(long lac) {
		this.lac = lac;
	}

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}
	
	public long getUtran_cid() {
		return utran_cid;
	}

	public void setUtran_cid(long utran_cid) {
		this.utran_cid = utran_cid;
	}

	public long getEutra_cid() {
		return eutra_cid;
	}

	public void setEutra_cid(long eutra_cid) {
		this.eutra_cid = eutra_cid;
	}

	public int getType() {
		return TYPE_3G;
	}

	public boolean isNeedOpenWifiRadio() {
		return needOpenWifiRadio;
	}

	public void setNeedOpenWifiRadio(boolean needOpenWifiRadio) {
		this.needOpenWifiRadio = needOpenWifiRadio;
	}

	@Override
	public NetworkModel onClone(){
		_3GModel netCopy = new _3GModel();
		netCopy.setPlmn(plmn);
		netCopy.setTac(tac);
		netCopy.setPriority(priority);
		netCopy.setAbandoned(abandoned);
		netCopy.setCid(cid);
		netCopy.setEutra_cid(eutra_cid);
		netCopy.setUtran_cid(utran_cid);
		netCopy.setLac(lac);
		netCopy.setName(name);
		netCopy.setNeedOpenWifiRadio(needOpenWifiRadio);
		return netCopy;
	}
}
