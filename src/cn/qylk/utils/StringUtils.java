package cn.qylk.utils;

import java.io.File;
import java.io.InputStream;

import cn.qylk.app.APP;

public final class StringUtils {

	/**
	 * 检查文本文件编码方式
	 * 
	 * @param InputStream
	 *            :返回时不关闭输入流
	 * @return 编码格式:UTF-8\UNICODE\UTF-16BE\UTF-16LE\GB2312
	 */
	public static String CheckCoding(InputStream in) {
		byte[] first3bytes = new byte[3];
		try {
			in.read(first3bytes, 0, 3);
		} catch (Exception e) {
			e.printStackTrace();
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

	/**艺术家信息路径
	 * @param track
	 * @return
	 */
	public static final File GetInfosPath(String track) {
		return new File(APP.Config.INFOSPATH + track);
	}

	/**
	 * 歌词路径
	 * @param title
	 * @return path
	 */
	public static final File GetLyricPath(String title) {
		return new File(APP.Config.LRCPATH + title + ".lrc");
	}

	/**图片路径
	 * @param artist
	 * @return
	 */
	public static final File GetPICPath(String artist) {
		return new File(APP.Config.PICPATH + artist + ".jpg");
	}


	public static final boolean IsEmpty(CharSequence s) {
		if (s != null && s.length() != 0)
			return false;
		else
			return true;
	}

	public static final String TimeFormat(int time) {
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
}
