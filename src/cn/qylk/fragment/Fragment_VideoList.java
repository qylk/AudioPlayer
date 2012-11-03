package cn.qylk.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.qylk.R;
import cn.qylk.VideoActivity;
import cn.qylk.adapter.VideoListAdapter;
import cn.qylk.database.MediaDatabase;
import cn.qylk.utils.SendAction;
import cn.qylk.utils.SendAction.ServiceControl;

public class Fragment_VideoList extends Fragment implements
		OnItemClickListener {
	private VideoListAdapter adapter;
	private ListView videolist;

	private Cursor GetCursor() {
		return MediaDatabase.VideoCursor();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.listview, null);
		videolist = (ListView) view.findViewById(R.id.mlt);
		adapter = new VideoListAdapter(inflater, GetCursor());// 适配
		videolist.setAdapter(adapter);// 绑定
		videolist.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		SendAction.SendControlMsg(ServiceControl.PAUSE);
		startActivity(new Intent(getActivity(), VideoActivity.class).putExtra(
				"position", position));
	}
}
