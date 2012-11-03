package cn.qylk.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import cn.qylk.R;

/**
 * 仿Launcher中的WorkSapce，可以左右滑动切换屏幕的类，支持显示系统背景和滑动 ，qylk修改精简
 * 
 * @author Yao.GUET blog: http://blog.csdn.net/Yao_GUET date: 2011-05-04
 */
public class MyScrollView extends ViewGroup {
	private static final int SNAP_VELOCITY = 400; // 最大滑动时间阈值
	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;
	private int mCurScreen;
	private Scroller mScroller;
	private int mTouchState = TOUCH_STATE_REST; // 触摸状态
	private VelocityTracker mVelocityTracker;

	public MyScrollView(Context context) {
		super(context);
		mScroller = new Scroller(context);
	}

	public MyScrollView(Context context, AttributeSet attrs) { // 构造器，初始化
		super(context, attrs);
		mScroller = new Scroller(context);
		TypedArray type = getContext().obtainStyledAttributes(attrs,
				R.styleable.MyScrollView);
		mCurScreen = type
				.getInteger(R.styleable.MyScrollView_default_screen, 0);// 获得默认屏位置
		type.recycle();
	}

	@Override
	public void computeScroll() { // 滑动计算
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	public int getCurScreen() {
		return mCurScreen;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) { // 开始布局
		int childLeft = 0;
		final int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View childView = getChildAt(i);
			if (childView.getVisibility() != View.GONE) {
				final int childWidth = childView.getMeasuredWidth();
				childView.layout(childLeft, 0, childLeft + childWidth,
						childView.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		// The children are given the same width and height as the scrollLayout
		final int count = getChildCount();
		for (int i = 0; i < count; i++)
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		scrollTo(mCurScreen * width, 0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) { // 触摸动作识别
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
		final int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished())
				mScroller.abortAnimation();
			break;
		case MotionEvent.ACTION_UP:
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int velocityX = (int) velocityTracker.getXVelocity();
			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) { // 左跳
				snapToScreen(mCurScreen - 1);
			} else if (velocityX < -SNAP_VELOCITY // 右跳
					&& mCurScreen < getChildCount() - 1) {
				snapToScreen(mCurScreen + 1);
			} else {
				snapToDestination(); // 手指拖拽屏幕时，调用
			}
			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return true;
	}

	public void setToScreen(int whichScreen) {
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		mCurScreen = whichScreen;
		scrollTo(whichScreen * getWidth(), 0);
	}

	/**
	 * According to the position of current layout scroll to the destination
	 * page.
	 */
	public void snapToDestination() { // 跳转到下一屏
		final int screenWidth = getWidth();
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth; // 计算下一屏
		snapToScreen(destScreen); // 跳转
	}

	public void snapToScreen(int whichScreen) { // 有动画地跳转到指定屏
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollX() != (whichScreen * getWidth())) {
			final int delta = whichScreen * getWidth() - getScrollX();
			mScroller.startScroll(getScrollX(), 0, delta, 0,
					Math.abs(delta) * 2);
			mCurScreen = whichScreen;
			invalidate(); // 重画区域
		}
	}
}
