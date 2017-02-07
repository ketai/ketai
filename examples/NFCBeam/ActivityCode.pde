
//public void onCreate(Bundle savedInstanceState) { 
  //super.onCreate(savedInstanceState);
public void resume() {  
  ketaiNFC = new KetaiNFC(this);
}

public void onNewIntent(Intent intent) { 
  if (ketaiNFC != null)
    ketaiNFC.handleIntent(intent);
}