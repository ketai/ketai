/**
 * <p>Ketai Sensor Library for Android: http://KetaiProject.org</p>
 *
 * <p>Ketai Location Features:
 * <ul>
 * <li>Uses GPS location data (latitude, longitude, altitude (if available)</li>
 * <li>Updates if location changes by 1 meter, or every 10 seconds</li>
 * <li>If unavailable, defaults to system provider (cell tower or WiFi network location)</li>
 * </ul>
 * <p>Syntax:
 * <ul>
 * <li>onLocationEvent(latitude, longitude, altitude)</li>
 * <li>onLocationEvent(latitude, longitude)</li>
 * <li>onLocationEvent(latitude, longitude, altitude)</li>
 * </p>
 * <p>Updated: 2012-03-10 Daniel Sauter/j.duran</p>
 */

import ketai.sensors.*; 

double longitude, latitude, altitude;
KetaiLocation location;

void setup() {
  orientation(LANDSCAPE);
  textAlign(CENTER, CENTER);
  textSize(36);
}

void draw() {
  background(78, 93, 75);
  if (location.getProvider() == "none")
    text("Location data is unavailable. \n" +
      "Please check your location settings.",  0, 0, width, height);
  else
    text("Latitude: " + latitude + "\n" + 
      "Longitude: " + longitude + "\n" + 
      "Altitude: " + altitude + "\n" + 
      "Provider: " + location.getProvider(),  0, 0, width, height);  
  // getProvider() returns "gps" if GPS is available
  // otherwise "network" (cell network) or "passive" (WiFi MACID)
}

void onResume()
{
  location = new KetaiLocation(this);
  super.onResume();
}


void onLocationEvent(double _latitude, double _longitude, double _altitude)
{
  longitude = _longitude;
  latitude = _latitude;
  altitude = _altitude;
  println("lat/lon/alt: " + latitude + "/" + longitude + "/" + altitude);
}

