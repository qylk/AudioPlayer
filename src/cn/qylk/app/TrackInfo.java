package cn.qylk.app;

import android.util.Log;

/**
 * 歌曲相关信息
 * @author qylk2012
 *
 */
public class TrackInfo implements Cloneable {
	public int id, duration, size;
	public String title, artist, path, album, mimetype, year;
 
	@Override
	public TrackInfo clone() {
		try { 
			return (TrackInfo) super.clone();
		} catch (CloneNotSupportedException e) {Log.e("TEST", "Info clone error");}
		return null;
	}
}
