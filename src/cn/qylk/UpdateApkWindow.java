package cn.qylk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cn.qylk.utils.UpdateApk;

public class UpdateApkWindow extends Activity implements OnClickListener {

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.updatenow:
			UpdateApk updateapk = new UpdateApk(this);
			updateapk.init().update();
			break;
		case R.id.updatelater:
			finish();
			break;
		default:
			break;
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Preference);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.updatewindow);
		setTitle("检测到新版本可用");
		Intent intent=getIntent();
		TextView title= (TextView) findViewById(R.id.toptip);
		title.setText("QPLAYER V"+intent.getStringExtra("ver"));
		TextView tv = (TextView) findViewById(R.id.versumary);
		tv.setText(intent.getStringExtra("info"));
		findViewById(R.id.updatenow).setOnClickListener(this);
		findViewById(R.id.updatelater).setOnClickListener(this);
	}
}
