package com.chinamobile.android.connectionmanager.omadm.protocol;

public class Item implements ReusableObject {

    // ------------------------------------------------------------ Private data
    private Target       target      ;
    private Source       source      ;
    private Meta         meta        ;
    private Data         data        ;
    private Boolean      moreData    ;

    private boolean      incompleteInfo = false;;

    // ------------------------------------------------------------ Constructors

    public Item() {}

    // ---------------------------------------------------------- Public methods

//    public static Item newInstance() {
//        return ObjectsPool.createItem();
//    }

    public void init() {
        target       = null;
        source       = null;
        meta         = null;
        data         = null;
        moreData     = null;
        incompleteInfo = false;;
    }

    /**
     * Returns the item target
     *
     * @return the item target
     */
    public Target getTarget() {
        return target;
    }

    /**
     * Sets the item target
     *
     * @param target the target
     *
     */
    public void setTarget(Target target) {
        this.target = target;
    }

    /**
     * Returns the item source
     *
     * @return the item source
     */
    public Source getSource() {
        return source;
    }

    /**
     * Sets the item source
     *
     * @param source the source
     *
     */
    public void setSource(Source source) {
        this.source = source;
    }

    /**
     * Returns the item meta element
     *
     * @return the item meta element
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * Sets the meta item
     *
     * @param meta the item meta element
     *
     */
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    /**
     * Returns the item data
     *
     * @return the item data
     *
     */
    public Data getData() {
        return data;
    }
    
    /**
     * Returns the hidden data. This is used in the bindingHiddenData.xml in order
     * to avoid to show sensitive data.
     *
     * @return <i>*****</i>
     */
    public Data getHiddenData() {
        return new Data("*****");
    }

    /**
     * Sets the item data
     *
     * @param data the item data
     *
     */
    public void setData(Data data) {
        this.data = data;
    }

    /**
     * Gets moreData property
     *
     * @return true if the data item is incomplete and has further chunks
     *         to come, false otherwise
     */
    public boolean isMoreData() {
        return (moreData != null);
    }

    /**
     * Gets the Boolean value of moreData
     *
     * @return true if the data item is incomplete and has further chunks
     *         to come, false otherwise
     */
    public Boolean getMoreData() {
        if (moreData == null || !moreData.booleanValue()) {
            return null;
        }
        return moreData;
    }

    /**
     * Sets the moreData property
     *
     * @param moreData the moreData property
     */
    public void setMoreData(Boolean moreData) {
        this.moreData = (moreData.booleanValue()) ? moreData : null;
    }

    /**
     * Returns incompleteInfo property
     * @return the property incompleteInfo
     */
    public boolean isWithIncompleteInfo() {
        return this.incompleteInfo;
    }

    /**
     * Sets the propert incompleteInfo
     * @param incompleteInfo boolean
     */
    public void setIncompleteInfo(boolean incompleteInfo) {
        this.incompleteInfo = incompleteInfo;
    }
}