void drawUI()
{
  //Draw top shelf UI buttons

  pushStyle();
  fill(0);
  stroke(255);
  rect(0, 0, width/3, 50);

  if (isConfiguring)
  {
    noStroke();
    fill(78, 93, 75);
  }
  else
    fill(0);

  rect(width/3, 0, width/3, 50);

  if (!isConfiguring)
  {  
    noStroke();
    fill(78, 93, 75);
  }
  else
  {
    fill(0);
    stroke(255);
  }
  rect((width/3)*2, 0, width/3, 50);

  fill(255);
  text("Keyboard", 5, 30); 
  text("WiFi Direct", width/3+5, 30); 
  text("Interact", width/3*2+5, 30); 

  popStyle();
}

//use event framework temporarily
public void keyEvent(processing.event.KeyEvent ke) {
  key = ke.getKey();
  keyCode = ke.getKeyCode();

  if (ke.getAction() == processing.event.KeyEvent.PRESSED)  //processing.event.KeyEvent.RELEASED
  {
    if (key == 'c')
    {
      if (devices.size() > 0)
        connectionList = new KetaiList(this, devices);
    }
    else if (key == 'd')
    {
      net.discover();
      println("device list contains "  + devices.size() + " elements");
    }
    else if (key == 'i')
      net.getConnectionInfo();
    else if (key == 'o')
    {
      if (net.getIPAddress().length() > 0)
        oscP5 = new OscP5(this, 12000);
    }
    else if (key == 'r')
    {
      if (oscP5 != null)
      {
        oscP5.stop();
        oscP5 = null;
      }
      net.reset();
      clientip = "";
    }
  }
}


//
//void keyPressed() {
//  if (key == 'c')
//  {
//    if (devices.size() > 0)
//      connectionList = new KetaiList(this, devices);
//  }
//  else if (key == 'd')
//  {
//    net.discover();
//    println("device list contains "  + devices.size() + " elements");
//  }
//  else if (key == 'i')
//    net.getConnectionInfo();
//  else if (key == 'o')
//  {
//    if (net.getIPAddress().length() > 0)
//      oscP5 = new OscP5(this, 12000);
//  }
//  else if (key == 'r')
//  {
//    if (oscP5 != null)
//    {
//      oscP5.stop();
//      oscP5 = null;
//    }
//    net.reset();
//    clientip = "";
//  }
//}

void onKetaiListSelection(KetaiList klist)
{
  String selection = klist.getSelection();
  println("CONNECTING FROM LIST TO: " + selection);
  net.connect(selection);
  //dispose of list for now  
  connectionList = null;
}

