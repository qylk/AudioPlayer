package cn.qylk.utils;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.util.Log;

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


	/**
	 * 访问网址，获取内容
	 * 
	 * @param url
	 * @return
	 * @throws
	 * @throws ParseException
	 * @throws IOException
	 */
	public static String HttpRequests(String url) {
		HttpPost request = new HttpPost(url);
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);// 连接超时
		try {
			HttpResponse httpResponse = client.execute(request); // 发送请求
			if (httpResponse.getStatusLine().getStatusCode() == 200)
				return EntityUtils.toString(httpResponse.getEntity()); // 返回数据
		} catch (IOException e) {
			Log.e("TEST", "网络不可用");
		} finally {
			client.getConnectionManager().shutdown(); // 释放网络连接资源
		}
		return null;
	}
}
