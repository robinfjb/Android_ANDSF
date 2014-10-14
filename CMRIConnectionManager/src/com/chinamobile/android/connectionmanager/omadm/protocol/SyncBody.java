package com.chinamobile.android.connectionmanager.omadm.protocol;

import java.util.Vector;

public class SyncBody {
	// ------------------------------------------------------------ Private data
    private Vector<ItemizedCommand> commands = new Vector<ItemizedCommand>();
    private Boolean finalMsg;

    public SyncBody() {
    }

    // ------------------------------------------------------------ Constructors

    /**
     * Create a new SyncBody object. The commands in <i>commands</i>
     * must be of the allowed types.
     *
     * @param commands The array elements must be an instance of one of these
     *                 classes: {@link Alert},  {@link Atomic}, {@link Copy},
     *                 {@link Exec}, {@link Get}, {@link Map}, {@link Put},
     *                 {@link Results}, {@link Search}, {@link Sequence},
     *                 {@link Status}, {@link Sync}, {@link Add}, {@link Move},
     *                 {@link Replace}, {@link Delete}
     * @param finalMsg is true if this is the final message that is being sent
     *
     */
    public SyncBody( final Vector<ItemizedCommand> commands, final boolean finalMsg) {

        setCommands(commands);
        this.finalMsg = (finalMsg) ? new Boolean(finalMsg) : null;
    }

    // ---------------------------------------------------------- Public methods

    /**
     *
     *  @return the return value is guaranteed to be non-null. Also,
     *          the elements of the array are guaranteed to be non-null.
     *
     */
    public Vector<ItemizedCommand> getCommands() {
        return commands;
    }

    /**
     * Sets the sequenced commands. The given commands must be of the allowed
     * types.
     *
     * @param commands the commands - NOT NULL and o the allowed types
     *
     * @throws IllegalArgumentException if the constraints are not met
     */
    public void setCommands(Vector<ItemizedCommand> commands) {
        if (commands == null) {
            throw new IllegalArgumentException("commands cannot be null");
        }
        this.commands = commands;
    }

    /**
     * Sets the message as final
     *
     * @param finalMsg the Boolean value of finalMsg property
     */
    public void setFinalMsg(Boolean finalMsg) {
        this.finalMsg = (finalMsg.booleanValue()) ? finalMsg : null;
    }

    /**
     * Gets the value of finalMsg property
     *
     * @return true if this is the final message being sent, otherwise false
     *
     */
    public boolean isFinalMsg() {
        return (finalMsg != null);
    }

    /**
     * Gets the value of finalMsg property
     *
     * @return true if this is the final message being sent, otherwise null
     *
     */
    public Boolean getFinalMsg() {
        if (finalMsg == null || !finalMsg.booleanValue()) {
            return null;
        }
        return finalMsg;
    }
}
