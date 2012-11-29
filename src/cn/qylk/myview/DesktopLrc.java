package cn.qylk.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class DesktopLrc extends LrcViewBase implements View.OnTouchListener {
	private WindowManager mWM;
	private Paint[] paint;
	private WindowManager.LayoutParams wmParams;

	public DesktopLrc(Context context) {
		this(context, null, 0);
	}

	public DesktopLrc(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DesktopLrc(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		super.setLrcTextSize(20.0f);
		super.setGap(5);
		paint = new Paint[] { ncPaint, cPaint };
		newWindow(context);
	}

	public void destoryView(Context context) {
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
				.removeView(this);
	}

	public void newWindow(Context context) {
		mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		this.setOnTouchListener(this);
		wmParams = new WindowManager.LayoutParams();
		wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
				| WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		wmParams.format = 1;
		wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE;
		wmParams.width = LayoutParams.MATCH_PARENT;
		wmParams.height = (int) (3*TextSize);
		mWM.addView(this, wmParams);// 将本歌词控件添加到窗口
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (index == -1) {
			canvas.drawText(lrcpac.title, centerX, centerY, cPaint);
			return;
		}
		int m = index % 2;
		if (shadow) {
			fp = (cTime - sTime) * 1.0f / lTime;
			fp = fp * 1.1f;
			paint[m].setShader(new LinearGradient(centerX - len / 2, centerY,
					centerX + len / 2, centerY, new int[] { cColor, ncColor },
					new float[] { fp, fp }, TileMode.CLAMP));
			paint[(m + 1) % 2].setShader(null);
		}
		int i = m == 0 ? 1 : -1;
		int j = -(i - 1) >> 1;
		if ((index + j) != lrcpac.getSum())
			canvas.drawText(lrcpac.list.get(index + j).lrcBody, centerX,
					centerY, ncPaint);
		if ((index + j + i) != lrcpac.getSum())
			canvas.drawText(lrcpac.list.get(index + j + i).lrcBody, centerX,
					centerY + TextSize+gap, cPaint);
	}

	@Override
	protected void OnNextLine() {
		super.OnNextLine();
		paint[index % 2].setColor(cColor);
		paint[(index + 1) % 2].setColor(ncColor);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		float lastX = 0, lastY = 0;
		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		if (action == MotionEvent.ACTION_DOWN) {
			lastX = x;
			lastY = y;
		} else if (action == MotionEvent.ACTION_MOVE) {
			wmParams.x += (int) (x - lastX);
			wmParams.y += (int) (y - lastY);
			mWM.updateViewLayout(this, wmParams);
		}
		return true;
	}
}
