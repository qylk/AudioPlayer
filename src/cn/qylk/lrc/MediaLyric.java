package cn.qylk.lrc;

import java.io.File;
import java.util.List;

import cn.qylk.QianQianLyrics.LyricResults;
import cn.qylk.QianQianLyrics.QianQianLyrics;
import cn.qylk.app.APP;
import cn.qylk.app.TrackInfo;
import cn.qylk.utils.FileHelper;
import cn.qylk.utils.StringUtils;

/**
 * @author qylk2012 2012-01-01<br>
 *         all rights resolved
 */
public class MediaLyric {
	/**
	 * 删除本地歌词文件
	 * 
	 * @param title
	 */
	public static final void DelLrc(String title) {
		StringUtils.GetLyricPath(title).delete();
	}

	/**
	 * 千千静听歌词下载 2012-06-06
	 * 
	 * @param entry
	 * @param path
	 * @param id
	 * @return maybe null
	 */
	public static List<LRCbean> downLoadLyric(TrackInfo entry, File path, int id) {
		new FileHelper().WriteFile(new QianQianLyrics().fetch(entry, id), path,"GB2312");
		return LrcParser.ParseLrc(path);
	}

	/**
	 * 利用三种方法获取歌词，一一尝试直到成功获取，它们是：本地sd卡、网络、歌曲文件tag
	 * 
	 * @param entry
	 * @param id
	 *            if id > 0,will directly load it form Internet;else if id < 0
	 *            will try at most three method to get it
	 * @return maybe null
	 */
	public static List<LRCbean> FetchLyric(TrackInfo entry, int id) {// 检测本地歌词
		File path = StringUtils.GetLyricPath(entry.title);// app自建歌词目录
		if (id >= 0)
			return downLoadLyric(entry, path, id);// 知道lrcID，直接从网络加载
		else if (path.exists()) // app目录下查询
			return LrcParser.ParseLrc(path);
		else if (LyricFromTag(entry.title, entry.path)) {// 从tag中读取（JNI）
			return LrcParser.ParseLrc(path);
		} else if (APP.Config.lrcDownloadEnable) {
			List<LyricResults> results = SearchLyrics(entry);// 先搜索获取lrcID
			if (results != null)
				return FetchLyric(entry, results.get(0).id);// 再根据lrcID下载
		}
		return null;
	}

	public native static boolean LyricFromTag(String title, String path);// 从tag标签中获取歌词（JNI调用）

	/**
	 * 千千静听歌词搜索 2012-06-06
	 * 
	 * @param entry
	 * @return mabe null
	 */
	public static List<LyricResults> SearchLyrics(TrackInfo entry) {
		return new QianQianLyrics().getList(entry);
	}
}
