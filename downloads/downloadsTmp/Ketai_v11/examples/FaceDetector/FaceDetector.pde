/**
 * <p>Ketai Library for Android: http://KetaiProject.org</p>
 *
 * <p>facedetector provides a face detector on android:
 * <ul>
 * <li>Wraps android face detection</li>
 * <li>Makes it available to any PImage</li>
 * </ul>
 * <p>Updated: 2012-05-18 Daniel Sauter/j.duran</p>
 */

import ketai.cv.facedetector.*;

PImage myImage;
int MAX_FACES = 20;
KetaiSimpleFace[] faces = new KetaiSimpleFace[MAX_FACES];

void setup()
{
  orientation(PORTRAIT);
  stroke(0, 255, 0);
  strokeWeight(2);
  noFill();
  rectMode(CENTER);
  noLoop();
  
  myImage = loadImage("faces.jpg");
}

void draw()
{
  faces = KetaiFaceDetector.findFaces(myImage, MAX_FACES); 

  image(myImage, 0, 0);
  
  for (int i=0; i < faces.length; i++)
  {
    //We only get the distance between the eyes so we base our bounding box off of that 
    rect(faces[i].location.x, faces[i].location.y, 2.5*faces[i].distance, 3*faces[i].distance);
  }
}

