/*
 * 
 */
package ketai.net.bluetooth;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import processing.core.PApplet;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * The Class KetaiBluetooth manages the bluetooth connections and service on the android device.
 * This class has been tested and can manage multiple simultaneous bluetooth connections.  The maximum
 *  number of connections varied by device limitations but 3 simultaneous connections were typical.
 * 
 * To receive data from bluetooth connections a sketch should define the following method:<br />
 * 
 * void onBluetoothDataEvent(String who, byte[] data)<br />
 * 
 * who - the name of the device sending the data<br />
 * data - byte array of the data received<br />
 */
public class KetaiBluetooth {
	
	/** The parent. */
	protected PApplet parent;
	
	/** The bluetooth adapter. */
	protected BluetoothAdapter bluetoothAdapter;
	
	/** The paired devices. */
	private HashMap<String, String> pairedDevices;
	
	/** The discovered devices. */
	private HashMap<String, String> discoveredDevices;
	
	/** The current connections. */
	private HashMap<String, KBluetoothConnection> currentConnections;
	
	/** The bt listener. */
	private KBluetoothListener btListener;
	
	/** The m connect thread. */
	private ConnectThread mConnectThread;
	
	/** The is started. */
	private boolean isStarted = false;
	// private boolean SLIPMode = false;
	/** The on bluetooth data event method. */
	protected Method onBluetoothDataEventMethod;

	// user the well-known ssp UUID: 00001101-0000-1000-8000-00805F9B34FB
	/** The my uuid secure. */
	protected UUID MY_UUID_SECURE = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	/** The my uuid insecure. */
	protected UUID MY_UUID_INSECURE = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");

	/** The name secure. */
	protected String NAME_SECURE = "BluetoothSecure";
	
	/** The name insecure. */
	protected String NAME_INSECURE = "BluetoothInsecure";

	/** The Constant BLUETOOTH_ENABLE_REQUEST. */
	final static int BLUETOOTH_ENABLE_REQUEST = 1;

	/**
	 * Instantiates a new ketai bluetooth instance
	 *
	 * @param _parent the calling sketch/activity
	 */
	public KetaiBluetooth(PApplet _parent) {
		parent = _parent;
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			PApplet.println("No Bluetooth Support.");
			return;
		}

