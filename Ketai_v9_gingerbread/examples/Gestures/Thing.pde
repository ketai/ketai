class Thing
{
  PVector location;
  PVector plocation;
  String  mText="";
  float life = frameRate * 3;

  public Thing(String _text, float x, float y)
  {
    mText = _text;
    location = new PVector(x, y);
  }

  public Thing(String _text, float x, float y, float px, float py)
  {
    mText = _text;
    location = new PVector(x, y);
    plocation = new PVector(px, py);
  }

  public void draw()
  {
    pushStyle();
    stroke(255);
    fill(255);
    life--;
    if (life > 0)
      text(mText, location.x, location.y);

    if (plocation != null)
      line(location.x, location.y, plocation.x, plocation.y);
    popStyle();
  }

  public boolean isDead()
  {
    return(life <= 0);
  }
}

