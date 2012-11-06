package cn.qylk.QianQianLyrics;

import java.io.IOException;
import java.util.List;
import cn.qylk.app.TrackInfo;
import cn.qylk.utils.WebUtils;

public class QianQianLyrics {
	private static String DownloadUrl = "http://ttlrcct2.qianqian.com/dll/lyricsvr.dll?dl?Id=%s&Code=%s";
	private QianQianEncoding coding;
	private static String SearchUrl = "http://ttlrcct2.qianqian.com/dll/lyricsvr.dll?sh?Artist=%s&Title=%s&Flags=0";

	public QianQianLyrics() {
		coding = new QianQianEncoding();
	}

	/**
	 * 千千静听歌词下载
	 * 
	 * @param entry
	 * @param lrcid
	 * @return
	 */
	public String fetch(TrackInfo entry, int lrcid) {
		try {
			return WebUtils.GetContent(getDownloadUrl(entry, lrcid), "UTF-8");
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 根据歌词id获取下载地址
	 * 
	 * @param entry
	 * @param lrcid
	 * @return
	 */
	private String getDownloadUrl(TrackInfo entry, int lrcid) {
		String code = coding.CreateQianQianCode(entry.artist, entry.title,
				lrcid);
		return String.format(DownloadUrl, lrcid, code);
	}

	/**
	 * 千千静听歌词搜索
	 * 
	 * @param artist
	 * @param track
	 * @return maybe null
	 */
	public List<LyricResults> getList(TrackInfo entry) {
		try {
			return QianQianParser.parseXml(search(entry));
		} catch (IOException e) {
			return null;
		}
	}

	private String getSearchUrl(TrackInfo entry) {
		return String.format(SearchUrl,
				coding.str2HexStr(entry.artist, "UTF-16LE"),
				coding.str2HexStr(entry.title, "UTF-16LE"));
	}

	public String search(TrackInfo entry) throws IOException {
		return WebUtils.GetContent(getSearchUrl(entry), "UTF-8");
	}
}
