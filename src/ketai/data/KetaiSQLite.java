/*
 * 
 */
package ketai.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import processing.core.PApplet;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;

/**
 * The Class KetaiSQLite provides access to the underlying SQLite database that 
 * 	every activity is provided with.
 */
public class KetaiSQLite {
	
	/** The database name. */
	private String DATABASE_NAME = "data";
	
	/** The Constant DATABASE_VERSION. */
	private static final int DATABASE_VERSION = 1;
	
	/** The data root directory. */
	private String DATA_ROOT_DIRECTORY = "_data";

	/** The context. */
	private Context context;
	
	/** The db. */
	private SQLiteDatabase db;
	
	/** The cursor. */
	private Cursor cursor;

	/** The sql statement. */
	private SQLiteStatement sqlStatement;

	/**
	 * Instantiates a new ketai sqlite object.
	 *
	 * @param context the context/PApplet/Activity using this class
	 */
	public KetaiSQLite(Context context) {
		this.context = context;
		DATABASE_NAME = context.getPackageName();
		DATA_ROOT_DIRECTORY = context.getPackageName();
		PApplet.println("data path"
				+ context.getDatabasePath(context.getPackageName())
						.getAbsolutePath());
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();

	}

	/**
	 * Instantiates a new ketai sqlite.
	 *
	 * @param context the context
	 * @param dbname the database name
	 */
	public KetaiSQLite(Context context, String dbname) {
		this.context = context;
		DATABASE_NAME = dbname;
		OpenHelper openHelper = new OpenHelper(this.context, dbname);
		this.db = openHelper.getWritableDatabase();
	}

	/**
	 * Load - loads an external .sql file into the local SQLite
	 * 			database service.  Useful for populating data
	 * 			for an activity to use.
	 *
	 * @param _context the _context
	 * @param filename the filename
	 * @param dbname the dbname
	 * @return true, if successful
	 */
	static public boolean load(Context _context, String filename, String dbname) {

		InputStream myInput;

		try {
			AssetManager assets = _context.getAssets();
			myInput = assets.open(filename);
			if (myInput == null)
				return false;

			String outFileName = _context.getDatabasePath(dbname)
					.getAbsolutePath();
			OutputStream myOutput = new FileOutputStream(outFileName);

			byte[] buffer = new byte[4096];
			int length;
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}

			// Close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();

			return true;

		} catch (FileNotFoundException e) {
			PApplet.println("Failed to load SQLite file(not found): "
					+ filename);
		} catch (IOException iox) {
			PApplet.println("IO Error in copying SQLite database " + filename
					+ ": " + iox.getMessage());
		}

