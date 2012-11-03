package cn.qylk;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.media.audiofx.Visualizer;
import android.media.audiofx.Visualizer.OnDataCaptureListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.qylk.adapter.trainAdptertest;
import cn.qylk.app.APP;
import cn.qylk.app.IPlayList.ListType;
import cn.qylk.app.ListTypeInfo;
import cn.qylk.app.MyAction;
import cn.qylk.app.PlayList;
import cn.qylk.app.SensorTest;
import cn.qylk.app.Tasks;
import cn.qylk.app.Tasks.onPostLrc;
import cn.qylk.app.Tasks.onPostPic;
import cn.qylk.app.TrackInfo;
import cn.qylk.database.MediaDatabase;
import cn.qylk.fragment.FragmentInfoInputDialog;
import cn.qylk.fragment.Fragment_Menus;
import cn.qylk.fragment.Fragment_MusicControls;
import cn.qylk.lrc.LRCbean;
import cn.qylk.lrc.MediaLyric;
import cn.qylk.lrc.ModifyLyric;
import cn.qylk.media.ArtistInfo;
import cn.qylk.myview.LrcPackage;
import cn.qylk.myview.LrcView;
import cn.qylk.myview.VisualizerView;
import cn.qylk.service.LocalService;
import cn.qylk.service.LocalService.TimerOut;
import cn.qylk.utils.SendAction;
import cn.qylk.utils.SendAction.ServiceControl;

/**
 * Main UI
 * 
 * @author qylk2012 <br>
 *         all rights resolved
 */
