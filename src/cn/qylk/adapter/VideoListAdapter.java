package cn.qylk.adapter;


import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.qylk.R;
import cn.qylk.utils.StringUtils;

/**
 * artist和album列表适配器
 * 
 * @author qylk2012
 * 
 */
public class VideoListAdapter extends BaseAdapter {
	LayoutInflater layoutinflater;
	private Cursor mycursor;

	public VideoListAdapter(LayoutInflater inflater, Cursor cursor) {
		layoutinflater = inflater;
		mycursor = cursor;
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
			convertView = layoutinflater.inflate(R.layout.video_list, null);
		mycursor.moveToPosition(position);
		StringBuilder sb = new StringBuilder().append(String.valueOf(position))
				.append('.').append(mycursor.getString(0));
		((TextView) convertView.findViewById(R.id.videotitle)).setText(sb
				.toString());
		((TextView) convertView.findViewById(R.id.videotype)).setText("视频格式:"
				+ mycursor.getString(1));
		((TextView) convertView.findViewById(R.id.videoduration))
				.setText("视频长度：" + StringUtils.TimeFormat(mycursor.getInt(2)));
		return convertView;
	}

	public void swapCursor(Cursor cursor) {
		this.mycursor = cursor;
		notifyDataSetChanged();
	}

}
