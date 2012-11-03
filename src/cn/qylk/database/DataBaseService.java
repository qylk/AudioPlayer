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
	 * "infos"为table名列"info"记录歌手信息，String型列"bio_flag"记录歌手信息下载历史，int型
	 * *列“pic_flag”记录图片下载历史，int型
	 */
	public static final int BIO_NULL = 1;
	public static final int BIO_SUC = 4;
	public static final int BIO_UNSET = 0;
	private static Context context = APP.getInstance();
	private static SQLiteDatabase db = new DatabaseHelper(context)
			.getWritableDatabase();
	public static final int PIC_NULL = 2;
	public static final int PIC_UNSET = -1;

	/**
	 * 查询标志记录
	 * 
	 * @param artist
	 * @return maybe {@link #BIO_NULL}、{@link #BIO_SUC}、{@link #BIO_UNSET}
	 */
	public static int BIOHistoryQuery(String artist) {
		Cursor cursor = db.rawQuery(
				"SELECT bio_flag FROM infos WHERE artist=?",
				new String[] { artist });
		int bioflag = BIO_UNSET;
		if (cursor.moveToFirst())
			bioflag = cursor.getInt(0);
		cursor.close();
		return bioflag;
	}

	/**
	 * 删除记录
	 * 
	 * @param artist
	 */
	public static synchronized void del(String artist) {// 删除
		db.execSQL("DELETE FROM infos WHERE artist=" + artist);
	}

	/**
	 * 查询歌手信息
	 * 
	 * @param artist
	 * @return 歌手信息
	 */
	public static String GetArtistInfo(String artist) {// 查询
		Cursor cursor = db.rawQuery("SELECT info FROM infos WHERE artist=?",
				new String[] { artist });
		String info = "无";
		if (cursor.moveToFirst())
			info = cursor.getString(0);
		cursor.close();
		return info;
	}

	/**
	 * 查询图片下载历史记录，确定是否下载
	 */
	public static boolean PICIsNotNull(String artist) {
		Cursor cursor = db.rawQuery(
				"SELECT pic_flag FROM infos WHERE artist=?",
				new String[] { artist });
		int picflag = PIC_UNSET;
		if (cursor.moveToFirst())
			picflag = cursor.getInt(0);
		cursor.close();
		return picflag == PIC_UNSET;
	}

	/**
	 * 记录歌手信息
	 * 
	 * @param artist
	 * @param info
	 *            maybe null if code!=BIO_SUC
	 */
	public static synchronized void RecordBio(String artist, String info,
			boolean suc) {
		ContentValues cv = new ContentValues();
		cv.put("bio_flag", suc ? BIO_SUC : BIO_NULL);
		if (info != null && suc)
			cv.put("info", info);
		int affectedrow = db.update("infos", cv, "artist=?",
				new String[] { artist });
		if (affectedrow == 0) {
			cv.put("artist", artist);
			db.insert("infos", null, cv);
		}
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