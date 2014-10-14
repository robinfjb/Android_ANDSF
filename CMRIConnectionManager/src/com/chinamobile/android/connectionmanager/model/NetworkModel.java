package com.chinamobile.android.connectionmanager.model;

import java.io.Serializable;

/**
 * The network data model 
 *
 */
public abstract class NetworkModel implements Comparable<NetworkModel>, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2690560251727428549L;
	public static final String NAME_WLAN = "3";
	public static final String NAME_3G = "1";
	public static final int TYPE_WIFI = 1;
	public static final int TYPE_3G = 2;
	protected String name;
	protected Integer priority = -1;
	protected boolean abandoned;// mark the network as abandoned and never try to connect it
								// the default value is false;
	public int retry;

	public abstract int getType();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAbandoned() {
		return abandoned;
	}

	public void setAbandoned(boolean abandoned) {
		this.abandoned = abandoned;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@Override
	public int compareTo(NetworkModel arg0) {
		final Integer p1 = this.getPriority();
		final Integer p2 = arg0.getPriority();
		if(p1 == p2) {
			if(this.getType() < arg0.getType()) {
				return -1;
			} else if(this.getType() > arg0.getType()) {
				return 1;
			}
		}
		return p1.compareTo(p2);
    }

	public abstract NetworkModel onClone();

	@Override
	public String toString() {
		return "NetworkModel [name=" + name + ", priority=" + priority
				+ ", abandoned=" + abandoned + ", retry=" + retry + "]";
	}
	
	
}
