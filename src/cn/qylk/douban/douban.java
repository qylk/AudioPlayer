package cn.qylk.douban;

import java.io.IOException;
import java.net.URLEncoder;

import org.json.JSONException;

import android.util.Log;

import cn.qylk.utils.WebUtils;

/**
 * @author qylk2012<div> all rights resolved
 * 
 */
public class douban {
	private static final String SEARCH = "https://api.douban.com/v2/music/search?";
	private static final String SUMMARY = "https://api.douban.com/v2/music/";

	public static String TrackSearch(String artist, String track)
			throws IOException, JSONException {
		String art = URLEncoder.encode(artist, "UTF-8");
		String tra = URLEncoder.encode(track, "UTF-8");
		String raw = WebUtils.GetContent(BuildSearchUrl(art, tra), "UTF-8");
		Log.i("TEST", raw);
		return JsonParser.ParseId(raw, artist);
	}

	private static String BuildSearchUrl(String art, String tra) {
		return SEARCH + "&q=" + art + "%20" + tra + "&count=1";
	}

	private static String BuildSummaryUrl(String id) {
		return SUMMARY + id;
	}

	public static String LoadSummary(String id) throws IOException,
			JSONException { // 访问api
		String raw = WebUtils.GetContent(BuildSummaryUrl(id), "UTF-8");
		Log.i("TEST", raw);
		return JsonParser.ParseSummary(raw);
	}
}
