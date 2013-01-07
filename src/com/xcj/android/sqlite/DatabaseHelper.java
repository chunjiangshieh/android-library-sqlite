package com.xcj.android.sqlite;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


public abstract class DatabaseHelper extends SQLiteOpenHelper{
	
	//默认的DB路径
	private static String DB_PATH = "/data/data/com.xcj.android.sqlite/databases/";
    //默认的DB名称
	private static String DB_NAME = "test.db";
//	默认的DB版本
//    private static int DB_VERSION = 1;
    
    
    
    private SQLiteDatabase myDataBase; 
    private final Context myContext;
    
    public DatabaseHelper(Context context,
    		String dbName,
    		int dbVersion) {
     	super(context, dbName, null, dbVersion);
     	DB_NAME = dbName;
//     	DB_VERSION = dbVersion;
        this.myContext = context;
        if(!isCreateDB()){
        	try {
				createDataBase();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }	
     
    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
    	boolean dbExist = checkDataBase();
    	if(dbExist){
    		//do nothing - database already exist
    	}else{
    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
       	try {
    			copyDataBase();
    		} catch (IOException e) {
         		throw new Error("Error copying database");
         	}
    	}
     }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
     	SQLiteDatabase checkDB = null;
     	try{
     		if(getDBPath() != null && !getDBPath().equals("")){
     			DB_PATH = getDBPath();
     		}
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
     	}catch(SQLiteException e){
     		//database does't exist yet.
     		throw new Error("Database does't exist yet.");
     	}
     	if(checkDB != null){
     		checkDB.close();
     	}
     	return checkDB != null ? true : false;
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
     	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
     	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
     	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
     	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
     	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
     }

    @Override
	public synchronized void close() {
     	    if(myDataBase != null)
    		    myDataBase.close();
     	    super.close();
 	}
 
    @Override
	public void onCreate(SQLiteDatabase db) {
//    	createTableA(db);
//    	createTableB(db);
    	if(isCreateDB()){
    	   	createTables(db);
    	}
 	}
	
    @Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//    	dropTableA(db);
//    	dropTableB(db);
//    	createTableA(db);
//    	createTableB(db);
    	if(isCreateDB()){
    		dropTables(db);
        	createTables(db);
    	}
 	}

    public void openDataBase() throws SQLException{
		if(getDBPath() != null && !getDBPath().equals("")){
 			DB_PATH = getDBPath();
 		}
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    }
    
    
//    private void createTableA(SQLiteDatabase db){
//		StringBuilder buf = new StringBuilder();
//		buf.append("CREATE TABLE ");
//		buf.append(DBDicTableA.TABLE_NAME);
//		buf.append(" (");
//		buf.append(DBDicTableA.COLUMN_ID);
//		buf.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
//		buf.append(DBDicTableA.COLUMN_NAME);
//		buf.append(" TEXT NOT NULL,");
//		buf.append(DBDicTableA.COLUMN_TYPE);
//		buf.append(" INTEGER);");
//		db.execSQL(buf.toString());
//	}
	
//	private void createTableB(SQLiteDatabase db){
//		StringBuilder buf = new StringBuilder();
//		buf.append("CREATE TABLE ");
//		buf.append(DBDicTableB.TABLE_NAME);
//		buf.append(" (");
//		buf.append(DBDicTableB.COLUMN_ID);
//		buf.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
//		buf.append(DBDicTableB.COLUMN_NAME);
//		buf.append(" TEXT NOT NULL,");
//		buf.append(DBDicTableB.COLUMN_AID);
//		buf.append(" INTEGER,");
//		buf.append(DBDicTableB.COLUMN_TYPE);
//		buf.append(" INTEGER);");
//		db.execSQL(buf.toString());
//	}
	
//	private void dropTableA(SQLiteDatabase db){
//		String sql = "DROP TABLE IF EXISTS " + DBDicTableA.TABLE_NAME;
//		db.execSQL(sql);
//	}
	
//	private void dropTableB(SQLiteDatabase db){
//		String sql = "DROP TABLE IF EXISTS " + DBDicTableB.TABLE_NAME;
//		db.execSQL(sql);
//	}
	
	/**
	 * 是否是创建表
	 * true：执行createTables和dropTables函数
	 * false: 执行createDataBase（Copy DB 文件）
	 * @return
	 */
	protected abstract boolean isCreateDB();
	
	/**
	 * 创建数据库表
	 * @param db
	 */
	protected abstract void createTables(SQLiteDatabase db);
	
	/**
	 * 删除数据库表
	 * @param db
	 */
	protected abstract void dropTables(SQLiteDatabase db);
	
	/**
	 * 获取数据库路径
	 * @return
	 */
	protected abstract String getDBPath();
	
}
