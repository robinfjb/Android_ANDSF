package com.chinamobile.android.connectionmanager.omadm.protocol;

public class SyncML {
	//------------------------------------------------------------- Private Data
    private SyncHdr  header;
    private SyncBody body;
            
    //--------------------------------------------------------------- Properties
            
    //------------------------------------------------------------- Constructors
    public SyncML() {}
            
    /**
     * Creates a new SyncML object from header and body.
     *
     * @param header the SyncML header - NOT NULL
     * @param body the SyncML body - NOT NULL
     *
     */
    public SyncML(final SyncHdr  header,
                  final SyncBody body) {
        setSyncHdr(header);
        setSyncBody(body);
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Returns the SyncML header
     *
     * @return the SyncML header
     *
     */
    public SyncHdr getSyncHdr() {
        return header;
    }

    /**
     * Sets the SyncML header
     *
     * @param header the SyncML header - NOT NULL
     *
     * @throws IllegalArgumentException if header is null
     */
    public void setSyncHdr(SyncHdr header) {
        if (header == null) {
            throw new IllegalArgumentException("header cannot be null");
        }
        this.header = header;
    }

    /**
     * Returns the SyncML body
     *
     * @return the SyncML body
     *
     */
    public SyncBody getSyncBody() {
        return body;
    }

    /**
     * Sets the SyncML body
     *
     * @param body the SyncML body - NOT NULL
     *
     * @throws IllegalArgumentException if body is null
     */
    public void setSyncBody(SyncBody body) {
        if (body == null) {
            throw new IllegalArgumentException("body cannot be null");
        }
        this.body = body;
    }

    /**
     * Is this message the last one of the package?
     *
     * @return lastMessage
     */
    public boolean isLastMessage() {
        return body.isFinalMsg();
    }

    /**
     * Sets lastMessage
     *
     * @param lastMessage the new lastMessage value
     *
     */
    public void setLastMessage() {
        body.setFinalMsg(new Boolean(true));
    }

    public SyncML clone() {
    	return new SyncML(header, body);
    }
    //-----------------------------------------------------------Private Methods
}
