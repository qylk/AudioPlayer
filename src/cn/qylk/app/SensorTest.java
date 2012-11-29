package cn.qylk.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import cn.qylk.utils.SendAction;
import cn.qylk.utils.SendAction.ServiceControl;

public class SensorTest implements Callback {
	private class SensorListenerImpl implements SensorEventListener{// 监听类

		@Override
		public void onAccuracyChanged(android.hardware.Sensor sensor,
				int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			int sensortype = event.sensor.getType();
			float[] values = event.values;
			if (sensortype == Sensor.TYPE_ACCELEROMETER) {
				long curTime = System.currentTimeMillis();
				// 测100毫秒 速度
				if ((curTime - lastUpdate) > 50) {
					long diffTime = (curTime - lastUpdate);
					x = values[0];
					y = values[1];
					z = values[2];
					float speed = Math
							.abs(x + y + z - last_x - last_y - last_z)
							/ diffTime * 10000;
					if (speed > SHAKE_THRESHOLD) {
						// 检测到摇晃后执行的代码
						mHandler.removeMessages(0);
						mHandler.sendEmptyMessageDelayed(0, 300);
					}
					last_x = x;
					last_y = y;
					last_z = z;
					lastUpdate = curTime;
				}
			}
		}
	}

	private static SensorTest sensor = new SensorTest();
	private static final int SHAKE_THRESHOLD = 5000; // 越小越灵敏

	public static SensorTest getInstance() {
		return sensor;
	}

	private Context context = APP.getInstance();
	private long lastUpdate;
	private Handler mHandler = new Handler(this);
	private SensorManager mSensorManager;
	private SensorListenerImpl SensorListenerImpl;
	private boolean started;
	private float x, y, z, last_x, last_y, last_z;

	public SensorTest() {
		SensorListenerImpl = new SensorListenerImpl();
	}

	@Override
	public boolean handleMessage(Message msg) {
		SendAction.SendControlMsg(ServiceControl.NEXT);// 换下一首歌
		return true;
	}

	public void StartService() {// 启动服务
		if (started)
			return;
		mSensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		Sensor sensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(SensorListenerImpl, sensor,
				SensorManager.SENSOR_DELAY_GAME);
		started = true;
	}

	public void StopService() {// 停止服务
		if (started) {
			mSensorManager.unregisterListener(SensorListenerImpl);
			started = false;
		}
	}
}
