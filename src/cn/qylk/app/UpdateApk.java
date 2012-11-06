package cn.qylk.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class UpdateApk {
	private final static String apkname = "qylk.apk";
	/**
	 * apk升级地址
	 */
	private static final String APKURL = "http://qylk2011.googlecode.com/files/qplayer18.apk";
	private Context context;
	private Handler handler;
	private boolean kill;
	private ProgressDialog pbar;
	private int percent;

	public UpdateApk(Context context) {
		this.context = context;
	}

	/**
	 * 下载apk包
	 * 
	 * @param url
	 * @throws IOException
	 */
	public void DownLoad(String url) throws IOException {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		long length = entity.getContentLength();
		InputStream is = entity.getContent();
		FileOutputStream fileOutputStream = null;
		if (is != null) {
			File file = new File(Environment.getExternalStorageDirectory(),
					apkname);
			fileOutputStream = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int ch = -1;
			int count = 0;
			while ((ch = is.read(buf)) != -1) {
				fileOutputStream.write(buf, 0, ch);
				count += ch;
				percent = (int) (count * 100 / length);
				if (percent % 5 == 0)
					handler.obtainMessage().sendToTarget();
				if (kill)
					break;
			}
			fileOutputStream.flush();
			fileOutputStream.close();
			buf = null;
			install();
		}
	}

	public UpdateApk init() {
		pbar = new ProgressDialog(context);
		pbar.setTitle("update");
		pbar.setMessage("waiting for apk...");
		pbar.setMax(100);
		pbar.setCancelable(false);
		pbar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pbar.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						kill = true;
					}
				});
		pbar.show();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				pbar.setProgress(percent);
				super.handleMessage(msg);
			}
		};
		return this;
	}

	/**
	 * 安装apk
	 */
	public void install() {
		if (kill)
			return;
		pbar.dismiss();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), apkname)),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 下载线程
	 * 
	 * @param url
	 */
	public void update() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					DownLoad(APKURL);
				} catch (Exception e) {
				}
			}
		}).start();
	}
}
