package cn.qylk.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import cn.qylk.app.TrackInfo;

public class ID3 {
	static {
		System.loadLibrary("ID3rw");
	}

	private native static void SaveToID3v2(String title, String artist,
			String album, String path);

	private static final byte[] TAG_ID = { (byte) 'T', (byte) 'A', (byte) 'G' };

	public void SaveId3v2(final TrackInfo info) {
		if (!info.path.endsWith("mp3"))
			return;
		new Thread(new Runnable() {

			@Override
			public void run() {
				SaveToID3v2(info.title, info.artist, info.album, info.path);
			}
		}).start();
	}

	private boolean seek(ByteBuffer buf) {
		byte[] buffer = new byte[3];
		buf.get(buffer, 0, 3);
		return Arrays.equals(buffer, TAG_ID);
	}

	/**
	 * @param file
	 * @return suc?
	 */
	public boolean getID3v1(String file, String[] buf) {
		try {
			File mp3 = new File(file);
			RandomAccessFile raf = new RandomAccessFile(mp3, "r");
			ByteBuffer byteBuffer = ByteBuffer.allocate(128);
			FileChannel fc = raf.getChannel();
			fc.position(mp3.length() - 128);
			fc.read(byteBuffer);
			byteBuffer.flip();
			if (!seek(byteBuffer))
				return false;
			byte[] dataBuffer = new byte[128];
			byteBuffer.position(0);
			byteBuffer.get(dataBuffer, 0, 128);
			buf[0] = new String(dataBuffer, 3, 30, "GB2312").trim();
			buf[1] = new String(dataBuffer, 33, 30, "GB2312").trim();
			buf[2] = new String(dataBuffer, 63, 30, "GB2312").trim();
			fc.close();
			raf.close();
			return true;
		} catch (IOException e) {
		}
		return false;
	}
}
