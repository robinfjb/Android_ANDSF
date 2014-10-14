package com.chinamobile.android.connectionmanager.omadm.protocol;

public class VerDTD {
	// ------------------------------------------------------------ Private data

    private String value;

    // ------------------------------------------------------------ Constructors

    /**
     * In order to expose the server configuration like WS this constructor
     * must be public
     */
    public VerDTD() {}

    /**
     * Creates a new VerDTD object with the given value
     *
     * @param value the version - NOT NULL
     *
     */
    public VerDTD(final String value) {
        setValue(value);
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Gets value properties
     *
     * @return value properties
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the version of DTD
     *
     * @param value the version of DTD
     */
    public void setValue(String value) {
        if ((value == null) || (value.length() == 0)) {
            throw new IllegalArgumentException("value cannot be null or empty");
        }
        this.value = value;
    }

    /**
     * Compares the string value to the specified input object.
     *
     * @param obj the object to be compared
     *
     * @return true if the specified input object equals the value of the
     *         VerDTD object
     *
     */
    public boolean equals(Object obj) {
        if ((obj instanceof VerDTD) == false) {
            return false;
        }
        return (((VerDTD) obj).getValue().equals(value));
    }
}
