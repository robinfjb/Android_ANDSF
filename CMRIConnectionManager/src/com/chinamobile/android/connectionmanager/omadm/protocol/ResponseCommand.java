package com.chinamobile.android.connectionmanager.omadm.protocol;

import java.util.Vector;

/**
 * This is the base (and abstract) class for response commands
 */
public abstract class ResponseCommand extends ItemizedCommand {

    // ---------------------------------------------------------- Protected data

    /**
     * Message reference
     */
    protected String msgRef;

    /**
     * Command reference
     */
    protected String cmdRef;

    /**
     * Target references
     */
    protected Vector targetRef = new Vector();

    /**
     * Source references
     */
    protected Vector sourceRef = new Vector();

    // ------------------------------------------------------------ Constructors

    /**
     * For serialization purposes
     */
    protected ResponseCommand() {}

    /**
     * Creates a new ResponseCommand object.
     *
     * @param cmdID the command idendifier  - NOT NULL
     * @param msgRef message reference
     * @param cmdRef command reference - NOT NULL
     * @param targetRefs target references
     * @param sourceRefs source references
     * @param items command items
     *
     * @throws IllegalArgumentException if any of the NOT NULL parameter is null
     */
    public ResponseCommand(
        final String cmdID     ,
        final String            msgRef    ,
        final String            cmdRef    ,
        final Vector            targetRefs,
        final Vector            sourceRefs,
        final Vector            items     ) {
        super(cmdID, items);

        setCmdRef(cmdRef);

        this.msgRef = msgRef;

        setTargetRef(targetRefs);
        setSourceRef(sourceRefs);
    }

    // ---------------------------------------------------------- Public methods

    public void init() {
        super.init();

        msgRef = null;
        cmdRef = null;
        targetRef.removeAllElements();
        sourceRef.removeAllElements();
    }

    /**
     * Returns the message reference
     *
     * @return the message reference
     *
     */
    public String getMsgRef() {
        return this.msgRef;
    }

    /**
     * Sets the message reference
     *
     * @param msgRef message reference
     */
    public void setMsgRef(String msgRef) {
        this.msgRef = msgRef;
    }

    /**
     * Returns the command reference
     *
     * @return the command reference
     *
     */
    public String getCmdRef() {
        return cmdRef;
    }

    /**
     * Sets the command reference
     *
     * @param cmdRef commandreference - NOT NULL
     *
     * @throws IllegalArgumentException if cmdRef is null
     */
    public void setCmdRef(String cmdRef) {
        if (cmdRef == null) {
            throw new IllegalArgumentException("cmdRef cannot be null");
        }
        this.cmdRef = cmdRef;
    }

    /**
     * Returns the target references
     *
     * @return the target references
     *
     */
    public Vector getTargetRef() {
        return this.targetRef;
    }

    /**
     * Sets the target references
     *
     * @param targetRefs target refrences
     */
    public void setTargetRef(Vector targetRef) {
        this.targetRef = targetRef;
    }

    /**
     * Sets a single target reference (remove all existing ones)
     */
    public void setTargetRef(TargetRef ref) {
        Vector refs = new Vector();
        refs.addElement(ref);
        setTargetRef(refs);
    }

    /**
     * Returns the source references
     *
     * @return the source references
     *
     */
    public Vector getSourceRef() {
        return this.sourceRef;
    }

    /**
     * Sets the source references
     *
     * @param sourceRefs source refrences
     */
    public void setSourceRef(Vector sourceRef) {
        this.sourceRef = sourceRef;
    }

    /**
     * Sets a single target reference (remove all existing ones)
     */
    public void setSourceRef(SourceRef ref) {
        Vector refs = new Vector();
        refs.addElement(ref);
        setSourceRef(refs);
    }


    /**
     * Returns the command name. It must be redefined by subclasses.
     *
     * @return the command name
     */
    abstract public String getName();
}
