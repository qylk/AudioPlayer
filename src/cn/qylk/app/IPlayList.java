package cn.qylk.app;

public interface IPlayList {
public static enum ListType{ALLSONGS,ALBUM,ARTIST,HISTORY,LOVE,PERSONAL,RECENTADD,SEARCH};
public static enum PlayMode{Normal,Repeat_One,Shuffle};
public int getId();
public int getIndex();
public PlayMode getMode();
public String getNextTitle();
public int getSum();
public TrackInfo getTrackEntity();
public ListTypeInfo getTypeInfo();
public void goLast();
public void goNext();
public void goTo(int pos);
public void setListType(ListTypeInfo type);
public void setMode(PlayMode mode);
public TrackInfo getPreviousEntity();
}
