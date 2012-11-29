package cn.qylk.app;

import cn.qylk.app.IPlayList.ListType;

public class ListTypeInfo {
	public String keyword;
	public ListType list = ListType.ALLSONGS;// 歌曲列表类型
	public int para;// 列表参数
	public int pos;// 当前歌曲位置

	public ListTypeInfo(ListType type, int para) {
		this(type, para, null, 0);
	}

	public ListTypeInfo(ListType type, int para, String kw) {
		this(type, para, kw, 0);
	}

	public ListTypeInfo(ListType type, int para, String kw, int index) {
		this.list = type;
		this.para = para;
		this.pos = index;
		this.keyword = kw;
	}
}
