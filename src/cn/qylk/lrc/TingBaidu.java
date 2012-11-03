package cn.qylk.lrc;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.qylk.app.APP;
import cn.qylk.utils.WebUtils;

/**
 * @author qylk2012
 * all rights resolved
 *@deprecated
 */
public class TingBaidu {
	private static final String CODE = "UTF-8"; // 中文编码方式
	private static final String TingBaidu = "http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.search.lrcys&format=json&query=";
	private static final byte[] UTFflag = new byte[] { (byte) 0xEF,
			(byte) 0xBB, (byte) 0xBF };

	private static boolean GetLrc(String lrclink, String filename)
			throws IOException {
		URL url = new URL(lrclink);
		String s = new String();
		URLConnection con = (URLConnection) url.openConnection();
		con.setConnectTimeout(3000);
		InputStream inputStream = con.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				inputStream));
		FileOutputStream outStream = new FileOutputStream(APP.LRCPATH
				+ filename + ".lrc", false);// 写入本地目录
		OutputStreamWriter writer = new OutputStreamWriter(outStream);
		outStream.write(UTFflag);// 先写UTF-8的BOM
		while ((s = br.readLine()) != null) {
			writer.write(s + "\n");
		}
		br.close();
		writer.flush();
		writer.close();// 记得关闭，将歌词文件从内存写回SD卡
		return true;
	}

	public static boolean LrcSearch(String title, String artist) {
		try {
			return SearchLrcUrl(title, artist);
		} catch (Throwable e) {
			System.out.print("Tingbaidu error");
		}
		return false;
	}

	/**
	 * 解析百度歌词地址
	 */
	public static String ParseTingBaidu(String jsonstr) throws JSONException {
		JSONObject jsonobj = new JSONObject(jsonstr);
		JSONArray Jary = (JSONArray) jsonobj.get("lrcys_list");
		JSONObject linkobj = (JSONObject) Jary.get(0);
		return (String) linkobj.get("lrclink");
	}

	private static boolean SearchLrcUrl(String title, String artist)
			throws IOException, JSONException {
		String filename = title;
		title = URLEncoder.encode(title, CODE);
		artist = URLEncoder.encode(artist, CODE);
		String url = TingBaidu + title + "$$$" + artist;
		String lrclink = ParseTingBaidu(WebUtils.GetContent(url, "UTF-8"));
		return GetLrc(lrclink, filename);
	}
}
