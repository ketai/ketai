package ketai.sensors;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import processing.core.PApplet;
import android.content.Context;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * The KetaiLocation class provides android location services data to a sketch.
 * 
 * To receive location data a sketch can define the following method:<br />
 * <br />
 * 
 * void onLocationEvent(Location l) : l is the raw android Location class<br />
 * void onLocationEvent(double lat, double lon) : lat, lon are the latitude &
 * longitude in degrees<br />
 * void onLocationEvent(double lat, double lon, double alt) : lat, lon are the
 * latitude & longitude in degrees, alt is altitude in meters<br />
 * void onLocationEvent(double lat, double lon, double alt. float acc) : lat,
 * lon are the latitude & longitude in degrees, alt is altitude in meters, acc
 * is the accuracy in meters<br />
 * 
 */
public class KetaiLocation implements LocationListener {

	/** The location manager. */
	private LocationManager locationManager = null;

	/** The parent. */
	private PApplet parent;

	/** The on location event method4arg. */
	private Method onLocationEventMethod1arg, onLocationEventMethod2arg,
			onLocationEventMethod3arg, onLocationEventMethod4arg;

	/** The provider. */
	private String provider;

	/** The location. */
	private Location location;

	/** The me. */
	KetaiLocation me;

	/** The min time. */
	private long minTime = 10000; // millis

	/** The min distance. */
	private float minDistance = 1; // meters

	/** The Constant SERVICE_DESCRIPTION. */
	final static String SERVICE_DESCRIPTION = "Android Location.";

	/** reference to callback object for updates */
	private Object callbackdelegate;

