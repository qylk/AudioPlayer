package cn.qylk.douban;

import java.io.IOException;
import java.net.URLEncoder;

import org.json.JSONException;

import cn.qylk.app.TrackInfo;
import cn.qylk.utils.FileHelper;
import cn.qylk.utils.StringUtils;
import cn.qylk.utils.WebUtils;

/**
 * @author qylk2012<div> all rights resolved
 * 
 */
public class douban {
	private static final String SEARCH = "https://api.douban.com/v2/music/search?";
	private static final String SUMMARY = "https://api.douban.com/v2/music/";

	private String BuildSearchUrl(String art, String tra) {
		return SEARCH + "&q=" + art + "%20" + tra + "&count=1";
	}

	private String BuildSummaryUrl(String id) {
		return SUMMARY + id;
	}

	/**
	 * 获取歌曲-艺术家信息,豆瓣网支持
	 * 
	 * @param track
	 * @return mabe null
	 */
	public String fetchSummary(TrackInfo track) {
		try {
			douban dou = new douban();
			String id = dou.TrackSearch(track.artist, track.title);
			if (id != null) {
				String smy = dou.LoadSummary(id);
				new FileHelper().WriteFile(smy,
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
	 * 返回解析结果
	 * @param id
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	private String LoadSummary(String id) throws IOException, JSONException { // 访问api
		String raw = WebUtils.GetContent(BuildSummaryUrl(id), "UTF-8");
		return JsonParser.ParseSummary(raw);
	}

	/**
	 * 信息检索
	 * @param artist
	 * @param track
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	private String TrackSearch(String artist, String track) throws IOException,
			JSONException {
		String art = URLEncoder.encode(artist, "UTF-8");
		String tra = URLEncoder.encode(track, "UTF-8");
		String raw = WebUtils.GetContent(BuildSearchUrl(art, tra), "UTF-8");
		return JsonParser.ParseId(raw, artist);
	}
}
