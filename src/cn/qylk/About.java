package cn.qylk;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
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
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setTheme(R.style.Theme_Preference);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutthis);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar);
		TextView title = (TextView) findViewById(R.id.mytitle);
		title.setText("清源林客");
		Button btn = (Button) findViewById(R.id.titlebtn);
		btn.setText(R.string.checknew);
		btn.setOnClickListener(this);
	}

	@Override
	protected void onPause() {
		hasnew = true;// 很可能出现新版本，此时onpause
		super.onPause();
	}

}
