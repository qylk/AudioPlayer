package cn.qylk.adapter;

import java.io.Serializable;

public class ItemData implements Serializable{
	private static final long serialVersionUID = 2033690280003132308L;
	private int[] imgs;
	private int mCount;
	private String[] mTitleID;

	public ItemData(int[] imgs, String titleID[], int count) {
		refreshData(imgs, titleID, count);
	}

	public int getCount() {
		return mCount;
	}

	public int getImgRes(int index) {
		return imgs[index];
	}

	public String getTitle(int index) {
		return mTitleID[index];
	}

	public void refreshData(int[] imgs, String titleID[], int count) {
		this.imgs = imgs;
		mTitleID = titleID;
		mCount = count;
	}

	public void SetItemTitle(int index, String newtitle) {
		mTitleID[index] = newtitle;
	}
}
