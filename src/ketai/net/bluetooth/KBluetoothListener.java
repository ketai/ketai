/*
 * 
 */
package ketai.net.bluetooth;

import java.io.IOException;

import processing.core.PApplet;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

/**
 * The listener interface for receiving KBluetooth events.
 * The class that is interested in processing a KBluetooth
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addKBluetoothListener<code> method. When
 * the KBluetooth event occurs, that object's appropriate
 * method is invoked.
 *
 * @see KBluetoothEvent
 */
public class KBluetoothListener extends Thread {
	
	/** The mm server socket. */
	private final BluetoothServerSocket mmServerSocket;
	
	/** The m socket type. */
	private String mSocketType;
	
	/** The m adapter. */
	private BluetoothAdapter mAdapter;
	
	/** The bt manager. */
	private KetaiBluetooth btManager;
	
	/** The go. */
	private boolean go = true;

	/**
	 * Instantiates a new k bluetooth listener.
	 *
	 * @param btm the Bluetooth Managing class
	 * @param secure secure setting 
	 */
	public KBluetoothListener(KetaiBluetooth btm, boolean secure) {
		BluetoothServerSocket tmp = null;
		mSocketType = secure ? "Secure" : "Insecure";
		btManager = btm;
		mAdapter = btManager.getBluetoothAdapater();

		// Create a new listening server socket
		try {
			if (secure) {
				tmp = mAdapter.listenUsingRfcommWithServiceRecord(
						btManager.NAME_SECURE, btManager.MY_UUID_SECURE);
			} else {
				tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(
						btManager.NAME_INSECURE, btManager.MY_UUID_INSECURE);
			}
		} catch (IOException e) {
			PApplet.println("Socket Type: " + mSocketType + "listen() failed"
					+ e);
		}
		mmServerSocket = tmp;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		PApplet.println("Socket Type: " + mSocketType + "BEGIN mAcceptThread"
				+ this);
		PApplet.println("AcceptThread" + mSocketType);

		BluetoothSocket socket = null;
		if (mmServerSocket == null) {
			PApplet.println("Failed to get socket for server! bye.");
			return;
		}
		while (go) {
			try {
				socket = mmServerSocket.accept();
				if (socket != null) {
					synchronized (this) {
						PApplet.println("Incoming connection from: "
								+ socket.getRemoteDevice().getName());
						btManager.connectDevice(socket);
						// mmServerSocket.close();
					}
				}
			} catch (IOException e) {
				PApplet.println("Socket Type: " + mSocketType
						+ "accept() failed" + e.getMessage());
			}
		}
		PApplet.println("END mAcceptThread, socket Type: " + mSocketType);

	}

	/**
	 * Cancel-close out resources
	 */
	public void cancel() {
		PApplet.println("Socket Type" + mSocketType + "cancel " + this);
		go = false;
		try {
			if (mmServerSocket != null)
				mmServerSocket.close();
		} catch (IOException e) {
			PApplet.println("Socket Type" + mSocketType
					+ "close() of server failed" + e.getMessage());
		}
	}

}
