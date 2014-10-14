package com.chinamobile.android.connectionmanager.util;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


/**
 * socket utility class
 *
 */
public class SocketUtil {
	public static String SOCKET_ADDRESS = "chinamobile.socket.address";
	private Context context;

	public SocketUtil(Context context) {
		this.context = context;
	}

	// background threads use this Handler to post messages to
	// the main application thread
	private final Handler handler = new Handler();

	/**
	 * a <code>runnable</code> for show message
	 *
	 */
	public class NotificationRunnable implements Runnable {
		private String message = null;

		public void run() {
			if (message != null && message.length() > 0) {
				showNotification(message);
			}
		}

		/**
		 * @param message
		 *            the message to set
		 */
		public void setMessage(String message) {
			this.message = message;
		}
	}

	// post this to the Handler when the background thread notifies
	private final NotificationRunnable notificationRunnable = new NotificationRunnable();

	public void showNotification(String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * listener for receiving the message
	 *
	 */
	class SocketListener extends Thread {
		private Handler handler = null;
		private NotificationRunnable runnable = null;

		public SocketListener(Handler handler, NotificationRunnable runnable) {
			this.handler = handler;
			this.runnable = runnable;
			this.handler.post(this.runnable);
		}

		/**
		 * Show UI notification.
		 * 
		 * @param message
		 */
		private void showMessage(String message) {
			this.runnable.setMessage(message);
			this.handler.post(this.runnable);
		}

		@Override
		public void run() {
			// showMessage("DEMO: SocketListener started!");
			try {
				LocalServerSocket server = new LocalServerSocket(SOCKET_ADDRESS);
				while (true) {
					LocalSocket receiver = server.accept();
					if (receiver != null) {
						InputStream input = receiver.getInputStream();

						// simply for java.util.ArrayList
						int readed = input.read();
						int size = 0;
						int capacity = 0;
						byte[] bytes = new byte[capacity];

						// reading
						while (readed != -1) {
							// java.util.ArrayList.Add(E e);
							capacity = (capacity * 3) / 2 + 1;
							// bytes = Arrays.copyOf(bytes, capacity);
							byte[] copy = new byte[capacity];
							System.arraycopy(bytes, 0, copy, 0, bytes.length);
							bytes = copy;
							bytes[size++] = (byte) readed;

							// read next byte
							readed = input.read();
						}

						showMessage(new String(bytes, 0, size));
					}
				}
			} catch (IOException e) {
				Log.e(getClass().getName(), e.getMessage());
			}
		}
	}

	/**
	 * wirte <code>String</code> into socket
	 * @param message
	 * @throws IOException
	 */
	public static void writeSocket(String message) throws IOException {
		LocalSocket sender = new LocalSocket();
		sender.connect(new LocalSocketAddress(SOCKET_ADDRESS));
		sender.getOutputStream().write(message.getBytes());
		sender.getOutputStream().close();
	}

//	public void main(String[] argv) {
//		new SocketListener(this.handler, this.notificationRunnable).start();
//
//		try {
//			writeSocket("hello");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
}
