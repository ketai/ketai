/**
 * <p>Ketai Sensor Library for Android: http://Ketai.org</p>
 *
 * <p>KetaiSensor Features:
 * <ul>
 * <li>handles incoming Sensor Events</li>
 * <li>Includes Accelerometer, Magnetometer, Gyroscope, GPS, Light, Proximity</li>
 * <li>Use KetaiNFC for Near Field Communication</li>
 * </ul>
 * <p>Updated: 2017-08-29 Daniel Sauter/j.duran</p>
 */

import ketai.sensors.*;

KetaiSensor sensor;
float rotationX, rotationY, rotationZ;

void setup()
{
 fullScreen();
  sensor = new KetaiSensor(this);
  sensor.start();
  fullScreen();
  orientation(PORTRAIT);

  textAlign(CENTER, CENTER);
  textSize(displayDensity * 24);
}

void draw()
{
  background(78, 93, 75);
  text("Gyroscope: \n" + 
    "x: " + nfp(rotationX, 1, 3) + "\n" +
    "y: " + nfp(rotationY, 1, 3) + "\n" +
    "z: " + nfp(rotationZ, 1, 3), 0, 0, width, height);
}

void onGyroscopeEvent(float x, float y, float z)
{
  rotationX = x;
  rotationY = y;
  rotationZ = z;
}