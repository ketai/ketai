//void onCreate(Bundle savedInstanceState)
void resume()
{
  //super.onCreate(savedInstanceState);

  if(net == null)
    net = new KetaiWiFiDirect(this);    
}