package cn.qylk.QianQianLyrics;

import java.io.UnsupportedEncodingException;

public class QianQianEncoding {

	public long Conv(int i) {
		long r = i % 4294967296l;
		if (i >= 0 && r > 2147483648l)
			r = r - 4294967296l;

		if (i < 0 && r < 2147483648l)
			r = r + 4294967296l;
		return r;
	}

	/**根据参数，获取访问所必需的code
	 * @param singer
	 * @param title
	 * @param lrcId
	 * @return
	 */
	public String CreateQianQianCode(String singer, String title,
			int lrcId) {
		String qqHexStr = null;
		qqHexStr = str2HexStr(singer + title,"UTF-8");
		int length = qqHexStr.length() / 2;
		int[] song = new int[length];
		for (int i = 0; i < length; i++) {
			song[i] = Integer
					.parseInt(qqHexStr.substring(i * 2, i * 2 + 2), 16);
		}
		int t1 = 0, t2 = 0, t3 = 0;
		t1 = (lrcId & 0x0000FF00) >> 8;
		if ((lrcId & 0x00FF0000) == 0) {
			t3 = 0x000000FF & ~t1;
		} else {
			t3 = 0x000000FF & ((lrcId & 0x00FF0000) >> 16);
		}
		t3 = t3 | ((0x000000FF & lrcId) << 8);
		t3 = t3 << 8;
		t3 = t3 | (0x000000FF & t1);
		t3 = t3 << 8;
		if ((lrcId & 0xFF000000) == 0) {
			t3 = t3 | (0x000000FF & (~lrcId));
		} else {
			t3 = t3 | (0x000000FF & (lrcId >> 24));
		}
		int j = length - 1;
		while (j >= 0) {
			int c = song[j];
			if (c >= 0x80)
				c = c - 0x100;
			t1 = (int) ((c + t2) & 0x00000000FFFFFFFF);
			t2 = (int) ((t2 << (j % 2 + 4)) & 0x00000000FFFFFFFF);
			t2 = (int) ((t1 + t2) & 0x00000000FFFFFFFF);
			j -= 1;
		}
		j = 0;
		t1 = 0;
		while (j <= length - 1) {
			int c = song[j];
			if (c >= 128)
				c = c - 256;
			int t4 = (int) ((c + t1) & 0x00000000FFFFFFFF);
			t1 = (int) ((t1 << (j % 2 + 3)) & 0x00000000FFFFFFFF);
			t1 = (int) ((t1 + t4) & 0x00000000FFFFFFFF);
			j += 1;
		}
		int t5 = (int) Conv(t2 ^ t3);
		t5 = (int) Conv(t5 + (t1 | lrcId));
		t5 = (int) Conv(t5 * (t1 | t3));
		t5 = (int) Conv(t5 * (t2 ^ lrcId));
		long t6 = (long) t5;
		if (t6 > 2147483648l)
			t5 = (int) (t6 - 4294967296l);
		return String.valueOf(t5);
	}

	public  String str2HexStr(String str,String encode) {
		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = null;
		try {
			bs = str.getBytes(encode);
		} catch (UnsupportedEncodingException e) {}
		for (int i = 0; i < bs.length; i++) {
			sb.append(chars[(bs[i] & 0x0f0) >> 4]);
			sb.append(chars[bs[i] & 0x0f]);
		}
		return sb.toString().trim();
	}
}
