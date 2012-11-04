package cn.qylk.douban;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author qylk2012<br>
 *         all rights resolved
 * 
 */
public final class JsonParser {
	/**
	 * 解析搜索结果,获得歌曲ID
	 * 
	 * @param jsonstr
	 * @param artist
	 * @return 可能为NULL或异常
	 * @throws JSONException
	 */
	public static String ParseId(String jsonstr, String artist)
			throws JSONException {
		JSONObject jsonobj = new JSONObject(jsonstr);
		int count = (Integer) jsonobj.get("count");
		if (count == 0)
			return null;
		JSONArray musics = jsonobj.getJSONArray("musics");
		JSONObject music = (JSONObject) musics.get(0);
		JSONArray author = music.getJSONArray("author");
		String art = ((JSONObject) author.get(0)).getString("name");
		if (art.equals(artist) || artist.contains(art)) {
			return music.getString("id");
		}
		return null;
	}

	/**
	 * 解析歌曲信息摘要
	 * 
	 * @param jsonstr
	 * @param artist
	 * @return 需要捕获异常
	 * @throws JSONException
	 */
	public static String ParseSummary(String jsonstr) throws JSONException {
		JSONObject jsonobj = new JSONObject(jsonstr);
		return jsonobj.getString("summary");
	}
}
