package cn.qylk.app;

import java.util.List;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import cn.qylk.QianQianLyrics.LyricResults;
import cn.qylk.lrc.LRCbean;
import cn.qylk.lrc.MediaLyric;
import cn.qylk.media.ArtistInfo;

public class Tasks {
	static class InfoTask2 extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			return ArtistInfo.TryToGetInfo(APP.list.getTrackEntity());
		}

		@Override
		protected void onPostExecute(String result) {
			if (postinfo != null)
				postinfo.onInfoGot(result);
			isacting = false;
			super.onPostExecute(result);
		}
	}

	static class LocalLrcTask extends AsyncTask<Integer, Void, List<LRCbean>> {
		private boolean usedweb;

		@Override
		protected List<LRCbean> doInBackground(Integer... params) {
			usedweb = (params[0] >= 0);
			return MediaLyric.FetchLyric(APP.list.getTrackEntity(), params[0]);
		}

		@Override
		protected void onPostExecute(List<LRCbean> result) {
			super.onPostExecute(result);
			if (postlrc != null)
				postlrc.onLrcGot(result, usedweb);
		}
	}

	static class LrcSearchTask extends
			AsyncTask<Boolean, Void, List<LyricResults>> {

		@Override
		protected List<LyricResults> doInBackground(Boolean... params) {
			return MediaLyric.SearchLyrics(APP.list.getTrackEntity());
		}

		@Override
		protected void onPostExecute(List<LyricResults> result) {
			super.onPostExecute(result);
			if (postlrcsearch != null)
				postlrcsearch.onLrcSearchDone(result);
		}
	}

	public interface onPostInfo {
		public void onInfoGot(String info);
	}

	public interface onPostLrc {
		public void onLrcGot(List<LRCbean> lrc, boolean usedweb);
	}

	public interface onPostLrcItems {
		public void onLrcSearchDone(List<LyricResults> items);
	}

	public interface onPostPic {
		public void onPicGot(Bitmap pic);
	}

	static class PicTask extends AsyncTask<Boolean, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Boolean... params) {
			return ArtistInfo.TryToGetPic(APP.list.getTrackEntity(), params[0]);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (postpic != null)
				postpic.onPicGot(result);
		}
	}

	private static boolean isacting;

	private static onPostInfo postinfo;
	private static onPostLrc postlrc;
	private static onPostLrcItems postlrcsearch;
	private static onPostPic postpic;

	/**
	 * 开始执行获取艺术家信息的任务
	 * 
	 * @param pi
	 */
	public static void startInfoTask2(onPostInfo pi) {
		if (isacting)
			return;
		postinfo = pi;
		isacting = true;
		new InfoTask2().execute();
	}

	/**
	 * 开始执行搜索歌词的任务
	 * 
	 * @param pls
	 */
	public static void startLrcSearchTask(onPostLrcItems pls) {
		postlrcsearch = pls;
		new LrcSearchTask().execute();
	}

	/**
	 * 开始执行获取歌词的任务
	 * 
	 * @param pl
	 * @param id
	 *            千千歌词id，若还没有，置-1来计划下载
	 */
	public static void startLrcTask(onPostLrc pl, int id) {
		postlrc = pl;
		new LocalLrcTask().execute(id);
	}

	/**
	 * 开始执行获取图片的任务
	 * 
	 * @param pp
	 * @param icon
	 */
	public static void startPicTask(onPostPic pp, boolean icon) {
		postpic = pp;
		new PicTask().execute(icon);
	}

}
