void onCreate(Bundle savedInstanceState)
{
  super.onCreate(savedInstanceState);

  if(net == null)
    net = new KetaiWiFiDirect(this);    
}


