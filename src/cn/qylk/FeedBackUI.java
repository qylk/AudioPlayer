package cn.qylk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 意见反馈界面
 * 
 * @author qylk2012<br>
 *         all rights resolved
 */
public class FeedBackUI extends Activity implements OnClickListener, Callback {
	static {
		System.loadLibrary("feedback");
	}
	private EditText content;
	private ProgressDialog posdlg;

	private static native boolean FeedBack(String content);

	private Handler handler;

	@Override
	public void onClick(View v) {
		if (content.getText().length() < 50) {
			Toast.makeText(this, "至少50字,请详述", Toast.LENGTH_SHORT).show();
			return;
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean suc = FeedBack(content.getText().toString().trim());
				handler.sendEmptyMessage(suc ? 1 : 0);
			}
		}).start();
		posdlg = ProgressDialog.show(this, "提交中...", "Please Wait...", true,
				false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Preference);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback);
		content = (EditText) findViewById(R.id.feedbackcontent);
		Button submit = (Button) findViewById(R.id.submitfeedback);
		submit.setOnClickListener(this);
		handler = new Handler(this);
	}

	@Override
	public boolean handleMessage(Message msg) {
		posdlg.dismiss();
		if (msg.what == 0)
			Toast.makeText(FeedBackUI.this, "提交失败了", Toast.LENGTH_LONG).show();
		else
			Toast.makeText(FeedBackUI.this, "提交成功,感谢您的支持!", Toast.LENGTH_LONG)
					.show();
		return true;
	}
}
