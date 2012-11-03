package cn.qylk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import cn.qylk.app.APP;
import cn.qylk.app.APPUtils;

public class Logo extends Activity {
	private Handler handler = new Handler() {

		@Override
		public void dispatchMessage(Message msg) {
			startActivity(new Intent(Logo.this, ListUI.class));
			finish();
			super.dispatchMessage(msg);
		}
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);// 显示欢迎画面
		APPUtils.ScanSD(false);
		if (!APP.Config.sdplunged || !APP.Config.Library) {// 是否加载sd卡
			new AlertDialog.Builder(this)
					.setMessage(R.string.nolibrary)
					.setPositiveButton(R.string.exit,
							new DialogInterface.OnClickListener() {// 未加载SD卡，退出提示
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).setCancelable(false).show();
			return;
		}
		APPUtils.ScanSD(false);
		handler.sendEmptyMessageDelayed(0, 3000);
	}
}