		if (!bluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			parent.startActivityForResult(enableBtIntent,
					BLUETOOTH_ENABLE_REQUEST);
		}
		pairedDevices = new HashMap<String, String>();
		discoveredDevices = new HashMap<String, String>();
		currentConnections = new HashMap<String, KBluetoothConnection>();
		findParentIntention();
	}

	/**
	 * Sets the sLIP mode(experimental).
	 *
	 * @param _flag the new sLIP mode
	 */
	public void setSLIPMode(boolean _flag) {
		// SLIPMode = _flag;
	}

	/**
	 * Checks if we've started.
	 *
	 * @return true, if is started
	 */
	public boolean isStarted() {
		return isStarted;
	}

	/**
	 * Gets the bluetooth adapater.
	 *
	 * @return the bluetooth adapater
	 */
	public BluetoothAdapter getBluetoothAdapater() {
		return bluetoothAdapter;
	}

	/**
	 * Checks if we are discovering devices.
	 *
	 * @return true, if we're discovering
	 */
	public boolean isDiscovering() {
		return bluetoothAdapter.isDiscovering();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String info = "KBluetoothManager dump:\n--------------------\nPairedDevices:\n";
		for (String key : pairedDevices.keySet()) {
			info += key + "->" + pairedDevices.get(key) + "\n";
		}

		info += "\n\nDiscovered Devices\n";
		for (String key : discoveredDevices.keySet()) {
			info += key + "->" + discoveredDevices.get(key) + "\n";
		}

		info += "\n\nCurrent Connections\n";
		for (String key : currentConnections.keySet()) {
			info += key + "->" + currentConnections.get(key) + "\n";
		}
		info += "\n-------------------------------\n";

		return info;
	}

	/**
	 * On activity result.
	 *
	 * @param requestCode the request code
	 * @param resultCode the result code
	 * @param data the data from the activty result
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case BLUETOOTH_ENABLE_REQUEST:
			if (resultCode == Activity.RESULT_OK) {
				PApplet.println("BT made available.");
			} else {
				// User did not enable Bluetooth or an error occurred
				PApplet.println("BT was not made available.");
			}
		}
	}

	/**
	 * Checks if we're discoverable.
	 *
	 * @return true, if we're discoverable
	 */
	public boolean isDiscoverable() {
		return (bluetoothAdapter.getScanMode() == BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
	}

	/**
	 * Start the service
	 *
	 * @return true, if successful
	 */
	public boolean start() {
		// start or re-start
		if (btListener != null) {
			stop();
			isStarted = false;
		}

		btListener = new KBluetoothListener(this, true);
		btListener.start();
		isStarted = true;
		findParentIntention();
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		parent.registerReceiver(mReceiver, filter);
		parent.registerReceiver(mReceiver, new IntentFilter(
				BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
		return isStarted;

	}

	/**
	 * Gets our hardware address.
	 *
	 * @return the address
	 */
	public String getAddress() {
		if (bluetoothAdapter != null)
			return bluetoothAdapter.getAddress();
		else
			return "";
	}

	/**
	 * Gets the discovered device names.
	 *
	 * @return  discovered device names
	 */
	public ArrayList<String> getDiscoveredDeviceNames() {
		ArrayList<String> devices = new ArrayList<String>();

		for (String key : discoveredDevices.keySet()) {
			if (key != null)
				devices.add(key);// key + "->" + discoveredDevices.get(key) +
									// "\n";
		}
		return devices;
	}

	/**
	 * Gets the paired device names.
	 *
	 * @return  paired device names
	 */
	public ArrayList<String> getPairedDeviceNames() {
		ArrayList<String> devices = new ArrayList<String>();

		pairedDevices.clear();
		Set<BluetoothDevice> bondedDevices = bluetoothAdapter
				.getBondedDevices();
		if (bondedDevices.size() > 0) {
			for (BluetoothDevice device : bondedDevices) {
				pairedDevices.put(device.getName(), device.getAddress());
				devices.add(device.getName());
			}
		}
		return devices;
	}

	/**
	 * Gets the connected device names.
	 *
	 * @return the connected device names
	 */
	public ArrayList<String> getConnectedDeviceNames() {
		ArrayList<String> devices = new ArrayList<String>();
		Set<String> connectedDevices = currentConnections.keySet();

		if (connectedDevices.size() > 0) {
			for (String device : connectedDevices) {
				KBluetoothConnection c = currentConnections.get(device);
				devices.add(c.getDeviceName() + "(" + device + ")");
			}
		}
		return devices;
	}

	/**
	 * Connect to device by name.
	 *
	 * @param _name the _name
	 * @return true, if successful
	 */
	public boolean connectToDeviceByName(String _name) {
		String address = "";
		if (pairedDevices.containsKey(_name)) {
			address = pairedDevices.get(_name);
		} else if (discoveredDevices.containsKey(_name)) {
			address = discoveredDevices.get(_name);
		}
		if (address.length() > 0 && currentConnections.containsKey(address)) {
			return true;
		}

		return connectDevice(address);
	}

	/**
	 * Connect device by hardware address (more reliable since HW addresses
	 * 	are supposed to be unique.
	 *
	 * @param _hwAddress the _hw address
	 * @return true, if successful
	 */
	public boolean connectDevice(String _hwAddress) {
		BluetoothDevice device;

		if (!BluetoothAdapter.checkBluetoothAddress(_hwAddress)) {
			PApplet.println("Bad bluetooth hardware address! : " + _hwAddress);
			return false;
		}
		device = bluetoothAdapter.getRemoteDevice(_hwAddress);

		if (mConnectThread == null) {
			mConnectThread = new ConnectThread(device, true);
			mConnectThread.start();
		} else if (mConnectThread.mmDevice.getAddress() != _hwAddress) {
			mConnectThread.cancel();
			mConnectThread = new ConnectThread(device, true);
			mConnectThread.start();
		}
		return false;
	}

	/**
	 * Connect device using slip.
	 *
	 * @param _hwAddress the _hw address
	 * @return true, if successful
	 */
	public boolean connectDeviceUsingSLIP(String _hwAddress) {
		return false;
	}

	/**
	 * Connect device.
	 *
	 * @param _socket the _socket
	 * @return true, if successful
	 */
	public boolean connectDevice(BluetoothSocket _socket) {

		KBluetoothConnection tmp = new KBluetoothConnection(this, _socket);

		if (tmp.isConnected())
			tmp.start();
		else {
			PApplet.println("Error trying to connect to "
					+ _socket.getRemoteDevice().getName() + " ("
					+ _socket.getRemoteDevice().getAddress() + ")");
			mConnectThread = null;
			return false;
		}
		if (tmp != null)
			if (!currentConnections.containsKey(_socket.getRemoteDevice()
					.getAddress()))
				currentConnections.put(_socket.getRemoteDevice().getAddress(),
						tmp);
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		return true;
	}

	/**
	 * Discover devices.
	 */
	public void discoverDevices() {
		discoveredDevices.clear();
		bluetoothAdapter.cancelDiscovery();
		if (bluetoothAdapter.startDiscovery())
			PApplet.println("Starting bt discovery.");
		else
			PApplet.println("BT discovery failed to start.");
	}

	/**
	 * Lookup address by name.
	 *
	 * @param _name the _name
	 * @return the string
	 */
	public String lookupAddressByName(String _name) {
		if (pairedDevices.containsKey(_name)) {
			return pairedDevices.get(_name);
		} else if (discoveredDevices.containsKey(_name)) {
			return discoveredDevices.get(_name);
		}
		return "";
	}

	/**
	 * Write to device name.
	 *
	 * @param _name the _name of the device/connection
	 * @param data the data
	 */
	public void writeToDeviceName(String _name, byte[] data) {
		String address = lookupAddressByName(_name);
		if (address.length() > 0)
			write(address, data);
		else
			PApplet.println("Error writing to " + _name
					+ ".  HW Address was not found.");
	}

	/**
	 * Write data to a device through their hardware address
	 *
	 * @param _deviceAddress the _device hardware address
	 * @param data the data
	 */
	public void write(String _deviceAddress, byte[] data) {
		bluetoothAdapter.cancelDiscovery();
		if (!currentConnections.containsKey(_deviceAddress)) {
			if (!connectDevice(_deviceAddress))
				return;
		}

		if (currentConnections.containsKey(_deviceAddress))
			currentConnections.get(_deviceAddress).write(data);

	}

	/**
	 * Send data to all conencted devices.
	 *
	 * @param data the data
	 */
	public void broadcast(byte[] data) {
		for (Map.Entry<String, KBluetoothConnection> device : currentConnections
				.entrySet()) {
			device.getValue().write(data);
		}
	}

	/**
	 * Removes the connection.
	 *
	 * @param c the connection reference
	 */
	protected void removeConnection(KBluetoothConnection c) {
		PApplet.println("KBTM removing connection for " + c.getAddress());
		if (currentConnections.containsKey(c.getAddress())) {
			c.cancel();
			currentConnections.remove(c.getAddress());
		}
	}

	/**
	 * Make discoverable.
	 */
	public void makeDiscoverable() {
		if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			parent.startActivity(discoverableIntent);
		}
	}

	/** The m receiver. */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device != null) {
					discoveredDevices
							.put(device.getName(), device.getAddress());
					PApplet.println("New Device Discovered: "
							+ device.getName());
				}
			}
		}
	};

	/**
	 * Find parent callback methods.
	 */
	private void findParentIntention() {
		try {
			onBluetoothDataEventMethod = parent.getClass().getMethod(
					"onBluetoothDataEvent",
					new Class[] { String.class, byte[].class });
			PApplet.println("Found onBluetoothDataEvent method.");
		} catch (NoSuchMethodException e) {
			PApplet.println("Did not find onBluetoothDataEvent callback method.");
		}

	}

	/**
	 * Stop.
	 */
	public void stop() {
		if (btListener != null) {
			btListener.cancel();
		}

		if (mConnectThread != null) {
			mConnectThread.cancel();
		}

		for (String key : currentConnections.keySet()) {
			currentConnections.get(key).cancel();
		}
		currentConnections.clear();
		btListener = null;
		mConnectThread = null;
	}

	/**
	 * The Class ConnectThread.
	 */
	private class ConnectThread extends Thread {
		
		/** The mm socket. */
		private final BluetoothSocket mmSocket;
		
		/** The mm device. */
		protected final BluetoothDevice mmDevice;
		
		/** The m socket type. */
		private String mSocketType;

		/**
		 * Instantiates a new connect thread.
		 *
		 * @param device the device
		 * @param secure the secure
		 */
		public ConnectThread(BluetoothDevice device, boolean secure) {
			mmDevice = device;
			BluetoothSocket tmp = null;
			mSocketType = secure ? "Secure" : "Insecure";

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				if (secure) {
					tmp = device
							.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
				} else {
					tmp = device
							.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
				}
			} catch (IOException e) {
				PApplet.println("Socket Type: " + mSocketType
						+ "create() failed" + e);
			}
			mmSocket = tmp;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			while (mmSocket == null)
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

			PApplet.println("BEGIN mConnectThread SocketType:" + mSocketType
					+ ":" + mmSocket.getRemoteDevice().getName());

			// Always cancel discovery because it will slow down a connection
			bluetoothAdapter.cancelDiscovery();

			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				if (mmSocket != null)
					mmSocket.connect();
				PApplet.println("KBTConnect thread connected!");
			} catch (IOException e) {
				// Close the socket
				try {
					mmSocket.close();
				} catch (IOException e2) {
					PApplet.println("unable to close() " + mSocketType
							+ " socket during connection failure" + e2);
				}
				mConnectThread = null;
				return;
			}

			// Start the connected thread
			connectDevice(mmSocket);// , mmDevice, mSocketType);
		}

		/**
		 * Cancel.
		 */
		public void cancel() {
			// try {
			// mmSocket.close();
			// } catch (IOException e) {
			// PApplet.println("close() of connect " + mSocketType
			// + " socket failed" + e);
			// }
		}

	}

}