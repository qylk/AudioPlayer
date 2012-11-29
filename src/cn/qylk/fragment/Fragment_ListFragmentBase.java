package cn.qylk.fragment;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.qylk.app.APP;
import cn.qylk.app.ListTypeInfo;
import cn.qylk.utils.SendAction;

public abstract class Fragment_ListFragmentBase extends Fragment implements
		OnScrollListener, Callback {

	protected ListView listview;
	protected BaseAdapter adapter;
	private WindowManager mWindowManager;
	private TextView FirstLetterTip;
	private CharSequence mPrevLetter = "";
	protected Handler handler = new Handler(this);;
	protected ListTypeInfo ListInfo;

	public Fragment_ListFragmentBase(ListTypeInfo listInfo) {
		ListInfo = listInfo;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		FirstLetterTip = new TextView(inflater.getContext());
		FirstLetterTip.setWidth(60);
		FirstLetterTip.setHeight(60);
		FirstLetterTip.setTextColor(Color.BLUE);
		FirstLetterTip.setTextSize(26);
		FirstLetterTip.setBackgroundColor(Color.YELLOW);
		FirstLetterTip.setGravity(Gravity.CENTER);

		mWindowManager = (WindowManager) getActivity().getSystemService(
				Context.WINDOW_SERVICE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mWindowManager.addView(FirstLetterTip, lp);
		View v = InitView(inflater, container, savedInstanceState);
		listview.setOnScrollListener(this);
		updateList();
		FirstLetterTip.setVisibility(View.INVISIBLE);
		return v;
	}

	protected abstract View InitView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState);

	protected abstract Cursor getCursor();

	public void updateList() {
		ListTypeInfo info = APP.list.getTypeInfo();
		((CommonAdapter) adapter).setId(info.para);
		adapter.notifyDataSetInvalidated();
	}

	protected Cursor fetchCursor() {
		return ((CommonAdapter) adapter).getCursor();
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case 0:
			ReFreshList();
			break;
		case 1:
			FirstLetterTip.setVisibility(View.INVISIBLE);
			break;
		}
		return true;
	}

	private void ReFreshList() {
		((CommonAdapter) adapter).RefreshList(getCursor());
		SendAction.SendListChangedSignal(ListInfo);
	}

	@Override
	public void onDestroyView() {
		mWindowManager.removeView(FirstLetterTip);
		super.onDestroyView();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (totalItemCount < firstVisibleItem + 5)
			return;
		CharSequence head = ((CommonAdapter) adapter)
				.getFirstChar(firstVisibleItem + 4);
		if (!head.equals(mPrevLetter)) {
			FirstLetterTip.setVisibility(View.VISIBLE);
			FirstLetterTip.setText(head);
			handler.removeMessages(1);
			handler.sendEmptyMessageDelayed(1, 1000);
			mPrevLetter = head;
		}
	}
}
