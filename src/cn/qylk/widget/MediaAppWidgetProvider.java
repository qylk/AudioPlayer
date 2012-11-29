package cn.qylk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;
import cn.qylk.ListUI;
import cn.qylk.R;
import cn.qylk.app.APP;
import cn.qylk.app.MyAction;
import cn.qylk.utils.SendAction;
import cn.qylk.utils.SendAction.ServiceControl;

/**
 * @author qylk2012
 * 
 */
public class MediaAppWidgetProvider extends AppWidgetProvider {
	private static Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			APP.getInstance().stopService(
					new Intent(MyAction.INTENT_START_SERVICE));
			super.handleMessage(msg);
		}
	};

	private static RemoteViews views;

	public static void updateAppWidget(Context context,
			AppWidgetManager appWidgeManger, int appWidgetId) {
		views = new RemoteViews(context.getPackageName(), R.layout.widget);
		Intent next = new Intent(MyAction.INTENT_WIDGET_CONTROL);// 下一曲广播意图
		next.putExtra("code", 1);// 加入标志以便区别是哪个按钮
		PendingIntent pendingNextIntent = PendingIntent.getBroadcast(context,
				1, next, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.widgetnext, pendingNextIntent);

		Intent play = new Intent(MyAction.INTENT_WIDGET_CONTROL);// 播放或暂停意图
		play.putExtra("code", 2);
		PendingIntent pendingPlayIntent = PendingIntent.getBroadcast(context,
				0, play, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.widgetplaypause, pendingPlayIntent);

		Intent pre = new Intent(MyAction.INTENT_WIDGET_CONTROL);// 上一曲意图
		pre.putExtra("code", 0);
		PendingIntent pendingPreIntent = PendingIntent.getBroadcast(context,
				-1, pre, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.widgetpre, pendingPreIntent);
		Intent intent = new Intent(context, ListUI.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		views.setOnClickPendingIntent(R.id.musictitle, pendingIntent);
		appWidgeManger.updateAppWidget(appWidgetId, views);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (views == null) {
			views = new RemoteViews(context.getPackageName(), R.layout.widget);
		}
		AppWidgetManager appWidgetManger = AppWidgetManager
				.getInstance(context);
		int[] appIds = appWidgetManger.getAppWidgetIds(new ComponentName(
				context, getClass()));
//		if (appIds.length == 0)
//			return;
		handler.removeMessages(0);
		String action = intent.getAction();
		if (action.equals(MyAction.INTENT_WIDGET_CONTROL)) {// widget按钮广播
			context.startService(new Intent(MyAction.INTENT_START_SERVICE));
			Bundle bundle = intent.getExtras();
			int code = bundle.getInt("code");// 得知是那个按钮
			SendAction.SendControlMsg(ServiceControl.values()[code]);
		} else if (action.equals(MyAction.INTENT_STATUS)) {// widget上信息内容更新广播
			Bundle bundle = intent.getExtras();
			boolean isplaying = bundle.getBoolean("isplaying");
			views.setImageViewResource(R.id.widgetplaypause,
					isplaying ? R.drawable.btn_pause_bg
							: R.drawable.btn_play_bg);
			if (!isplaying)
				handler.sendEmptyMessageDelayed(0, 60 * 1000);//暂停状态下，60秒后关闭Service
		} else if (action.equals(MyAction.INTENT_UI_UPDATE)) {
			views.setImageViewResource(R.id.widgetplaypause,
					R.drawable.btn_pause_bg);
			views.setTextViewText(R.id.musictitle,
					APP.list.getTrackEntity().title);// 标题
			views.setTextViewText(R.id.musicinfo,
					APP.list.getTrackEntity().artist);// 艺术家和专辑
		}
		appWidgetManger.updateAppWidget(appIds, views);
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
