package cn.qylk.service;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import cn.qylk.ListUI;
import cn.qylk.R;
import cn.qylk.app.APP;
import cn.qylk.app.APPUtils;
import cn.qylk.app.IPlayList.PlayMode;
import cn.qylk.app.MyAction;
import cn.qylk.app.PublicReceiver;
import cn.qylk.app.SensorTest;
import cn.qylk.app.SleepTimer;
import cn.qylk.app.TrackInfo;
import cn.qylk.lrc.LRCbean;
import cn.qylk.lrc.MediaLyric;
import cn.qylk.myview.DesktopLrc;
import cn.qylk.myview.LrcPackage;
import cn.qylk.utils.SendAction;
import cn.qylk.utils.SendAction.ServiceControl;

/**
 * 后台服务
 * 
 * @author qylk2011 all rights resolved
 */
public class LocalService extends Service { // 服务
	public class MyBinder extends Binder {
		public LocalService getService() {
			return LocalService.this;
		}
	}

	/**
	 * 因为本程序支持后台无界面播放，当前台UI销毁后，<br>
	 * 考虑到如有电话接入，也必须停止或暂停播放，因此只能<br>
	 * 把广播设置在service中
	 */
	private class T_Receiver extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			if (state == TelephonyManager.CALL_STATE_RINGING) {// 有电话接入
				if (mPlayer.isPlaying())
					PauseOrContinue(true);
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	private class task extends TimerTask {
		@Override
		public void run() {
			if (mPlayer.isPlaying()) {
				if (deslrc != null)
					deslrc.updateView(GetMediaPos());
				if (listener != null)
					listener.ontimeout(GetMediaPos());
				if (++i == 10)
					sendPosition(GetMediaPos());
			}
		}
	}

	public interface TimerOut {
		public void ontimeout(int pos);
	}

