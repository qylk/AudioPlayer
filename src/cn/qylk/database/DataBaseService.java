package cn.qylk.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.qylk.app.APP;

/**
 * @author qylk2012
 */
public class DataBaseService {
	/*
	 * *列“pic_flag”记录图片下载历史，int型
	 */
	private static Context context = APP.getInstance();
	private static SQLiteDatabase db = new DatabaseHelper(context)
			.getWritableDatabase();
	public static final int PIC_NULL = 2;
	public static final int PIC_UNSET = -1;

	/**
	 * 删除记录
	 * 
	 * @param artist
	 */
	public static synchronized void del(String artist) {// 删除
		db.execSQL("DELETE FROM his WHERE artist=" + artist);
	}

	/**
	 * 查询图片下载历史记录，确定是否下载
	 */
	public static boolean PICIsNotNull(String artist) {
		Cursor cursor = db.rawQuery("SELECT pic_flag FROM his WHERE artist=?",
				new String[] { artist });
		int picflag = PIC_UNSET;
		if (cursor.moveToFirst()) {
			picflag = cursor.getInt(0);
		}
		cursor.close();
		return picflag == PIC_UNSET;
	}

	/**
	 * 记下云端无此歌手图片，下次不再访问云端
	 * 
	 * @param artist
	 */
	public static synchronized void RecordPICNULL(String artist) {
		ContentValues cv = new ContentValues();
		cv.put("pic_flag", PIC_NULL);
		int affectredow = db.update("infos", cv, "artist=?",
				new String[] { artist });
		if (affectredow == 0) {
			cv.put("artist", artist);
			db.insert("infos", null, cv);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		db.close();
		super.finalize();
	}
}