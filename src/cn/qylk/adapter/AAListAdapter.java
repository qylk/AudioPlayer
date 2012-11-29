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

/**
 * artist和album列表适配器
 * 
 * @author qylk2012
 * 
 */
public class AAListAdapter extends BaseAdapter implements Closeable,CommonAdapter {
	class ViewHolder {
		public TextView tv1, tv2, tv3;
	}
	private int curId;
	LayoutInflater layoutinflater;
	private Cursor mycursor;
	public AAListAdapter(LayoutInflater inflater, Cursor cursor) {
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
		ViewHolder holder;
		if (convertView == null) {
			convertView = layoutinflater.inflate(R.layout.artist_album_list,
					null);
			holder = new ViewHolder();
			holder.tv1 = (TextView) convertView.findViewById(R.id.listitem);
			holder.tv2 = (TextView) convertView.findViewById(R.id.sum);
			holder.tv3 = (TextView) convertView.findViewById(R.id.artist4this);
			convertView.setTag(holder);// 以tag寻找更快
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		mycursor.moveToPosition(position);
		StringBuilder sb = new StringBuilder().append(String.valueOf(position))
				.append('.').append(mycursor.getString(0));
		holder.tv1.setText(sb.toString());
		holder.tv2.setText(mycursor.getInt(1) + "首");
		holder.tv3.setText(mycursor.getString(2));
		if (mycursor.getInt(3) == curId) {
			holder.tv1.setTextColor(Color.BLUE);
			holder.tv2.setTextColor(Color.BLUE);
			holder.tv3.setTextColor(Color.BLUE);
		} else {
			holder.tv1.setTextColor(Color.WHITE);
			holder.tv2.setTextColor(Color.WHITE);
			holder.tv3.setTextColor(Color.WHITE);
		}
		return convertView;
	}

	@Override
	public void setId(int id) {
		curId = id;
	}

	@Override
	public CharSequence getFirstChar(int position) {
		mycursor.moveToPosition(position);
		return mycursor.getString(0).subSequence(0,1);
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
