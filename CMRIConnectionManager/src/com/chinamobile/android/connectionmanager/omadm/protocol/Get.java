package com.chinamobile.android.connectionmanager.omadm.protocol;

import java.util.Vector;

public class Get extends ItemizedCommand {

    // --------------------------------------------------------------- Constants
    public static String COMMAND_NAME = "Get";

    // ------------------------------------------------------------ Private data
    private String lang;
    // ------------------------------------------------------------ Constructors

    public Get() {}

    /**
     * Creates a new Get object with the given command identifier,
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
    public Get(final String cmdID,
               final boolean noResp,
               final String lang,
               final Meta meta,
               final Vector items) {
        super(cmdID, meta, items);

        this.noResp  = (noResp) ? new Boolean(noResp) : null;
        this.lang   = lang;

    }

   // ----------------------------------------------------------- Public methods

    /**
     * Gets the command name property
     *
     * @return the command name property
     */
    public String getName() {
        return Get.COMMAND_NAME;
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
}
