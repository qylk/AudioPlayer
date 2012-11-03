package cn.qylk.utils;

import cn.qylk.app.APP;
import cn.qylk.app.ListTypeInfo;
import cn.qylk.app.MyAction;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

/**
 * Send Broadcasts
 * 
 * @author qylk2012
 * 
 */
public final class SendAction {
	public static enum ServiceControl {
		PRE, NEXT, PAUSE_CONTINE, PAUSE, PLAYNEW
	}

	private static Context ct = APP.getInstance();
	private static Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			ct.sendBroadcast((Intent) msg.obj);// 发送广播
			super.handleMessage(msg);
		}
	};

	/**
	 * 向后台播放进程发送控制信息
	 */
	public static void SendControlMsg(ServiceControl action) {
		Intent intent = new Intent(MyAction.INTENT_BTNACTION);
		intent.putExtra("action", action);
		handler.removeMessages(0);
		Message msg = handler.obtainMessage(0, intent);
		handler.sendMessageDelayed(msg, 150);// 缓冲处理,防止按键抖动
	}

	public static void SendExitToUI() {
		Intent intent = new Intent(MyAction.INTENT_EXIT);
		ct.sendBroadcast(intent);
	}

	public static void SendListChangedSignal(ListTypeInfo info) {
		Intent intent = new Intent(MyAction.INTENT_LISTCHANGED);
		intent.putExtra("list", info.list);
		intent.putExtra("para", info.para);
		ct.sendBroadcast(intent);
	}

	/**
	 * 通知UI更新
	 */
	public static void SendMsg_UI_Update() {
		ct.sendBroadcast(new Intent(MyAction.INTENT_UI_UPDATE));
	}

	/**
	 * 向UI发送播放状态
	 * 
	 * @param isplaying
	 */
	public static void SendStatusChanged(boolean isplaying) {
		Intent intent = new Intent(MyAction.INTENT_STATUS);
		intent.putExtra("isplaying", isplaying);
		ct.sendBroadcast(intent);
	}
}
