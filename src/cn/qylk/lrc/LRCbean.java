package cn.qylk.lrc;

public class LRCbean implements Comparable<LRCbean> {
	public int beginTime = 0;
	public int lineTime = 0;
	public String lrcBody = "";
	@Override
	public int compareTo(LRCbean another) {// 排序参数
		return this.beginTime - another.beginTime;
	}
}
