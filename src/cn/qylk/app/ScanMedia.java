package cn.qylk.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.widget.Toast;
import cn.qylk.R;
import cn.qylk.app.IPlayList.ListType;
import cn.qylk.utils.SendAction;

public class ScanMedia {

	/**
	 * 扫描存储卡
	 * 
	 * @param registerReceiver
	 *            是否接收扫描完闭的通知
	 */
	public  void ScanSD(boolean notification) {
		if (notification) {
			IntentFilter filter = new IntentFilter();// 过滤器
			filter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
			filter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
			filter.addDataScheme("file");
			APP.getInstance().registerReceiver(new ScanSdReceiver(), filter);
		}
		APP.getInstance().sendBroadcast(
				new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
						+ APP.Config.SDDIR)));
	}

	class ScanSdReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action))
				Toast.makeText(context, R.string.scaning, Toast.LENGTH_LONG)
						.show();
			else if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
				Toast.makeText(context, R.string.scaned, Toast.LENGTH_LONG)
						.show();
				SendAction.SendListChangedSignal(new ListTypeInfo(
						ListType.ALLSONGS, "library"));
				APP.getInstance().unregisterReceiver(this);
			}
		}
	}
}