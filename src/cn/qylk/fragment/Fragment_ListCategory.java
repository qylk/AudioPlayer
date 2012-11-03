package cn.qylk.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import cn.qylk.R;
import cn.qylk.app.IPlayList.ListType;
import cn.qylk.app.ListTypeInfo;
import cn.qylk.myview.MyImageView;

public class Fragment_ListCategory extends Fragment implements
		MyImageView.OnViewClick {

	@Override
	public void onClick(int id) {
		FragmentTransaction fragmentTransaction = getActivity()
				.getFragmentManager().beginTransaction();
		fragmentTransaction
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		switch (id) {
		case R.id.lib:
			fragmentTransaction.replace(R.id.realtabcontent,
					new Fragment_PlayList(new ListTypeInfo(ListType.ALLSONGS,
							"all")));
			break;
		case R.id.artists:
			fragmentTransaction.replace(R.id.realtabcontent,
					new Fragment_ArtistAlbumList(ListType.ARTIST));
			break;
		case R.id.albums:
			fragmentTransaction.replace(R.id.realtabcontent,
					new Fragment_ArtistAlbumList(ListType.ALBUM));
			break;
		case R.id.fav:
			fragmentTransaction.replace(R.id.realtabcontent,
					new Fragment_PlayList(new ListTypeInfo(ListType.LOVE,
							String.valueOf(-1))));
			break;
		case R.id.newadd:
			fragmentTransaction.replace(R.id.realtabcontent,
					new Fragment_PlayList(new ListTypeInfo(ListType.RECENTADD,
							"rec")));
			break;
		case R.id.history:
			fragmentTransaction.replace(R.id.realtabcontent,
					new Fragment_PlayList(new ListTypeInfo(ListType.HISTORY,
							"his")));
			break;
		case R.id.searchopen:
			fragmentTransaction.replace(R.id.realtabcontent,
					new Fragment_PlayList(
							new ListTypeInfo(ListType.SEARCH, "?")));
			break;
		default:
			break;
		}
		fragmentTransaction.addToBackStack(null);// 无tag
		fragmentTransaction.commit();
	}

	// /**
	// * 更改个人列表名称
	// */
	// private void DealItem() {
	// Cursor cursor = MediaDatabase.GetPlayListsWithCursor();
	// int i = BASE;
	// sum = BASE + 1;// 列表最后一定有个“新建列表”，所以数量至少BASE+1;
	// while (cursor.moveToNext() && i < MAX - 1) {
	// plistids[i - BASE] = cursor.getInt(0);
	// itemData.SetItemTitle(i++, cursor.getString(1));
	// sum++;
	// }
	// cursor.close();
	// }

	// public interface OnGridViewItemClicked {
	// public void onclick(int position, int sum, int[] plistids);
	// }
	//
	// public void SetItemClickListerner(OnGridViewItemClicked l) {
	// listener = l;
	// }

	// @Override
	// public void ondelete(int index) {
	// MediaDatabase.RemovePlayList(plistids[index - BASE]);
	// DealItem();
	// mGridViewAdapter.refreshData(itemData, sum);
	// }

	// @Override
	// public void onItemClick(AdapterView<?> parent, View view, int position,
	// long id) {
	// if (position == sum - 1) {// 最后的”新建列表“被按下
	// if (sum == MAX)// 已达数量上限
	// Toast.makeText(getActivity(), "projected", Toast.LENGTH_LONG)
	// .show();
	// else {// 新建一个列表
	// final EditText edittext = new EditText(getActivity());
	// edittext.setSingleLine(true);
	// edittext.setText("<NEW LIST>");
	// new AlertDialog.Builder(getActivity())
	// .setTitle(R.string.newlist)
	// .setView(edittext)
	// .setPositiveButton(R.string.save,
	// new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// String name = edittext.getText()
	// .toString();
	// if (name.length() == 0)
	// return;
	// MediaDatabase.NewPlayList(name);
	// DealItem();
	// // mGridViewAdapter.refreshData(itemData,sum);
	// }
	// })
	// .setNegativeButton(R.string.operation_cancel, null)
	// .show();
	// }
	// } else
	// dispatchClick(position, sum, plistids);
	// }
	//
	// public void dispatchClick(int position, int sum, int[] plistids) {
	// FragmentTransaction fragmentTransaction = getActivity()
	// .getSupportFragmentManager().beginTransaction();
	// fragmentTransaction.setCustomAnimations(R.anim.push_left_in,
	// R.anim.push_left_out);
	// switch (position) {
	// case 0:// 全部
	// fragmentTransaction.replace(R.id.realtabcontent,
	// new Fragment_MusicList(ListProvider.LIST_ALLSONGS, "all"));
	// break;
	// case 1:// 歌手
	// case 2:// 专辑
	// fragmentTransaction.replace(R.id.realtabcontent,
	// new Fragment_ArtistAlbumList(position));
	// break;
	// case 3:// 收藏
	// fragmentTransaction.replace(
	// R.id.realtabcontent,
	// new Fragment_MusicList(ListProvider.LIST_LOVE, String
	// .valueOf(-1)));
	// break;
	// case 4:// 历史
	// fragmentTransaction.replace(R.id.realtabcontent,
	// new Fragment_MusicList(ListProvider.LIST_HISTORY, "his"));
	// break;
	// case 5:// 文件夹
	// return;
	// case 6:
	// fragmentTransaction.replace(R.id.realtabcontent,
	// new Fragment_MusicList(ListProvider.LIST_RECENTADD, "rec"));
	// break;
	// case 7:// list1
	// case 8:// list2
	// case 9:// list3
	// case 10:// list4
	// fragmentTransaction.replace(
	// R.id.realtabcontent,
	// new Fragment_MusicList(ListProvider.LIST_PLAY, String
	// .valueOf(plistids[position - 7])));
	// break;
	// default:
	// break;
	// }
	// fragmentTransaction.addToBackStack(null);// 无tag
	// fragmentTransaction.commit();
	// }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ScrollView ListGrid = (ScrollView) inflater.inflate(R.layout.iconlist,
				null);
		((MyImageView) ListGrid.findViewById(R.id.lib)).setOnClickIntent(this);
		((MyImageView) ListGrid.findViewById(R.id.artists))
				.setOnClickIntent(this);
		((MyImageView) ListGrid.findViewById(R.id.albums))
				.setOnClickIntent(this);
		((MyImageView) ListGrid.findViewById(R.id.fav)).setOnClickIntent(this);
		((MyImageView) ListGrid.findViewById(R.id.newadd))
				.setOnClickIntent(this);
		((MyImageView) ListGrid.findViewById(R.id.history))
				.setOnClickIntent(this);
		((MyImageView) ListGrid.findViewById(R.id.searchopen))
				.setOnClickIntent(this);
		// mGridViewAdapter = new ListGridViewAdapter(inflater, itemData,
		// R.layout.metroview, sum);
		// ListGrid.setAdapter(mGridViewAdapter);
		// mGridViewAdapter.setOnDeleteListener(this);
		// ListGrid.setOnItemClickListener(this);
		// ListGrid.setOnItemLongClickListener(new OnItemLongClickListener() {
		//
		// @Override
		// public boolean onItemLongClick(AdapterView<?> parent, View view,
		// final int position, long id) {
		// if (position > BASE - 1 && position != sum - 1) {
		// mGridViewAdapter.PrepareDelete(position);
		// ListGrid.requestFocus();// 获取焦点，否则此时按Back键无法响应ListGrid的按键回调方法
		// }
		// return true;
		// }
		// });
		// ListGrid.setOnKeyListener(new OnKeyListener() {// ListGrid的按键回调方法
		//
		// @Override
		// public boolean onKey(View v, int keyCode, KeyEvent event) {
		// if (keyCode == KeyEvent.KEYCODE_BACK)
		// if (mGridViewAdapter.IsDeleting()) {
		// mGridViewAdapter.CancelDeleting();// 关闭删除模式，即让图标上的小红叉消失
		// return true;
		// }
		// return false;
		// }
		// });
		// getSherlockActivity().getSupportActionBar().setTitle("home");
		return ListGrid;
	}
}
