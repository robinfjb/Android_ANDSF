package com.chinamobile.android.connectionmanager.omadm.protocol;

import java.util.Vector;


/**
 * This is a base class for "command" classes
 */
public abstract class ItemizedCommand extends AbstractCommand {
	// ---------------------------------------------------------- Protected data

    //
    // subclasses must have access the following properties
    //
    protected Vector<Item> items = new Vector<Item>();
    protected Meta   meta ;

    // ------------------------------------------------------------ Constructors

    /** For serialization purposes */
    protected ItemizedCommand() {}

    /**
     * Create a new ItemizedCommand object with the given commandIdentifier,
     * meta object and an array of item
     *
     * @param cmdID the command identifier - NOT NULL
     * @param meta the meta object
     * @param items an array of item - NOT NULL
     *
     */
    public ItemizedCommand(String cmdID, Meta meta, Vector<Item> items) {
        super(cmdID);

        if (cmdID == null) {
            throw new IllegalArgumentException("cmdID cannot be null or empty");
        }

        if (items == null) {
            items = new Vector<Item>();
        }

        this.meta  = meta;
        setItems(items);
    }

    /**
     * Create a new ItemizedCommand object with the given commandIdentifier
     * and an array of item
     *
     * @param cmdID the command identifier - NOT NULL
     * @param items an array of item - NOT NULL
     *
     */
    public ItemizedCommand(final String  cmdID, Vector<Item> items) {
        this(cmdID, null, items);
    }

    // ---------------------------------------------------------- Public methods
    public void init() {
        super.init();

        items.removeAllElements();
        meta  = null;
    }



    /**
     * Gets the array of items
     *
     * @return the array of items
     */
    public Vector<Item> getItems() {
        return this.items;
    }

    /**
     * Sets a list of Item object
     *
     * @param items a list of Item object
     */
    public void setItems(Vector<Item> items) {
        if (items != null) {
            this.items = items;
        } else {
            this.items = null;
        }
    }

    /**
     * Sets a single item in the list of items (all existing ones are removed)
     *
     * @param item the item to be added
     */
    public void setItem(Item item) {
        Vector<Item> it = new Vector<Item>();
        it.addElement(item);
        setItems(it);
    }

    public Item getItem() {
    	return getItems().get(0);
    }
    /**
     * Gets the Meta object
     *
     * @return the Meta object
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * Sets the Meta object
     *
     * @param meta the Meta object
     *
     */
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    /**
     * Gets the name of the command
     *
     * @return the name of the command
     */
    public abstract String getName();
}
