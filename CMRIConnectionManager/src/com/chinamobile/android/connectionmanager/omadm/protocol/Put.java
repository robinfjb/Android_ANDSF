package com.chinamobile.android.connectionmanager.omadm.protocol;

import java.util.Vector;

public class Put extends ItemizedCommand {

    // --------------------------------------------------------------- Constants
    public static String COMMAND_NAME = "Put";

    // ------------------------------------------------------------ Private data
    private String lang;

    // ------------------------------------------------------------ Constructors

    public Put() {}

    /**
     * Creates a new Put object given its elements.
     *
     * @param cmdID the command identifier - NOT NULL
     * @param noResp is &lt;NoResponse/&gt; required?
     * @param lang Preferred language
     * @param cred authentication credentials
     * @param meta meta information
     * @param items Item elements - NOT NULL
     *
     * @throws IllegalArgumentException if any NOT NULL parameter is null
     */
    public Put(
        final String  cmdID ,
        final boolean noResp,
        final String  lang  ,
        final Meta    meta  ,
        final Vector  items ) {
        super(cmdID, meta, items);

        this.noResp  = (noResp) ? new Boolean(noResp) : null;
        this.lang   = lang;
    }



   // ----------------------------------------------------------- Public methods

    /**
     * Returns the preferred language
     *
     * @return the preferred language
     *
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the preferred language
     *
     * @param lang new preferred language
     */
    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setNoResp(Boolean value) {
        noResp = value;
    }

    /**
     * Returns the command name
     *
     * @return the command name
     */
    public String getName() {
        return Put.COMMAND_NAME;
    }
}
