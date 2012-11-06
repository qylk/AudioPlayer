package cn.qylk.app;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.preference.PreferenceManager;
import android.widget.Toast;
import cn.qylk.R;
import cn.qylk.UpdateApkWindow;
import cn.qylk.utils.WebUtils;

/**
 * 检查更新
 * 
 * @author qylk2012<br>
 * 
 */
public class Update implements Callback {

	static final byte NEED = 1, NONEED = 0, FAIL = -1;
	/**
	 * 升级信息获取地址
	 */
	public static final String UPDATECHECKURL = "https://raw.github.com/qylk/AudioPlayer/master/version";
	private Thread CheckUpdateThread = new Thread(new Runnable() {

		@Override
		public void run() {
			ExameVerCode();
		}
	});
	private Handler handler = new Handler(this);
	private byte code;
	private String info, ver, localver;

	/**
	 * 处理检查结果
	 */
	public void DisplayMsg() {
		if (code == NONEED) {
			Toast.makeText(APP.getInstance(), R.string.noupdate,
					Toast.LENGTH_LONG).show();
			SharedPreferences mPerferences = PreferenceManager
					.getDefaultSharedPreferences(APP.getInstance());
			Editor editor = mPerferences.edit();
			editor.putLong("lastcheck", System.currentTimeMillis());
			editor.commit();
		} else if (code == FAIL)
			Toast.makeText(APP.getInstance(), R.string.networkfail,
					Toast.LENGTH_LONG).show();
		else
			APP.getInstance().startActivity(
					new Intent(APP.getInstance(), UpdateApkWindow.class)
							.putExtra("ver", ver).putExtra("info", info)
							.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}

	/**
	 * 获取最新版本号，检查是否需要更新
	 */
	private void ExameVerCode() {
		ver = GetNewVersion();
		if (ver != null) {
			if (!ver.equals(localver))
				code = NEED;// 需更新
			else
				code = NONEED;// 不需要更新
		} else
			code = FAIL;// 无法更新
		handler.sendEmptyMessage(0);
	}

	/**
	 * 获取服务器端新版本版本号和版本信息
	 * 
	 */
	public String GetNewVersion() {
		try {
			return ParseVersion(WebUtils.GetContent(UPDATECHECKURL, "GB2312"));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 解析新版本
	 * 
	 * @param jsonstr
	 * @return
	 * @throws JSONException
	 */
	public String ParseVersion(String jsonstr) throws JSONException {
		JSONObject jsonobj = new JSONObject(jsonstr);
		JSONObject version = (JSONObject) jsonobj.get("version");
		String ver = (String) version.get("ver");
		this.info = (String) version.get("info");
		return ver;
	}

	/**
	 * start a thread
	 * @param context
	 */
	public void start(Context context) {
		localver = context.getResources().getString(R.string.version);
		CheckUpdateThread.start();
	}

	@Override
	public boolean handleMessage(Message msg) {
		DisplayMsg();
		return true;
	}
}
