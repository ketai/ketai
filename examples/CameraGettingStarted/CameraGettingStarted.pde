/**
 * <p>Ketai Sensor Library for Android: http://ketai.org</p>
 *
 * <p>Ketai Camera Features:
 * <ul>
 * <li>Interface for built-in camera</li>
 * <li>Access camera flash</li>
 * </ul>
 * <p>Updated: 2017-09-01 Daniel Sauter/j.duran</p>
 */

import ketai.camera.*;

KetaiCamera cam;

void setup() {
  fullScreen();
  orientation(LANDSCAPE);

  imageMode(CENTER);
  textAlign(CENTER, CENTER);
  textSize(displayDensity * 25);
  cam = new KetaiCamera(this, 640, 480, 24);
}

void draw() {

  if (cam != null && cam.isStarted())
    image(cam, width/2, height/2, width, height);
  else
  {
    background(128);
    text("Camera is currently off.", width/2, height/2);
  }
  drawUI();
}

void onCameraPreviewEvent()
{
  cam.read();
}

void mousePressed()
{
  //Toggle Camera on/off
  if (mouseX < width/3 && mouseY < 100)
  {
    if (cam.isStarted())
    {
      cam.stop();
    } else
      cam.start();
  }

  if (mouseX < 2*width/3 && mouseX > width/3 && mouseY < 100)
  {
    if (cam.getNumberOfCameras() > 1)
    {
      cam.setCameraID((cam.getCameraID() + 1 ) % cam.getNumberOfCameras());
    }
  }

  //Toggle Camera Flash
  if (mouseX > 2*width/3 && mouseY < 100)
  {
    if (cam.isFlashEnabled())
      cam.disableFlash();
    else
      cam.enableFlash();
  }
}

void drawUI()
{
  pushStyle();
  textAlign(LEFT);
  fill(0);
  stroke(255);
  rect(0, 0, width/3, 100);
  rect(width/3, 0, width/3, 100);

  rect((width/3)*2, 0, width/3, 100);

  fill(255);
  if (cam.isStarted())
    text("Camera Off", 5, 80); 
  else
    text("Camera On", 5, 80); 

  if (cam.getNumberOfCameras() > 0)
  {
    text("Switch Camera", width/3 + 5, 80);
  }

  if (cam.isFlashEnabled())
    text("Flash Off", width/3*2 + 5, 80); 
  else
    text("Flash On", width/3*2 + 5, 80); 

  popStyle();
}