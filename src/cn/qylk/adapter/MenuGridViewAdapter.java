package cn.qylk.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.qylk.R;

public class MenuGridViewAdapter extends BaseAdapter {
	int[] imgs = new int[] { R.drawable.menu_syssettings,
			R.drawable.menu_timer, R.drawable.menu_quickfind,
			R.drawable.menu_modifylrc, R.drawable.menu_delete,
			R.drawable.menu_refresh_list };
	String[] menu_name_array;
	LayoutInflater mLayoutInflater;
	ItemData mMenuItemData;

	public MenuGridViewAdapter(LayoutInflater Inflater) {
		mLayoutInflater = Inflater;
		menu_name_array = Inflater.getContext().getResources()
				.getStringArray(R.array.menu_item_name);
		mMenuItemData = new ItemData(imgs, menu_name_array,
				menu_name_array.length);
	}

	@Override
	public int getCount() {
		return mMenuItemData.getCount();
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
		convertView = mLayoutInflater.inflate(R.layout.gridview_item, null);
		ImageView imageView = (ImageView) convertView
				.findViewById(R.id.imageview);
		imageView.setBackgroundDrawable(mLayoutInflater.getContext()
				.getResources().getDrawable(mMenuItemData.getImgRes(position)));
		TextView textView = (TextView) convertView.findViewById(R.id.textview);
		textView.setText(mMenuItemData.getTitle(position));
		return convertView;
	}
}
