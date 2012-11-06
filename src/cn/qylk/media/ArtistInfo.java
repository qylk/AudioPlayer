package cn.qylk.media;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import cn.qylk.LastFm.JsonParser;
import cn.qylk.LastFm.Lastfm;
import cn.qylk.app.APP;
import cn.qylk.app.TrackInfo;
import cn.qylk.database.DataBaseService;
import cn.qylk.douban.douban;
import cn.qylk.utils.BitmapUtils;
import cn.qylk.utils.FileHelper;
import cn.qylk.utils.StringUtils;
import cn.qylk.utils.WebUtils;

/**
 * 歌曲艺术家相关
 * 
 * @author qylk2012
 */
public class ArtistInfo {
	public native static boolean ApicFromTag(String artist, String path);// 从tag标签中获取歌词（JNI调用）

	/**
	 * 歌手生平信息
	 * 
	 * @param artist
	 * @return
	 */
	public static String TryToGetInfo(TrackInfo track) {
		if (track.artist.contains("unknow"))
			return "未知";
		File summaryfile = StringUtils.GetInfosPath(track.title);// 可能采用两种命名方法
		File summaryfile2 = StringUtils.GetInfosPath(track.artist);
		if (summaryfile.exists()) {
			return new FileHelper().ReadFile(summaryfile);
		} else if (summaryfile2.exists()) {
			return new FileHelper().ReadFile(summaryfile2);
		} else {
			String sumary = new douban().fetchSummary(track);
			if (sumary != null)
				return sumary;
			else
				try {
					sumary = JsonParser.ParseInfo(
							new Lastfm().ArtistInfoSearch(track.artist),
							track.artist);
					new FileHelper().WriteFile(sumary,
							StringUtils.GetInfosPath(track.artist));
					return sumary;
				} catch (JSONException e0) {// 解析异常
				} catch (IOException e) {
					return "错误";
				}
		}
		return "无";
	}

	/**
	 * 获取图片
	 * 
	 * @param track
	 * @param icon
	 *            返回小图标
	 * @return
	 */
	public static Bitmap TryToGetPic(TrackInfo track, boolean icon) {
		boolean local = false;
		String artist = track.artist;
		if (track.artist.contains("unknown")) {// 如果艺术家未知，就取歌曲名代替,并且跳过联网下载
			artist = track.title;
		}
		File path = StringUtils.GetPICPath(artist);
		if (path.exists())// 查询本地是否存在歌手图片
			local = true;
		else if (ApicFromTag(artist, track.path))
			local = true;
		else if (!icon && DataBaseService.PICIsNotNull(artist)
				&& APP.Config.PicDownloadEnable) {
			if (webFetchPic(artist))
				local = true;
		}
		if (local) {
			if (icon)
				return BitmapUtils.compress(path.toString());
			else
				return BitmapFactory.decodeFile(path.toString());
		}
		return null;
	}

	/**
	 * 下载图片
	 * 
	 * @param artist
	 * @return success?
	 */
	private static boolean webFetchPic(String artist) {
		try {
			String url = JsonParser.ParsePICUrl(new Lastfm().PICSearch(artist),
					artist);
			new FileHelper().WriteFile(WebUtils.FetchFile(url), StringUtils
					.GetPICPath(artist).toString());
			return true;
		} catch (JSONException e1) {
			DataBaseService.RecordPICNULL(artist);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		return false;
	}
}
