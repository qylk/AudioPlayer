package cn.qylk;

import java.util.Timer;
import java.util.TimerTask;

import cn.qylk.R;
import cn.qylk.database.MediaDatabase;
import cn.qylk.utils.StringUtils;
import android.app.Activity;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * @author qylk2012
 *
 */
public class VideoActivity extends Activity implements OnClickListener {
	private final class SurfaceCallback implements SurfaceHolder.Callback {
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			play();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (mediaPlayer != null) {
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
	public static final float scale_169 = 9.0f / 16;// 16：9尺寸
	public static final float scale_43 = 3.0f / 4;// 4:3尺寸
	private LinearLayout controller;
	private Cursor cursor;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				videobar.setProgress(mediaPlayer.getCurrentPosition());
				break;
			case 1:
				controller.setVisibility(View.INVISIBLE);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	private MediaPlayer mediaPlayer;
	private ImageView playButton;
	private TextView pos, dura, title;
	private int position;

	private SurfaceView surfaceView;

	private Timer timer;

	private SeekBar videobar;

	private void next() {
		if (cursor.moveToNext())
			;
		else
			cursor.moveToFirst();
		play();
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
					.setImageResource(mediaPlayer.isPlaying() ? R.drawable.btn_video_pause
							: R.drawable.btn_video_play);
			break;
		case R.id.videopre:
			pre();
			break;
		case R.id.videonext:
			next();
			break;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video);
		position = getIntent().getExtras().getInt("position");
		playButton = (ImageView) this.findViewById(R.id.videoplay);
		ImageView NextButton = (ImageView) this.findViewById(R.id.videonext);
		ImageView PreButton = (ImageView) this.findViewById(R.id.videopre);
		videobar = (SeekBar) findViewById(R.id.videobar);
		title = (TextView) findViewById(R.id.videotitle);
		pos = (TextView) findViewById(R.id.videopos);
		dura = (TextView) findViewById(R.id.videodura);
		controller = (LinearLayout) findViewById(R.id.controller);
		playButton.setOnClickListener(this);
		NextButton.setOnClickListener(this);
		PreButton.setOnClickListener(this);
		cursor = MediaDatabase.VideoCursor();
		cursor.moveToPosition(position);
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setScreenOnWhilePlaying(true);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
		int width = getWindowManager().getDefaultDisplay().getWidth();
		surfaceView.getHolder().setFixedSize(width, (int) (width * scale_169));
		surfaceView.getHolder()
				.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceView.getHolder().addCallback(new SurfaceCallback());
		surfaceView.setFocusable(true);
		surfaceView.requestFocus();
		surfaceView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				handler.removeMessages(1);
				handler.sendEmptyMessageDelayed(1, 5 * 1000);
				controller.setVisibility(View.VISIBLE);
				return true;
			}
		});
		mediaPlayer.setDisplay(surfaceView.getHolder());
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
	}

	@Override
	protected void onPause() {
		mediaPlayer.pause();
		timer.cancel();
		super.onPause();
	}

	public void play() {
		try {
			title.setText(cursor.getString(0));
			position = cursor.getPosition();
			if (timer != null)
				timer.cancel();
			videobar.setMax(cursor.getInt(2));
			timer = new Timer();
			timer.schedule(new task(), 0, 1000);
			dura.setText(StringUtils.TimeFormat(cursor.getInt(2)));
			videobar.setMax(cursor.getInt(2));
			mediaPlayer.reset();
			mediaPlayer.setDataSource(cursor.getString(3));
			mediaPlayer.prepare();
			mediaPlayer.start();
			handler.sendEmptyMessageDelayed(1, 5 * 1000);
		} catch (Exception e) {
		}
	}

	private void pre() {
		if (cursor.moveToPrevious())
			;
		else
			cursor.moveToLast();
		play();
	}
}