package cn.qylk.utils;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class WebUtils {

	/**
	 * 从api获取图片地址，下载图片
	 * 
	 * @return
	 */
	public static InputStream FetchFile(String url) throws IOException {// 从api获取图片地址后，下载图片并写入本地目录
		URL aurl = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) aurl
				.openConnection();
		connection.setReadTimeout(5000);
		connection.setRequestMethod("GET");
		connection.setConnectTimeout(4000);
		return connection.getInputStream();
	}

	public static String GetContent(String url, String coding)
			throws IOException {
		InputStream in = FetchFile(url);
		StringBuilder sb = new StringBuilder();
		byte[] b = new byte[1024];
		int n;
		while ((n=in.read(b)) != -1) {
			String s = new String(b, 0, n, coding);
			sb.append(s);
		}
		in.close();
		return sb.toString();
	}
}
