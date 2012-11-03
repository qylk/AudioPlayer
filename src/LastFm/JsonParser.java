package LastFm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author qylk2011 all rights resolved
 * 
 */
public final class JsonParser {
	private static final int MAXSIZE = 601;// 最大图片宽度

	/**
	 * 解析获取歌手传记
	 * 
	 * @param jsonstr
	 * @return infomation of the artist that you needed <br>
	 *         if exption occurs,null will be returned;
	 * @throws JSONException
	 */
	public static String ParseInfo(String jsonstr, String artist)
			throws JSONException {
		JSONObject jsonobj = new JSONObject(jsonstr);
		JSONObject Artist = (JSONObject) jsonobj.get("artist");
		JSONObject bio = (JSONObject) Artist.get("bio");
		String info = (String) bio.get("summary");
		if (info.equals(""))
			throw new JSONException("null");
		return info;
	}

	/**
	 * 解析歌手图片地址
	 * 
	 * @param jsonstr
	 * @return a string of picture’s url you need ，or null if exption occurs
	 * @throws JSONException
	 * @throws NumberFormatException
	 */
	public static String ParsePICUrl(String jsonstr, String artist)
			throws NumberFormatException, JSONException {
		String url = null;
		JSONObject jsonobj = new JSONObject(jsonstr);
		JSONObject images = (JSONObject) jsonobj.get("images");
		JSONObject attr = (JSONObject) images.get("@attr");
		String total = (String) attr.get("total");
		int n = Integer.valueOf(total).intValue();// 返回数量
		if (n == 0)
			throw new JSONException("null");
		else if (n == 1) {
			JSONObject data = (JSONObject) images.get("image");
			JSONObject sizes = (JSONObject) data.get("sizes");
			JSONArray Jary2 = (JSONArray) sizes.get("size");
			JSONObject tag0 = (JSONObject) Jary2.get(0);
			int width = Integer.valueOf(tag0.get("width").toString());
			int height = Integer.valueOf(tag0.get("height").toString());
			if (width < MAXSIZE && height < MAXSIZE) {
				url = tag0.getString("#text");
			}
		} else {
			JSONArray Jary = (JSONArray) images.get("image");
			for (int j = 0; j < Jary.length(); j++) {
				JSONObject data = (JSONObject) Jary.get(j);
				JSONObject sizes = (JSONObject) data.get("sizes");
				JSONArray Jary2 = (JSONArray) sizes.get("size");
				JSONObject tag0 = (JSONObject) Jary2.get(0);// 只求原始图片
				int width = Integer.valueOf(tag0.get("width").toString());
				int height = Integer.valueOf(tag0.get("height").toString());
				if (width < MAXSIZE && height < MAXSIZE) {// 在可选时选择合适大小的图片资源
					url = tag0.getString("#text");
					break;
				}
			}
		}
		return url;
	}

	/**
	 * 解析获取音轨的可能作者（已在api地址中设置最多返回5个,勿随便修改）
	 * 
	 * @param jsonstr
	 * @return List<String> which contains results or null if exption occurs;
	 * @deprecated
	 * @throws JSONException
	 */
	public static String[] ParseTracks(String jsonstr) throws JSONException {
		JSONObject jsonobj = new JSONObject(jsonstr);

		JSONObject result = (JSONObject) jsonobj.get("results");
		String total = (String) result.get("opensearch:totalResults");
		int m = Integer.valueOf(total);
		if (m == 0)
			return null;
		m = m > 5 ? 5 : m;
		String[] list = new String[m];// 这里注意结果已经设定不多于5个
		JSONObject trackmatch = (JSONObject) result.get("trackmatches");
		if (m == 1) {
			JSONObject track = (JSONObject) trackmatch.get("track");
			list[0] = (String) track.get("artist");
		} else {
			JSONArray JAry = (JSONArray) trackmatch.get("track");
			for (int j = 0; j < m; j++)
				list[j] = (String) ((JSONObject) JAry.get(j)).get("artist");
		}
		return list;
	}

}
