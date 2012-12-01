package cn.qylk.app;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.preference.PreferenceManager;
import cn.qylk.app.IPlayList.ListType;
import cn.qylk.database.MediaDatabase;

/**
 * 提供全局context
 * 
 */
public class APP extends Application {
	public static class Config {
		public static boolean desklrc;
		public static final String SDDIR = Environment
				.getExternalStorageDirectory().getPath();
		public static final String INFOSPATH = SDDIR + "/qylk/infos/";

		public static int lastbarekpoint;
		public static long lastcheck;
		public static boolean Library;

		public static boolean light;
		/**
		 * 日志
		 */
		public static final String LOGPATH = SDDIR + "/qylk/log/";
		public static int lrccolor;
		public static boolean lrcDownloadEnable;
		/**
		 * APP自建歌词目录
		 */
		public static final String LRCPATH = SDDIR + "/qylk/lrc/";
		public static boolean lrcshadow;
		public static boolean onlywifi;
		public static boolean PicDownloadEnable;
		/**
		 * 歌手图片目录
		 */
		public static final String PICPATH = SDDIR + "/qylk/pic/";

		public static boolean sdplunged;
		public static boolean shake;
		public static boolean unplunge;
		public static boolean visualwave;
		public static boolean wifi;
		public static short eq;

		/**
		 * 上一次的播放状态
		 * 
		 * @return
		 */
		public static ListTypeInfo getLast() {
			SharedPreferences mPerferences = PreferenceManager
					.getDefaultSharedPreferences(instance);
			int type = mPerferences.getInt("lasttype",
					ListType.ALLSONGS.ordinal());
			ListType listtype = ListType.values()[type];
			int para = mPerferences.getInt("lastpara", 0);
			int index = mPerferences.getInt("lastindex", 0);
			lastbarekpoint = mPerferences.getInt("lastbreak", 0);
			return new ListTypeInfo(listtype, para, null, index);
		}

		/**
		 * 加载程序配置
		 */
		public static void LoadConfig() {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) { // 检测是否加载了SD卡
				Config.sdplunged = true;
				// ----------------创建应用程序所需目录---------------------------
				new File(LRCPATH).mkdirs();
				new File(PICPATH).mkdir();
				new File(LOGPATH).mkdir();
				new File(INFOSPATH).mkdir();
			}
			Library = MediaDatabase.TestDB();
			ConnectivityManager cm = (ConnectivityManager) instance
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
					.isConnected();
			SharedPreferences mPerferences = PreferenceManager
					.getDefaultSharedPreferences(instance);
			light = mPerferences.getBoolean("light", false);
			lrcDownloadEnable = mPerferences.getBoolean("lyric", true);
			lrcshadow = mPerferences.getBoolean("lrcshadow", false);
			unplunge = mPerferences.getBoolean("unpluge", true);
			shake = mPerferences.getBoolean("shake", false);
			PicDownloadEnable = mPerferences.getBoolean("pic", false);
			onlywifi = mPerferences.getBoolean("wifimode", false);
			desklrc = mPerferences.getBoolean("desklrc", false);
			visualwave = mPerferences.getBoolean("eqpanel", true);
			if (onlywifi)
				PicDownloadEnable &= wifi;
			lastcheck = mPerferences.getLong("lastcheck", 0);
			lrccolor = mPerferences.getInt("lrccolor", Color.GREEN);
			eq=(short) mPerferences.getInt("eq", 0);
		}

		/**
		 * 记录播放状态
		 * 
		 * @param pos
		 */
		public static void StoreLast(int pos) {
			lastbarekpoint = pos;
			SharedPreferences mPerferences = PreferenceManager
					.getDefaultSharedPreferences(APP.getInstance());
			Editor editor = mPerferences.edit();
			ListTypeInfo info = list.getTypeInfo();
			editor.putInt("lastindex", info.pos);
			editor.putInt("lastbreak", pos);
			editor.putInt("lasttype", info.list.ordinal());
			editor.putInt("lastpara", info.para);
			editor.commit();
		}
	}

	private static APP instance;

	public static PlayList list;

	public static APP getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		Config.LoadConfig();
		if (!Config.sdplunged || !Config.Library)
			return;
		list = new PlayList(Config.getLast());
		startService(new Intent(MyAction.INTENT_START_SERVICE)); // 启动服务
		if (System.currentTimeMillis() - APP.Config.lastcheck > 7 * 24 * 3600 * 1000)
			new Update().start(this);
	}
}
