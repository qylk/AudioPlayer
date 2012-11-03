package cn.qylk.myview;

import java.io.File;
import java.util.List;

import cn.qylk.app.APP;
import cn.qylk.app.TrackInfo;
import cn.qylk.lrc.LRCbean;

public class LrcPackage {
	public int duration;
	public List<LRCbean> list;
	public File path;
	public String title;

	public LrcPackage(List<LRCbean> list, TrackInfo track) {
		this.list = list;
		this.title = track.title;
		this.duration = track.duration;
		this.path=new File(APP.LRCPATH+title+".lrc");
	}
	
	public int getSum(){
		return list.size();
	}
}
