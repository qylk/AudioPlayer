package cn.qylk.adapter;

import java.io.Closeable;
import java.io.IOException;

import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.qylk.R;
import cn.qylk.fragment.CommonAdapter;

/* 
 * @author qylk2012
 * 
 */
public class PersonalListAdapter extends BaseAdapter implements Closeable,
		CommonAdapter {
	private int curId;
	LayoutInflater layoutinflater;
	private Cursor mycursor;

	public PersonalListAdapter(LayoutInflater inflater, Cursor cursor) {
		layoutinflater = inflater;
		mycursor = cursor;
	}

	@Override
	public void close() throws IOException {
		mycursor.close();
	}

	@Override
	public int getCount() {
		return mycursor.getCount();
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
			convertView = layoutinflater.inflate(R.layout.personal_list, null);
		mycursor.moveToPosition(position);
		TextView tv = (TextView) convertView.findViewById(R.id.listitem);
		tv.setText(mycursor.getString(1));
		if (mycursor.getInt(0) == curId)
			tv.setTextColor(Color.BLUE);
		else
			tv.setTextColor(Color.WHITE);
		return convertView;
	}

	@Override
	public void setId(int id) {
		curId = id;
	}

	@Override
	public CharSequence getFirstChar(int position) {
		mycursor.moveToPosition(position);
		return mycursor.getString(1).subSequence(0, 1);
	}

	@Override
	public void RefreshList(Cursor c) {
		this.mycursor = c;
		notifyDataSetChanged();
	}

	@Override
	public Cursor getCursor() {
		return mycursor;
	}

}
