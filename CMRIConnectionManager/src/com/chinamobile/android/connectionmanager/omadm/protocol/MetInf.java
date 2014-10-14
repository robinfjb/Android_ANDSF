package com.chinamobile.android.connectionmanager.omadm.protocol;

public class MetInf implements ReusableObject {

    // ------------------------------------------------------------ Private data
    private String    format    ;
    private String    type      ;
    // ------------------------------------------------------------ Constructors

    /**
     * For serialization purposes
     */
    public MetInf() {
        set(null,
            null
        );
    }

    // ---------------------------------------------------------- Public methods
//    public static MetInf newInstance() {
//        return ObjectsPool.createMetInf();
//    }

    /**
     * Returns format
     *
     * @return format
     */
    public String getFormat() {
        return format;
    }

    /**
     * Sets format
     *
     * @param format the new format value
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * Returns type
     *
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type
     *
     * @param type the new type value
     */
    public void setType(String type) {
        this.type = type;
    }

    // --------------------------------------------------------- Private methods

    /**
     * Sets all properties in once.
     *
     * @param format the encoding format
     * @param type usually a MIME type
     * @param mark the mark element
     * @param sizeInBytes the data size in bytes
     * @param anchor the Anchor
     * @param version the data version
     * @param nonce the next nonce value
     * @param maxMsgSize the maximum message size in bytes
     * @param emi experimental meta info
     * @param memoryInfo memory information
     *
     */
    private void set(
                     final String    format    ,
                     final String    type
                    ) {
        this.format     = format;
        this.type       = type;
    }

    public void init() {
        format     = null;
        type       = null;
    }
}
