package cn.qylk;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.qylk.app.APP;
import cn.qylk.app.IPlayList.ListType;
import cn.qylk.app.ListTypeInfo;
import cn.qylk.app.MyAction;
import cn.qylk.app.Tasks;
import cn.qylk.app.Tasks.onPostPic;
import cn.qylk.app.TrackInfo;
import cn.qylk.fragment.Fragment_ListCategory;
import cn.qylk.fragment.Fragment_ListFragmentBase;
import cn.qylk.fragment.Fragment_PlayList;
import cn.qylk.fragment.Fragment_VideoList;
import cn.qylk.log.CrashHandler;
import cn.qylk.service.LocalService;
import cn.qylk.utils.SendAction;
import cn.qylk.utils.SendAction.ServiceControl;

/**
 * @author qylk2012<br>
 *         all rights resolved
 */
public class ListUI extends Activity implements OnClickListener, onPostPic,
		TabListener {
	private class AnActionModeOfEpicProportions implements ActionMode.Callback {
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			mode.finish();
			return true;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			menu.add("Search").setActionView(R.layout.edittext)
					.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {

		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return true;
		}
	}

	public static LocalService Service;
	static {
		System.loadLibrary("tagjni");// 加载JNI链接库，jni文件夹下有C源代码及make文件参考
	}
	private boolean isVisible;
	private ProgressBar pbar;
	private ImageView playorpause, next, icon;
	private BroadcastReceiver PosReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (isVisible)
				pbar.setProgress(intent.getExtras().getInt("pos"));
		}
	};

	private BroadcastReceiver Receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(MyAction.INTENT_UI_UPDATE)) {// 接收来自service的歌曲基本信息
				UpdateUI();
			} else if (action.equals(MyAction.INTENT_STATUS)) {// 接收播放状态消息
				if (Service != null)
					playorpause
							.setImageResource(Service.IsPlaying() ? R.drawable.btn_pause_bg
									: R.drawable.btn_play_bg);
			} else if (action.equals(MyAction.INTENT_EXIT)) {
				Exit(true);
			}
		}
	};

	private ServiceConnection ServiceConnector = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Service = ((LocalService.MyBinder) service).getService();
			Service.DesktopLrc(false);
			UpdateUI();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
	};

	private TextView title, artist;

	/**
	 * 退出界面
	 * 
	 * @param withservice
	 *            是否销毁Service
	 */
	private void Exit(boolean withservice) {
		unregisterReceiver(PosReceiver);
		unregisterReceiver(Receiver);
		if (withservice)
			stopService(new Intent(MyAction.INTENT_START_SERVICE));
		if (APP.Config.desklrc)
			Service.DesktopLrc(true);
		unbindService(ServiceConnector);// 解绑定
		Service = null;
		finish();
	}

	@Override
	public void onBackPressed() {
		if (getFragmentManager().getBackStackEntryCount() == 0) {// 根fragment
			Exit(!Service.IsPlaying());
		}
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {// 底部播放控制条
		switch (v.getId()) {
		case R.id.stplay:// 播放
			SendAction.SendControlMsg(ServiceControl.PAUSE_CONTINE);
			break;
		case R.id.stnext:// 下一曲
			SendAction.SendControlMsg(ServiceControl.NEXT);
			break;
		case R.id.sticon:// 进入播放界面
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_buttom,
					R.anim.slide_out_buttom);
			break;
		default:
			break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(android.R.style.Theme_Holo);
		super.onCreate(savedInstanceState);
		CrashHandler.GetInstance().init();// 注册异常捕获
		setContentView(R.layout.listui);
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayShowHomeEnabled(false);// 不显示主界面按钮
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionbar.setDisplayShowTitleEnabled(false);// 不显示标题
		Tab tab = actionbar.newTab();
		tab.setText("音乐库");
		tab.setTag(Fragment_ListCategory.class);
		tab.setTabListener(this);
		actionbar.addTab(tab, true);
		Tab tab2 = actionbar.newTab();
		tab2.setText("视频库");
		tab2.setTag(Fragment_VideoList.class);
		tab2.setTabListener(this);
		actionbar.addTab(tab2);

		playorpause = (ImageView) findViewById(R.id.stplay);
		next = (ImageView) findViewById(R.id.stnext);
		icon = (ImageView) findViewById(R.id.sticon);
		title = (TextView) findViewById(R.id.sttitle);
		artist = (TextView) findViewById(R.id.startist);
		pbar = (ProgressBar) findViewById(R.id.pbar);

		playorpause.setOnClickListener(this);
		icon.setOnClickListener(this);
		next.setOnClickListener(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyAction.INTENT_STATUS);// 过滤器设置
		filter.addAction(MyAction.INTENT_UI_UPDATE);
		filter.addAction(MyAction.INTENT_EXIT);
		registerReceiver(Receiver, filter);// 注册

		filter = new IntentFilter();
		filter.addAction(MyAction.INTENT_POSITION);// 过滤器设置
		registerReceiver(PosReceiver, filter);// 注册
		startService(new Intent(MyAction.INTENT_START_SERVICE));
		bindService(new Intent(MyAction.INTENT_START_SERVICE),
				ServiceConnector, Context.BIND_AUTO_CREATE); // 绑定后台服务
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("menu");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		OpenSearch();
		return false;
	}

	@Override
	protected void onPause() {
		isVisible = false;
		super.onPause();
	}

	@Override
	public void onPicGot(Bitmap pic) {
		if (pic == null)// 底部艺术家小图标
			icon.setImageResource(R.drawable.default_icon);
		else
			icon.setImageBitmap(pic);
	}

	@Override
	protected void onResume() {
		isVisible = true;
		UpdateUI();
		super.onResume();
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		int n = getFragmentManager().getBackStackEntryCount();
		for (int i = 0; i < n; i++)
			getFragmentManager().popBackStack();
		Fragment fragment = Fragment.instantiate(this,
				((Class<?>) tab.getTag()).getName(), null);
		ft.replace(R.id.realtabcontent, fragment, tab.getTag().toString());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	public void OpenSearch() {
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		fragmentTransaction
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		fragmentTransaction.replace(R.id.realtabcontent, new Fragment_PlayList(
				new ListTypeInfo(ListType.SEARCH, 0, "?")));
		fragmentTransaction.addToBackStack(null);// 无tag
		fragmentTransaction.commit();
		startActionMode(new AnActionModeOfEpicProportions());
	}

	private void UpdateUI() {
		if (!isVisible || Service == null)
			return;
		TrackInfo track = APP.list.getTrackEntity();
		pbar.setMax(track.duration);
		title.setText(track.title);// 标题
		artist.setText(track.artist);// 艺术家
		playorpause
				.setImageResource(Service.IsPlaying() ? R.drawable.btn_pause_bg
						: R.drawable.btn_play_bg);
		Tasks.startPicTask(ListUI.this, true);
		Fragment plist = getFragmentManager().findFragmentByTag("list");
		if (plist instanceof Fragment_ListFragmentBase)
			((Fragment_ListFragmentBase) plist).updateList();
	}
}
