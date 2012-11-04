package cn.qylk.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import cn.qylk.ListUI;
import cn.qylk.MainActivity;
import cn.qylk.R;
import cn.qylk.app.APP;
import cn.qylk.app.IPlayList.PlayMode;
import cn.qylk.app.MyAction;
import cn.qylk.app.PlayList;
import cn.qylk.database.MediaDatabase;
import cn.qylk.service.LocalService;
import cn.qylk.utils.StringUtils;

public class Fragment_MusicControls extends Fragment implements OnClickListener {
	private class PosReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(MyAction.INTENT_POSITION))
				posbar.setProgress(intent.getExtras().getInt("pos"));
			else if (intent.getAction().equals(MyAction.INTENT_STATUS)) {// 接收播放状态消息
				playorpause.setImageResource(intent.getExtras().getBoolean(
						"isplaying") ? R.drawable.btn_pause_bg
						: R.drawable.btn_play_bg);
			}
		}
	}

	private TextView dura, curpos;
	private PlayList list = APP.list;
	private SeekBar posbar;
	private PosReceiver posreceiver;
	private LocalService Service = ListUI.Service;
	private ImageView voicesearch, info, playorpause, love, next, pre, mode,
			eq;

	private void InitView(View root) {
		voicesearch = (ImageView) root.findViewById(R.id.voicesearch);
		info = (ImageView) root.findViewById(R.id.openinfo);
		playorpause = (ImageView) root.findViewById(R.id.play_pause);
		love = (ImageView) root.findViewById(R.id.love);
		dura = (TextView) root.findViewById(R.id.dura);
		curpos = (TextView) root.findViewById(R.id.pos);
		next = (ImageView) root.findViewById(R.id.next);
		pre = (ImageView) root.findViewById(R.id.pre);
		mode = (ImageView) root.findViewById(R.id.mode);
		eq = (ImageView) root.findViewById(R.id.seteq);
		posbar = (SeekBar) root.findViewById(R.id.progressbar);
		info.setOnClickListener(this);
		playorpause.setOnClickListener(this);
		mode.setOnClickListener(this);
		voicesearch.setOnClickListener(this);
		next.setOnClickListener(this);
		pre.setOnClickListener(this);
		love.setOnClickListener(this);
		eq.setOnClickListener(this);
		posreceiver = new PosReceiver();
		IntentFilter filter = new IntentFilter();// 过滤器
		filter.addAction(MyAction.INTENT_POSITION);
		filter.addAction(MyAction.INTENT_STATUS);
		getActivity().registerReceiver(posreceiver, filter);

		posbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // （随上面的线程每秒发生一次）
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				curpos.setText(StringUtils.TimeFormat(progress));// 更新显示时间进度
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) { // 停止拖拉进度条
				Service.SetMediaPos(seekBar.getProgress());// 调整歌曲进度
				((MainActivity) getActivity()).updateLrcPos(seekBar
						.getProgress());
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {// 通过传过来的BUTTON ID可以判断Button的类型
		case R.id.play_pause:
			Service.PauseOrContinue(Service.IsPlaying());
			break;
		case R.id.pre:// 上一首
			Service.pre();
			break;
		case R.id.next:// 下一首
			Service.next();
			break;
		case R.id.love:
			boolean isloved = MediaDatabase.IsLoved(list.getId());
			love.setImageResource(isloved ? R.drawable.tool_favorite_off
					: R.drawable.tool_favorite_on);
			MediaDatabase.recordLove(list.getId(), !isloved);
			break;
		case R.id.mode:// 播放模式
			ShiftMode();
			break;
		case R.id.openinfo:// 显示歌手信息
			((MainActivity) getActivity()).showInfo();
			break;
		case R.id.seteq:
			CharSequence[] presets = getResources().getStringArray(
					R.array.eqpresets);
			new AlertDialog.Builder(getActivity())
					.setTitle("均衡器设置")
					.setSingleChoiceItems(presets, Service.getEQ(),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Service.setEQ((short) which);
								}
							}).show();
			break;
		case R.id.voicesearch:
			((MainActivity) getActivity()).startVoiceRecognition();
			break;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.buttons, null);
		InitView(root);
		return root;
	}

	@Override
	public void onDestroyView() {
		getActivity().unregisterReceiver(posreceiver);
		super.onDestroyView();
	}

	/**
	 * 播放模式转换
	 */
	private void ShiftMode() {
		int res;
		PlayMode mode2;
		switch (list.getMode()) {
		case Normal:
			res = R.drawable.tool_shuffle;
			mode2 = PlayMode.Shuffle;
			break;
		case Shuffle:
			res = R.drawable.tool_repeat_none;
			mode2 = PlayMode.Repeat_One;
			break;
		case Repeat_One:
		default:
			res = R.drawable.tool_repeat_all;
			mode2 = PlayMode.Normal;
			break;
		}
		mode.setImageResource(res);
		list.setMode(mode2);
	}

	public void updateViewElements(int dur) {
		posbar.setMax(dur);// 设置进度条最大值
		posbar.setProgress(0);
		dura.setText(StringUtils.TimeFormat(dur)); // 显示歌曲时间
		boolean isloved = MediaDatabase.IsLoved(list.getId());
		love.setImageResource(isloved ? R.drawable.tool_favorite_on
				: R.drawable.tool_favorite_off);
		if (Service.IsPlaying())
			playorpause.setImageResource(R.drawable.btn_pause_bg);// 更新按钮图标
	}

}
