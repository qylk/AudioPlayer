package cn.qylk.fragment;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CheckBox;
import android.widget.ListView;
import cn.qylk.R;
import cn.qylk.adapter.MusicListAdapter;
import cn.qylk.app.APP;
import cn.qylk.app.ListTypeInfo;
import cn.qylk.database.MediaDatabase;
import cn.qylk.dialog.PersonalListChoosingDialog;
import cn.qylk.utils.SendAction;
import cn.qylk.utils.SendAction.ServiceControl;

/**
 * 歌曲列表基类
 * 
 * @author qylk 2012-04-01
 */
public class Fragment_PlayList extends Fragment implements OnItemClickListener,
		OnItemLongClickListener {
	private class AnActionModeOfEpicProportions implements ActionMode.Callback {
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			DispatchMenuClick(item.getItemId());
			mode.finish();
			return true;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			menu.add(Menu.NONE, 0, 1, "Share").setShowAsAction(
					MenuItem.SHOW_AS_ACTION_ALWAYS);
			menu.add(Menu.NONE, 1, 2, "Add to..").setShowAsAction(
					MenuItem.SHOW_AS_ACTION_ALWAYS);
			menu.add(Menu.NONE, 2, 3, "Remove").setShowAsAction(
					MenuItem.SHOW_AS_ACTION_ALWAYS);
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			adapter.OnActionMode(false, 0);
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return true;
		}
	}

	public MusicListAdapter adapter;
	private Handler handler;
	private ListTypeInfo ListInfo;
	public ListView musiclist;

	public Fragment_PlayList(ListTypeInfo listInfo) {
		ListInfo = listInfo;
	}

	@Deprecated
	public Fragment_PlayList() {
	}

	private void AddToPersonalList(Integer[] ids) {
		new PersonalListChoosingDialog(getActivity()).Build(ids).show();
	}

	/**
	 * 处理actionmode的菜单被按事件
	 * 
	 * @param id
	 */
	private void DispatchMenuClick(int id) {
		Integer[] ids = adapter.GetSelectedList();// 已选列表
		if (ids.length == 0)// 空列表
			return;
		switch (id) {
		case 0:// 发送
			Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
			intent.setType("audio/mpeg");
			File[] files = MediaDatabase.GetPaths(ids);// 获取物理路径
			ArrayList<Uri> bfiles = new ArrayList<Uri>(files.length);
			for (File f : files)
				bfiles.add(Uri.fromFile(f));
			intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, bfiles);
			startActivity(intent);// 开始发送
			break;
		case 1:// 添加到列表
			AddToPersonalList(ids);
			break;
		case 2:// 删除
			remove(ids);
			break;
		default:
			break;
		}
	}

	private void DispatchMusicItemClick(int position) {
		ListInfo.pos = position;
		APP.list.setListType(ListInfo);
		SendAction.SendControlMsg(ServiceControl.PLAYNEW);
	}

	private Cursor getCursor() {
		return MediaDatabase.GetCursor(ListInfo);
	}

	/**
	 * 取得列表
	 * 
	 * @param inflater
	 */
	private void GetList(LayoutInflater inflater) {
		adapter = new MusicListAdapter(inflater, getCursor());// 适配器
		musiclist.setAdapter(adapter);// 绑定
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.listview, null);
		musiclist = (ListView) view.findViewById(R.id.mlt);
		GetList(inflater);
		musiclist.setOnItemClickListener(this);
		musiclist.setOnItemLongClickListener(this);
		musiclist.setSelectionFromTop(APP.list.getIndex(), 150);
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				ReFreshList();
				super.handleMessage(msg);
			}
		};
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (!adapter.getState())
			DispatchMusicItemClick(position);
		else {
			adapter.ToggleSelection(position);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (adapter.getState())
			return true;
		getActivity().startActionMode(new AnActionModeOfEpicProportions());// 开启actionmode菜单
		adapter.OnActionMode(true, position);
		return true;
	}

	private void PostListChangedSignal() {
		SendAction.SendListChangedSignal(ListInfo);
	}

	/**
	 * 刷新列表
	 */
	private void ReFreshList() {
		adapter.swapCursor(getCursor());
		PostListChangedSignal();
	}

	/**
	 * 移除歌曲
	 * 
	 * @param ids
	 */
	public void remove(final Integer[] ids) {
		final CheckBox cb = (CheckBox) getActivity().getLayoutInflater()
				.inflate(R.layout.deleteoption, null);
		new AlertDialog.Builder(getActivity())
				.setTitle("Remove/Delete " + ids.length + " Files").setView(cb)
				.setPositiveButton("YES", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								if (cb.isChecked()) {// 物理删除
									File[] files = MediaDatabase.GetPaths(ids);// 获取物理路径
									for (File f : files) {
										new File(f.getPath()).delete();// 删除SD卡上物理文件
									}
								}
								for (int id : ids) {
									MediaDatabase.removeAudio(id);// 删除数据库记录
								}
								handler.sendEmptyMessage(0);
							}
						}).start();
					}
				}).setNegativeButton("NO", null).show();
	}

}
