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
 * <p>Updated: 2012-10-20 Daniel Sauter/j.duran</p>
 */
//The following are required for setup
import android.content.Intent;
import android.os.Bundle;

 
import ketai.net.nfc.*;

String textRead = "";
KetaiNFC ketaiNFC;

void setup()
{   
  orientation(LANDSCAPE);
  textAlign(CENTER, CENTER);
  textSize(36);
}

void draw()
{
  background(78, 93, 75);
  text("Last tag read:\n"+ textRead, width/2, height/2);
  text("<Touch tag to read>", width/2, height-35);
}

void onNFCEvent(String txt)
{
  textRead = txt;
}


//set our write string....
void mousePressed()
{
  ketaiNFC.write("" + millis());
}


//Press any key to cancel write
void keyPressed()
{
  ketaiNFC.cancelWrite();
}

