import ketai.ui.*;

KetaiList selectionlist;
KetaiVibrate vibe;
ArrayList<String> colorlist = new ArrayList<String>();
color backgroundcolor = color(0, 0, 0);

void setup()
{
  orientation(LANDSCAPE);
  textSize(28);
  textAlign(CENTER);
  vibe = new KetaiVibrate(this);
  colorlist.add("Black");
  colorlist.add("Red");
  colorlist.add("Green");
  colorlist.add("Blue");
  colorlist.add("Gray");
  for(int i = 0; i < 20; i++)
    colorlist.add("Stub Entry " + i);
}


void draw()
{
  background(backgroundcolor);
  
  drawUI();
  text("click screen to change background color", width/2, height/2);
}


void mousePressed()
{
  if (mouseY < 100)
  {
    if (mouseX < width/3)
      KetaiKeyboard.toggle(this);
    else if (mouseX > width/3 && mouseX < width-(width/3))
      KetaiAlertDialog.popup(this, "Pop Up!", "this is a popup message box");
    else
      vibe.vibrate(1000);
  }
  else
    selectionlist = new KetaiList(this, colorlist);
}

void onKetaiListSelection(KetaiList klist)
{
  String selection = klist.getSelection();
  if (selection == "Black")
    backgroundcolor = color(0, 0, 0);
  else if (selection == "Red")
    backgroundcolor = color(255, 0, 0);
  else if (selection == "Green")
    backgroundcolor = color(0, 255, 0);
  else if (selection == "Blue")
    backgroundcolor = color(0, 0, 255);
  else if (selection == "Gray")
    backgroundcolor = color(128, 128, 128);
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
  text("Keyboard", 5, 60); 
  text("PopUp", width/3 + 5, 60); 
  text("Vibrate", width/3*2 + 5, 60); 
  popStyle();
}

