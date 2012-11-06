package cn.qylk.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtils {	
	/**
	 * 获取缩略图
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap compress(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false;
		int be = (int) Math.rint((float) options.outHeight / 50);
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;
		return BitmapFactory.decodeFile(path, options); // 返回缩略图
	}
}
