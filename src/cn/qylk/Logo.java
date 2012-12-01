package cn.qylk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import cn.qylk.app.APP;
import cn.qylk.app.ScanMedia;

public class Logo extends Activity implements Callback {
	private Handler handler = new Handler(this);

	@Override
	public boolean handleMessage(Message msg) {
		startActivity(new Intent(Logo.this, ListUI.class));
		finish();
		return true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);// 显示欢迎画面
		new ScanMedia().ScanSD(false);
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
		handler.sendEmptyMessageDelayed(0, 3000);
	}
}
