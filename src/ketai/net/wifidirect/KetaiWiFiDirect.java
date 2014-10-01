/*
 * 
 */
package ketai.net.wifidirect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import processing.core.PApplet;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;

/**
 * The KetaiWiFiDirect class is an experimental class that provides a wifi direct access to 
 * 	a sketch.  WifiDirect provides peer-to-peer networking between devices.  No wireless 
 * 	infrastructure is needed.  
 */
public class KetaiWiFiDirect extends BroadcastReceiver implements
		ChannelListener, ConnectionInfoListener, ActionListener,
		PeerListListener {

	/** The parent. */
	PApplet parent;
	
	/** The manager. */
	private WifiP2pManager manager;
	
	/** The is wifi p2p enabled. */
	private boolean isWifiP2pEnabled = false;
	
	/** The retry channel. */
	private boolean retryChannel = false;
	
	/** The peers. */
	private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

	/** The intent filter. */
	private final IntentFilter intentFilter = new IntentFilter();
	
	/** The channel. */
	private Channel channel;
	
	/** The ip. */
	private String ip = "";

	/**
	 * Instantiates a new ketai wi fi direct object
	 *
	 * @param _parent the calling sketch/Activity/PApplet
	 */
	public KetaiWiFiDirect(PApplet _parent) {
		parent = _parent;
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intentFilter
				.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

		manager = (WifiP2pManager) parent
				.getSystemService(Context.WIFI_P2P_SERVICE);

		channel = manager.initialize(parent, parent.getMainLooper(), this);
		parent.registerReceiver(this, intentFilter);
		parent.registerMethod("resume", this);
		parent.registerMethod("pause", this);
	}

	/**
	 * Sets the checks if wifi p2p enabled.
	 *
	 * @param isWifiP2pEnabled the new checks if is wifi p2p enabled
	 */
	public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
		this.isWifiP2pEnabled = isWifiP2pEnabled;
	}

	/**
	 * Resume. (used by android activity administration)
	 */
	public void resume() {
		parent.registerReceiver(this, intentFilter);
	}

	/**
	 * Pause.(used by android activity administration)
	 */
	public void pause() {
		parent.unregisterReceiver(this);
	}

	/**
	 * Connect using a specified configuration
	 *
	 * @param config the config
	 */
	private void connectToConfig(WifiP2pConfig config) {
		manager.connect(channel, config, new ActionListener() {

			public void onSuccess() {
				// WiFiDirectBroadcastReceiver will notify us. Ignore for now.
			}

			public void onFailure(int reason) {
				PApplet.println("Connect failed. Retry." + reason);
			}
		});
	}

	/**
	 * Disconnect. clean out resources
	 */
	public void disconnect() {
		manager.removeGroup(channel, new ActionListener() {

			public void onFailure(int reasonCode) {
				PApplet.println("Disconnect failed. Reason :" + reasonCode);

			}

			public void onSuccess() {

			}

		});
	}

	/* (non-Javadoc)
	 * @see android.net.wifi.p2p.WifiP2pManager.ChannelListener#onChannelDisconnected()
	 */
	public void onChannelDisconnected() {
		// we will try once more
		if (manager != null && !retryChannel) {
			PApplet.println("Channel lost. Trying again");
			retryChannel = true;
			manager.initialize(parent, parent.getMainLooper(), this);
		} else {
			PApplet.println("Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.");
		}
	}

	/**
	 * Cancel disconnect.
	 */
	public void cancelDisconnect() {

		/*
		 * A cancel abort request by user. Disconnect i.e. removeGroup if
		 * already connected. Else, request WifiP2pManager to abort the ongoing
		 * request
		 */
		if (manager != null) {

			manager.cancelConnect(channel, new ActionListener() {

				public void onSuccess() {
					PApplet.println("Aborting connection");
				}

				public void onFailure(int reasonCode) {
					PApplet.println("Connect abort request failed. Reason Code: "
							+ reasonCode);
				}
			});
		}
	}

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

			// UI update to indicate wifi p2p status.
			int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
			if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
				// Wifi Direct mode is enabled
				this.setIsWifiP2pEnabled(true);
			} else {
				this.setIsWifiP2pEnabled(false);
			}
			PApplet.println("P2P state changed - " + state);
		} else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

			// request available peers from the wifi p2p manager. This is an
			// asynchronous call and the calling activity is notified with a
			// callback on PeerListListener.onPeersAvailable()
			if (manager != null) {
				manager.requestPeers(channel, (PeerListListener) this);
			}
			PApplet.println("P2P peers changed");
		} else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION
				.equals(action)) {

			if (manager == null) {
				return;
			}

			NetworkInfo networkInfo = (NetworkInfo) intent
					.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

			if (networkInfo.isConnected()) {

				// we are connected with the other device, request connection
				// info to find group owner IP

				manager.requestConnectionInfo(channel, this);
			} else {
				// It's a disconnect
			}
		} else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION
				.equals(action)) {

			PApplet.println("p2p device changed"
					+ (WifiP2pDevice) intent
							.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
		}
	}

	/**
	 * Gets the connection information.
	 *
	 * @return the connection info
	 */
	public void getConnectionInfo() {
		manager.requestConnectionInfo(channel, this);
	}

	/* (non-Javadoc)
	 * @see android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener#onConnectionInfoAvailable(android.net.wifi.p2p.WifiP2pInfo)
	 */
	public void onConnectionInfoAvailable(WifiP2pInfo arg0) {

		WifiP2pInfo info = arg0;
		if (arg0.groupFormed == false) {
			ip = "";
			return;
		}
		ip = info.groupOwnerAddress.getHostAddress();
		PApplet.println("Connection info available for :" + arg0.toString()
				+ "--" + info.groupOwnerAddress.getHostAddress());
	}

	/**
	 * Gets the iP address used by the connection.
	 *
	 * @return the iP address
	 */
	public String getIPAddress() {
		return ip;
	}

	/**
	 * Discover.
	 */
	public void discover() {
		if (manager != null) {
			manager.discoverPeers(channel, this);
		}
	}

	/* (non-Javadoc)
	 * @see android.net.wifi.p2p.WifiP2pManager.ActionListener#onFailure(int)
	 */
	public void onFailure(int arg0) {
		switch (arg0) {
		case 0:
			PApplet.println("WifiDirect failed " + arg0);
			break;
		case 1:
			PApplet.println("WifiDirect failed " + arg0);
			break;
		case 2:
			PApplet.println("WifiDirect failed " + arg0);
			break;
		default:
			PApplet.println("WifiDirect failed " + arg0);
			break;
		}
	}

	/* (non-Javadoc)
	 * @see android.net.wifi.p2p.WifiP2pManager.ActionListener#onSuccess()
	 */
	public void onSuccess() {
		PApplet.println("WifiDirect succeeded ");

	}

	/* (non-Javadoc)
	 * @see android.net.wifi.p2p.WifiP2pManager.PeerListListener#onPeersAvailable(android.net.wifi.p2p.WifiP2pDeviceList)
	 */
	public void onPeersAvailable(WifiP2pDeviceList arg0) {
		Collection<WifiP2pDevice> list = arg0.getDeviceList();
		if (list.size() > 0) {
			peers.clear();
			for (Iterator<WifiP2pDevice> i = list.iterator(); i.hasNext();)
				peers.add(i.next());
			PApplet.println("New KetaiWifiDirect peer list received:");
			for (WifiP2pDevice d : peers) {
				PApplet.println("\t\t" + d.deviceName + ":" + d.deviceAddress);
			}

		}
	}

	/**
	 * Gets the hardware address of the wifi interface
	 *
	 * @return the hardware address
	 */
	public String getHardwareAddress() {

		// WifiP2pDevice w = new WifiP2pDevice();
		// PApplet.println("Device :" + w.toString());
		WifiManager wm = (WifiManager) parent
				.getSystemService(Context.WIFI_SERVICE);
		String mac = wm.getConnectionInfo().getMacAddress();
		return mac;
	}

	/**
	 * Reset.
	 */
	public void reset() {
		peers.clear();
		manager.cancelConnect(channel, this);
		manager.removeGroup(channel, this);

	}

	/**
	 * Gets the peer name list.
	 *
	 * @return the peer name list
	 */
	public ArrayList<String> getPeerNameList() {
		ArrayList<String> names = new ArrayList<String>();
		for (WifiP2pDevice d : peers)
			names.add(d.deviceName);

		return names;
	}

	/**
	 * Connect to a device by name.
	 *
	 * @param deviceName the device name
	 */
	public void connect(String deviceName) {

		// obtain a peer from the WifiP2pDeviceList
		WifiP2pDevice device = null;

		for (WifiP2pDevice d : peers) {
			if (d.deviceAddress == deviceName || d.deviceName == deviceName)
				device = d;
		}

		// if (device == null)
		// return;

		WifiP2pConfig config = new WifiP2pConfig();

		if (device != null)
			config.deviceAddress = device.deviceAddress;
		else
			config.deviceAddress = deviceName;

		manager.connect(channel, config, new ActionListener() {
			public void onSuccess() {
				// success logic
			}

			public void onFailure(int reason) {
				PApplet.println("Failed to connect to device (" + reason + ")");
			}
		});
	}
}
