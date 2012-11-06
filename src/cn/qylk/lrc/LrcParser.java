package cn.qylk.lrc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;

/**
 * @author qylk2012
 * 
 */
public class LrcParser {

	/**
	 * 解析歌词，参数是歌词文件路径
	 * 
	 * @param lrcpath
	 *            lrcfile absolute path<br>
	 * @return a lrclist: List<LRCbean> instead of Vector<LRCbean> Since v2.5.6
	 */
	public static List<LRCbean> ParseLrc(File lrcpath) {
		try {
			FileInputStream lrcst = new FileInputStream(lrcpath);
			BufferedReader br = new BufferedReader(new InputStreamReader(lrcst,"gb2312"));
			String str;
			int j;
			int[] starttime = new int[20];// 设置最多支持20条歌词复用，否则将发生异常
			List<LRCbean> lrclist = new ArrayList<LRCbean>(100);// 初始默认100条歌词，多数应该够用
			while ((str = br.readLine()) != null) { // 逐行读取
				int TimeNum = 0; // 时间标签数量
				if (str.equals(""))
					continue;// 抛弃空行 注:br.readLine()遇到空行返回空字符串
				j = 0;
				String str1[] = str.split("\\]"); // 分离对象
				for (int i = 0; i < str1.length; i++) {
					String str2[] = str1[i].split("\\["); // 去除'['
					str1[i] = str2[str2.length - 1]; // 抽取出时间 片段
					int ms = Timeparse(str1[i]);
					if (ms != -1) { // 判断是否为时间标签
						TimeNum++; // 计数器加1
						starttime[j++] = ms;
					}
				}
				// 处理歌词复用的情况 如：[00:16:01][00:11:02]*********
				for (int i = 0; i < TimeNum; i++) {
					LRCbean lrc = new LRCbean();
					lrc.beginTime = starttime[i];
					if (TimeNum < str1.length) { // 如果有歌词正文
						lrc.lrcBody = str1[str1.length - 1]; // str1[]中最后一个就是歌词句了。
					} else
						lrc.lrcBody = "***";// 空歌词行，自己加上一句
					lrclist.add(lrc); // 装载入歌词表
				}
			}
			int sum = lrclist.size() - 1;
			if (sum == -1)
				return null;
			for (int i = sum; i > 0; i--)// 必须从歌词最后一句倒着算，这样也可就解决了歌词复用的问题
			{
				int delt = lrclist.get(i).beginTime
						- lrclist.get(i - 1).beginTime;
				if (delt < 0) {// 一般歌词复用，前一个时间标签比后一个时间标签大，如[00:16:01][00:11:02]*****
					lrclist.get(i - 1).lrcBody = lrclist.get(i).lrcBody;// 为复用歌词设置歌词正文，即把[00:16:01]这个标签的歌词正文设为跟[00:11:02]的一样
				}
			}
			Collections.sort(lrclist); // 将读取的歌词按起始时间排序 ，很重要的一步
			int delt = 0;
			for (int i = 0; i < sum; i++)// 计算每句歌词持续时间
			{
				delt = lrclist.get(i + 1).beginTime - lrclist.get(i).beginTime;// 起始时间差
				lrclist.get(i).lineTime = delt;
			}
			return lrclist;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("QPLAY_ParseLrc", e.toString());
		}
		return null;
	}

	/**
	 * 解析时间标签
	 * 
	 * @param string
	 * @return
	 * @throws Exception
	 */
	private static int Timeparse(String string) throws NumberFormatException { //
		String str[] = string.split(":|\\."); // 分离时间：分、秒、毫秒
		if (str.length <= 1 || str.length > 3)// 2012-10-12修复为<=1,原为=1，导致当string为"..."报错
			return -1;// 歌词时间标签至少有两部分构成
		char c = str[0].charAt(0);// 取第一个字符
		if (c < '0' || c > '9')
			return -1; // 判断第一字符是否为规范数字,这里把超过100分钟的音乐歌词也排除了
		// 不返回就默认是时间标签了
		int m, s, ms = 0; // 转换时间格式
		m = Integer.parseInt(str[0]);// 分
		s = Integer.parseInt(str[1]);// 秒
		if (str.length == 3) {// 一般时间标签分离长度为3：分、秒、毫秒
			ms = Integer.parseInt(str[2]);// 如此处理，兼容没有毫秒的歌词文件 如：[00:01]****
			if (ms < 100 && ms > 10)
				ms = ms * 10;// 百分制转化为千分制
			else if (ms < 10)
				ms = ms * 100;// 十分制转化为千分制
		}
		return (m * 60 + s) * 1000 + ms;
	}

}