	private static final int NOTIFICATION_ID = -1;
	private MyBinder binder = new MyBinder();
	/**
	 * 播放远程控制
	 */
	private BroadcastReceiver Control = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(MyAction.INTENT_BTNACTION)) {
				Bundle bundle = intent.getExtras();
				ServiceControl control = (ServiceControl) bundle
						.getSerializable("action");
				switch (control) {
				case PRE:// 上一首
					pre();
					break;
				case NEXT:// 下一首
					next();
					break;
				case PAUSE_CONTINE:
					PauseOrContinue(mPlayer.isPlaying());
					break;
				case PAUSE:
					PauseOrContinue(true);
					break;
				case PLAYNEW:
					play();
					break;
				default:
					break;
				}
			} else if (action.equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
				if (APP.Config.unplunge)
					PauseOrContinue(true);
			}
		}
	};
	private DesktopLrc deslrc;
	private int i;
	private TimerOut listener;
	private AudioManager mAudioManager;
	private ComponentName mbCN;
	private MediaPlayer mPlayer;// 播放器
	private NotificationManager NM;
	private int SysMaxVolume;
	private Timer timer;
	private TrackInfo track;
	private WakeLock wakeLock;// 用于阻止睡眠的锁
	private boolean flag;

	/**
	 * 调整音量
	 * 
	 * @param value
	 *            ：(以100为最大值)
	 */
	public void AdjVolume(int value) {
		AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);// 音频服务
		am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) SysMaxVolume
				* value / 100, 0);
	}

	private void cancelNotification() {
		NM.cancel(NOTIFICATION_ID);
	}

	private void dealDeskLrc() {
		List<LRCbean> lrc = MediaLyric.FetchLyric(track, -1);
		if (lrc != null) {
			deslrc.setLyric(new LrcPackage(lrc, track));
			deslrc.initLrcIndex(GetMediaPos());
		}
	}

	public void DesktopLrc(boolean show) {
		if (show) {
			deslrc = new DesktopLrc(this);
			dealDeskLrc();
		} else if (deslrc != null) {
			deslrc.destoryView(this);
			deslrc = null;
		}
	}

	/**
	 * 获取播放实时位置
	 */
	public int GetMediaPos() {
		return mPlayer.getCurrentPosition();
	}

	private int getSysMaxVolume() {
		AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);// 音频服务
		return am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// 系统音量最大值，并非100
	}

	/**
	 * 获取音频流音量，已换算成百分制，以100为最大音量
	 */
	public int getVolume() {
		AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);// 音频服务
		return (int) am.getStreamVolume(AudioManager.STREAM_MUSIC) * 100
				/ SysMaxVolume;
	}

	public MediaPlayer getplayer() {
		return mPlayer;
	}

	/**
	 * 恢复上一次播放位置
	 */
	private void InitMediaStatus() {
		try {
			InitPlayer();
			mPlayer.seekTo(APP.Config.lastbarekpoint);
			SendStatusChanged(false);
		} catch (IOException e) {
			Log.v("TEST", "IOException in init mediaplayer");
		} catch (IllegalArgumentException e) {
			Log.v("TEST", "IllegalArgumentException in init mediaplayer");
		}
	}

	private void InitPlayer() throws IOException, IllegalArgumentException {
		SendAction.SendMsg_UI_Update();
		mPlayer.reset();
		mPlayer.setDataSource(track.path);
		mPlayer.prepare();
		if (flag) {
			TrackInfo info = APP.list.getPreviousEntity();
			if (info.id != track.id)
				APPUtils.SaveId3v2(info);
			flag = false;
		}
	}

	/**
	 * 是否在播放
	 * 
	 * @return
	 */
	public boolean IsPlaying() {
		if (mPlayer != null)
			return mPlayer.isPlaying();
		else
			return false;
	}

	/**
	 * 下一首
	 */
	public void next() {// 下一首
		APP.list.goNext();
		play();
	}

	private void Notification(int msg) {
		Notification(getResources().getString(msg));
	}

	/**
	 * 通知栏信息
	 */
	private void Notification(String msg) {
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, ListUI.class), 0);
		Notification notification = new Notification.Builder(this)
				.setContentIntent(contentIntent)
				.setSmallIcon(R.drawable.myicon)
				.setWhen(System.currentTimeMillis()).setAutoCancel(false)
				.setContentTitle("QMUSIC PLAYING").setContentText(msg)
				.getNotification();
		NM.notify(NOTIFICATION_ID, notification);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.v("TEST", "OnBind");
		return binder;
	}

	@Override
	public void onCreate() {
		Log.v("TEST", "Service Creating");
		PowerManager pm = (PowerManager) getApplicationContext()
				.getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Service");
		wakeLock.acquire();

		IntentFilter filter = new IntentFilter();
		filter.addAction(MyAction.INTENT_BTNACTION);// 过滤器设置
		filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
		registerReceiver(Control, filter);// 注册广播接收器

		SysMaxVolume = getSysMaxVolume();
		TelephonyManager manager = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		manager.listen(new T_Receiver(), PhoneStateListener.LISTEN_CALL_STATE);// 监听电话状态
		NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification(R.string.qylk);// 通知栏提示信息提示
		track = APP.list.getTrackEntity();
		Log.i("TEST", track.path);
		mPlayer = new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mPlayer.setOnCompletionListener(new OnCompletionListener() {// 一曲完成，自动播放下一曲
			@Override
			public void onCompletion(MediaPlayer mp) {
				if (APP.list.getMode() != PlayMode.Repeat_One) {
					next();
				} else
					play();
			}
		});
		InitMediaStatus();
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mbCN = new ComponentName(getPackageName(),
				PublicReceiver.class.getName());
		mAudioManager.registerMediaButtonEventReceiver(mbCN);// 注册一个MedioButtonReceiver广播监听
		startTimer();
		Log.v("TEST", "Service Created");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		timercancel();
		cancelNotification();
		SleepTimer.getInstance().cancel();
		APP.Config.StoreLast(GetMediaPos());
		SendStatusChanged(false);
		if (wakeLock != null) {
			wakeLock.release();// 释放睡眠锁
			wakeLock = null;
		}
		mPlayer.stop();
		mPlayer.release();
		mAudioManager.unregisterMediaButtonEventReceiver(mbCN);
		unregisterReceiver(Control);// 解注册
		DesktopLrc(false);
		SensorTest.getInstance().StopService();
		Log.v("TEST", "Service Destory");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;// 非粘滞
	}

	// public void effect() {
	// Equalizer mEqualizer = new Equalizer(0, mPlayer.getAudioSessionId());
	// mEqualizer.setEnabled(true);
	// short bands = mEqualizer.getNumberOfBands();
	// short minEQLevel = mEqualizer.getBandLevelRange()[0];
	// short maxEQLevel = mEqualizer.getBandLevelRange()[1];
	// mEqualizer.setBandLevel(band, (short) data);
	// }

	/**
	 * 暂停或继续
	 * 
	 * @param pause
	 *            true:pause<br>
	 *            false otherwise
	 */
	public void PauseOrContinue(boolean pause) {// 暂停播放
		if (pause) {
			mPlayer.pause();
			cancelNotification();
		} else {
			mPlayer.start();
			Notification(track.title + "-->" + track.artist);
		}
		SendStatusChanged(!pause);
	}

	/**
	 * 播放
	 */
	private void play() {// 播放
		try {
			Notification(track.title + "-->" + track.artist);
			InitPlayer();
			mPlayer.start();
			if (deslrc != null)
				dealDeskLrc();
		} catch (Exception e) {
			Log.e("TEST", "ERROR IN PLAY()");
		}
	}

	/**
	 * 上一首
	 */
	public void pre() {// 上一首
		APP.list.goLast();
		play();
	}

	private void sendPosition(int pos) {
		i = 0;
		Intent intent = new Intent(MyAction.INTENT_POSITION);
		intent.putExtra("pos", pos);
		sendBroadcast(intent);// 向UI发送消息,告知当前歌曲id
	}

	/**
	 * 发送后台歌曲播放状态
	 * 
	 * @return Bundle
	 */
	private void SendStatusChanged(boolean isplaying) {
		SendAction.SendStatusChanged(isplaying);
	}

	/**
	 * 设置播放位置
	 */
	public void SetMediaPos(int pos) {
		mPlayer.seekTo(pos);
	}

	public void setTimeOutListener(TimerOut listener) {
		this.listener = listener;
	}

	private void startTimer() {
		timer = new Timer();
		timer.schedule(new task(), 0, 100);
	}

	public void timercancel() {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
	}

	public void setRWFlag(boolean rw) {
		this.flag = rw;
	}
}