package cn.qylk.dialog;

import cn.qylk.R;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ColorPickerDialog extends DialogPreference {
	private static class ColorPickerView extends View {
		private static final int CENTER_RADIUS = 30;
		private static final int CENTER_X = 100;
		private static final int CENTER_Y = 100;
		private static final float PI = 3.1415926f;
		private Paint mCenterPaint;
		private final int[] mColors;
		private boolean mHighlightCenter;
		private int[] mHSVColors;
		private Paint mHSVPaint;
		private OnColorChangedListener mListener;
		private Paint mPaint;
		private boolean mRedrawHSV;
		private boolean mTrackingCenter;

		ColorPickerView(Context c, OnColorChangedListener l, int color) {
			super(c);
			mListener = l;
			mColors = new int[] { 0xFFFF0000, 0xFFFF00FF, 0xFF0000FF,
					0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000 };
			Shader s = new SweepGradient(0, 0, mColors, null);
			mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaint.setShader(s);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(55);
			mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mCenterPaint.setColor(color);
			mCenterPaint.setStrokeWidth(5);
			mHSVColors = new int[] { 0xFF000000, color, 0xFFFFFFFF };
			mHSVPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mHSVPaint.setStrokeWidth(10);
			mRedrawHSV = true;
		}
		private int ave(int s, int d, float p) {
			return s + java.lang.Math.round(p * (d - s));
		}
		public int getHLColor() {
			return mCenterPaint.getColor();
		}

		private int interpColor(int colors[], float unit) {
			if (unit <= 0) {
				return colors[0];
			}
			if (unit >= 1) {
				return colors[colors.length - 1];
			}
			float p = unit * (colors.length - 1);
			int i = (int) p;
			p -= i;
			// now p is just the fractional part [0...1) and i is the index
			int c0 = colors[i];
			int c1 = colors[i + 1];
			int a = ave(Color.alpha(c0), Color.alpha(c1), p);
			int r = ave(Color.red(c0), Color.red(c1), p);
			int g = ave(Color.green(c0), Color.green(c1), p);
			int b = ave(Color.blue(c0), Color.blue(c1), p);
			return Color.argb(a, r, g, b);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			float r = CENTER_X - mPaint.getStrokeWidth() * 0.5f;
			canvas.translate(CENTER_X, CENTER_X);
			int c = mCenterPaint.getColor();
			if (mRedrawHSV) {
				mHSVColors[1] = c;
				mHSVPaint.setShader(new LinearGradient(-100, 0, 100, 0,
						mHSVColors, null, Shader.TileMode.CLAMP));
			}
			canvas.drawOval(new RectF(-r, -r, r, r), mPaint);
			canvas.drawCircle(0, 0, CENTER_RADIUS, mCenterPaint);
			canvas.drawRect(new RectF(-100, 110, 100, 130), mHSVPaint);
			if (mTrackingCenter) {
				mCenterPaint.setStyle(Paint.Style.STROKE);
				if (mHighlightCenter) {
					mCenterPaint.setAlpha(0xFF);
				} else {
					mCenterPaint.setAlpha(0x80);
				}
				canvas.drawCircle(0, 0,
						CENTER_RADIUS + mCenterPaint.getStrokeWidth(),
						mCenterPaint);
				mCenterPaint.setStyle(Paint.Style.FILL);
				mCenterPaint.setColor(c);
			}
			mRedrawHSV = true;
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			setMeasuredDimension(CENTER_X * 2, (CENTER_Y + 35) * 2);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX() - CENTER_X;
			float y = event.getY() - CENTER_Y;
			boolean inCenter = FloatMath.sqrt(x * x + y * y) <= CENTER_RADIUS;
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mTrackingCenter = inCenter;
				if (inCenter) {
					mHighlightCenter = true;
					invalidate();
					break;
				}
			case MotionEvent.ACTION_MOVE:
				if (mTrackingCenter) {
					if (mHighlightCenter != inCenter) {
						mHighlightCenter = inCenter;
						invalidate();
					}
				} else if (x >= -100 && x <= 100 && y <= 130 && y >= 110) // see
				{
					int a, r, g, b, c0, c1;
					float p;
					// set the center paint to this color
					if (x < 0) {
						c0 = mHSVColors[0];
						c1 = mHSVColors[1];
						p = (x + 100) / 100;
					} else {
						c0 = mHSVColors[1];
						c1 = mHSVColors[2];
						p = x / 100;
					}
					a = ave(Color.alpha(c0), Color.alpha(c1), p);
					r = ave(Color.red(c0), Color.red(c1), p);
					g = ave(Color.green(c0), Color.green(c1), p);
					b = ave(Color.blue(c0), Color.blue(c1), p);
					mCenterPaint.setColor(Color.argb(a, r, g, b));
					mRedrawHSV = false;
					invalidate();
				} else if (y < 135) {
					float angle = (float) java.lang.Math.atan2(y, x);
					// need to turn angle [-PI ... PI] into unit [0....1]
					float unit = angle / (2 * PI);
					if (unit < 0) {
						unit += 1;
					}
					mCenterPaint.setColor(interpColor(mColors, unit));
					invalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
				if (mTrackingCenter) {
					if (inCenter) {
						mListener.colorChanged(mCenterPaint.getColor());
					}
					mTrackingCenter = false; // so we draw w/o halo
					invalidate();
				}
				break;
			}
			return true;
		}
	}
	public interface OnColorChangedListener {
		void colorChanged(int color);
	}
	private ColorPickerView mCPView;
	private int mCurrentColor;

	private int mInitialColor;

	private TextView preview;

	public ColorPickerDialog(Context contex) {
		this(contex, null);
	}

	public ColorPickerDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ColorPickerDialog(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		View view = LayoutInflater.from(getContext()).inflate(
				R.layout.colorpickerpreference, null);
		TextView title = (TextView) view.findViewById(R.id.titles);
		title.setText(getTitle());
		preview = (TextView) view.findViewById(R.id.preview);
		preview.setText(getSummary());
		SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
		mInitialColor = prefs.getInt(getKey(), Color.GREEN);
		preview.setTextColor(mInitialColor);
		return view;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			mCurrentColor = mCPView.getHLColor();
			preview.setTextColor(mCurrentColor);
			SharedPreferences.Editor editor = getEditor();
			editor.putInt(getKey(), mCurrentColor);
			editor.commit();
			callChangeListener(Integer.valueOf(mCurrentColor));
		}
	}

	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		super.onPrepareDialogBuilder(builder);
		OnColorChangedListener l = new OnColorChangedListener() {
			public void colorChanged(int color) {
				mCurrentColor = color;
				onDialogClosed(true);
				getDialog().dismiss();
			}
		};
		LinearLayout layout = new LinearLayout(getContext());
		layout.setPadding(10, 10, 10, 0);
		layout.setOrientation(LinearLayout.VERTICAL);
		mCPView = new ColorPickerView(getContext(), l, mInitialColor);
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params1.gravity = Gravity.CENTER;
		mCPView.setLayoutParams(params1);
		layout.addView(this.mCPView);
		layout.setId(android.R.id.widget_frame);
		builder.setView(layout);
	}
}
