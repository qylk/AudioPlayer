package cn.qylk.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import cn.qylk.app.APP;

public final class StringUtils {

	/**
	 * 检查文本文件编码方式
	 * 
	 * @param file
	 * @return
	 */
	public static String CheckCoding(File file) {
		byte[] first3bytes = new byte[3];
		try {
			FileInputStream in = new FileInputStream(file);
			in.read(first3bytes, 0, 3);
			in.close();
		} catch (Exception e) {
		}
		if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB
				&& first3bytes[2] == (byte) 0xBF)
			return "UTF-8";
		else if (first3bytes[0] == (byte) 0xFF && first3bytes[1] == (byte) 0xFE)
			return "UNICODE";
		else if (first3bytes[0] == (byte) 0xFE && first3bytes[1] == (byte) 0xFF)
			return "UTF-16BE";
		else if (first3bytes[0] == (byte) 0xFF && first3bytes[1] == (byte) 0xFF)
			return "UTF-16LE";
		else
			return "GB2312";
	}

	/**
	 * 获取歌词目录
	 * 
	 * @param title
	 * @return path
	 */
	public static File GetLyricPath(String title) {
		return new File(APP.LRCPATH + title + ".lrc");
	}

	public static File GetPICPath(String artist) {
		return new File(APP.PICPATH + artist + ".jpg");
	}

	public static File GetInfosPath(String track) {
		return new File(APP.INFOSPATH + track);
	}

	/**
	 * 
	 * 不重复的随机序列表，列表长度由参数sum指定
	 */
	public static Integer[] GetRandomList(int sum) {
		int n = sum;
		List<Integer> temp = new ArrayList<Integer>(sum);
		List<Integer> temp2 = new ArrayList<Integer>(sum);
		for (int j = 0; j < sum; j++)
			temp2.add(j);
		for (int i = 0; i < sum; i++) {
			int r = (int) (Math.random() * n);
			temp.add(temp2.get(r));
			temp2.remove(r);
			n--;
		}
		temp2 = null;
		return temp.toArray(new Integer[sum]);
	}

	public static boolean IsEmpty(CharSequence s) {
		if (s != null && s.length() != 0)
			return false;
		else
			return true;
	}

	public static String TimeFormat(int time) {
		int min = time / 60000;
		int sec = (time % 60000) / 1000;
		StringBuilder sb = new StringBuilder(6);
		if (min < 10)
			sb.append('0');
		sb.append(Integer.toString(min)).append(':');
		if (sec < 10)
			sb.append('0');
		sb.append(Integer.toString(sec));
		return sb.toString();
	}

	public static void WriteStringToDisk(String str, File path)
			throws IllegalArgumentException {
		if (str == null)
			throw new IllegalArgumentException("str must not be null");
		try {
			FileOutputStream outStream = new FileOutputStream(path, false);// 写入本地目录
			OutputStreamWriter writer = new OutputStreamWriter(outStream);
			writer.write(str);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String ReadFileContent(File file) {
		StringBuffer sb = new StringBuffer();
		try {
			FileInputStream info = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(info));
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
}
