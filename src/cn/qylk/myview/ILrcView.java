package cn.qylk.myview;

public interface ILrcView {
	public void clearView();
	public void initLrcIndex(int curpos);
	public void setFirstColor(int color);
	public void setGap(int gap);
	public void setLrcTextSize(float size);
	public void setLyric(LrcPackage lrc);
	public void setOffset(int offset);
	public void setSecondColor(int color);
	public void setShadow(boolean shadow);
	public void updateView(int progress);
}
