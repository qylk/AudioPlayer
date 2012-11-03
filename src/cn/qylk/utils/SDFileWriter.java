package cn.qylk.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

/**
 * SDcard 保存
 * 
 * @author qylk2012
 */
public class SDFileWriter {
	private static final byte[] ReadInputStream(InputStream instream)
			throws IOException {
		byte[] buffer = new byte[1024];
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();// 向内存输入的流，完成数据的累加
		int len;
		while ((len = instream.read(buffer)) != -1) {// 从网络输入流中读取数据到buffer中
			outstream.write(buffer, 0, len);// 将buffer中读到的数据写到内存输出流中
		}
		outstream.close();
		return outstream.toByteArray(); // 返回完整的数据
	}

	/**
	 * 保存图片
	 */
	public static void writePic(InputStream instream, String path) throws IOException{// 从api获取图片地址后，下载图片并写入本地目录
		byte[] data = ReadInputStream(instream);
		FileOutputStream outStream = new FileOutputStream(new File(path));// 写出图片到SD卡
		outStream.write(data);// 写入
		outStream.close(); // 关闭
		instream.close();
		data = null;
	}

	/**
	 * 保存字符串
	 * 
	 * @param path
	 * @param content
	 */
	public static void writeString(File path, String content) {
		if (content == null)
			return;
		try {
			FileOutputStream outStream = new FileOutputStream(path, false);// 写入本地目录
			OutputStreamWriter writer = new OutputStreamWriter(outStream,
					"GB2312");
			writer.write(content);
			writer.flush();
			writer.close();
		} catch (IOException e) {/* do nothing */
		}
	}
}
