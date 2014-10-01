package ketai.sensors;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import processing.core.PApplet;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * The KetaiSensor class provides access to android sensors. To receive sensor
 * data the following methods can be defined in your sketch:<br />
 * <br />
 * 
 * void onSensorEvent(SensorEvent e) - raw android sensor event <br />
 * void onAccelerometerEvent(float x, float y, float z, long a, int b): x,y,z
 * force in m/s^2, a=timestamp(nanos), b=accuracy<br />
 * void onAccelerometerEvent(float x, float y, float z): x,y,z force in m/s2<br />
 * void onOrientationEvent(float x, float y, flaot z, long a, int b): x,y,z
 * rotation in degrees, a=timestamp(nanos), b=accuracy<br />
 * void onOrientationEvent(float x, float y, float z) : x,y,z rotation in
 * degrees<br />
 * void onMagneticFieldEvent(float x, float y, float z, long a, int b) : x,y,z
 * geomag field in uT, a=timestamp(nanos), b=accuracy<br />
 * void onMagneticFieldEvent(float x, float y, float z): x,y,z geomagnetic field
 * in uT<br />
 * void onGyroscopeEvent(float x, float y, float z, long a, int b):x,y,z
 * rotation in rads/sec, a=timestamp(nanos), b=accuracy<br />
 * void onGyroscopeEvent(float x, float y, float z): x,y,z rotation in rads/sec<br />
 * void onGravityEvent(float x, float y, float z, long a, int b): x,y,z force of
 * gravity in m/s^2, a=timestamp(nanos), b=accuracy<br />
 * void onGravityEvent(float x, float y, float z): x,y,z rotation in m/s^s<br />
 * void onProximityEvent(float d, long a, int b): d distance from sensor
 * (typically 0,1), a=timestamp(nanos), b=accuracy<br />
 * void onProximityEvent(float d): d distance from sensor (typically 0,1)<br />
 * void onLightEvent(float d, long a, int b): d illumination from sensor in lx<br />
 * void onLightEvent(float d): d illumination from sensor in lx<br />
 * void onPressureEvent(float p, long a, int b): p ambient pressure in hPa or
 * mbar, a=timestamp(nanos), b=accuracy<br />
 * void onPressureEvent(float p): p ambient pressure in hPa or mbar<br />
 * void onTemperatureEvent(float t, long a, int b): t temperature in degrees in
 * degrees Celsius, a=timestamp(nanos), a=timestamp(nanos), b=accuracy<br />
 * void onTemperatureEvent(float t): t temperature in degrees in degrees Celsius<br />
 * void onLinearAccelerationEvent(float x, float y, float z, long a, int b):
 * x,y,z acceleration force in m/s^2, minus gravity, a=timestamp(nanos),
 * b=accuracy<br />
 * void onLinearAccelerationEvent(float x, float y, float z): x,y,z acceleration
 * force in m/s^2, minus gravity<br />
 * void onRotationVectorEvent(float x, float y, float z, long a, int b): x,y,z
 * rotation vector values, a=timestamp(nanos), b=accuracy<br />
 * void onRotationVectorEvent(float x, float y, float z):x,y,z rotation vector
 * values<br />
 * void onAmibentTemperatureEvent(float t): same as temp above (newer API)<br />
 * void onRelativeHumidityEvent(float h): h ambient humidity in percentage<br />
 */
public class KetaiSensor implements SensorEventListener {

	/** The sensor manager. */
	private SensorManager sensorManager = null;

	/** The is registered. */
	private boolean isRegistered = false;

	/** The parent. */
	private PApplet parent;
	public Object callbackdelegate;

	/** The on sensor event method. */
	private Method onSensorEventMethod;

	/** The magnetometer data. */
	float[] accelerometerData, magnetometerData; // for getting orientation data

	// Simple methods are of the form v1,v2,v3,v4 (typically x,y,z values)
	// and the non-simple methods take values of v1,v2,v3, time, accuracy.
	// see:
	// http://developer.android.com/reference/android/hardware/SensorEvent.html#values
	/** The on relative humidity event method. */
	private Method onAccelerometerEventMethod,
			onAccelerometerEventMethodSimple, onOrientationSensorEventMethod,
			onOrientationSensorEventMethodSimple, onGyroscopeSensorEventMethod,
			onGyroscopeSensorEventMethodSimple,

