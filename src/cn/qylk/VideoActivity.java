package cn.qylk;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cn.qylk.database.MediaDatabase;
import cn.qylk.utils.StringUtils;

/**
 * @author qylk2012
 * 
 */
// TODO 按下home见后回来无法继续播放的问题
public class VideoActivity extends Activity implements OnClickListener {
	private class OnDoubleClick extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			surfaceView
					.getHolder()
					.setFixedSize(
							(int) (height * (widescale ? scale_43 : scale_169)),
							height);
			widescale = !widescale;
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			showControls();
			return super.onSingleTapConfirmed(e);
		}
	}
	private final class SurfaceCallback implements SurfaceHolder.Callback {
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			mediaPlayer.setDisplay(holder);
			mediaPlayer.setScreenOnWhilePlaying(true);
			PowerManager pm = (PowerManager) getApplicationContext()
					.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm
					.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Service");
			wakeLock.acquire();
			play(position);
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (mediaPlayer != null) {
				position = mediaPlayer.getCurrentPosition();
				mediaPlayer.reset();
				mediaPlayer.release();
				mediaPlayer = null;
			}
		}
	}

	private class task extends TimerTask {
		@Override
		public void run() {
			if (mediaPlayer.isPlaying()) {
				handler.sendEmptyMessage(0);
			}
		}
	}

	private static final float scale_169 = 16.0f / 9;// 16：9尺寸

	private static final float scale_43 = 4.0f / 3;// 16：9尺寸

	private RelativeLayout controller;
	private Cursor cursor;
	private GestureDetector gd;
	private Handler handler;
	private int height;
	private MediaPlayer mediaPlayer;
	private ImageView playButton;
	private TextView pos, dura, title;
	private int position;
	private SurfaceView surfaceView;
	private Timer timer;
	private SeekBar videobar;
	private WakeLock wakeLock;

	private boolean widescale;

	private void next() {
		if (!cursor.moveToNext())
			cursor.moveToFirst();
		play(0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.videoplay:
			if (mediaPlayer.isPlaying())
				mediaPlayer.pause();
			else
				mediaPlayer.start();
			playButton
					.setImageResource(mediaPlayer.isPlaying() ? R.drawable.btn_pause_bg
							: R.drawable.btn_play_bg);
			break;
		case R.id.videopre:
			pre();
			break;
		case R.id.videonext:
			next();
			break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.video);
		int index;
		if (savedInstanceState == null)
			index = getIntent().getExtras().getInt("position");
		else {
			index = savedInstanceState.getInt("index");
			position = savedInstanceState.getInt("pos");
		}
		playButton = (ImageView) this.findViewById(R.id.videoplay);
		videobar = (SeekBar) findViewById(R.id.videobar);
		title = (TextView) findViewById(R.id.videotitle);
		pos = (TextView) findViewById(R.id.videopos);
		dura = (TextView) findViewById(R.id.videodura);
		controller = (RelativeLayout) findViewById(R.id.controller);
		playButton.setOnClickListener(this);
		this.findViewById(R.id.videonext).setOnClickListener(this);
		this.findViewById(R.id.videopre).setOnClickListener(this);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				videobar.setProgress(mediaPlayer.getCurrentPosition());
				super.handleMessage(msg);
			}
		};
		gd = new GestureDetector(this, new OnDoubleClick());
		cursor = MediaDatabase.VideoCursor();
		cursor.moveToPosition(index);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onDestroy() {
		if (wakeLock != null) {
			wakeLock.release();// 释放睡眠锁
			wakeLock = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mediaPlayer.pause();
		timer.cancel();
		super.onPause();
	}

	@Override
	protected void onResume() {
		surfaceView = (SurfaceView) this.findViewById(R.id.surface);
		surfaceView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gd.onTouchEvent(event);// 处理双击事件
				return true;
			}
		});
		mediaPlayer = new MediaPlayer();
		SurfaceHolder holder = surfaceView.getHolder();
		height = getWindowManager().getDefaultDisplay().getHeight();
		holder.addCallback(new SurfaceCallback());
		surfaceView.setFocusable(true);
		surfaceView.requestFocus();
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				next();
			}
		});
		videobar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				pos.setText(StringUtils.TimeFormat(progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				mediaPlayer.seekTo(seekBar.getProgress());
			}
		});
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("index", cursor.getPosition());
		outState.putInt("pos", mediaPlayer.getCurrentPosition());
		super.onSaveInstanceState(outState);
	}

	public void play(int pos) {
		try {
			title.setText(cursor.getString(0));
			if (timer != null)
				timer.cancel();
			videobar.setMax(cursor.getInt(2));
			timer = new Timer();
			timer.schedule(new task(), 0, 1000);
			dura.setText(StringUtils.TimeFormat(cursor.getInt(2)));
			mediaPlayer.reset();
			mediaPlayer.setDataSource(cursor.getString(3));
			mediaPlayer.prepare();
			mediaPlayer.seekTo(pos);
			mediaPlayer.start();
			setScale();
		} catch (Exception e) {
		}
	}

	private void pre() {
		if (!cursor.moveToPrevious())
			cursor.moveToLast();
		play(0);
	}

	private void setScale() {
		int w = mediaPlayer.getVideoWidth();
		int h = mediaPlayer.getVideoHeight();
		float wh = w * 1.0f / h;
		if (wh - 1.3f < 0.2f)
			widescale = false;
		else
			widescale = true;
		surfaceView.getHolder().setFixedSize(
				(int) (height * (widescale ? scale_169 : scale_43)), height);
	}

	private void showControls() {
		if (controller.getVisibility() == View.INVISIBLE) {
			controller.setVisibility(View.VISIBLE);
		} else
			controller.setVisibility(View.INVISIBLE);
	}
}