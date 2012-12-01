package cn.qylk.fragment;

import java.io.File;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.qylk.R;
import cn.qylk.adapter.AAListAdapter;
import cn.qylk.app.APP;
import cn.qylk.app.IPlayList.ListType;
import cn.qylk.app.ListTypeInfo;
import cn.qylk.database.MediaDatabase;
import cn.qylk.utils.SendAction;
import cn.qylk.utils.SendAction.ServiceControl;

public class Fragment_ArtistAlbumList extends Fragment_ListFragmentBase
		implements OnItemClickListener {
	private ListType type;

	public Fragment_ArtistAlbumList(ListType type) {
		super(null);
		this.type = type;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo acmf = (AdapterContextMenuInfo) item
				.getMenuInfo();
		fetchCursor().moveToPosition(acmf.position);
		final int id = fetchCursor().getInt(3);
		if (item.getItemId() == 0) {
			APP.list.setListType(new ListTypeInfo(type, id, null, 0));
			SendAction.SendControlMsg(ServiceControl.PLAYNEW);
		} else if (item.getItemId() == 1) {
			new AlertDialog.Builder(getActivity())
					.setTitle("Delete this Artist or Album?")
					.setPositiveButton("确定", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							File[] files = MediaDatabase.GetPaths(id,
									type == ListType.ARTIST ? true : false);// 获取物理路径
							for (File f : files) {
								new File(f.getPath()).delete();// 删除SD卡上物理文件
							}
							MediaDatabase.removeArtistOrAlbum(id,
									type == ListType.ARTIST ? true : false);
							handler.sendEmptyMessage(0);
						}
					}).setNegativeButton(R.string.operation_cancel, null)
					.show();
		}
		return super.onContextItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		fetchCursor().moveToPosition(position);
		FragmentTransaction fragmentTransaction = getActivity()
				.getFragmentManager().beginTransaction();
		fragmentTransaction
				.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
		fragmentTransaction.replace(R.id.realtabcontent, new Fragment_PlayList(
				new ListTypeInfo(type, ((CommonAdapter) adapter).getCursor()
						.getInt(3))), "list");
		fragmentTransaction
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}

	@Override
	protected View InitView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.listview, null);
		listview = (ListView) view.findViewById(R.id.mlt);
		adapter = new AAListAdapter(inflater, getCursor());// 适配
		listview.setAdapter(adapter);// 绑定
		listview.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.add(Menu.NONE, 0, 1, R.string.play);
				menu.add(Menu.NONE, 1, 2, "移除");
			}
		});
		listview.setOnItemClickListener(this);
		return view;
	}

	@Override
	protected Cursor getCursor() {
		return type == ListType.ARTIST ? MediaDatabase.ArtistCursor()
				: MediaDatabase.AlbumCursor();
	}
}
