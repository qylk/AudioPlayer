package cn.qylk.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;

public class LrcView extends LrcViewBase {

	public LrcView(Context context) {
		this(context, null);
	}

	public LrcView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (lrcpac == null) {
			return;
		}
		float tempY = centerY;// 中间高度
		if (index != -1) {
			fp = (cTime - sTime) * 1.0f / lTime;
			float plus = lTime == 0 ? 0 : fp * TextSize;
			canvas.translate(0, -plus);
			if (shadow) {// 如果设有阴影
				fp *= 1.1f;//修正中文误差
				cPaint.setShader(new LinearGradient(centerX - len / 2, centerY,
						centerX + len / 2, centerY, new int[] { cColor,
								Color.WHITE }, new float[] { fp, fp },
						TileMode.CLAMP));
			}
			canvas.drawText(lrcpac.list.get(index).lrcBody, centerX, centerY,
					cPaint);
		} else
			tempY = centerY - TextSize;
		// 画出本 句之 后的句 子
		int sum = lrcpac.getSum();
		for (int i = index + 1; i < sum; i++) {// 往下推 移
			tempY += TextSize;
			if (tempY > centerY + centerY) {// 超出屏幕下方
				break;
			}
			canvas.drawText(lrcpac.list.get(i).lrcBody, centerX, tempY, ncPaint);
		}
		tempY = centerY;// 画出本 句之 前的句 子
		for (int i = index - 1; i >= 0; i--) {// 向上推 移
			tempY -= TextSize;
			if (tempY < 0)
				break;
			canvas.drawText(lrcpac.list.get(i).lrcBody, centerX, tempY, ncPaint);
		}
	}
}
