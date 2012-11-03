package cn.qylk.app;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 倒计时
 * 
 * @author qylk2011 all rights resolved
 */
public class SleepTimer {
	private static SleepTimer st;

	public static SleepTimer getInstance() {
		if (st == null)
			st = new SleepTimer();
		return st;
	}

	private boolean on;
	private long starttime;
	private Timer timer;

	public void cancel() {
		if (on) {
			timer.cancel();
			on = false;
		}
	}

	/**
	 * 剩余时间
	 * 
	 * @return
	 */
	public long getRemain() {
		return System.currentTimeMillis() - starttime;
	}

	public boolean isOntimer() {
		return on;
	}

	/**
	 * 新建timer
	 * 
	 * @param delay
	 */
	public void newTimer(long delay, TimerTask task) {
		cancel();
		timer = new Timer();
		timer.schedule(task, delay);
		on = true;
		starttime = System.currentTimeMillis();
	}
}
