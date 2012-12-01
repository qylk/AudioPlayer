package cn.qylk.app;

import java.util.ArrayList;
import java.util.Collections;

import cn.qylk.database.MediaDatabase;

public class PlayList implements IPlayList {
	private ArrayList<Integer> ids;
	private int lastId;
	private PlayMode mode = PlayMode.Normal;
	private int sum;
	private TrackInfo track = new TrackInfo();
	private ListTypeInfo typeinfo;

	public PlayList(ListTypeInfo last) {
		setListType(last);
	}

	public int getId() {
		return ids.get(typeinfo.pos);
	}

	@Override
	public int getIndex() {
		return typeinfo.pos;
	}

	@Override
	public PlayMode getMode() {
		return mode;
	}

	@Override
	public String getNextTitle() {
		return MediaDatabase.getTrackInfo(ids.get((typeinfo.pos + 1) % sum),
				null).title;
	}

	@Override
	public TrackInfo getPreviousEntity() {
		return MediaDatabase.getTrackInfo(lastId, null);
	}

	@Override
	public int getSum() {
		return sum;
	}

	@Override
	public TrackInfo getTrackEntity() {
		return track;
	}

	public ListTypeInfo getTypeInfo() {
		return typeinfo;
	}

	@Override
	public void goLast() {
		record();
		if (typeinfo.pos == 0)
			typeinfo.pos += sum;
		typeinfo.pos = (typeinfo.pos - 1) % sum;
		MediaDatabase.getTrackInfo(getId(), track);
	}

	@Override
	public void goNext() {
		record();
		typeinfo.pos = (typeinfo.pos + 1) % sum;
		MediaDatabase.getTrackInfo(getId(), track);
	}

	@Override
	public void goTo(int pos) {
		typeinfo.pos = pos;
		MediaDatabase.getTrackInfo(getId(), track);
	}

	/**
	 * 记录播放历史
	 */
	private void record() {
		lastId = getId();
		if (typeinfo.list != ListType.HISTORY)
			MediaDatabase.TimeRecode(getId());
	}

	public void setListType(ListTypeInfo info) {
		if (mode == PlayMode.Shuffle)// 当前正处于随机播放中，当用户从列表界面选择歌曲时，需要更改为顺序播放。
			setMode(PlayMode.Normal);
		if (typeinfo == null || info.para != typeinfo.para
				|| info.list != typeinfo.list) {// 条件1：typeinfo==null,即初次建表，条件2：列表不同
			this.ids = MediaDatabase.getIDS(info);
			sum = ids.size();
			this.typeinfo = info;
			mode = PlayMode.Normal;
			if (sum == 0) {
				setListType(new ListTypeInfo(ListType.ALLSONGS, 0));
			}
		} else
			typeinfo.pos = info.pos;
		if (info.pos >= sum)
			info.pos = 0;
		MediaDatabase.getTrackInfo(getId(), track);
	}

	@Override
	public void setMode(PlayMode mode) {
		this.mode = mode;
		if (mode == PlayMode.Shuffle)
			Collections.shuffle(ids);
		else if (mode == PlayMode.Normal)
			Collections.sort(ids);
	}
}
