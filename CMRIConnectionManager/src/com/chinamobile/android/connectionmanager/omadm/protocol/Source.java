package com.chinamobile.android.connectionmanager.omadm.protocol;

public class Source implements ReusableObject {
    // ------------------------------------------------------------ Private data

    /**
     * Specifies the source specific URI
     */
    private String locURI;

    /**
     * Specifies the display name for the  source address
     */
    private String locName;

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new Source object
     */
    public Source() {
    }

    // ------------------------------------------------------ Public methods
//    public static Source newInstance() {
//        return ObjectsPool.createSource();
//    }

    public void init() {
        locURI = null;
        locName = null;
    }

    /**
     * Returns the source URI value
     *
     * @return the source URI value
     */
    public String getLocURI() {
        return locURI;
    }

    /**
     * Sets the source URI
     *
     * @param locURI the source URI - NOT NULL
     *
     * @throws IllegalArgumentException if locURI is null
     */
    public void setLocURI(final String locURI) {
        if (locURI == null) {
            throw new IllegalArgumentException("locURI cannot be null");
        }
        this.locURI = locURI;
    }

    /**
     * Returns the source display name
     *
     * @return the source display name
     *
     */
    public String getLocName() {
        return locName;
    }

    /**
     * Sets the local name property
     *
     * @param locName the local name property
     *
     */
    public void setLocName(String locName) {
        this.locName = locName;
    }
}
