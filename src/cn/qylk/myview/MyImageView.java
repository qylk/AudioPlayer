package cn.qylk.myview;

import android.content.Context;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * 带触感的ImageView，按下时图片会变小，松开便恢复原状
 * 
 * @author qylk2012 <br>
 *         all rights reserved
 * 
 */
public class MyImageView extends ImageView {
	public interface OnViewClick {
		public void onClick(int id);
	}

	private static int alpha = 220;

	private static final int ANIMATION_START = 1, RESTORE_START = 2;
	private Handler handler = new Handler() {
		private Matrix matrix = new Matrix();

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			matrix.set(getImageMatrix());
			switch (msg.what) {
			case ANIMATION_START:// 开始缩小
				if (!isFinish) {
					return;
				} else {
					isFinish = false;
					BeginScale(matrix, Scale);
					isFinish = true;
				}
				break;
			case RESTORE_START:// 恢复大小
				if (!isFinish) {
					handler.sendEmptyMessage(RESTORE_START);
				} else {
					isFinish = false;
					BeginScale(matrix, 1.0f / Scale);
					if (!isActionMove && onclick != null)
						onclick.onClick(getId());
					isFinish = true;
				}
				break;
			}
		}
	};
	private boolean isFinish = true, isActionMove;
	OnViewClick onclick = null;

	private float Scale = 0.94f;

	private int vWidth, vHeight;

	public MyImageView(Context context) {
		this(context, null);
	}

	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setAlpha(alpha);
	}

	/**
	 * 图片缩放
	 * 
	 * @param matrix
	 * @param scale
	 *            >1.0放大，<1.0缩小
	 */
	private synchronized void BeginScale(Matrix matrix, float scale) {
		matrix.postScale(scale, scale, vWidth / 2, vHeight / 2);// 从中心位置缩放
		setImageMatrix(matrix);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		vWidth = w - getPaddingLeft() - getPaddingRight();
		vHeight = h - getPaddingTop() - getPaddingBottom();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			isActionMove = false;
			handler.sendEmptyMessage(ANIMATION_START);
			break;
		case MotionEvent.ACTION_MOVE:
			float x = event.getX();
			float y = event.getY();
			if (x > vWidth || y > vHeight || x < 0 || y < 0) {// 触控不在控件上
				isActionMove = true;
			} else {
				isActionMove = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			handler.sendEmptyMessage(RESTORE_START);
			break;
		case MotionEvent.ACTION_CANCEL:
			isActionMove = true;
			handler.sendEmptyMessage(RESTORE_START);
			break;
		}
		return true;
	}

	public void setOnClickIntent(OnViewClick onclick) {
		this.onclick = onclick;
	}
}
