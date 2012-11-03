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

import cn.qylk.app.APP;

/**
 * @author qylk2011 下载歌词的网络连接类，使用SOSO音乐API
 * all rights resolved
 * @deprecated
 */
public class SOSOLrc {
	private static final String CODE = "GB2312"; // 中文编码方式
	private static final String SoSo = "http://cgi.music.soso.com/fcgi-bin/fcg_download_lrc.q?song=";
	private static final byte[] UTFBIO = new byte[] { (byte) 0xEF, (byte) 0xBB,
			(byte) 0xBF };// "UTF-8" BIO

	/**
	 * @param title
	 * @param artist
	 * @return true for success
	 */
	public static Boolean LrcSearch(String title, String artist) {
		try {
			return SearchWeb(title, artist);
		} catch (IOException e) {
			return false;
		}
	}

	private static boolean SearchWeb(String title, String artist)
			throws IOException {
		String name = URLEncoder.encode(title, CODE);
		artist = URLEncoder.encode(artist, CODE);
		String s = new String();
		String url=SoSo + name + "&singer=" + artist + "&down=1";
		URLConnection con = new URL(url)
				.openConnection();
		con.setConnectTimeout(2000);
		String type = con.getHeaderField("Content-Type");
		if (type.contains("gb2312"))
			return false;
		InputStream inputStream = con.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				inputStream, CODE));
		char c[] = new char[1];
		br.read(c, 0, 1);
		if (c[0] != '[')
			return false;
		FileOutputStream outStream = new FileOutputStream(APP.LRCPATH
				+ title + ".lrc", false);
		OutputStreamWriter writer = new OutputStreamWriter(outStream, "UTF-8");
		outStream.write(UTFBIO);
		while ((s = br.readLine()) != null) {
			writer.write(s.toString());
			writer.write("\n");
		}
		br.close();
		writer.flush();
		writer.close();// 记得关闭，将歌词文件从内存写回SD卡
		return true;// 标志
	}
}
