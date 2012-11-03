package cn.qylk.app;

import cn.qylk.app.IPlayList.ListType;

public class ListTypeInfo {
	public ListType list = ListType.ALLSONGS;//歌曲列表类型
	public String para;//列表参数
	public int pos;//当前歌曲位置

	public ListTypeInfo(ListType type, String para) {
		this(type, para, 0);
	}

	public ListTypeInfo(ListType type, String para, int index) {
		this.list = type;
		this.para = para;
		this.pos = index;
	}
}
