
import ketai.data.*;

KetaiSQLite db;
String CREATE_DB_SQL = "CREATE TABLE data ( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, age INTEGER NOT NULL DEFAULT '0');";

void setup()
{
  db = new KetaiSQLite( this);  // open database file

  if ( db.connect() )
  {
    // for initial app launch there are no tables so we make one
    if (!db.tableExists("data"))
      db.execute(CREATE_DB_SQL);

    println("data count for data table: "+db.getRecordCount("data"));

    //lets insert a random number or records
    int count = (int)random(1, 5);

    for (int i=0; i < count; i++)
      if (!db.execute("INSERT into data (`name`,`age`) VALUES ('person"+(int)random(0, 100)+"', '"+(int)random(1, 100)+"' )"))
        println("error w/sql insert");

    println("data count for data table after insert: "+db.getRecordCount("data"));

    // read all in table "table_one"
    db.query( "SELECT * FROM data" );

    while (db.next ())
    {
      println("----------------");
      print( db.getString("name") );
      print( "\t"+db.getInt("age") );
      println("\t"+db.getInt("foobar"));   //doesn't exist we get '0' returned
      println("----------------");
    }
  }
}

