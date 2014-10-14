package com.chinamobile.android.connectionmanager.omadm.protocol;

public class Status extends ResponseCommand implements ReusableObject {
	private static final String COMMAND_NAME = "Status";

	// ------------------------------------------------------------ Private data

	private Data data;
	private String cmd;

	// ------------------------------------------------------------ Constructors

	public Status() {
	}

	// ---------------------------------------------------------- Public methods

//	public static Status newInstance() {
//		return ObjectsPool.createStatus();
//	}

	public void init() {
		super.init();

		data = null;
		cmd = null;
	}

	/**
	 * Returns the status data
	 * 
	 * @return the status data
	 * 
	 */
	public Data getData() {
		return data;
	}

	/**
	 * Sets the status data
	 * 
	 * @param data
	 *            the new data
	 * 
	 * @throws IllegalArgumentException
	 *             if data is null
	 */
	public void setData(Data data) {
		if (data == null) {
			throw new IllegalArgumentException("data cannot be null");
		}
		this.data = data;
	}

	/**
	 * Returns the cmd element
	 * 
	 * @return the cmd element
	 */
	public String getCmd() {
		return cmd;
	}

	/**
	 * Sets the cmd element
	 * 
	 * @param cmd
	 *            the new cmd element - NOT NULL
	 * 
	 * @throws IllegalArgumentException
	 *             if cmd is null
	 */
	public void setCmd(String cmd) {
		if (cmd == null) {
			throw new IllegalArgumentException("cmd cannot be null");
		}
		this.cmd = cmd;
	}

	/**
	 * Returns the status code as int
	 * 
	 * @return the status code as int
	 */
	public int getStatusCode() {
		return Integer.parseInt(data.getData());
	}

	/**
	 * Returns the command name
	 * 
	 * @return the command name
	 */
	public String getName() {
		return Status.COMMAND_NAME;
	}
}
