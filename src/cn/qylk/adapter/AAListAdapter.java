package cn.qylk.adapter;

import cn.qylk.R;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * artist和album列表适配器
 * 
 * @author qylk2012
 * 
 */
public class AAListAdapter extends BaseAdapter {
	LayoutInflater layoutinflater;
	private Cursor mycursor;

	public AAListAdapter(LayoutInflater inflater, Cursor cursor) {
		layoutinflater = inflater;
		mycursor = cursor;
	}

	@Override
	public int getCount() {
		return mycursor.getCount();
	}

	public Cursor getCursor() {
		return mycursor;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = layoutinflater.inflate(R.layout.artist_album_list,
					null);
		mycursor.moveToPosition(position);
		StringBuilder sb = new StringBuilder().append(String.valueOf(position))
				.append('.').append(mycursor.getString(0));
		((TextView) convertView.findViewById(R.id.listitem)).setText(sb
				.toString());
		((TextView) convertView.findViewById(R.id.sum)).setText(mycursor
				.getInt(1) + "首");
		((TextView) convertView.findViewById(R.id.artist4this))
				.setText(mycursor.getString(2));
		return convertView;
	}

	public void swapCursor(Cursor cursor) {
		this.mycursor = cursor;
		notifyDataSetChanged();
	}

}
