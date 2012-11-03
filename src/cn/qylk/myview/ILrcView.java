package cn.qylk.myview;

public interface ILrcView {
	public void clearView();
	public void initLrcIndex(int curpos);
	public void setFirstColor(int color);
	public void setSecondColor(int color);
	public void setOffset(int offset);
	public void setLyric(LrcPackage lrc);
	public void setShadow(boolean shadow);
	public void setLrcTextSize(float size);
	public void updateView(int progress);
	public void setGap(int gap);
}
