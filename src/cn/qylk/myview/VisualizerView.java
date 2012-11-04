package cn.qylk.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class VisualizerView extends View {

	private Paint mForePaint = new Paint();
	private Paint poPaint = new Paint();
	private static final int mSpectrumNum = 32;
	private float[] toppoint = new float[mSpectrumNum];
	private byte[] mBytes = new byte[mSpectrumNum];
	private float[] mPoints = new float[mSpectrumNum * 4];
	private int height, width;
	private int baseX;
	private float scalefit;

	public VisualizerView(Context context) {
		this(context, null);

	}

	public VisualizerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
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

	public VisualizerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	private void init() {
		poPaint.setStrokeWidth(3f);
		poPaint.setColor(0xFFFFFF00);
		mForePaint.setColor(0xFFFFFFFF);
		mForePaint.setStyle(Style.STROKE);
		mForePaint.setStrokeWidth(6f);
	}

	public void updateVisualizer(byte[] fft) {
		// FFT数据详见public int getFft (byte[] fft)方法解释;
		// 以下做了非标准FFT显示处理.只是为了达到较佳的显示效果
		for (int i = 0, j = 0; j < mSpectrumNum; j++) {
			mBytes[j] = (byte) ((Math.hypot(fft[i], fft[i + 1]) / scalefit));
			i += 1;
		}
		invalidate();
	}

	public void ClearView() {
		setVisibility(View.INVISIBLE);
	}

	public void setUpView() {
		setVisibility(View.VISIBLE);
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
				toppoint[i] = height - mBytes[i];
			else
				toppoint[i] += 4;
			if (toppoint[i] > height - 4)
				toppoint[i] = height - 4;
			canvas.drawRect(xi - 3, toppoint[i], xi + 3, toppoint[i] + 2,
					poPaint);
		}
		canvas.drawLines(mPoints, mForePaint);
	}
}
