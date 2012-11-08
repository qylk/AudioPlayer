package cn.qylk.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import cn.qylk.app.APP;

public abstract class LrcViewBase extends TextView implements ILrcView {
	protected int cColor = APP.Config.lrccolor, ncColor = Color.WHITE;
	protected int centerX, centerY;
	protected Paint cPaint, ncPaint;
	protected float fp;
	protected int index;
	protected float len;
	protected int gap=0;
	protected LrcPackage lrcpac;
	protected int lTime, cTime, sTime;
	protected int nextpoint, offset;
	protected boolean shadow = APP.Config.lrcshadow;
	protected float TextSize=16.0f;
	protected Typeface Texttypeface = Typeface.DEFAULT;

	public LrcViewBase(Context context) {
		this(context, null, 0);
	}

	public LrcViewBase(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LrcViewBase(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void clearView() {
		setText("");
		lrcpac = null;
	}

	public LrcPackage GetLrcPackage() {
		return lrcpac;
	}

	public int GetOffset() {
		return offset;
	}

	protected void init() {
		setLrcTextSize(TextSize);
		ncPaint = new Paint();// 非高亮 部分
		ncPaint.setAntiAlias(true);
		ncPaint.setTextAlign(Paint.Align.CENTER);
		ncPaint.setTextSize(TextSize);
		ncPaint.setColor(ncColor);
		ncPaint.setTypeface(Texttypeface);
		ncPaint.setShadowLayer(3, 2, -1, Color.BLACK);
		cPaint = new Paint();// 高亮部 分 当前歌 词
		cPaint.setAntiAlias(true);
		cPaint.setTextAlign(Paint.Align.CENTER);
		cPaint.setTypeface(Texttypeface);
		cPaint.setTextSize(TextSize);
		cPaint.setColor(cColor);
		cPaint.setShadowLayer(3, 2, -1, Color.BLACK);
	}

	public void initLrcIndex(int curpos) {
		int i = 0;
		int sum = lrcpac.getSum();
		while (i < sum) {
			if (curpos < lrcpac.list.get(i).beginTime) {
				break;// 查询到了索引，返回
			}
			i++;
		}
		index = i - 1;
		updatedata();
		updateView(curpos);
	}

	@Override
	public void setGap(int gap) {
		this.gap = gap;
	}

	@Override
	protected abstract void onDraw(Canvas canvas);

	@Override
	protected void onSizeChanged(int w, int h, int ow, int oh) {
		super.onSizeChanged(w, h, ow, oh);
		centerX = w >> 1;// 屏宽一半
		centerY = h >> 1;// 屏高一半
	}

	public void setFirstColor(int color) {
		cColor = color;
		cPaint.setColor(color);
	}

	public void setSecondColor(int color) {
		ncPaint.setColor(color);
	}

	public void setOffset(int offset) {
		this.offset += offset;
		nextpoint = lrcpac.list.get(index + 1).beginTime + offset;
	}

	public void setLyric(LrcPackage lrc) {
		this.lrcpac = lrc;
		index = -1;
		offset = 0;
	}

	public void setShadow(boolean shadow) {
		this.shadow = shadow;
		if (!shadow)
			cPaint.setShader(null);
	}

	public void setLrcTextSize(float size) {
		float des = getResources().getDisplayMetrics().density;
		TextSize = size * des;
	}

	protected void updatedata() {
		if (index == -1) {
			nextpoint = lrcpac.list.get(0).beginTime + offset;
			return;
		}
		sTime = lrcpac.list.get(index).beginTime + offset;// 开始时间
		if (index == lrcpac.getSum() - 1) {
			nextpoint = lrcpac.duration + 1500;
			lTime = lrcpac.duration - sTime;
		} else {
			nextpoint = lrcpac.list.get(index + 1).beginTime + offset;// 获取下一句时间起点
			lTime = lrcpac.list.get(index).lineTime;
		}
		len = this.getTextSize() * lrcpac.list.get(index).lrcBody.length();
	}

	protected void OnNextLine() {
		index++;
		updatedata();
	}

	public void updateView(int progress) {
		if (lrcpac == null)
			return;
		cTime = progress;
		if (nextpoint < progress) {
			OnNextLine();
		}
		this.postInvalidate();
	}
}
