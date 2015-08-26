/**
 * <p>Ketai Sensor Library for Android: http://KetaiProject.org</p>
 *
 * <p>Ketai Camera Features:
 * <ul>
 * <li>Interface for built-in camera</li>
 * <li>TODO: fix HACK of camera registration that currently exceptions in setup() at the moment.</li>
 * </ul>
 * <p>Updated: 2012-10-21 Daniel Sauter/j.duran</p>
 */

import ketai.camera.*;

KetaiCamera cam;

void setup() {
  orientation(LANDSCAPE);
  imageMode(CENTER);
  textSize(45);
}

void draw() {
  
  if(cam != null && cam.isStarted())
    image(cam, width/2, height/2, width, height);
  else
    {
      background(128);
      text("Waiting for camera....touch to activate", 100, height/2);
    }
}

void onCameraPreviewEvent()
{
  cam.read();
}

// start/stop camera preview by tapping the screen
void mousePressed()
{
  //HACK: Instantiate camera once we are in the sketch itself
  if(cam == null)
      cam = new KetaiCamera(this, 640, 480, 24);
      
  if (cam.isStarted())
  {
    cam.stop();
  }
  else
    cam.start();
}
void keyPressed() {
  if(cam == null)
    return;
    
  if (key == CODED) {
    if (keyCode == MENU) {
      if (cam.isFlashEnabled())
        cam.disableFlash();
      else
        cam.enableFlash();
    }
  }
}