			onMagneticFieldSensorEventMethod,
			onMagneticFieldSensorEventMethodSimple, onLightSensorEventMethod,
			onLightSensorEventMethodSimple, onProximitySensorEventMethod,
			onProximitySensorEventMethodSimple, onPressureSensorEventMethod,
			onPressureSensorEventMethodSimple, onTemperatureSensorEventMethod,
			onTemperatureSensorEventMethodSimple,
			onRotationVectorSensorEventMethod,
			onRotationVectorSensorEventMethodSimple,
			onGravitySensorEventMethod, onGravitySensorEventMethodSimple,
			onLinearAccelerationSensorEventMethod,
			onLinearAccelerationSensorEventMethodSimple,
			onAmbientTemperatureEventMethod, onRelativeHumidityEventMethod;

	/** The relative humidity sensor enabled. */
	private boolean accelerometerSensorEnabled, magneticFieldSensorEnabled,
			orientationSensorEnabled, proximitySensorEnabled, useSimulator,
			lightSensorEnabled, pressureSensorEnabled,
			temperatureSensorEnabled, gyroscopeSensorEnabled,
			rotationVectorSensorEnabled, linearAccelerationSensorEnabled,
			gravitySensorEnabled, ambientTemperatureSensorEnabled,
			relativeHumiditySensorEnabled;

	/** The time of last update. */
	private long delayInterval, timeOfLastUpdate;

	/** The Constant SERVICE_DESCRIPTION. */
	final static String SERVICE_DESCRIPTION = "Android Sensors.";

	/**
	 * Instantiates a new ketai sensor.
	 * 
	 * @param pParent
	 *            PApplet/sketch instance
	 */
	public KetaiSensor(PApplet pParent) {
		parent = pParent;
		findParentIntentions();
		useSimulator = false;
		sensorManager = (SensorManager) parent.getApplicationContext()
				.getSystemService(Context.SENSOR_SERVICE);
		delayInterval = timeOfLastUpdate = 0;
	}

	public static boolean remapCoordinateSystem(float[] inR, int X, int Y,
			float[] outR) {
		return SensorManager.remapCoordinateSystem(inR, X, Y, outR);
	}

	/**
	 * Use simulator.
	 * 
	 * @param flag
	 *            the flag
	 */
	public void useSimulator(boolean flag) {
		useSimulator = flag;
	}

	/**
	 * Using simulator.
	 * 
	 * @return true, if successful
	 */
	public boolean usingSimulator() {
		return useSimulator;
	}

	/**
	 * Sets the delay interval.
	 * 
	 * @param pDelayInterval
	 *            the new delay interval
	 */
	public void setDelayInterval(long pDelayInterval) {
		delayInterval = pDelayInterval;
	}

	/**
	 * Enable accelerometer.
	 */
	public void enableAccelerometer() {
		accelerometerSensorEnabled = true;
	}

	/**
	 * Enable rotation vector.
	 */
	public void enableRotationVector() {
		rotationVectorSensorEnabled = true;
	}

	/**
	 * Enable linear acceleration.
	 */
	public void enableLinearAcceleration() {
		linearAccelerationSensorEnabled = true;
	}

	/**
	 * Disable accelerometer.
	 */
	public void disableAccelerometer() {
		accelerometerSensorEnabled = true;
	}

	/**
	 * Enable magentic field.
	 */
	public void enableMagenticField() {
		magneticFieldSensorEnabled = true;
	}

	/**
	 * Disable magnetic field.
	 */
	public void disableMagneticField() {
		magneticFieldSensorEnabled = true;
	}

	/**
	 * Enable orientation.
	 */
	public void enableOrientation() {
		orientationSensorEnabled = true;
	}

	/**
	 * Disable orientation.
	 */
	public void disableOrientation() {
		orientationSensorEnabled = false;
	}

	/**
	 * Enable proximity.
	 */
	public void enableProximity() {
		proximitySensorEnabled = true;
	}

	/**
	 * Disable proximity.
	 */
	public void disableProximity() {
		proximitySensorEnabled = false;
	}

	/**
	 * Disablelinear acceleration.
	 */
	public void disablelinearAcceleration() {
		linearAccelerationSensorEnabled = false;
	}

	/**
	 * Disable rotation vector.
	 */
	public void disableRotationVector() {
		rotationVectorSensorEnabled = false;
	}

	/**
	 * Enable light.
	 */
	public void enableLight() {
		lightSensorEnabled = true;
	}

