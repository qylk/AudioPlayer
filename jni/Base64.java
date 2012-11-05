package test;

import java.io.ByteArrayOutputStream;

public class Base64 {
	private final static char[] BASE64_ENCODING_TABLE;
	private final static byte[] BASE64_DECODING_TABLE;
	static {
		BASE64_ENCODING_TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
				.toCharArray();
		BASE64_DECODING_TABLE = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59,
				60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8,
				9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
				25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34,
				35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50,
				51, -1, -1, -1, -1, -1 };
	}

	private Base64() {
	}

	/**
	 * 将数据进行Base64编码。
	 * 
	 * @param data
	 *            数据
	 * @param offset
	 *            数据中的初始偏移量
	 * @param length
	 *            写入的字节数
	 * @return 编码后的字符串
	 */
	public final static String encode(byte[] data, int offset, int length) {
		if (data == null) {
			return null;
		}
		StringBuffer buffer = new StringBuffer();
		int[] temp = new int[3];
		int end = offset + length;

		while (offset < end) {
			temp[0] = data[offset++] & 255;

			if (offset == data.length) {
				buffer.append(BASE64_ENCODING_TABLE[(temp[0] >>> 2) & 63]);
				buffer.append(BASE64_ENCODING_TABLE[(temp[0] << 4) & 63]);
				buffer.append('=');
				buffer.append('=');
				break;
			}
			temp[1] = data[offset++] & 255;
			if (offset == data.length) {
				buffer.append(BASE64_ENCODING_TABLE[(temp[0] >>> 2) & 63]);
				buffer.append(BASE64_ENCODING_TABLE[((temp[0] << 4) | (temp[1] >>> 4)) & 63]);
				buffer.append(BASE64_ENCODING_TABLE[(temp[1] << 2) & 63]);
				buffer.append('=');
				break;
			}
			temp[2] = data[offset++] & 255;
			buffer.append(BASE64_ENCODING_TABLE[(temp[0] >>> 2) & 63]);
			buffer.append(BASE64_ENCODING_TABLE[((temp[0] << 4) | (temp[1] >>> 4)) & 63]);
			buffer.append(BASE64_ENCODING_TABLE[((temp[1] << 2) | (temp[2] >>> 6)) & 63]);
			buffer.append(BASE64_ENCODING_TABLE[temp[2] & 63]);
		}
		return buffer.toString();
	}

	/**
	 * 将数据进行Base64编码。
	 * 
	 * @param data
	 *            数据
	 * @return 编码后的字符串
	 */
	public final static String encode(byte[] data) {
		return encode(data, 0, data.length);
	}

	/**
	 * 将字符串进行Base64编码。
	 * 
	 * @param str
	 *            字符串
	 * @return 编码后的字符串
	 */
	public final static String encode(String str) {
		return encode(str.getBytes());
	}

	/**
	 * 对使用Base64编码的字符串进行解码。
	 * 
	 * @param str
	 *            经过编码的字符串
	 * @return 解码后的数据
	 */
	public final static byte[] decode(String str) {
		if (str == null) {
			return null;
		}
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] data = str.getBytes();
		int[] temp = new int[4];
		int index = 0;
		while (index < data.length) {
			do {
				temp[0] = BASE64_DECODING_TABLE[data[index++]];
			} while (index < data.length && temp[0] == -1);
			if (temp[0] == -1) {
				break;
			}
			do {
				temp[1] = BASE64_DECODING_TABLE[data[index++]];
			} while (index < data.length && temp[1] == -1);
			if (temp[1] == -1) {
				break;
			}
			buffer.write(((temp[0] << 2) & 255) | ((temp[1] >>> 4) & 255));
			do {
				temp[2] = data[index++];
				if (temp[2] == 61) {
					return buffer.toByteArray();
				}
				temp[2] = BASE64_DECODING_TABLE[temp[2]];
			} while (index < data.length && temp[2] == -1);
			if (temp[2] == -1) {
				break;
			}
			buffer.write(((temp[1] << 4) & 255) | ((temp[2] >>> 2) & 255));
			do {
				temp[3] = data[index++];

				if (temp[3] == 61) {
					return buffer.toByteArray();
				}
				temp[3] = BASE64_DECODING_TABLE[temp[3]];
			} while (index < data.length && temp[3] == -1);
			if (temp[3] == -1) {
				break;
			}
			buffer.write(((temp[2] << 6) & 255) | temp[3]);
		}
		return buffer.toByteArray();
	}
}
