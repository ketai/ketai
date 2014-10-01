/**
 * <p>Ketai Sensor Library for Android: http://KetaiProject.org</p>
 *
 * <p>KetaiSQLite Features:
 * <ul>
 * <li>Captures Sensor data into SQLite database</li>
 * <li>Exports data into .csv flat file</li>
 * <li>Captures all sensors registered via SensorEvent into one db/file</li>
 * </ul>
 * <p>Updated: 2012-07-25 Daniel Sauter/j.duran</p>
 */

import ketai.data.*;
import ketai.sensors.*;

KetaiSensor sensor;
KetaiSQLite db;
Boolean isCapturing = false;

String CREATE_DB_SQL = "CREATE TABLE data ( time INTEGER PRIMARY KEY, x FLOAT NOT NULL, y FLOAT NOT NULL, z FLOAT NOT NULL);";

void setup()
{
  db = new KetaiSQLite(this);
  sensor = new KetaiSensor(this);
  frameRate(5);
  orientation(LANDSCAPE);
  textAlign(CENTER, CENTER);
  textSize(36);

  //lets make our table if it is the first time we're running 
  if ( db.connect() )
  {
    // for initial app launch there are no tables so we make one
    if (!db.tableExists("data"))
      db.execute(CREATE_DB_SQL);
  }
  sensor.start();
}

void draw() {
  background(78, 93, 75);
  // Status and data count
  if (isCapturing)
    text("Recording Accellerometer Data...\n(touch screen to stop)", width/2, height/4);
  else
  {
    plotData();
    text("Visualizing last " + width + " point(s) of data.", width/2, height/4);
  } 
  text("Current Data count: " + db.getDataCount(), width/2, height-height/4);
}

void mousePressed()
{
  if (isCapturing)
    isCapturing = false;
  else
    isCapturing = true;
}
/*
      collect accelerometer data and save it to the database
*/
void onAccelerometerEvent(float x, float y, float z, long time, int accuracy)
{
  if (db.connect() && isCapturing)
  {
    if (!db.execute("INSERT into data (`time`,`x`,`y`,`z`) VALUES ('"+System.currentTimeMillis()+"', '"+x+"', '"+y+"', '"+z+"')"))
      println("Failed to record data!" );
  }
}

void plotData()
{
  if (db.connect())
  {
    pushStyle();
    noStroke();
    db.query( "SELECT * FROM data ORDER BY time DESC LIMIT " + width );
    int  i = 0;   
    long mymin = Long.parseLong(db.getFieldMin("data", "time"));
    long mymax = Long.parseLong(db.getFieldMax("data", "time"));
    while (db.next ())
    {

      float x = db.getFloat("x");
      float y = db.getFloat("y");
      float z = db.getFloat("z");
      long  t = db.getLong("time");
      int plotx = (int)maplong(t, mymin, mymax, 0, width);
      
      fill(255, 0, 0);
      ellipse(plotx, map(x, -30, 30, 0, height), 5, 5);
      fill(0, 255, 0);
      ellipse(i, map(y, -30, 30, 0, height), 5, 5);
      fill(0, 0, 255);
      ellipse(i, map(z, -30, 30, 0, height), 5, 5);

      i++;
    }
    popStyle();
  }
}

long maplong(long value, long istart, long istop, long ostart, long ostop) {
  long divisor = istop - istart;
  return (ostart + (ostop - ostart) * (value - istart) / divisor);
}

