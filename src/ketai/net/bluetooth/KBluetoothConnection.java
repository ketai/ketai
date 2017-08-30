/*
 * 
 */
package ketai.net.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import processing.core.PApplet;
import android.bluetooth.BluetoothSocket;

/**
 * The Class KBluetoothConnection.
 */
public class KBluetoothConnection extends Thread {

	/** The mm socket. */
	private BluetoothSocket mmSocket;

	/** The mm in stream. */
	private InputStream mmInStream;

	/** The mm out stream. */
	private OutputStream mmOutStream;

	/** The is connected. */
	private boolean isConnected = false;

	/** The address. */
	private String address = "";

	/** The btm. */
	private KetaiBluetooth btm;

	/**
	 * Instantiates a new bluetooth connection.
	 *
	 * @param _btm
	 *            the Bluetooth managing class
	 * @param socket
	 *            the socket reference for the connection
	 */
	public KBluetoothConnection(KetaiBluetooth _btm, BluetoothSocket socket) {
		PApplet.println("create Connection thread to " + socket.getRemoteDevice().getName());
		btm = _btm;
		mmSocket = socket;
		InputStream tmpIn = null;
		OutputStream tmpOut = null;
		address = socket.getRemoteDevice().getAddress();

		try {
			// socket.connect();
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		} catch (IOException e) {
			PApplet.println("temp sockets not created: " + e.getMessage());
		}

		mmInStream = tmpIn;
		mmOutStream = tmpOut;
		isConnected = true;
	}

	/**
	 * Gets the hardware address.
	 *
	 * @return the address (hardware)
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Gets the device name.
	 *
	 * @return the device name
	 */
	public String getDeviceName() {
		if (mmSocket == null)
			return "";
		return mmSocket.getRemoteDevice().getName();
	}

	public void disconnect() {
		PApplet.println("Disconnecting device : " + address);
		if (mmInStream != null) {
			try {
				PApplet.println("Closing input stream for " + address);
				mmInStream.close();
			} catch (IOException e) {
				PApplet.println("Error closing input stream for " + address + " : " + e.getMessage());
			}
			mmInStream = null;
		}
		if (mmOutStream != null) {
			try {
				PApplet.println("Closing output stream for " + address);
				mmOutStream.close();
			} catch (IOException e) {
				PApplet.println("Error closing output stream for " + address + " : " + e.getMessage());
			}
			mmOutStream = null;
		}
		if (mmSocket != null) {
			try {
				PApplet.println("Closing socket for " + address);

				mmSocket.close();
			} catch (IOException e) {
				PApplet.println("Error closing Socket for " + address + " : " + e.getMessage());
			}
			mmSocket = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		PApplet.println("BEGIN mConnectedThread to " + address);
		byte[] buffer = new byte[1024];
		int bytes;

		// Keep listening to the InputStream while connected
		while (true) {
			try {
				// Read from the InputStream
				bytes = mmInStream.read(buffer);
				byte[] data = Arrays.copyOfRange(buffer, 0, bytes);

				// PApplet.println(bytes + " bytes read from "
				// + mmSocket.getRemoteDevice().getName());

				if (btm.onBluetoothDataEventMethod != null) {
					try {
						btm.onBluetoothDataEventMethod.invoke(btm.parent, new Object[] { this.address, data });
					} catch (IllegalAccessException e) {
						PApplet.println("Error in reading connection data.:" + e.getMessage());
					} catch (InvocationTargetException e) {
						PApplet.println("Error in reading connection data.:" + e.getMessage());
					}
				}
				// // Send the obtained bytes to the UI Activity
				// mHandler.obtainMessage(BluetoothChat.MESSAGE_READ, bytes, -1,
				// buffer).sendToTarget();
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {

				}
			} catch (IOException e) {
				btm.removeConnection(this);
				PApplet.println(getAddress() + " disconnected" + e.getMessage());
				// notify manager that we've gone belly up
				// connectionLost();
				isConnected = false;
				break;
			}
		}
	}

	/**
	 * Checks if we are connected.
	 *
	 * @return true, if is connected
	 */
	public boolean isConnected() {
		return isConnected;
	}

	/**
	 * Write data to the connection
	 *
	 * @param buffer
	 *            the buffer
	 */
	public void write(byte[] buffer) {
		try {
			// PApplet.println("KBTConnection thread writing " + buffer.length
			// + " bytes to " + address);

			mmOutStream.write(buffer);
		} catch (IOException e) {
			PApplet.println(getAddress() + ": Exception during write" + e.getMessage());
			btm.removeConnection(this);
		}
	}

	/**
	 * Cancel, close out the resource
	 */
	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) {
			PApplet.println("close() of connect socket failed" + e.getMessage());
		}
	}

}
