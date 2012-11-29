package cn.qylk.fragment;

import android.database.Cursor;

public interface CommonAdapter {
	public void setId(int id);

	public CharSequence getFirstChar(int position);

	public Cursor getCursor();

	public void RefreshList(Cursor c);
}
