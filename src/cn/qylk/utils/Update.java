package cn.qylk.utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import cn.qylk.R;
import cn.qylk.UpdateApkWindow;

public class Update {

	static final byte NEED = 1, NONEED = 0, FAIL = -1;
	/**
	 * 升级信息获取地址
	 */
	public static final String UPDATECHECKURL = "http://qylk2011.googlecode.com/files/version";

	private Thread CheckUpdateThread = new Thread(new Runnable() {
		public void run() {
			ExameVerCode();
		}
	});

	private byte code;

	private Context context;
	private Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			DisplayMsg();
			super.dispatchMessage(msg);
		}
	};
	private String[] newversion;

	public Update(Context context) {
		this.context = context;
	}

	/**
	 * 处理检查结果
	 */
	public void DisplayMsg() {
		if (code == NONEED)
			Toast.makeText(context, "NO NEED UPDATE", Toast.LENGTH_LONG).show();
		else if (code == FAIL)
			Toast.makeText(context, R.string.networkfail, Toast.LENGTH_LONG)
					.show();
		else
			context.startActivity(new Intent(context, UpdateApkWindow.class)
					.putExtra("ver", newversion[0]).putExtra("info",
							newversion[1]));
	}

	/**
	 * 获取最新版本号，检查是否需要更新
	 */
	private void ExameVerCode() {
		newversion = GetNewVersion();
		if (newversion != null) {
			String localversion = context.getResources().getString(
					R.string.version);
			if (!newversion[0].equals(localversion))
				code = NEED;// 需更新
			else
				code = NONEED;// 不需要更新
		} else
			code = FAIL;// 无法更新
		handler.obtainMessage().sendToTarget();
	}

	/**
	 * 获取服务器端新版本版本号和版本信息
	 * 
	 */
	public String[] GetNewVersion() {
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
	public String[] ParseVersion(String jsonstr) throws JSONException {
		JSONObject jsonobj = new JSONObject(jsonstr);
		JSONObject version = (JSONObject) jsonobj.get("version");
		String ver = (String) version.get("ver");
		String info = (String) version.get("info");
		return new String[] { ver, info };
	}

	public void start() {
		Toast.makeText(context, "CHECKING...", Toast.LENGTH_SHORT).show();
		CheckUpdateThread.start();
	}
}
