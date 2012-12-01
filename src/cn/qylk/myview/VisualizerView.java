package cn.qylk.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class VisualizerView extends View {

	private static final int mSpectrumNum = 32;
	private int baseX;
	private int height, width;
	private byte[] mBytes = new byte[mSpectrumNum];
	private Paint mForePaint = new Paint();
	private float[] mPoints = new float[mSpectrumNum * 4];
	private Paint poPaint = new Paint();
	private float scalefit;
	private float[] toppoint = new float[mSpectrumNum];
	private int xplot[] = new int[mSpectrumNum + 1];

	public VisualizerView(Context context) {
		this(context, null);

	}

	public VisualizerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VisualizerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void ClearView() {
		for (int i = 0; i < mSpectrumNum; i++) {
			mBytes[i] = 0;
			toppoint[i] = height;
		}
		invalidate();
	}

	private void init() {
		poPaint.setStrokeWidth(2f);
		poPaint.setColor(0xFFFFFF00);
		mForePaint.setColor(0xFFFFFFFF);
		mForePaint.setStyle(Style.STROKE);
		mForePaint.setStrokeWidth(12f);
		for (int i = 0; i <= mSpectrumNum; i++) {
			xplot[i] = 0;
			xplot[i] = (int) (0.5 + Math.pow(63, (double) i / mSpectrumNum));
			if (i > 0 && xplot[i] <= xplot[i - 1])
				xplot[i] = xplot[i - 1] + 1;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (int i = 0; i < mSpectrumNum; i++) {
			final int xi = baseX * i + baseX / 2;
			mPoints[i * 4] = xi;
			mPoints[i * 4 + 1] = height;
			mPoints[i * 4 + 2] = xi;
			mPoints[i * 4 + 3] = height - mBytes[i] + 3;
			if (mPoints[i * 4 + 3] < toppoint[i])
				toppoint[i] = mPoints[i * 4 + 3];
			else
				toppoint[i] += 4;
			if (toppoint[i] > height - 4)
				toppoint[i] = height - 4;
			canvas.drawRect(xi - 6, toppoint[i], xi + 6, toppoint[i] + 2,
					poPaint);
		}
		canvas.drawLines(mPoints, mForePaint);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		height = h;
		width = w;
		baseX = width / mSpectrumNum;
		scalefit = 128.0f / height;
		for (int i = 0; i < toppoint.length; i++) {
			toppoint[i] = height - 4;
		}
		super.onSizeChanged(w, h, oldw, oldh);
	}

	public void setUpView() {
		setVisibility(View.VISIBLE);
	}

	private void getModule(byte[] fft) {
		int len = fft.length;
		double t;
		for (int i = 0, j = 0; i < len; i++, j++) {
			t = Math.hypot(fft[i], fft[i + 1]);
			if (t > 127)
				fft[j] = (byte) 127;
			else
				fft[j] = (byte) t;
			i++;
		}
	}

	public void updateVisualizer(byte[] fft) {
		getModule(fft);
		// FFT数据详见public int getFft (byte[] fft)方法解释;
		// 以下做了非标准FFT显示处理.只是为了达到较佳的显示效果
		int xi, y;
		for (int i = 0; i < mSpectrumNum; i++) {
			mBytes[i] = 0;
			for (xi = xplot[i], y = xplot[i + 1]; xi < y; xi++) {
				if (fft[xi] > mBytes[i])
					mBytes[i] = fft[xi];
			}
			mBytes[i] = (byte) (mBytes[i] / scalefit);
		}
		invalidate();
	}
}
