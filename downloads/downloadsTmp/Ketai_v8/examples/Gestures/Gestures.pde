import ketai.ui.*;

KetaiGesture gesture;
float Size = 10;
float Angle = 0;
PImage img;
ArrayList<Thing> things = new ArrayList<Thing>();

void setup()
{
  orientation(LANDSCAPE);
  gesture = new KetaiGesture(this);
  img = loadImage("toci.jpg");
  textSize(32);
  textAlign(CENTER);
  imageMode(CENTER);
}

void draw()
{
  background(128);
  pushMatrix();
  translate(width/2, height/2);
  rotate(Angle);
  image(img, 0, 0, Size, Size);
  popMatrix();
  
  //if we have things lets reverse through them 
  //  so we can delete dead ones and draw live ones
  if (things.size() > 0)
    for (int i = things.size()-1; i >= 0; i--)
    {
      Thing t = things.get(i);
      if (t.isDead())
        things.remove(t);
      else
        t.draw();
    }
}

void onDoubleTap(float x, float y)
{
  things.add(new Thing("DOUBLE", x, y));
}

void onTap(float x, float y)
{
  things.add(new Thing("SINGLE", x, y));
}

void onLongPress(float x, float y)
{
  things.add(new Thing("LONG", x, y));
}

//the coordinates of the start of the gesture, 
//     end of gesture and velocity in pixels/sec
void onFlick( float x, float y, float px, float py, float v)
{
  things.add(new Thing("FLICK:"+v, x, y, px, py));
}

void onPinch(float x, float y, float d)
{
  Size = constrain(Size+d, 10, 2000);
}

void onRotate(float x, float y, float ang)
{
  Angle += ang;
}

//these still work if we forward MotionEvents below
void mouseDragged()
{
}

void mousePressed()
{}


public boolean surfaceTouchEvent(MotionEvent event) {

  //call to keep mouseX, mouseY, etc updated
  super.surfaceTouchEvent(event);

  //forward event to class for processing
  return gesture.surfaceTouchEvent(event);
}

