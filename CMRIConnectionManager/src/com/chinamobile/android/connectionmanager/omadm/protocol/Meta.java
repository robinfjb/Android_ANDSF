package com.chinamobile.android.connectionmanager.omadm.protocol;

public class Meta implements ReusableObject {

    // ------------------------------------------------------------ Private data
    private MetInf metInf;

    // ------------------------------------------------------------ Constructors

    /**
     * For serialization purposes
     */
    public Meta() {
        set(null,
            null
        );
    }

    // ---------------------------------------------------------- Public methods
//    public static Meta newInstance() {
//        return ObjectsPool.createMeta();
//    }

    public void init() {
        metInf = null;
    }

    /**
     * Returns the <i>metInf</i> object. If null, a new instance is created and
     * stored in <i>metInf</i>
     *
     * @return the value of <i>metInf</i> or a new instance if <i>metInf</i> is null
     */
    public MetInf getMetInf() {
        if (metInf == null) {
            return (metInf = new MetInf());
        }

        return metInf;
    }

    /**
     * Sets <i>metInf</i> to the given value.
     *
     * @param metInf the new <i>metInf</i> value
     */
    public void setMetInf(MetInf metInf) {
        this.metInf = metInf;
    }

    /**
     * This get method always returns null. This is a used in the JiBX mapping
     * in order to do not output the MetInf element.
     *
     * @return always null
     */
    public MetInf getNullMetInf() {
        return null;
    }

    /**
     * Returns format
     *
     * @return format
     */
    public String getFormat() {
        return getMetInf().getFormat();
    }

    /**
     * Sets format
     *
     * @param format the new format value
     */
    public void setFormat(String format) {
        getMetInf().setFormat(format);
    }

    /**
     * Returns type
     *
     * @return type
     */
    public String getType() {
        return getMetInf().getType();
    }

    /**
     * Sets type
     *
     * @param type the new type value
     */
    public void setType(String type) {
        getMetInf().setType(type);
    }

    // --------------------------------------------------------- Private methods

    /**
     * Sets all properties in once.
     *
     * @param fieldLevel the field level
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

        getMetInf(); // if still null, a new instance will be created

        metInf.setFormat     (format    );
        metInf.setType       (type      );
    }
}
