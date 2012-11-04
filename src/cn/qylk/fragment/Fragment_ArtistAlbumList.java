package cn.qylk.fragment;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.qylk.R;
import cn.qylk.adapter.AAListAdapter;
import cn.qylk.app.APP;
import cn.qylk.app.ListTypeInfo;
import cn.qylk.app.IPlayList.ListType;
import cn.qylk.database.MediaDatabase;
import cn.qylk.utils.SendAction;
import cn.qylk.utils.SendAction.ServiceControl;

public class Fragment_ArtistAlbumList extends Fragment implements
		OnItemClickListener {
	private ListView aalist;
	private AAListAdapter adapter;
	private ListType type;

	public Fragment_ArtistAlbumList(ListType type) {
		this.type = type;
	}

	private Cursor GetCursor() {
		return type == ListType.ARTIST ? MediaDatabase.ArtistCursor()
				: MediaDatabase.AlbumCursor();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo acmf = (AdapterContextMenuInfo) item
				.getMenuInfo();
		adapter.getCursor().moveToPosition(acmf.position);
		APP.list.setListType(new ListTypeInfo(type, adapter.getCursor().getString(0),0));
		SendAction.SendControlMsg(ServiceControl.PLAYNEW);
		return super.onContextItemSelected(item);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.listview, null);
		aalist = (ListView) view.findViewById(R.id.mlt);
		adapter = new AAListAdapter(inflater, GetCursor());// 适配
		aalist.setAdapter(adapter);// 绑定
		aalist.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.add(Menu.NONE, 0, 1, R.string.play);
			}
		});
		aalist.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		adapter.getCursor().moveToPosition(position);
		FragmentTransaction fragmentTransaction = getActivity()
				.getFragmentManager().beginTransaction();
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
		fragmentTransaction.replace(R.id.realtabcontent,
				new Fragment_PlayList(new ListTypeInfo(type, adapter.getCursor().getString(0))),"list");
		 fragmentTransaction
		 .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}

	@SuppressWarnings("unused")
	private void ReFreshList() {
		adapter.swapCursor(GetCursor());
	}
}
