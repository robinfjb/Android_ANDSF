package com.chinamobile.android.connectionmanager.omadm.protocol;

/**
 * This is a base class for "command" classes
 */
public abstract class AbstractCommand {

    // ---------------------------------------------------------- Protected data
    protected String  cmdID     ;
    protected Boolean noResp    ;
    protected Meta    meta      ;

    // ------------------------------------------------------------ Constructors

    /** For serialization purposes */
    protected AbstractCommand() {}

    /**
     * Create a new AbstractCommand object with the given commandIdentifier
     * and noResponse
     *
     * @param cmdID the command identifier - NOT NULL
     * @param noResp true if the command doesn't require a response
     *
     */
    public AbstractCommand(final String cmdID, final boolean noResp) {
        setCmdID(cmdID);
        this.noResp  = (noResp) ? new Boolean(noResp) : null;
    }

    /**
     * Create a new AbstractCommand object with the given commandIdentifier
     *
     * @param cmdID the command identifier - NOT NULL
     *
     */
    public AbstractCommand(final String cmdID) {
        this(cmdID, false);
    }

    /**
     * Create a new AbstractCommand object with the given commandIdentifier
     * and noResponse
     *
     * @param cmdID the command identifier - NOT NULL
     * @param noResponse true if the command doesn't require a response
     * @param meta the Meta object
     */
    public AbstractCommand(final String cmdID,
                           final boolean noResp,
                           final Meta meta) {
        setCmdID(cmdID);
        this.noResp  = (noResp) ? new Boolean(noResp) : null;
        setMeta(meta);
    }

    // ---------------------------------------------------------- Public methods
    public void init() {
        cmdID      = null;
        noResp     = null;
        meta       = null;
    }

    /**
     * Get CommandIdentifier property
     *
     * @return the command identifier - NOT NULL
     */
    public String getCmdID() {
        return this.cmdID;
    }

    /**
     * Sets the CommandIdentifier property
     *
     * @param cmdID the command identifier
     *
     */
    public void setCmdID(String cmdID) {
        if (cmdID == null) {
            throw new IllegalArgumentException("cmdID cannot be null");
        }
        this.cmdID = cmdID;
    }

    /**
     * Gets noResp property
     *
     * @return true if the command doesn't require a response, false otherwise
     */
    public boolean isNoResp() {
        return (noResp != null);
    }

    public Boolean getNoResp() {
        if (noResp == null || !noResp.booleanValue()) {
            return null;
        }
        return noResp;
    }

    /**
     * Sets noResp true if no response is required
     *
     * @param noResp is true if no response is required
     *
     */
    public void setNoResp(Boolean noResp) {
        this.noResp = (noResp.booleanValue()) ? noResp : null;
    }

    /**
     * Gets an Meta object
     *
     * @return an Meta object
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * Sets Meta object
     *
     * @param meta the meta object
     *
     */
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    /**
     * Get name property
     *
     * @return the name of the command
     */
    public abstract String getName();
}