	/**
	 * Disable light.
	 */
	public void disableLight() {
		lightSensorEnabled = true;
	}

	/**
	 * Enable pressure.
	 */
	public void enablePressure() {
		pressureSensorEnabled = true;
	}

	/**
	 * Disable pressure.
	 */
	public void disablePressure() {
		pressureSensorEnabled = true;
	}

	/**
	 * Enable temperature.
	 */
	public void enableTemperature() {
		temperatureSensorEnabled = true;
	}

	/**
	 * Disable temperature.
	 */
	public void disableTemperature() {
		temperatureSensorEnabled = false;
	}

	/**
	 * Enable gyroscope.
	 */
	public void enableGyroscope() {
		gyroscopeSensorEnabled = true;
	}

	/**
	 * Disable gyroscope.
	 */
	public void disableGyroscope() {
		gyroscopeSensorEnabled = false;
	}

	/**
	 * Disable amibent temperature.
	 */
	public void disableAmibentTemperature() {
		ambientTemperatureSensorEnabled = false;
	}

	/**
	 * Disable relative humidity sensor.
	 */
	public void disableRelativeHumiditySensor() {
		relativeHumiditySensorEnabled = false;
	}

	/**
	 * Enable amibent temperature.
	 */
	public void enableAmibentTemperature() {
		ambientTemperatureSensorEnabled = true;
	}

	/**
	 * Enable relative humidity sensor.
	 */
	public void enableRelativeHumiditySensor() {
		relativeHumiditySensorEnabled = true;
	}

	/**
	 * Enable all sensors.
	 */
	public void enableAllSensors() {
		accelerometerSensorEnabled = magneticFieldSensorEnabled = orientationSensorEnabled = proximitySensorEnabled = lightSensorEnabled = pressureSensorEnabled = temperatureSensorEnabled = gyroscopeSensorEnabled = linearAccelerationSensorEnabled = rotationVectorSensorEnabled = ambientTemperatureSensorEnabled = relativeHumiditySensorEnabled = true;
	}

	/**
	 * Checks if is accelerometer available.
	 * 
	 * @return true, if is accelerometer available
	 */
	public boolean isAccelerometerAvailable() {
		return isSensorSupported(Sensor.TYPE_ACCELEROMETER);
	}

	/**
	 * Checks if is linear acceleration available.
	 * 
	 * @return true, if is linear acceleration available
	 */
	public boolean isLinearAccelerationAvailable() {
		return isSensorSupported(Sensor.TYPE_LINEAR_ACCELERATION);
	}

	/**
	 * Checks if is rotation vector available.
	 * 
	 * @return true, if is rotation vector available
	 */
	public boolean isRotationVectorAvailable() {
		return isSensorSupported(Sensor.TYPE_ROTATION_VECTOR);
	}

	/**
	 * Checks if is magentic field available.
	 * 
	 * @return true, if is magentic field available
	 */
	public boolean isMagenticFieldAvailable() {
		return isSensorSupported(Sensor.TYPE_MAGNETIC_FIELD);
	}

	/**
	 * Checks if is orientation available.
	 * 
	 * @return true, if is orientation available
	 */
	public boolean isOrientationAvailable() {
		return isSensorSupported(Sensor.TYPE_ORIENTATION);
	}

	/**
	 * Checks if is proximity available.
	 * 
	 * @return true, if is proximity available
	 */
	public boolean isProximityAvailable() {
		return isSensorSupported(Sensor.TYPE_PROXIMITY);
	}

	/**
	 * Checks if is light available.
	 * 
	 * @return true, if is light available
	 */
	public boolean isLightAvailable() {
		return isSensorSupported(Sensor.TYPE_LIGHT);
	}

	/**
	 * Checks if is pressure available.
	 * 
	 * @return true, if is pressure available
	 */
	public boolean isPressureAvailable() {
		return isSensorSupported(Sensor.TYPE_PRESSURE);
	}

	/**
	 * Checks if is temperature available.
	 * 
	 * @return true, if is temperature available
	 */
	public boolean isTemperatureAvailable() {
		return isSensorSupported(Sensor.TYPE_TEMPERATURE);
	}

	/**
	 * Checks if is gyroscope available.
	 * 
	 * @return true, if is gyroscope available
	 */
	public boolean isGyroscopeAvailable() {
		return isSensorSupported(Sensor.TYPE_GYROSCOPE);
	}

