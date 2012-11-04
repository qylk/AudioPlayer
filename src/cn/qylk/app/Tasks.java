package cn.qylk.app;

import java.util.List;

import QianQianLyrics.LyricResults;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import cn.qylk.lrc.LRCbean;
import cn.qylk.lrc.MediaLyric;
import cn.qylk.media.ArtistInfo;

public class Tasks {
	private boolean isacting;

	private class InfoTask2 extends AsyncTask<Void, Void, String> {

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

	private class LocalLrcTask extends AsyncTask<Integer, Void, List<LRCbean>> {
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

	private class LrcSearchTask extends
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

	public interface onPostLrc {
		public void onLrcGot(List<LRCbean> lrc, boolean usedweb);
	}

	public interface onPostInfo {
		public void onInfoGot(String info);
	}

	public interface onPostLrcItems {
		public void onLrcSearchDone(List<LyricResults> items);
	}

	public interface onPostPic {
		public void onPicGot(Bitmap pic);
	}

	private class PicTask extends AsyncTask<Boolean, Void, Bitmap> {

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

	private onPostLrc postlrc;
	private onPostInfo postinfo;
	private onPostLrcItems postlrcsearch;

	private onPostPic postpic;

	public void startInfoTask2(onPostInfo pi) {
		if (isacting)
			return;
		postinfo = pi;
		isacting = true;
		new InfoTask2().execute();
	}

	public void startLrcSearchTask(onPostLrcItems pls) {
		postlrcsearch = pls;
		new LrcSearchTask().execute();
	}

	public void startLrcTask(onPostLrc pl, int id) {
		postlrc = pl;
		new LocalLrcTask().execute(id);
	}

	public void startPicTask(onPostPic pp, boolean icon) {
		postpic = pp;
		new PicTask().execute(icon);
	}

}
