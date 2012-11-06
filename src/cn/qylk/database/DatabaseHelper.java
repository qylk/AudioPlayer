package cn.qylk.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	public DatabaseHelper(Context ct) {
		super(ct, "his.db", null, 3);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE his(id INTEGER PRIMARY KEY AUTOINCREMENT,artist TEXT,pic_flag INTEGER DEFAULT -1)");
		// db.execSQL("CREATE TABLE history(id INTEGER PRIMARY KEY AUTOINCREMENT,musicid INTEGER,lastplaytime LONG)");
		// db.execSQL("CREATE TABLE love(id INTEGER PRIMARY KEY AUTOINCREMENT,musicid INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS his");
		onCreate(db);
	}
}
