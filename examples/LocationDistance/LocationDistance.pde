/**
 * <p>Ketai Sensor Library for Android: http://Ketai.org</p>
 *
 * <p>KetaiLocation Features:
 * <ul>
 * <li>Uses GPS location data (latitude, longitude, altitude (if available)</li>
 * <li>Updates if location changes by 1 meter, or every 10 seconds</li>
 * <li>If unavailable, defaults to system provider (cell tower or WiFi network location)</li>
 * </ul>
 * More information:
 * http://developer.android.com/reference/android/location/Location.html</p>
 *
 * <p>Updated: 2017-08-29 Daniel Sauter/j.duran</p>
 */

import ketai.sensors.*; 
import android.location.Location.*;

double longitude, latitude, altitude, accuracy;
KetaiLocation location;
Location uic;

void setup() {
  fullScreen();
  //creates a location object that refers to UIC
  fullScreen();
  orientation(LANDSCAPE);
  
  location = new KetaiLocation(this);
  
  uic = new Location("uic"); // Example location: the University of Illinois at Chicago
  uic.setLatitude(41.874698);
  uic.setLongitude(-87.658777);

  textAlign(CENTER, CENTER);

  textSize(36*displayDensity);

}
void draw() {
  background(78, 93, 75);
  if (location == null || location.getProvider() == "none")
    text("Location data is unavailable. \n" +
      "Please check your location settings.", 0, 0, width, height);
  else
    text("Location data:\n" + 
      "Latitude: " + latitude + "\n" + 
      "Longitude: " + longitude + "\n" + 
      "Altitude: " + altitude + "\n" +
      "Accuracy: " + accuracy + "\n" +
      "Distance to UIC: "+ location.getLocation().distanceTo(uic) + " m\n" + 
      "Provider: " + location.getProvider(), 20, 0, width, height);
}

void onLocationEvent(Location _location)
{
  //print out the location object
  println("onLocation event: " + _location.toString());
  longitude = _location.getLongitude();
  latitude = _location.getLatitude();
  altitude = _location.getAltitude();
  accuracy = _location.getAccuracy();
}