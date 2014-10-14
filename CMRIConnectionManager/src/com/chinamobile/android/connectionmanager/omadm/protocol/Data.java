package com.chinamobile.android.connectionmanager.omadm.protocol;

public class Data {
	 // ---------------------------------------------------------- Protected data
    protected String data;
    protected byte[] binData;

    // ------------------------------------------------------------ Constructors
    /**
     * Creates a new Data object with the given data value
     *
     * @param data the data value
     *
     */
    public Data(final String data) {
        this.data = data;
    }

    public Data(final byte[] binData) {
        this.binData = binData;
    }

    public void init() {
        data =null;
        binData = null;
    }

    /**
     * Sets the data property
     *
     * @param data the data property
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Gets the data properties
     *
     * @return the data properties
     */
    public String getData() {
        return data;
    }

    public void setBinData(byte[] binData) {
        this.binData = binData;
    }

    public byte[] getBinData() {
        return binData;
    }

    /**
     * Returns the size of the item (once formatted). In case Data contains
     * DevInf or hancor the size is just an approximation and not the real value
     * which is unknown until we format the value.
     */ 
    public int getSize() {
        if (data != null) {
            return data.length();
        } else if (binData != null) {
            return binData.length;
        } else {
            return 0;
        }
    }
}
