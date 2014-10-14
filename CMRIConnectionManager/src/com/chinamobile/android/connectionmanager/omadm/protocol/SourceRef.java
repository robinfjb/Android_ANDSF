package com.chinamobile.android.connectionmanager.omadm.protocol;

/**
 * This class represents the &lt;SourceRef&gt; element as defined by the SyncML
 * representation specifications
 */
public class SourceRef implements ReusableObject {

    // ------------------------------------------------------------ Private data
    private String value;
    private Source source;

    // ------------------------------------------------------------ Constructors

    /**
     * In order to expose the server configuration like WS this constructor
     * must be public
     */
    public SourceRef() {}

    /**
     * Creates a new SourceRef object given the referenced value.
     *
     */
    public SourceRef(final String value) {
    }

    // ---------------------------------------------------------- Public methods
//    public static SourceRef newInstance() {
//        return ObjectsPool.createSourceRef();
//    }

    public void init() {
        value = null;
        source = null;
    }


    /**
     * Returns the value
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the reference value. If value is null, the empty string is adopted.
     *
     * @param value the reference value - NULL
     */
    public void setValue(String value) {
        this.value = (value == null) ? "" : value;
    }

    /**
     * Gets the Source property
     *
     * @return source the Source object property
     */
    public Source getSource() {
        return this.source;
    }

    /**
     * Sets the Source property
     *
     * @param source the Source object property - NOT NULL
     */
    public void setSource(Source source) {
        if (source == null) {
            throw new IllegalArgumentException("source cannot be null");
        }
        this.source = source;
    }
}