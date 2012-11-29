package cn.qylk.adapter;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;

import android.database.Cursor;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cn.qylk.R;
import cn.qylk.fragment.CommonAdapter;

/**
 * 歌曲列表适配
 * 
 * @author qylk2012
 * 
 */
public class MusicListAdapter extends BaseAdapter implements Closeable,CommonAdapter {
	class ViewHolder {
		public TextView artist;
		public ImageButton Cbox;
		public ImageView show;
		public TextView title;
	}

	final static int TITLE = 4, ARTIST = 2, _ID = 0;
	private int curID;
	private LayoutInflater Inflater;
	private SparseBooleanArray innerList;
	private Cursor myCur;
	private boolean OnActionMode;
	private HashSet<Integer> selectedlist;

	public MusicListAdapter(LayoutInflater Inflater, Cursor cur) {
		myCur = cur;
		this.Inflater = Inflater;
		selectedlist = new HashSet<Integer>();
		innerList = new SparseBooleanArray(cur.getCount());
	}

	public void ClearAllSelection() {
		selectedlist.clear();
		innerList.clear();
	}

	@Override
	public void close() throws IOException {
		myCur.close();
	}

	@Override
	public int getCount() {
		return myCur.getCount();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 获取所有已经选择的项目的id
	 * 
	 * @return HashSet<Integer>
	 */
	public HashSet<Integer> GetSelectedHashSet() {
		return selectedlist;
	}

	/**
	 * 获取所有已经选择的项目的id
	 * 
	 * @return HashSet<Integer>
	 */
	public Integer[] GetSelectedList() {
		return selectedlist.toArray(new Integer[selectedlist.size()]);
	}

	/**
	 * 是否处于ActionMode状态
	 * 
	 * @return
	 */
	public boolean getState() {
		return OnActionMode;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = Inflater.inflate(R.layout.musiclist_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.ltitle);
			holder.artist = (TextView) convertView.findViewById(R.id.lartist);
			holder.Cbox = (ImageButton) convertView.findViewById(R.id.checkBox);
			holder.show = (ImageView) convertView.findViewById(R.id.music);
			convertView.setTag(holder);// 以tag寻找更快
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.Cbox.setVisibility(OnActionMode ? View.VISIBLE : View.INVISIBLE);
		myCur.moveToPosition(position);
		StringBuilder sb = new StringBuilder().append(String.valueOf(position))
				.append('.').append(myCur.getString(TITLE).trim());// 序号加标题
		holder.show.setImageResource(R.drawable.songimage);// 歌曲图标
		holder.title.setText(sb.toString());// 标题
		int id = myCur.getInt(_ID);
		holder.artist.setText(myCur.getString(ARTIST));// 艺术家显示
		if (curID == id) {
			holder.title.setTextColor(Color.BLUE);
			holder.artist.setTextColor(Color.BLUE);
		} else {
			holder.title.setTextColor(Color.WHITE);
			holder.artist.setTextColor(Color.WHITE);
		}
		holder.Cbox
				.setBackgroundResource(innerList.get(position) ? R.drawable.checkbox_checked
						: R.drawable.checkbox_unchecked);
		return convertView;
	}

	/**
	 * 开启、关闭列表选择模式
	 * 
	 * @param true：ON<br>
	 *        false:OFF
	 * @param initpos
	 */
	public void OnActionMode(boolean on, int initpos) {
		if (OnActionMode && on)
			return;
		ClearAllSelection();
		if (on) {
			myCur.moveToPosition(initpos);
			selectedlist.add(myCur.getInt(_ID));
			innerList.put(initpos, true);
		}
		OnActionMode = on;
		notifyDataSetChanged();
	}

	/**
	 * 切换ActionMode下的选择项
	 * 
	 * @param position
	 *            切换状态的位置
	 */
	public void ToggleSelection(int position) {
		myCur.moveToPosition(position);
		int id = myCur.getInt(_ID);
		if (innerList.get(position)) {
			selectedlist.remove(id);
			innerList.put(position, false);
		} else {
			selectedlist.add(id);
			innerList.put(position, true);
		}
		notifyDataSetChanged();
	}

	@Override
	public void setId(int id) {
		curID = id;
	}

	@Override
	public CharSequence getFirstChar(int position) {
		myCur.moveToPosition(position);
		return myCur.getString(TITLE).subSequence(0,1);
	}

	@Override
	public void RefreshList(Cursor c) {
		this.myCur = c;
		notifyDataSetChanged();
	}

	@Override
	public Cursor getCursor() {
		return myCur;
	}
}