		return false;
	}

	/**
	 * Gets the path of the database
	 *
	 * @return the path of the database
	 */
	public String getPath() {
		return this.db.getPath();
	}

	/**
	 * Gets the db from the sqlite service
	 *
	 * @return the db reference.
	 */
	public SQLiteDatabase getDb() {
		return this.db;
	}

	/**
	 * Connect to the database.
	 *
	 * @return true, if successful
	 */
	public boolean connect() {
		return db.isOpen();
	}

	/**
	 * Close the database.
	 */
	public void close() {
		if (db != null)
			db.close();
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		close();
	}

	/**
	 * Execute an SQL statement
	 *
	 * @param _sql the SQL statement to execute.
	 * @return true, if successful
	 */
	public boolean execute(String _sql) {
		try {
			db.execSQL(_sql);
			return true;
		} catch (SQLiteException x) {
			PApplet.println("Error executing sql statement: " + x.getMessage());
			return false;
		}
	}

	/**
	 * Query the database.
	 *
	 * @param _query the query string
	 * @return true, if successful
	 */
	public boolean query(String _query) {
		try {
			cursor = this.db.rawQuery(_query, null);
			return true;
		} catch (SQLiteException x) {
			PApplet.println("Error executing query: " + x.getMessage());
			return false;
		}
	}

	/**
	 * Next - move to the next object in our last result set.
	 *
	 * @return true, if successful
	 */
	public boolean next() {
		if (cursor == null)
			return false;
		return cursor.moveToNext();
	}

	/**
	 * Gets the double value from a column in our resultset
	 *
	 * @param _col the column/field index number
	 * @return the double
	 */
	public double getDouble(int _col) {
		if (_col < 0)
			return 0;

		return cursor.getDouble(_col);
	}

	/**
	 * Gets the double  value from a column in our resultset.
	 *
	 * @param field the field name
	 * @return the double
	 */
	public double getDouble(String field) {
		int i = cursor.getColumnIndex(field);
		return getDouble(i);
	}

	/**
	 * Gets the float value from a column in our resultset.
	 *
	 * @param _col the column index number
	 * @return the float
	 */
	public float getFloat(int _col) {
		if (_col < 0)
			return 0;

		return cursor.getFloat(_col);
	}

	/**
	 * Gets the float value from a column in our resultset.
	 *
	 * @param field the field mame
	 * @return the float
	 */
	public float getFloat(String field) {
		int i = cursor.getColumnIndex(field);
		return getFloat(i);
	}

	/**
	 * Gets the int value from a column index in our resultset.
	 *
	 * @param _col the column index number
	 * @return the int
	 */
	public int getInt(int _col) {
		if (_col < 0)
			return 0;

		return cursor.getInt(_col);
	}

	/**
	 * Gets the int value from a column in our resultset.
	 *
	 * @param field the field name
	 * @return the int
	 */
	public int getInt(String field) {
		int i = cursor.getColumnIndex(field);
		return getInt(i);
	}

	/**
	 * Gets the long value from a column in our resultset.
	 *
	 * @param _col the column/field index number
	 * @return the long
	 */
	public long getLong(int _col) {

		if (_col < 0)
			return 0;

		return cursor.getLong(_col);
	}

	/**
	 * Gets the long value from a column in our resultset.
	 *
	 * @param field the field/column name
	 * @return the long
	 */
	public long getLong(String field) {
		int i = cursor.getColumnIndex(field);
		return getLong(i);
	}

	/**
	 * Gets the blob value from a column in our resultset.
	 *
	 * @param _col the column/field index number
	 * @return the blob
	 */
	public byte[] getBlob(int _col) {
		if (_col < 0)
			return null;

		return cursor.getBlob(_col);
	}

	/**
	 * Gets the blob value from a column in our resultset.
	 *
	 * @param field the field/column name
	 * @return the blob
	 */
	public byte[] getBlob(String field) {
		int i = cursor.getColumnIndex(field);

		return getBlob(i);
	}

	/**
	 * Gets the string value from a column in our resultset.
	 *
	 * @param _col the column/field index number
	 * @return the string
	 */
	public String getString(int _col) {
		if (_col < 0)
			return null;
		return cursor.getString(_col);
	}

	/**
	 * Gets the string value from a column in our resultset.
	 *
	 * @param field the field/column name
	 * @return the string
	 */
	public String getString(String field) {
		int i = cursor.getColumnIndex(field);
		return getString(i);
	}

	/**
	 * Gets the table names in our database.
	 *
	 * @return the names of the tables in the database
	 */
	public String[] getTables() {
		String s = "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;";
		ArrayList<String> tables = new ArrayList<String>();
		try {
			Cursor cursor = this.db.rawQuery(s, null);
			if (cursor.moveToFirst()) {
				do {
					if (cursor.getString(0) != "android_metadata")
						tables.add(cursor.getString(0));
				} while (cursor.moveToNext());
			}
		} catch (SQLiteException x) {
			x.printStackTrace();
		}
		String[] strArray = new String[tables.size()];
		tables.toArray(strArray);

		return strArray;
	}

	/**
	 * Gets the field names
	 *
	 * @param table the table name
	 * @return the fields
	 */
	public String[] getFields(String table) {
		String s = "PRAGMA table_info(" + table + ");";
		ArrayList<String> fields = new ArrayList<String>();
		try {
			Cursor cursor = this.db.rawQuery(s, null);
			if (cursor.moveToFirst()) {
				do {
					fields.add(cursor.getString(1));
				} while (cursor.moveToNext());
			}
		} catch (SQLiteException x) {
			x.printStackTrace();
		}
		String[] strArray = new String[fields.size()];
		fields.toArray(strArray);

		return strArray;
	}

	/**
	 * Gets the field min value
	 *
	 * @param table the table name
	 * @param field the field name
	 * @return the field min value
	 */
	public String getFieldMin(String table, String field) {
		String q = "SELECT MIN(" + field + ") FROM " + table;
		this.sqlStatement = this.db.compileStatement(q);
		String c = this.sqlStatement.simpleQueryForString();
		if (c == null)
			return "0";
		return c;
	}

	/**
	 * Gets the field max value.
	 *
	 * @param table the table name
	 * @param field the field name
	 * @return the field max value
	 */
	public String getFieldMax(String table, String field) {

		String q = "SELECT MAX(" + field + ") FROM " + table;
		this.sqlStatement = this.db.compileStatement(q);
		String c = this.sqlStatement.simpleQueryForString();
		if (c == null)
			return "0";
		return c;
	}

	/**
	 * Gets the record count for the table.
	 *
	 * @param table the table name
	 * @return the record count
	 */
	public long getRecordCount(String table) {
		this.sqlStatement = this.db.compileStatement("SELECT COUNT(*) FROM "
				+ table);
		long c = this.sqlStatement.simpleQueryForLong();
		return c;
	}

	/**
	 * Gets the data count (records in all tables in the database)
	 *
	 * @return the data count
	 */
	public long getDataCount() {
		long count = 0;
		String tablename;
		try {
			Cursor cursor = this.db.rawQuery("select name from SQLite_Master",
					null);
			if (cursor.moveToFirst()) {
				do {
					tablename = cursor.getString(0);

					// skip the android-specific table in our count
					if (tablename.equals("android_metadata"))
						continue;
					this.sqlStatement = this.db
							.compileStatement("SELECT COUNT(*) FROM "
									+ tablename);
					long c = this.sqlStatement.simpleQueryForLong();
					count += c;

				} while (cursor.moveToNext());
			}

			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		} catch (SQLiteException x) {
			x.printStackTrace();
		}
		return count;
	}

	/**
	 * Check to see if the table exists.
	 *
	 * @param _table the table name
	 * @return true, if successful
	 */
	public boolean tableExists(String _table) {
		Cursor cursor = this.db
				.rawQuery("select name from SQLite_Master", null);
		if (cursor.moveToFirst()) {
			do {
				PApplet.println("DataManager found this table: "
						+ cursor.getString(0));
				if (cursor.getString(0).equalsIgnoreCase(_table))
					return true;
			} while (cursor.moveToNext());
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return false;
	}

	/**
	 * Export data to a text file (tab delimited).
	 *
	 * @param _targetDirectory the target directory
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void exportData(String _targetDirectory) throws IOException {

		String directory = String.valueOf(System.currentTimeMillis());

		// First make sure the target directory exists....
		File dir = new File(Environment.getExternalStorageDirectory(),
				DATA_ROOT_DIRECTORY + "/" + directory);
		if (!dir.exists()) {
			if (dir.mkdirs())
				PApplet.println("success making directory: "
						+ dir.getAbsolutePath());
			else {
				PApplet.println("Failed making directory. Check your sketch permissions or that your device is not connected in disk mode.");
				return;
			}
		}
		String tablename;
		int rowCount = 0;

		try {
			Cursor cursor = this.db.rawQuery("select name from SQLite_Master",
					null);
			if (cursor.moveToFirst() && cursor.getCount() > 0) {
				String row = "";
				do {
					tablename = cursor.getString(0);

					// skip the android-specific table in our count
					if (tablename.equals("android_metadata"))
						continue;
					Cursor c = this.db.rawQuery("SELECT * FROM " + tablename,
							null);

					if (c.moveToFirst()) {
						do {
							int i = c.getColumnCount();
							for (int j = 0; j < i; j++)
								row += c.getString(j) + "\t";
							row += "\n";
							rowCount++;
							if (rowCount > 100) {
								if (row.length() > 0)
									this.writeToFile(row,
											dir.getAbsolutePath(), tablename);
								row = "";
								rowCount = 0;
							}
						} while (c.moveToNext());
						writeToFile(row, dir.getAbsolutePath(), tablename);
						row = "";
						rowCount = 0;
					}
				} while (cursor.moveToNext());
			}

			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			deleteAllData();
		} catch (SQLiteException x) {
			x.printStackTrace();
		}
	}

	/**
	 * Delete all data in our database. Deletes all tables.
	 */
	public void deleteAllData() {
		String tablename;
		try {
			Cursor cursor = this.db.rawQuery("select name from SQLite_Master",
					null);
			if (cursor.moveToFirst()) {
				do {
					tablename = cursor.getString(0);

					// skip the android-specific table in our count
					if (tablename.equals("android_metadata"))
						continue;
					this.db.delete(tablename, null, null);
				} while (cursor.moveToNext());
			}

			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		} catch (SQLiteException x) {
			x.printStackTrace();
		}
	}

	/**
	 * Write to file.
	 *
	 * @param data the data
	 * @param _dir the _dir
	 * @param exportFileName the export file name
	 */
	private void writeToFile(String data, String _dir, String exportFileName) {
		try {
			PApplet.print(".");
			String fileToWrite = _dir + "/" + exportFileName + ".csv";
			FileWriter fw = new FileWriter(fileToWrite, true);
			BufferedWriter out = new BufferedWriter(fw);
			out.write(data);
			out.close();
			fw.close();
		} catch (Exception x) {
			PApplet.println("Error exporting data. ("
					+ x.getMessage()
					+ ") Check the sketch permissions or that the device is not connected in disk mode.");
		}
	}

	/**
	 * The Class OpenHelper.
	 */
	private class OpenHelper extends SQLiteOpenHelper {

		/**
		 * Instantiates a new open helper.
		 *
		 * @param context the context
		 */
		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/**
		 * Instantiates a new open helper.
		 *
		 * @param context the context
		 * @param dbname the dbname
		 */
		OpenHelper(Context context, String dbname) {
			super(context, dbname, null, DATABASE_VERSION);

		}

		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
		 */
		public void onCreate(SQLiteDatabase db) {
		}

		/* (non-Javadoc)
		 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
		 */
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