	/**
	 * Checks if is ambient temperature available.
	 * 
	 * @return true, if is ambient temperature available
	 */
	public boolean isAmbientTemperatureAvailable() {
		return isSensorSupported(Sensor.TYPE_AMBIENT_TEMPERATURE);
	}

	/**
	 * Checks if is relative humidity available.
	 * 
	 * @return true, if is relative humidity available
	 */
	public boolean isRelativeHumidityAvailable() {
		return isSensorSupported(Sensor.TYPE_RELATIVE_HUMIDITY);
	}

	/**
	 * List.
	 * 
	 * @return the collection<? extends string>
	 */
	public Collection<? extends String> list() {
		Vector<String> list = new Vector<String>();

		List<Sensor> foo = sensorManager.getSensorList(Sensor.TYPE_ALL);
		for (Sensor s : foo) {
			list.add(s.getName());
			PApplet.println("\tKetaiSensor sensor: " + s.getName() + ":"
					+ s.getType());
		}
		return list;
		// String returnList[] = new String[list.size()];
		// list.copyInto(returnList);
		// return returnList;
	}

	/**
	 * Checks if is started.
	 * 
	 * @return true, if is started
	 */
	public boolean isStarted() {
		return isRegistered;
	}

	/**
	 * Start services.
	 */
	public void start() {
		PApplet.println("KetaiSensor: start()...");

		if (accelerometerSensorEnabled) {
			Sensor s = sensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			if (s != null)
				sensorManager.registerListener(this, s,
						SensorManager.SENSOR_DELAY_UI);
		}
		if (magneticFieldSensorEnabled) {
			Sensor s = sensorManager
					.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			if (s != null)
				sensorManager.registerListener(this, s,
						SensorManager.SENSOR_DELAY_UI);
		}
		if (pressureSensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
			if (s != null)
				sensorManager.registerListener(this, s,
						SensorManager.SENSOR_DELAY_UI);
		}
		if (orientationSensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
			if (s != null)
				sensorManager.registerListener(this, s,
						SensorManager.SENSOR_DELAY_UI);
		}
		if (proximitySensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
			if (s != null)
				sensorManager.registerListener(this, s,
						SensorManager.SENSOR_DELAY_UI);
		}
		if (temperatureSensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
			if (s != null)
				sensorManager.registerListener(this, s,
						SensorManager.SENSOR_DELAY_UI);
		}
		if (gyroscopeSensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
			if (s != null)
				sensorManager.registerListener(this, s,
						SensorManager.SENSOR_DELAY_UI);
		}
		if (rotationVectorSensorEnabled) {
			Sensor s = sensorManager
					.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
			if (s != null)
				sensorManager.registerListener(this, s,
						SensorManager.SENSOR_DELAY_UI);
		}
		if (linearAccelerationSensorEnabled) {
			Sensor s = sensorManager
					.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
			if (s != null)
				sensorManager.registerListener(this, s,
						SensorManager.SENSOR_DELAY_UI);
		}
		if (lightSensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
			if (s != null)
				sensorManager.registerListener(this, s,
						SensorManager.SENSOR_DELAY_UI);
		}
		if (gravitySensorEnabled) {
			Sensor s = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
			if (s != null)
				sensorManager.registerListener(this, s,
						SensorManager.SENSOR_DELAY_UI);
		}
		if (ambientTemperatureSensorEnabled) {
			Sensor s = sensorManager
					.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
			if (s != null)
				sensorManager.registerListener(this, s,
						SensorManager.SENSOR_DELAY_UI);
		}
		if (relativeHumiditySensorEnabled) {
			Sensor s = sensorManager
					.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
			if (s != null)
				sensorManager.registerListener(this, s,
						SensorManager.SENSOR_DELAY_UI);
		}
		isRegistered = true;
	}

