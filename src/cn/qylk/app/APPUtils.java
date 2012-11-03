package cn.qylk.app;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.widget.Toast;
import cn.qylk.R;
import cn.qylk.app.IPlayList.ListType;
import cn.qylk.utils.SendAction;

public class APPUtils {
	public native static void SaveToID3v2(String title, String artist,
			String album, String path);

	/**
	 * 扫描存储卡
	 * 
	 * @param registerReceiver
	 *            是否接收扫描完闭的通知
	 */
	public static void ScanSD(boolean notification) {
		if (notification) {
			IntentFilter filter = new IntentFilter();// 过滤器
			filter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
			filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
			filter.addDataScheme("file");
			APP.getInstance().registerReceiver(new ScanSdReceiver(), filter);
		}
		APP.getInstance().sendBroadcast(
				new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
						+ APP.SDDIR)));
	}

	public static void SaveId3v2(final TrackInfo info) {
		if (!info.path.endsWith("mp3"))
			return;
		new Thread(new Runnable() {

			@Override
			public void run() {
				SaveToID3v2(info.title, info.artist, info.album, info.path);
			}
		}).start();
	}

	private static boolean seek(ByteBuffer buf) {
		byte[] buffer = new byte[3];
		buf.get(buffer, 0, 3);
		byte[] TAG_ID = { (byte) 'T', (byte) 'A', (byte) 'G' };
		return Arrays.equals(buffer, TAG_ID);
	}

	public static String[] getID3v1(String file) {
		try {
			File mp3 = new File(file);
			RandomAccessFile raf = new RandomAccessFile(mp3, "r");
			ByteBuffer byteBuffer = ByteBuffer.allocate(128);
			FileChannel fc = raf.getChannel();
			fc.position(mp3.length() - 128);
			fc.read(byteBuffer);
			byteBuffer.flip();
			if (!seek(byteBuffer))
				return null;
			byte[] dataBuffer = new byte[128];
			byteBuffer.position(0);
			byteBuffer.get(dataBuffer, 0, 128);
			String[] ID3 = new String[3];
			ID3[0] = new String(dataBuffer, 3, 30, "GB2312").trim();
			ID3[1] = new String(dataBuffer, 33, 30, "GB2312").trim();
			ID3[2] = new String(dataBuffer, 63, 30, "GB2312").trim();
			fc.close();
			raf.close();
			return ID3;
		} catch (IOException e) {
		}
		return null;
	}
}

class ScanSdReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action))
			Toast.makeText(context, R.string.scaning, Toast.LENGTH_LONG).show();
		else if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
			Toast.makeText(context, R.string.scaned, Toast.LENGTH_LONG).show();
			SendAction.SendListChangedSignal(new ListTypeInfo(
					ListType.ALLSONGS, "library"));
			APP.getInstance().unregisterReceiver(this);
		}
	}
}