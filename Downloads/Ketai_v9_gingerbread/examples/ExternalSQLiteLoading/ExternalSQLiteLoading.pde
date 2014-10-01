/*
  This example illustrates how to load a pre-built database file so that it may be used in an android
    sketch.  
    
    Datasets come from : 
    (large)  - http://chinookdatabase.codeplex.com/
    (small)  - http://www.gaia-gis.it/spatialite-2.3.1/resources.html

*/

import ketai.data.*;

KetaiSQLite db;
KetaiSQLite db2;

void setup()
{
    //KetaiSQLite.load(context, filename, dbname) 
  if (KetaiSQLite.load(this, "large.sqlite", "large")) //loads an sql db file from the data folder
    println("loaded 1st db file!");
  
  if(KetaiSQLite.load(this, "sample.sqlite", "sample"))
    println("loaded 2nd db file!");
    

  db = new KetaiSQLite( this, "large");   
  db2 = new KetaiSQLite(this, "sample");

  if (db.connect())
  {
    String[] tables = db.getTables();
    for(int i = 0; i < tables.length; i++)
      println("table:\t" + tables[i] + " with " + db.getRecordCount(tables[i]) + " record(s)");
  }

  println("=----------------------------------------------");

  if(db2.connect())
  {
    String[] tables = db2.getTables();
    for(int i = 0; i < tables.length; i++)
    {
      println("table:\t" + tables[i] + " with " + db2.getRecordCount(tables[i]) + " record(s)");
    }      
  }
}