	/**
	 * Stop services.
	 */
	public void stop() {
		PApplet.println("KetaiSensor: Stop()....");
		sensorManager.unregisterListener(this);
		isRegistered = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.hardware.SensorEventListener#onSensorChanged(android.hardware
	 * .SensorEvent)
	 */
	public void onSensorChanged(SensorEvent arg0) {

		Date date = new Date();
		long now = date.getTime();
		// PApplet.println("onSensorChanged: " + arg0.timestamp + ":" +
		// arg0.sensor.getType());

		if (now < timeOfLastUpdate + delayInterval)
			return;

		timeOfLastUpdate = now;

		if (onSensorEventMethod != null) {
			try {
				onSensorEventMethod.invoke(callbackdelegate,
						new Object[] { arg0 });
				return;
			} catch (Exception e) {
				PApplet.println("Disabling onSensorEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onSensorEventMethod = null;
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_ACCELEROMETER
				&& accelerometerSensorEnabled) {
			if (onAccelerometerEventMethod != null) {
				try {
					// holding accel data for orientation
					accelerometerData = arg0.values.clone();
					onAccelerometerEventMethod.invoke(callbackdelegate,
							new Object[] { arg0.values[0], arg0.values[1],
									arg0.values[2], arg0.timestamp,
									arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onAccelerometerEvent():"
							+ e.getMessage());
					e.printStackTrace();
				}
			}

			if (onAccelerometerEventMethodSimple != null) {
				try {
					// holding accel data for orientation calc
					accelerometerData = arg0.values.clone();
					onAccelerometerEventMethodSimple.invoke(callbackdelegate,
							new Object[] { arg0.values[0], arg0.values[1],
									arg0.values[2] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onAccelerometerEvent() [simple]:"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_GRAVITY
				&& gravitySensorEnabled) {
			if (onGravitySensorEventMethod != null) {
				try {
					onGravitySensorEventMethod.invoke(callbackdelegate,
							new Object[] { arg0.values[0], arg0.values[1],
									arg0.values[2], arg0.timestamp,
									arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onGravityEvent():" + e.getMessage());
					e.printStackTrace();
				}
			}

			if (onGravitySensorEventMethodSimple != null) {
				try {
					onGravitySensorEventMethodSimple.invoke(callbackdelegate,
							new Object[] { arg0.values[0], arg0.values[1],
									arg0.values[2] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onGravityEvent()[simple]:"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_ORIENTATION
				&& orientationSensorEnabled) {
			if (onOrientationSensorEventMethod != null) {
				try {
					onOrientationSensorEventMethod.invoke(callbackdelegate,
							new Object[] { arg0.values[0], arg0.values[1],
									arg0.values[2], arg0.timestamp,
									arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onOrientationEvent():"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
			if (onOrientationSensorEventMethodSimple != null) {
				try {
					onOrientationSensorEventMethodSimple.invoke(
							callbackdelegate, new Object[] { arg0.values[0],
									arg0.values[1], arg0.values[2] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onOrientationEvent()[simple] :"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD
				&& magneticFieldSensorEnabled) {
			if (onMagneticFieldSensorEventMethod != null) {
				try {
					magnetometerData = arg0.values.clone();
					onMagneticFieldSensorEventMethod.invoke(callbackdelegate,
							new Object[] { arg0.values[0], arg0.values[1],
									arg0.values[2], arg0.timestamp,
									arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onMagneticFieldEvent():"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
			if (onMagneticFieldSensorEventMethodSimple != null) {
				try {
					magnetometerData = arg0.values.clone();
					onMagneticFieldSensorEventMethodSimple.invoke(
							callbackdelegate, new Object[] { arg0.values[0],
									arg0.values[1], arg0.values[2] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onMagneticFieldEvent()[simple]:"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_GYROSCOPE
				&& gyroscopeSensorEnabled) {
			if (onGyroscopeSensorEventMethod != null) {
				try {
					onGyroscopeSensorEventMethod.invoke(callbackdelegate,
							new Object[] { arg0.values[0], arg0.values[1],
									arg0.values[2], arg0.timestamp,
									arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onGyroscopeEvent():"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
			if (onGyroscopeSensorEventMethodSimple != null) {
				try {
					onGyroscopeSensorEventMethodSimple.invoke(callbackdelegate,
							new Object[] { arg0.values[0], arg0.values[1],
									arg0.values[2] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onGyroscopeEvent()[simple]:"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_LIGHT && lightSensorEnabled) {
			if (onLightSensorEventMethod != null) {
				try {

					onLightSensorEventMethod.invoke(callbackdelegate,
							new Object[] { arg0.values[0], arg0.timestamp,
									arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onLightEvent():" + e.getMessage());
					e.printStackTrace();
				}
			}
			if (onLightSensorEventMethodSimple != null) {
				try {

					onLightSensorEventMethodSimple.invoke(callbackdelegate,
							new Object[] { arg0.values[0] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onLightEvent()[simple]r:"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_PROXIMITY
				&& proximitySensorEnabled) {
			if (onProximitySensorEventMethod != null) {
				try {
					onProximitySensorEventMethod.invoke(callbackdelegate,
							new Object[] { arg0.values[0], arg0.timestamp,
									arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onProximityEvent():"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
			if (onProximitySensorEventMethodSimple != null) {
				try {
					onProximitySensorEventMethodSimple.invoke(callbackdelegate,
							new Object[] { arg0.values[0] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onProximityEvent()[simple]:"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
		}
		if (arg0.sensor.getType() == Sensor.TYPE_PRESSURE
				&& pressureSensorEnabled) {

			if (onPressureSensorEventMethod != null) {
				try {
					onPressureSensorEventMethod.invoke(callbackdelegate,
							new Object[] { arg0.values[0], arg0.timestamp,
									arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onPressureEvent()r:"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
			if (onPressureSensorEventMethodSimple != null) {
				try {
					onPressureSensorEventMethodSimple.invoke(callbackdelegate,
							new Object[] { arg0.values[0] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onPressureEvent()[simple]:"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_TEMPERATURE
				&& temperatureSensorEnabled) {
			if (onTemperatureSensorEventMethod != null) {
				try {
					onTemperatureSensorEventMethod.invoke(callbackdelegate,
							new Object[] { arg0.values[0], arg0.timestamp,
									arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onTemperatureEvent():"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
			if (onTemperatureSensorEventMethodSimple != null) {
				try {
					onTemperatureSensorEventMethodSimple.invoke(
							callbackdelegate, new Object[] { arg0.values[0] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onTemperatureEvent()[simple]:"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
		}
		if (arg0.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION
				&& linearAccelerationSensorEnabled) {

			if (onLinearAccelerationSensorEventMethod != null) {
				try {
					onLinearAccelerationSensorEventMethod.invoke(
							callbackdelegate, new Object[] { arg0.values[0],
									arg0.values[1], arg0.values[2],
									arg0.timestamp, arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onLinearAccelerationEvent():"
							+ e.getMessage());
					e.printStackTrace();
				}
			}

			if (onLinearAccelerationSensorEventMethodSimple != null) {
				try {
					onLinearAccelerationSensorEventMethodSimple.invoke(
							callbackdelegate, new Object[] { arg0.values[0],
									arg0.values[1], arg0.values[2] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onLinearAccelerationEvent()[simple]:"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR
				&& rotationVectorSensorEnabled) {
			if (onRotationVectorSensorEventMethod != null) {
				try {
					onRotationVectorSensorEventMethod.invoke(callbackdelegate,
							new Object[] { arg0.values[0], arg0.values[1],
									arg0.values[2], arg0.timestamp,
									arg0.accuracy });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onRotationVectorEvent():"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
			if (onRotationVectorSensorEventMethodSimple != null) {
				try {
					onRotationVectorSensorEventMethodSimple.invoke(
							callbackdelegate, new Object[] { arg0.values[0],
									arg0.values[1], arg0.values[2] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onRotationVectorEvent()[simple]:"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
		}
		if (arg0.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE
				&& ambientTemperatureSensorEnabled) {
			if (onAmbientTemperatureEventMethod != null) {
				try {
					onAmbientTemperatureEventMethod.invoke(callbackdelegate,
							new Object[] { arg0.values[0] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onAmbientTemperatureEvent():"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
		}

		if (arg0.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY
				&& relativeHumiditySensorEnabled) {
			if (onRelativeHumidityEventMethod != null) {
				try {
					onRelativeHumidityEventMethod.invoke(callbackdelegate,
							new Object[] { arg0.values[0] });
					timeOfLastUpdate = now;
					return;
				} catch (Exception e) {
					PApplet.println("Error onRelativeHumidityEventMethod():"
							+ e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.hardware.SensorEventListener#onAccuracyChanged(android.hardware
	 * .Sensor, int)
	 */
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/**
	 * Checks if is sensor supported.
	 * 
	 * @param type
	 *            the type
	 * @return true, if is sensor supported
	 */
	private boolean isSensorSupported(int type) {
		List<Sensor> foo = sensorManager.getSensorList(Sensor.TYPE_ALL);
		for (Sensor s : foo) {
			if (type == s.getType())
				return true;
		}
		return false;
	}

	/**
	 * Find parent intentions.
	 */
	private void findParentIntentions() {
		findObjectIntentions(parent);
	}

	private void findObjectIntentions(Object o) {
		callbackdelegate = o;

		try {
			onSensorEventMethod = o.getClass().getMethod("onSensorEvent",
					new Class[] { SensorEvent.class });
		} catch (NoSuchMethodException e) {
		}

		try {
			onAccelerometerEventMethod = o.getClass().getMethod(
					"onAccelerometerEvent",
					new Class[] { float.class, float.class, float.class,
							long.class, int.class, });
			accelerometerSensorEnabled = true;
			PApplet.println("Found onAccelerometerEvent	Method...in "
					+ o.getClass());

		} catch (NoSuchMethodException e) {
		}

		try {
			onAccelerometerEventMethodSimple = o.getClass().getMethod(
					"onAccelerometerEvent",
					new Class[] { float.class, float.class, float.class });
			accelerometerSensorEnabled = true;
			PApplet.println("Found onAccelerometerEventMethod(simple)...");

		} catch (NoSuchMethodException e) {
		}

		try {
			onOrientationSensorEventMethod = o.getClass().getMethod(
					"onOrientationEvent",
					new Class[] { float.class, float.class, float.class,
							long.class, int.class, });
			orientationSensorEnabled = true;
			PApplet.println("Found onOrientationEventMethod...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onOrientationSensorEventMethodSimple = o.getClass().getMethod(
					"onOrientationEvent",
					new Class[] { float.class, float.class, float.class });
			orientationSensorEnabled = true;
			PApplet.println("Found onOrientationEventMethod(simple)...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onMagneticFieldSensorEventMethod = o.getClass().getMethod(
					"onMagneticFieldEvent",
					new Class[] { float.class, float.class, float.class,
							long.class, int.class });
			magneticFieldSensorEnabled = true;
			PApplet.println("Found onMagneticFieldEventMethod...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onMagneticFieldSensorEventMethodSimple = o.getClass().getMethod(
					"onMagneticFieldEvent",
					new Class[] { float.class, float.class, float.class });
			magneticFieldSensorEnabled = true;
			PApplet.println("Found onMagneticFieldEventMethod(simple)...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onGyroscopeSensorEventMethod = o.getClass().getMethod(
					"onGyroscopeEvent",
					new Class[] { float.class, float.class, float.class,
							long.class, int.class, });
			gyroscopeSensorEnabled = true;
			PApplet.println("Found onGyroscopeEventMethod...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onGyroscopeSensorEventMethodSimple = o.getClass().getMethod(
					"onGyroscopeEvent",
					new Class[] { float.class, float.class, float.class });
			gyroscopeSensorEnabled = true;
			PApplet.println("Found onGyroscopeEventMethod(simple)...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onGravitySensorEventMethod = o.getClass().getMethod(
					"onGravityEvent",
					new Class[] { float.class, float.class, float.class,
							long.class, int.class, });
			gravitySensorEnabled = true;
			PApplet.println("Found onGravityEvenMethod...");

		} catch (NoSuchMethodException e) {
		}

		try {
			onGravitySensorEventMethodSimple = o.getClass().getMethod(
					"onGravityEvent",
					new Class[] { float.class, float.class, float.class });
			gravitySensorEnabled = true;
			PApplet.println("Found onGravityEvenMethod(simple)...");

		} catch (NoSuchMethodException e) {
		}

		try {
			onProximitySensorEventMethod = o.getClass().getMethod(
					"onProximityEvent",
					new Class[] { float.class, long.class, int.class });
			proximitySensorEnabled = true;
			PApplet.println("Found onLightEventMethod...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onProximitySensorEventMethodSimple = o.getClass().getMethod(
					"onProximityEvent", new Class[] { float.class });
			proximitySensorEnabled = true;
			PApplet.println("Found onProximityEventMethod(simple)...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onLightSensorEventMethod = o.getClass().getMethod("onLightEvent",
					new Class[] { float.class, long.class, int.class });
			lightSensorEnabled = true;
			PApplet.println("Found onLightEventMethod...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onLightSensorEventMethodSimple = o.getClass().getMethod(
					"onLightEvent", new Class[] { float.class });
			lightSensorEnabled = true;
			PApplet.println("Found onLightEventMethod(simple)...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onPressureSensorEventMethod = o.getClass().getMethod(
					"onPressureEvent",
					new Class[] { float.class, long.class, int.class });
			pressureSensorEnabled = true;
			PApplet.println("Found onPressureEventMethod...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onPressureSensorEventMethodSimple = o.getClass().getMethod(
					"onPressureEvent", new Class[] { float.class });
			pressureSensorEnabled = true;
			PApplet.println("Found onPressureEventMethod(simple)...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onTemperatureSensorEventMethod = o.getClass().getMethod(
					"onTemperatureEvent",
					new Class[] { float.class, long.class, int.class });
			temperatureSensorEnabled = true;
			PApplet.println("Found onTemperatureEventMethod...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onTemperatureSensorEventMethodSimple = o.getClass().getMethod(
					"onTemperatureEvent", new Class[] { float.class });
			temperatureSensorEnabled = true;
			PApplet.println("Found onTemperatureEventMethod(simple)...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onLinearAccelerationSensorEventMethod = o.getClass().getMethod(
					"onLinearAccelerationEvent",
					new Class[] { float.class, float.class, float.class,
							long.class, int.class });
			linearAccelerationSensorEnabled = true;
			PApplet.println("Found onLinearAccelerationEventMethod...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onLinearAccelerationSensorEventMethodSimple = o
					.getClass()
					.getMethod(
							"onLinearAccelerationEvent",
							new Class[] { float.class, float.class, float.class });
			linearAccelerationSensorEnabled = true;
			PApplet.println("Found onLinearAccelerationEventMethod(simple)...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onRotationVectorSensorEventMethod = o.getClass().getMethod(
					"onRotationVectorEvent",
					new Class[] { float.class, float.class, float.class,
							long.class, int.class });
			rotationVectorSensorEnabled = true;
			PApplet.println("Found onRotationVectorEvenMethod...");

		} catch (NoSuchMethodException e) {
		}

		try {
			onRotationVectorSensorEventMethodSimple = o.getClass().getMethod(
					"onRotationVectorEvent",
					new Class[] { float.class, float.class, float.class });
			rotationVectorSensorEnabled = true;
			PApplet.println("Found onRotationVectorEventMethod(simple)...");

		} catch (NoSuchMethodException e) {
		}

		try {
			onAmbientTemperatureEventMethod = o.getClass().getMethod(
					"onAmibentTemperatureEvent", new Class[] { float.class });
			ambientTemperatureSensorEnabled = true;
			PApplet.println("Found onAmbientTemperatureEvent callback...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onRelativeHumidityEventMethod = o.getClass().getMethod(
					"onRelativeHumidityEvent", new Class[] { float.class });
			relativeHumiditySensorEnabled = true;
			PApplet.println("Found onRelativeHumidityEventMethod...");
		} catch (NoSuchMethodException e) {
		}

	}

	/**
	 * Start service.
	 */
	public void startService() {
		start();
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status (not used)
	 */
	public int getStatus() {
		return 0;
	}

	/**
	 * Stop service.
	 */
	public void stopService() {
		stop();
	}

	/**
	 * Gets the service description.
	 * 
	 * @return the service description
	 */
	public String getServiceDescription() {
		return SERVICE_DESCRIPTION;
	}

	/**
	 * Gets the rotation matrix from vector.
	 * 
	 * @param R
	 *            the r
	 * @param rotationVector
	 *            the rotation vector
	 * @return the rotation matrix from vector
	 */
	public static void getRotationMatrixFromVector(float[] R, float[] rotationVector) {
		SensorManager.getRotationMatrixFromVector(R, rotationVector);
	}

	/**
	 * Gets the quaternion from vector.
	 * 
	 * @param Q
	 *            the q
	 * @param rv
	 *            the rv
	 * @return the quaternion from vector
	 */
	public void getQuaternionFromVector(float[] Q, float[] rv) {
		SensorManager.getQuaternionFromVector(Q, rv);
	}

	/**
	 * Gets the orientation.
	 * 
	 * @return the orientation
	 */
	public float[] getOrientation() {
		float[] values = new float[3];
		float[] R = new float[16];
		float[] I = new float[9];

		if (!isStarted() && !this.accelerometerSensorEnabled
				&& !this.magneticFieldSensorEnabled) {
			PApplet.println("Cannot compute orientation until sensor service is started and accelerometer and magnetometer must also be enabled.");
			values = new float[3];
			return values;
		}

		if (SensorManager.getRotationMatrix(R, I, accelerometerData,
				magnetometerData))
			values = SensorManager.getOrientation(null, values);

		return values;
	}

	public void register(Object delegate) {
		PApplet.println("KetaiSensor delegating Events to class: "
				+ delegate.getClass());
		findObjectIntentions(delegate);
	}
}
