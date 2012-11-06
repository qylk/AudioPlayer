package cn.qylk.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import cn.qylk.utils.SendAction;
import cn.qylk.utils.SendAction.ServiceControl;

public class MediaBtnReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_MEDIA_BUTTON)) {
			KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
			if (event == null || event.getAction() != KeyEvent.ACTION_UP)
				return;
			abortBroadcast();
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_HEADSETHOOK:
			case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
				SendAction.SendControlMsg(ServiceControl.PAUSE_CONTINE);
				break;
			case KeyEvent.KEYCODE_MEDIA_NEXT:
				SendAction.SendControlMsg(ServiceControl.NEXT);
				break;
			case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
				SendAction.SendControlMsg(ServiceControl.PRE);
				break;
			}
		}
	}
}
