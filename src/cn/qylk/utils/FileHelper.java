package cn.qylk.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * 文件操作
 * 
 * @author qylk2012
 */
public class FileHelper {

	/**
	 * default coding is "utf-8";
	 * 
	 * @param str
	 * @param path
	 */
	public void WriteFile(String str, File path) {
		WriteFile(str, path, "UTF-8");
	}
	/**
	 * default coding is "utf-8";
	 * @param file
	 * @return
	 */
	public String ReadFile(File file) {
		return ReadFile(file,"UTF-8");
	}
	/**
	 * 从磁盘读文件内容并返回
	 * 
	 * @param file
	 *            file content should not be large
	 * @return maybe ""
	 */
	public String ReadFile(File file,String enc) {
		StringBuffer sb = new StringBuffer();
		try {
			FileInputStream info = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(info, enc));
			String str;
			while ((str = br.readLine()) != null) {
				sb.append(str).append("\r\n");
			}
			return sb.toString();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return "";
	}

	public void WriteFile(String str, File path, String enc)
			throws IllegalArgumentException {
		if (str == null)
			throw new IllegalArgumentException("str must not be null");
		try {
			FileOutputStream outStream = new FileOutputStream(path, false);// 写入本地目录
			OutputStreamWriter writer = new OutputStreamWriter(outStream, enc);
			writer.write(str);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void WriteFile(InputStream instream, String path) throws IOException {
		byte[] buffer = new byte[1024];
		FileOutputStream outStream = new FileOutputStream(new File(path));// 写出图片到SD卡
		int len;
		while ((len = instream.read(buffer)) != -1)
			outStream.write(buffer, 0, len);
		outStream.close(); // 关闭
		instream.close();
	}
}
