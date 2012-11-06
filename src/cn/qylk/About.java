package cn.qylk;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.qylk.app.Update;

/**
 * 关于界面
 * 
 * @author qylk2012<br>
 *         all rights resolved
 */
public class About extends Activity implements OnClickListener {
	private int count;
	private boolean hasnew;

	@Override
	public void onClick(View v) {
		count++;
		if (count > 2 && !hasnew) {
			Toast.makeText(this, "别再点了,有更新会通知你的!", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "后台检查更新中...", Toast.LENGTH_SHORT).show();
			new Update().start(this);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Preference);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutthis);
		TextView update = (TextView) findViewById(R.id.btn_checknew);
		update.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		update.setOnClickListener(this);
	}

	@Override
	protected void onPause() {
		hasnew = true;// 很可能出现新版本，此时onpause
		super.onPause();
	}

}
