package cn.qylk.media;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;

import LastFm.JsonParser;
import LastFm.Lastfm;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import cn.qylk.app.APP;
import cn.qylk.app.TrackInfo;
import cn.qylk.database.DataBaseService;
import cn.qylk.douban.douban;
import cn.qylk.utils.BitmapUtils;
import cn.qylk.utils.SDFileWriter;
import cn.qylk.utils.StringUtils;
import cn.qylk.utils.WebUtils;

/**
 * 歌曲相关
 * 
 * @author qylk2012
 */
public class ArtistInfo {
	public native static boolean ApicFromTag(String artist, String path);// 从tag标签中获取歌词（JNI调用）

	private static String fetchSummary(TrackInfo track) {
		try {
			String id = douban.TrackSearch(track.artist, track.title);
			if (id != null) {
				String smy = douban.LoadSummary(id);
				StringUtils.WriteStringToDisk(smy,
						StringUtils.GetInfosPath(track.title));
				return smy;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 歌手生平信息
	 * 
	 * @param artist
	 * @return
	 */
	public static String TryToGetInfo(TrackInfo track) {
		if (track.artist.contains("unknow"))
			return "未知";
		File summaryfile = StringUtils.GetInfosPath(track.title);
		File summaryfile2 = StringUtils.GetInfosPath(track.artist);
		if (summaryfile.exists()) {
			return StringUtils.ReadFileContent(summaryfile);
		} else if (summaryfile2.exists()) {
			return StringUtils.ReadFileContent(summaryfile2);
		} else {
			String sumary = fetchSummary(track);
			if (sumary != null)
				return sumary;
			else
				try {
					sumary = JsonParser
							.ParseInfo(Lastfm.ArtistInfoSearch(track.artist),
									track.artist);
					StringUtils.WriteStringToDisk(sumary,
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
	 * @param artist
	 * @param title
	 * @param fpath
	 *            歌曲路径，必要时使用其tag，获取图片
	 * @param size
	 * <br>
	 *            {@link BitmapUtils#LARGE} 高分辨率原始图片<br>
	 *            {@link BitmapUtils#MIDDLE} 中等分辨率图片，200px宽<br>
	 *            {@link BitmapUtils#SMALL} 低分辨率图标，50px宽<br>
	 * @param usenetwork
	 *            是否必要时使用网络下载
	 * @return Bitmap
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
			String url = JsonParser.ParsePICUrl(Lastfm.PICSearch(artist),
					artist);
			SDFileWriter.writePic(WebUtils.FetchFile(url), StringUtils
					.GetPICPath(artist).toString());
			return true;
		} catch (JSONException e1) {
			DataBaseService.RecordPICNULL(artist);
		} catch (IOException e2) {
		}
		return false;
	}
}