public class MainActivity extends Activity implements View.OnClickListener,
		onPostPic, onPostLrc {
	private class Receiver extends BroadcastReceiver {// 当后台播放下一首歌后，通知UI
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(MyAction.INTENT_UI_UPDATE)) {
				DealNew();// 开始更新界面
			} else if (intent.getAction().equals(MyAction.INTENT_EXIT)) {
				finish();
			}
		}
	}
	private static final int PREFERENCE_CODE = 1008;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	private Animation animationdown;
	private Visualizer mVisualizer;
	private Fragment_MusicControls controls;
	private TextView ctitle, artist, nexttitle, nolrctip;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			DealNew();
			super.handleMessage(msg);
		}
	};
	private boolean haslrc;
	private PlayList list;
	private LinearLayout lrcadj;
	private LrcView lrcview;
	private boolean panelopened, menuopend;
	private ImageView picshow;
	private Receiver receiver;// 消息接收器
	public LocalService Service;
	private Tasks tasks;
	private VisualizerView visualizerview;
	private TrackInfo trackentity;

	/**
	 * 处理设置
	 */
	private void Date_Service() {
		tasks = new Tasks();
		list = APP.list;
		trackentity = list.getTrackEntity();
		Service = ListUI.Service;
		Service.setTimeOutListener(new TimerOut() {

			@Override
			public void ontimeout(int pos) {
				if (haslrc)
					lrcview.updateView(pos);
			}
		});
		LoadConfig();
		handler.sendEmptyMessageDelayed(1, 300);
	}

	/**
	 * 所有UI更新从这开始的
	 */
	private void DealNew() {
		haslrc = false;
		updateUI();
		StartLoad(-1);
		tasks.startPicTask(this, false);
		lrcview.clearView();
		
	}

	/**
	 * 开始加载歌词，鉴于手动下载歌词可能会修改TAG信息，仍需要更新title显示，因而在这里 更新title显示，
	 * 
	 * @param id
	 */
	public void StartLoad(int id) {
		SetTitle();
		tasks.startInfoTask();
		tasks.startLrcTask(this, id);
	}

	private void FindView() {
		setContentView(R.layout.main);
		ctitle = (TextView) findViewById(R.id.titledisplay);
		artist = (TextView) findViewById(R.id.artistdis);
		nexttitle = (TextView) findViewById(R.id.nextmusic);
		nolrctip = (TextView) findViewById(R.id.lrclink);
		nolrctip.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		lrcadj = (LinearLayout) findViewById(R.id.lrcadj);
		visualizerview = (VisualizerView) findViewById(R.id.visualizer);
		lrcadj.setOnClickListener(this);
		findViewById(R.id.delay).setOnClickListener(this);
		findViewById(R.id.ok).setOnClickListener(this);
		findViewById(R.id.advance).setOnClickListener(this);
		nolrctip.setOnClickListener(this);
		picshow = (ImageView) findViewById(R.id.albumpic);
		lrcview = (LrcView) findViewById(R.id.lrcshow);
		FragmentTransaction transcation = getFragmentManager()
				.beginTransaction();
		controls = new Fragment_MusicControls();
		transcation.replace(R.id.content, controls);
		transcation.commit();
	}

	private void LoadConfig() {
		SetSensor(APP.Config.shake);
		SetLight(APP.Config.light);
		SetVisualizer(APP.Config.visualwave);
		lrcview.setFirstColor(APP.Config.lrccolor);
		lrcview.setShadow(APP.Config.lrcshadow);
	}

	private void SetVisualizer(boolean visual) {
		if (visual && mVisualizer == null) {
			setupVisualizerFxAndUi();
			visualizerview.setUpView();
		} else if (mVisualizer != null) {
			mVisualizer.setEnabled(false);
			mVisualizer.release();
			visualizerview.ClearView();
			mVisualizer = null;
		}
	}

	/**
	 * 获取歌词失败
	 */
	private void LrcFail(boolean showtips) {
		nolrctip.setVisibility(View.VISIBLE);// 无歌词提示
	}

	/**
	 * 获取歌词成功
	 */
	private void LrcSuc() {
		nolrctip.setVisibility(View.INVISIBLE);
		haslrc = true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			ArrayList<String> matches = data// 获取结果
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			for (String s : matches) {
				if (MediaDatabase.GetCursor(
						new ListTypeInfo(ListType.SEARCH, s)).moveToFirst()) {
					Toast.makeText(this, s, Toast.LENGTH_LONG).show();
					list.setListType(new ListTypeInfo(ListType.SEARCH, s));
					SendAction.SendControlMsg(ServiceControl.PLAYNEW);// 通知改变列表播放
					return;
				}
			}
			Toast.makeText(this, "no result", Toast.LENGTH_LONG).show();
		} else if (requestCode == PREFERENCE_CODE && resultCode == 1) {
			APP.Config.LoadConfig();
			LoadConfig();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		if (panelopened)
			showInfo();
		else
			super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {// 通过传过来的BUTTON ID可以判断Button的类型
		case R.id.lrclink:// 点击"歌词下载"
			OpenInfoDialog();// 出现对话框
			break;
		case R.id.delay:// 歌词延迟0.5秒
			lrcview.setOffset(500);
			break;
		case R.id.advance:// 歌词提前0.5秒
			lrcview.setOffset(-500);
			break;
		case R.id.ok:// 确认修改并保存歌词
			lrcadj.setVisibility(View.INVISIBLE);
			if (lrcview.GetOffset() == 0 || !haslrc) // 没有做出修改
				break;
			new ModifyLyric().SetOffset(lrcview.GetOffset()).ModifyandSave(
					lrcview.GetLrcPackage());// 开始保存
			break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		RegBc();// 注册广播
		FindView(); // UI控件初始化
		Date_Service(); // 处理设置
		super.onCreate(savedInstanceState);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("menu");// 必须创建一项，但不会让它显示出来
		animationdown = AnimationUtils.loadAnimation(this, R.anim.down);
		getFragmentManager().addOnBackStackChangedListener(
				new OnBackStackChangedListener() {
					@Override
					public void onBackStackChanged() {
						menuopend = !menuopend;
					}
				});
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onDestroy() {// 主进程结束，退出程序，该函数由系统自动调用
		unregisterReceiver(receiver);
		if (APP.Config.light)
			SetLight(false);
		if (APP.Config.visualwave)
			mVisualizer.release();
		super.onDestroy();
	}

	@Override
	public void onLrcGot(List<LRCbean> lrc, boolean usedweb) {
		if (lrc != null) {
			lrcview.setLyric(new LrcPackage(lrc, list.getTrackEntity()));
			lrcview.initLrcIndex(Service.GetMediaPos());
			LrcSuc();
		} else
			LrcFail(false);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (menuopend) {
			animationdown.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation animation) {
					getFragmentManager().popBackStack();
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
			});
			View menuview = getFragmentManager().findFragmentByTag("menu")
					.getView();
			menuview.startAnimation(animationdown);
		} else {
			if (panelopened)
				showInfo();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			Fragment_Menus me = new Fragment_Menus();
			ft.setCustomAnimations(R.anim.intest, R.anim.outtest);
			ft.add(R.id.content, me, "menu");
			ft.addToBackStack(null);
			ft.commit();
		}
		return false;
	}

	@Override
	public void onPicGot(Bitmap pic) {
		if (pic != null)
			picshow.setImageBitmap(pic);
		else
			picshow.setImageResource(R.drawable.album_list_default);
	}

	public void openPreference() {
		Intent intent = new Intent(this, Preference.class);
		startActivityForResult(intent, PREFERENCE_CODE);
	}

	public void setupVisualizerFxAndUi() {
		mVisualizer = new Visualizer(Service.getplayer().getAudioSessionId());
		// 参数内必须是2的位
		mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[0]);
		// 设置允许波形表示，并且捕获它
		mVisualizer.setDataCaptureListener(new OnDataCaptureListener() {
			@Override
			public void onWaveFormDataCapture(Visualizer visualizer,
					byte[] waveform, int samplingRate) {
			}

			@Override
			public void onFftDataCapture(Visualizer visualizer, byte[] fft,
					int samplingRate) {
				visualizerview.updateVisualizer(fft);
			}
		}, Visualizer.getMaxCaptureRate() / 2, false, true);
		mVisualizer.setEnabled(true);
	}

	/**
	 * 歌词信息填写界面
	 */
	private void OpenInfoDialog() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		FragmentInfoInputDialog dialog = new FragmentInfoInputDialog(
				trackentity, "搜索歌词");
		dialog.show(getFragmentManager(), "lrc");
		ft.addToBackStack(null);
	}

	/**
	 * 广播接受器注册
	 */
	private void RegBc() {// 注册broadcast的接收器
		receiver = new Receiver();// 新建接收器实例
		IntentFilter filter = new IntentFilter();// 过滤器
		filter.addAction(MyAction.INTENT_UI_UPDATE);
		filter.addAction(MyAction.INTENT_EXIT);
		registerReceiver(receiver, filter);
	}

	/**
	 * 屏幕常亮
	 */
	private void SetLight(boolean light) {
		if (light)
			getWindow()
					.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		else
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	public void SearchLrc() {
		final AutoCompleteTextView autv = new AutoCompleteTextView(this);
		autv.setThreshold(1);
		autv.setHint(R.string.searchtip);
		autv.setCompletionHint("qylk2012 supported");
		autv.setAdapter(new trainAdptertest(this, null));
		new AlertDialog.Builder(this).setTitle("search").setView(autv)
				.setPositiveButton(R.string.play, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String text = autv.getText().toString();
						if (MediaDatabase.GetCursor(
								new ListTypeInfo(ListType.SEARCH, text))
								.moveToFirst()) {
							list.setListType(new ListTypeInfo(ListType.SEARCH,
									text));
							SendAction.SendControlMsg(ServiceControl.PLAYNEW);
						} else
							Toast.makeText(MainActivity.this, "Nothing Found!",
									Toast.LENGTH_LONG).show();
					}
				}).setNegativeButton(R.string.operation_cancel, null).show();
	}

	/**
	 * 设置标题
	 */
	private void SetTitle() {
		ctitle.setText(list.getIndex() + 1 + "/" + list.getSum() + "\t"
				+ trackentity.title);
		artist.setText(trackentity.artist);
	}

	/**
	 * 甩歌
	 */
	private void SetSensor(boolean shake) {
		if (shake)
			SensorTest.getInstance().StartService();// 服务实例化
		else
			SensorTest.getInstance().StopService();// 暂停服务
	}

	/**
	 * 显示歌曲信息板
	 */
	public void showInfo() {
		final View v = findViewById(R.id.infoarea);
		final TextView artistinfo = (TextView) findViewById(R.id.artistinfo);
		panelopened = !panelopened;
		if (!panelopened) {
			Animation animation = AnimationUtils.loadAnimation(this,
					R.anim.hide);
			animation.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationEnd(Animation animation) {
					v.setVisibility(View.INVISIBLE);
					artistinfo.setText("");
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
				}
			});
			v.startAnimation(animation);
			return;
		} else {
			artistinfo.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					showInfo();
				}
			});
			Animation animation = AnimationUtils.loadAnimation(this,
					R.anim.show);
			v.setVisibility(View.VISIBLE);
			v.startAnimation(animation);
		}
		StringBuilder sb = new StringBuilder();
		String trackinfo = String.format(
				"曲名:%s\n艺术家:%s\n专辑:%s\n类型:%s\n大小:%.1f MB\n发行期:%s\n路径:%s\n",
				trackentity.title, trackentity.artist, trackentity.album,
				trackentity.mimetype, 1.0f * trackentity.size / 1024 / 1024,
				trackentity.year, trackentity.path);
		sb.append(trackinfo);
		sb.append("\r\n\r\n【");
		sb.append(trackentity.artist);
		sb.append("信息】\r\n");
		sb.append(ArtistInfo.GetInfoFromDatabase(trackentity.artist));
		artistinfo.setText(sb.toString());
	}

	public void showLrcAdjustbtns() {
		if (haslrc)
			lrcadj.setVisibility(View.VISIBLE);
	}

	public void startVoiceRecognition() {
		if (getPackageManager().queryIntentActivities(
				new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0).size() == 0) {
			Toast.makeText(MainActivity.this, "No Client", Toast.LENGTH_LONG)
					.show();
			return;
		}
		if (Service.IsPlaying())
			Service.PauseOrContinue(true);
		startVoiceRecognitionActivity();
	}

	public void delLyric() {
		if (!haslrc)
			return;
		haslrc = false;
		lrcview.clearView();
		MediaLyric.DelLrc(trackentity.title);
		nolrctip.setVisibility(View.VISIBLE);
	}

	/**
	 * 启动语音识别
	 */
	private void startVoiceRecognitionActivity() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "title or artist");
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
	}

	public void updateLrcPos(int pos) {
		if (haslrc)
			lrcview.initLrcIndex(pos); // 歌词进度调整
	}

	/**
	 * 换歌都要做的事：更新界面
	 */
	private void updateUI() {
		int duration = trackentity.duration;// 获取曲长
		controls.updateViewElements(duration);
		nexttitle.setText(list.getNextTitle());// 下一曲:
		picshow.setImageResource(R.drawable.loading);// 显示加载图标
	}
}
