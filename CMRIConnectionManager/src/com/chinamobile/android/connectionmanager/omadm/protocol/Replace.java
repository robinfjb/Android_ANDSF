package com.chinamobile.android.connectionmanager.omadm.protocol;

import java.util.Vector;

public class Replace extends ItemizedCommand {
	 // --------------------------------------------------------------- Constants
    public static String COMMAND_NAME = "Replace";

    // ------------------------------------------------------------ Private data
    private String lang;
    private int data;
    // ------------------------------------------------------------ Constructors

    public Replace() {}

    /**
     * Creates a new Replace object with the given command identifier,
     * noResponse, language, credential, meta and an array of item
     *
     * @param cmdID the command identifier - NOT NULL
     * @param noResp true if no response is required
     * @param lang the preferred language for results data
     * @param cred the authentication credential
     * @param meta the meta information
     * @param items the array of item - NOT NULL
     *
     */
    public Replace(final String cmdID,
            final boolean noResp,
            final String lang,
            final int    data,
            final Meta meta,
            final Vector items) {
     super(cmdID, meta, items);

     this.noResp  = (noResp) ? new Boolean(noResp) : null;
     this.lang   = lang;
     this.data = data;
 }
    
    /**
     * Gets the command name property
     *
     * @return the command name property
     */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return Replace.COMMAND_NAME;
	}
	
	/**
     * Returns the preferred language
     *
     * @return the preferred language
     *
     */
    public String getLang() {
        return lang;
    }

    public void setNoResp(Boolean value) {
        noResp = value;
    }

    /**
     * Sets the preferred language
     *
     * @param lang new preferred language
     */
    public void setLang(String lang) {
        this.lang = lang;
    }

    /**
     * Gets the replace code
     *
     * @return the alert code
     */
    public int getData() {
        return data;
    }

    /**
     * Sets the replace code
     *
     * @param data the alert code
     */
    public void setData(int data) {
        this.data = data;
    }
}
