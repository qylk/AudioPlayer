package cn.qylk;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cn.qylk.utils.Update;

/**
 * 关于界面
 * 
 * @author qylk2012 all rights resolved
 */
public class About extends Activity implements OnClickListener {

	@Override
	public void onClick(View v) {
		new Update(this).start();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Preference);
		super.onCreate(savedInstanceState);
		// 使用网页显示
		// WebView aboutView = new WebView(this);
		// aboutView.loadUrl("file:///android_asset/about.html");
		// setContentView(aboutView);
		setContentView(R.layout.aboutthis);
		TextView update = (TextView) findViewById(R.id.btn_checknew);
		update.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		update.setOnClickListener(this);
	}
}
