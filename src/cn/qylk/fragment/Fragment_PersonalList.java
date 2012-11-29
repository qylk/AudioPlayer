package cn.qylk.fragment;

import android.app.FragmentTransaction;
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
import cn.qylk.adapter.PersonalListAdapter;
import cn.qylk.app.APP;
import cn.qylk.app.IPlayList.ListType;
import cn.qylk.app.ListTypeInfo;
import cn.qylk.database.MediaDatabase;
import cn.qylk.utils.SendAction;
import cn.qylk.utils.SendAction.ServiceControl;

public class Fragment_PersonalList extends Fragment_ListFragmentBase implements
		OnItemClickListener {

	public Fragment_PersonalList() {
		super(null);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo acmf = (AdapterContextMenuInfo) item
				.getMenuInfo();
		fetchCursor().moveToPosition(acmf.position);
		APP.list.setListType(new ListTypeInfo(ListType.PERSONAL, fetchCursor()
				.getInt(0), null, 0));
		SendAction.SendControlMsg(ServiceControl.PLAYNEW);
		return super.onContextItemSelected(item);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		((CommonAdapter) adapter).getCursor().moveToPosition(position);
		FragmentTransaction fragmentTransaction = getActivity()
				.getFragmentManager().beginTransaction();
		fragmentTransaction
				.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
		fragmentTransaction.replace(R.id.realtabcontent, new Fragment_PlayList(
				new ListTypeInfo(ListType.PERSONAL, fetchCursor().getInt(0))),
				"list");
		fragmentTransaction
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}

	@Override
	protected Cursor getCursor() {
		return MediaDatabase.GetPersonalListUsingCursor();
	}

	@Override
	protected View InitView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.listview, null);
		listview = (ListView) view.findViewById(R.id.mlt);
		adapter = new PersonalListAdapter(inflater, getCursor());// 适配
		listview.setAdapter(adapter);// 绑定
		listview.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.add(Menu.NONE, 0, 1, R.string.play);
			}
		});
		listview.setOnItemClickListener(this);
		return view;
	}
}
