package com.chinamobile.android.connectionmanager.omadm.protocol;

/**
 * This class represents the &lt;TargetRef&gt; element as defined by the SyncML
 * representation specifications
 *
 * @version $Id: TargetRef.java,v 1.2 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public class TargetRef implements ReusableObject {

    // ------------------------------------------------------------ Private data

    private String value;
    private Target target;

    // ------------------------------------------------------------ Constructors

    /**
     * For serialization purposes
     */
    public TargetRef() {}

//    // ---------------------------------------------------------- Public methods
//    public static TargetRef newInstance() {
//        return ObjectsPool.createTargetRef();
//    }

    public void init() {
        value = null;
        target = null;
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
        if (value == null) {
            this.value = "";
        } else {
            this.value = value;
        }
    }

    /**
     * Gets the Target property
     *
     * @return target the Target property
     */
    public Target getTarget() {
        return this.target;
    }

    /**
     * Sets the Target property
     *
     * @param target the Target property
     */
    public void setTarget(Target target) {
        if (target == null) {
            throw new IllegalArgumentException("Target cannot be null");
        }
        this.target = target;
    }
}