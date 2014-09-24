
/*
  The following code allows the sketch to handle all NFC events
 when it is running.  Eventually we would like to handle this
 in a more elegant manner for now cut'n'paste will suffice.  
 */
//====================================================================
public void onCreate(Bundle savedInstanceState) { 
  ketaiNFC = new KetaiNFC(this);
  super.onCreate(savedInstanceState);
}

public void onNewIntent(Intent intent) { 
  if (ketaiNFC != null)
    ketaiNFC.handleIntent(intent);
}

//====================================================================