	/**
	 * Instantiates a new ketai location.
	 * 
	 * @param pParent
	 *            the calling sketch/Activity/PApplet
	 */
	public KetaiLocation(PApplet pParent) {
		parent = pParent;
		me = this;
		locationManager = (LocationManager) parent.getApplicationContext()
				.getSystemService(Context.LOCATION_SERVICE);
		PApplet.println("KetaiLocationManager instantiated:"
				+ locationManager.toString());
		findObjectIntentions(parent);
		start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.location.LocationListener#onLocationChanged(android.location.
	 * Location)
	 */
	public void onLocationChanged(Location _location) {
		PApplet.println("LocationChanged:" + _location.toString());
		location = _location;

		if (onLocationEventMethod1arg != null)
			try {
				onLocationEventMethod1arg.invoke(callbackdelegate,
						new Object[] { location });

				return;
			} catch (Exception e) {
				PApplet.println("Disabling onLocationEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onLocationEventMethod1arg = null;
			}

		if (onLocationEventMethod2arg != null)
			try {
				onLocationEventMethod2arg.invoke(
						callbackdelegate,
						new Object[] { location.getLatitude(),
								location.getLongitude() });
				return;
			} catch (Exception e) {
				PApplet.println("Disabling onLocationEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onLocationEventMethod2arg = null;
			}

		if (onLocationEventMethod3arg != null)
			try {
				onLocationEventMethod3arg
						.invoke(callbackdelegate,
								new Object[] { location.getLatitude(),
										location.getLongitude(),
										location.getAltitude() });
				return;
			} catch (Exception e) {
				PApplet.println("Disabling onLocationEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onLocationEventMethod3arg = null;
			}
		if (onLocationEventMethod4arg != null)
			try {
				onLocationEventMethod4arg
						.invoke(callbackdelegate,
								new Object[] { location.getLatitude(),
										location.getLongitude(),
										location.getAltitude(),
										location.getAccuracy() });
				return;
			} catch (Exception e) {
				PApplet.println("Disabling onLocationEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onLocationEventMethod4arg = null;
			}
	}

	/**
	 * Gets the last location.
	 * 
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Checks if started.
	 * 
	 * @return true, if started
	 */
	public boolean isStarted() {
		return (onLocationEventMethod4arg != null);
	}

	/**
	 * Start services.
	 */
	public void start() {
		PApplet.println("KetaiLocationManager: start()...");

		List<String> foo = locationManager.getAllProviders();
		PApplet.println("KetaiLocationManager All Provider(s) list: ");

		for (String s : foo) {
			PApplet.println("\t" + s);
		}

		if (!determineProvider()) {
			PApplet.println("Error obtaining location provider.  Check your location settings.");
			provider = "none";
		}

		if (location == null) {
			foo = locationManager.getProviders(true);
			PApplet.println("KetaiLocationManager Enabled Provider(s) list: ");
			for (String s : foo) {
				if (location == null) {
					android.location.Location l = locationManager
							.getLastKnownLocation(s);
					if (l != null) {
						location = new Location(l);
						PApplet.println("\t" + s

						+ " - lastLocation for provider:" + location.toString());
					}
				}
			}

			if (location == null)
				location = new Location("default");
		}
		// send last location
		onLocationChanged(location);
	}

	/**
	 * Stop. - clean resources
	 */
	public void stop() {
		PApplet.println("KetaiLocationManager: Stop()....");
		locationManager.removeUpdates(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	public void onProviderDisabled(String arg0) {
		PApplet.println("LocationManager onProviderDisabled: " + arg0);
		determineProvider();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	public void onProviderEnabled(String arg0) {
		PApplet.println("LocationManager onProviderEnabled: " + arg0);
		determineProvider();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String,
	 * int, android.os.Bundle)
	 */
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		PApplet.println("LocationManager onStatusChanged: " + arg0 + ":" + arg1
				+ ":" + arg2.toString());
		determineProvider();
	}

	/**
	 * Gets the location provider.
	 * 
	 * @return the provider
	 */
	public String getProvider() {
		return provider;
	}

	/**
	 * Returns a list of Location providers
	 * 
	 * @return the collection<? extends string>
	 */
	public Collection<? extends String> list() {
		Vector<String> list = new Vector<String>();
		list.add("Location");
		return list;
	}

	/**
	 * Sets the update update rate based on time or distance traveled.
	 * 
	 * @param millis
	 *            the millis
	 * @param meters
	 *            the meters
	 */
	public void setUpdateRate(int millis, int meters) {
		minTime = millis;
		minDistance = meters;

		determineProvider();
	}

	/**
	 * Determine provider, GPS preferred.
	 * 
	 * @return true, if successful
	 */
	private boolean determineProvider() {
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			provider = LocationManager.GPS_PROVIDER;
		else
			provider = locationManager.getBestProvider(new Criteria(), true);
		if (provider == null)
			return false;
		PApplet.println("Requesting location updates from: " + provider);

		parent.runOnUiThread(new Runnable() {
			public void run() {
				locationManager.requestLocationUpdates(provider, minTime,
						minDistance, me);
			}
		});

		return true;
	}

	/**
	 * Find parent callback methods
	 */
	private void findObjectIntentions(Object o) {
		callbackdelegate = o;

		try {
			onLocationEventMethod1arg = callbackdelegate.getClass().getMethod(
					"onLocationEvent", new Class[] { Location.class });
			PApplet.println("Found Advanced onLocationEventMethod(Location)...");

		} catch (NoSuchMethodException e) {
		}

		try {
			onLocationEventMethod2arg = callbackdelegate.getClass().getMethod(
					"onLocationEvent",
					new Class[] { double.class, double.class });
			PApplet.println("Found Advanced onLocationEventMethod(long, lat)...");

		} catch (NoSuchMethodException e) {
		}

		try {
			onLocationEventMethod3arg = callbackdelegate.getClass().getMethod(
					"onLocationEvent",
					new Class[] { double.class, double.class, double.class });
			PApplet.println("Found basic onLocationEventMethod(long,lat,alt)...");

		} catch (NoSuchMethodException e) {
		}

		try {
			onLocationEventMethod4arg = callbackdelegate.getClass().getMethod(
					"onLocationEvent",
					new Class[] { double.class, double.class, double.class,
							float.class });
			PApplet.println("Found basic onLocationEventMethod(long,lat,alt, acc)...");

		} catch (NoSuchMethodException e) {
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.location.LocationListener#onLocationChanged(android.location.
	 * Location)
	 */
	public void onLocationChanged(android.location.Location arg0) {
		onLocationChanged(new Location(arg0));
	}

	public void register(Object delegate) {
		boolean running = isStarted();
		if (running)
			stop();
		findObjectIntentions(delegate);
		if (running)
			start();
	}
}
