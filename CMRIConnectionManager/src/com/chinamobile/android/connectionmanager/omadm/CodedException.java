package com.chinamobile.android.connectionmanager.omadm;

/**
 * exception of code type
 *
 */
public class CodedException extends RuntimeException {

	/** Storage error. (Problem accessing the backend storage, read or write) */
	public static final int STORAGE_ERROR = 10;
	/** Out of memory error. It's not used at the moment */
	public static final int MEMORY_ERROR = 11;
	/** The limit (memory, items) in the client has been reached */
	public static final int LIMIT_ERROR = 12;

	/** Another sync is in progress */
	public static final int CONCURRENCE_ERROR = 100;

	/** These excpetions are generated by the HttpTranportAgent */
	public static final int DATA_NULL = 200;
	public static final int CONN_NOT_FOUND = 201;
	public static final int ILLEGAL_ARGUMENT = 202;
	public static final int WRITE_SERVER_REQUEST_ERROR = 203;
	public static final int ERR_READING_COMPRESSED_DATA = 204;
	public static final int CONNECTION_BLOCKED_BY_USER = 205;
	public static final int READ_SERVER_RESPONSE_ERROR = 206;
	public static final int OPERATION_INTERRUPTED = 207;

	/** The code of the exception */
	private int code;

	/**
	 * Constructs an instance of <code>CodedException</code> with thei code and
	 * specified detail message.
	 * 
	 * @param code
	 *            the error code
	 * @param msg
	 *            the detail message.
	 */
	public CodedException(int code, String msg) {
		super(msg);
		this.code = code;
	}

	/** Returns the code of this exception */
	public int getCode() {
		return code;
	}

}