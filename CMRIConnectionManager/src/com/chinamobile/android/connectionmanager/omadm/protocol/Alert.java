package com.chinamobile.android.connectionmanager.omadm.protocol;

import java.util.Vector;

public class Alert extends ItemizedCommand {

    // --------------------------------------------------------------- Constants
    public static String COMMAND_NAME = "Alert";

    // ------------------------------------------------------------ Private data
    private int data;
    private String correlator;
    // ------------------------------------------------------------ Constructors

    public Alert() {}

    /**
     * Creates a new Alert object with the given command identifier,
     * noResponse, authentication credential, alert code, the link correlator
     * and array of item
     *
     * @param cmdID  the command identifier - NOT NULL
     * @param noResp is true if no response is required
     * @param cred   the authentication credential
     * @param data   the code of Alert
     * @param correlator the link between the command and an asynchronous response
     * @param items the array of item - NOT NULL
     *
     */
    public Alert(final String  cmdID     ,
                 final boolean noResp    ,
                 final int     data      ,
                 final String  correlator,
                 final Vector  items     ) {

        super(cmdID, items);

        this.noResp  = (noResp) ? new Boolean(noResp) : null;
        this.data    = data      ;
        this.correlator = correlator;
    }

    /**
     * Creates a new Alert object with the given command identifier,
     * noResponse, authentication credential, alert code and array of item
     *
     * @param cmdID command identifier - NOT NULL
     * @param noResp is true if no response is required
     * @param cred the authentication credential
     * @param data the code of Alert
     * @param items the array of item - NOT NULL
     *
     */
    public Alert(final String  cmdID ,
                 final boolean noResp,
                 final int     data  ,
                 final Vector  items ) {
        this(cmdID, noResp, data, null, items);
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Gets the alert code
     *
     * @return the alert code
     */
    public int getData() {
        return data;
    }

    /**
     * Sets the alert code
     *
     * @param data the alert code
     */
    public void setData(int data) {
        this.data = data;
    }

    /**
     * Gets the correlator property
     *
     * @return the correlator property
     */
    public String getCorrelator() {
        return correlator;
    }

    /**
     * Sets the link between the command and an asynchronous response
     *
     * @param correlator the link
     */
    public void setCorrelator(String correlator) {
        this.correlator = correlator;
    }

    /**
     * Gets the command name property
     *
     * @return the command name property
     */
    public String getName() {
        return Alert.COMMAND_NAME;
    }
}
