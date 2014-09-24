/**
 * <p>Ketai Sensor Library for Android: http://KetaiProject.org</p>
 *
 * <p>Ketai NFC Features:
 * <ul>
 * <li>handles incoming Near Field Communication Events</li>
 * </ul>
 * <p>Note:
 * Add the following within the sketch activity to the AndroidManifest.xml:
 * 
 * <uses-permission android:name="android.permission.NFC" /> 
 *
 * <intent-filter>
 *   <action android:name="android.nfc.action.TECH_DISCOVERED"/>
 * </intent-filter>
 *
 * <intent-filter>
 *  <action android:name="android.nfc.action.NDEF_DISCOVERED"/>
 * </intent-filter>
 *
 * <intent-filter>
 *  <action android:name="android.nfc.action.TAG_DISCOVERED"/>
 *  <category android:name="android.intent.category.DEFAULT"/>
 * </intent-filter>
 *
 * </p> 
 * <p>Updated: 2012-10-21 Daniel Sauter/j.duran</p>
 */
//The following are required for setup
import android.content.Intent;
import android.os.Bundle;

import ketai.net.nfc.*;

String writeStatus = "";
KetaiNFC ketaiNFC;

void setup()
{   
  orientation(LANDSCAPE);
  textAlign(CENTER, CENTER);
  textSize(36);
  String d = "Ketai writing tag at: " + month()+"/"+day()+"/"+year()+" "+hour()+":"+minute()+":"+second();
  ketaiNFC.write(d);
}

void draw()
{
  background(78, 93, 75);
  text("<Touch tag to write message>\nLast Write Status: "    + writeStatus, width/2, height/2);
  //  if (frameCount % (int)frameRate == 0)
  //  {
  //    println("setting write.");
  //    String d = "Ketai writing tag at: " + month()+"/"+day()+"/"+year()+" "+hour()+":"+minute()+":"+second();
  //    ketaiNFC.write(d);
  //  }
}

void mousePressed()
{
  println("setting write.");
  String d = "Ketai writing tag at: " + month()+"/"+day()+"/"+year()+" "+hour()+":"+minute()+":"+second();
  ketaiNFC.write(d);
  writeStatus = "";
}

void onNFCWrite(boolean result, String message)
{
  if (result)
    writeStatus = "SUCCESS writing tag!";
  else
    writeStatus = message;
}

//Press any key to cancel write
void keyPressed()
{
  ketaiNFC.cancelWrite();
}

