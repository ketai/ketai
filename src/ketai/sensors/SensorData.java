/* 
 * Class to store the data of a SensorEvent, and the associated sensor type.
 * 
 */

package ketai.sensors;

import processing.core.PApplet;
import android.hardware.SensorEvent;

/**
 * Abstraction class for all sensor events
 * 
 */
public class SensorData {
	public int sensorType;
	public int accuracy;
	public long timestamp;
	public float[] values;
	
	SensorData(SensorEvent event) {
	  sensorType = event.sensor.getType();
	  accuracy = event.accuracy;
	  timestamp = event.timestamp;
	  values = new float[event.values.length];
	  PApplet.arrayCopy(event.values, values);	  
	}
}
