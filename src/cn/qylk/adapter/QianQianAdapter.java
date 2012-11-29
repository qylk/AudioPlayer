package cn.qylk.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.qylk.R;
import cn.qylk.QianQianLyrics.LyricResults;

public class QianQianAdapter extends BaseAdapter {
	Context context;
	List<LyricResults> items;

	public QianQianAdapter(List<LyricResults> items, Context ct) {
		context = ct;
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(context).inflate(
				R.layout.dropdown_text, null);
		((TextView) convertView.findViewById(R.id.itemdis1)).setText(items
				.get(position).track);
		((TextView) convertView.findViewById(R.id.itemdis2)).setText(items
				.get(position).artist);
		return convertView;
	}

}
