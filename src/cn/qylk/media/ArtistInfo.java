package cn.qylk.media;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;

import LastFm.JsonParser;
import LastFm.Lastfm;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import cn.qylk.app.APP;
import cn.qylk.app.TrackInfo;
import cn.qylk.database.DataBaseService;
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

	public static String GetInfoFromDatabase(String artist) {
		return DataBaseService.GetArtistInfo(artist);
	}

	/**
	 * 歌手生平信息
	 * 
	 * @param artist
	 * @return
	 */
	public static boolean TryToGetInfo(String artist) {
		if (artist.contains("unknow"))
			return false;
		int resultcode = DataBaseService.BIOHistoryQuery(artist);
		if (resultcode == DataBaseService.BIO_UNSET
				&& APP.Config.BioDownloadEnable) {
			try {
				String info = JsonParser.ParseInfo(
						Lastfm.ArtistInfoSearch(artist), artist);
				DataBaseService.RecordBio(artist, info, true);
				return true;
			} catch (JSONException e0) {// 解析异常
				// 记录异常，下次不再尝试
				DataBaseService.RecordBio(artist, null, false);
			} catch (IOException e) {
			}
		}
		return resultcode == DataBaseService.BIO_SUC;
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
