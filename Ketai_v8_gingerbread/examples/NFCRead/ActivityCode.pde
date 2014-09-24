//The following are required for setup
import android.content.Intent;
import android.os.Bundle;

/*
  The following code allows the sketch to handle all NFC events
 when it is running.  Eventually we would like to handle this
 in a more elegant manner for now cut'n'paste will suffice.  
 */
//====================================================================
public void onCreate(Bundle savedInstanceState) { 
  super.onCreate(savedInstanceState);
  ketaiNFC = new KetaiNFC(this);
}

public void onNewIntent(Intent intent) { 
  if (ketaiNFC != null)
    ketaiNFC.handleIntent(intent);
}

void onResume()
{
  super.onResume();

  if (ketaiNFC != null)
    ketaiNFC.onResume();
}

public void onPause()
{
  super.onPause();
  if (ketaiNFC != null)
    ketaiNFC.onPause();
}

//====================================================================

