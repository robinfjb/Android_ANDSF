package com.chinamobile.android.connectionmanager.omadm.protocol;

public class Target implements ReusableObject{
	 // ------------------------------------------------------------ Private data
    private String locURI;
    private String locName;

    // ------------------------------------------------------------ Constructors

    /**
     * For serialization purposes
     */
    public Target() {}

    Target(String locURI) {
        this.locURI = locURI;
    }

    // ---------------------------------------------------------- Public methods

//    public static Target newInstance() {
//        return ObjectsPool.createTarget();
//    }


    public void init() {
        locURI  = null;
        locName = null;
    }

    /** Gets locURI properties
     * @return locURI properties
     */
    public String getLocURI() {
        return locURI;
    }

    /**
     * Sets locURI property
     * @param locURI the locURI
     */
    public void setLocURI(String locURI) {
        if (locURI == null) {
            throw new IllegalArgumentException("locURI cannot be null");
        }
        this.locURI = locURI;
    }

    /**
     * Gets locName properties
     * @return locName properties
     */
    public String getLocName() {
        return locName;
    }

    /**
     * Sets locName property
     * @param locName the locURI
     */
    public void setLocName(String locName) {
        this.locName = locName;
    }
}
