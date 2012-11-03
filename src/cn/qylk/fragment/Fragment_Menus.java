package cn.qylk.fragment;

import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import cn.qylk.MainActivity;
import cn.qylk.R;
import cn.qylk.adapter.MenuGridViewAdapter;
import cn.qylk.app.APPUtils;
import cn.qylk.app.SleepTimer;
import cn.qylk.utils.SendAction;

public class Fragment_Menus extends Fragment implements OnItemClickListener {

	private void dtimer() {
		final EditText view = new EditText(getActivity());
		view.setText(R.string.deault_time);
		view.setSingleLine(true);
		view.setInputType(InputType.TYPE_CLASS_NUMBER);
		new AlertDialog.Builder(getActivity())
				.setTitle(R.string.input_tips)
				.setView(view)
				.setPositiveButton(R.string.start,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String timertime = view.getText().toString();
								if (timertime.length() == 0
										|| timertime.length() > 3)
									return;// 无效时间
								int sleeptime = Integer.valueOf(timertime);// 取得时间
								SleepTimer.getInstance().newTimer(
										sleeptime * 60000 + 5000,
										new TimerTask() {
											@Override
											public void run() {
												SendAction.SendExitToUI();
											}
										});
							}
						}).setNegativeButton(R.string.operation_cancel, null)
				.show();
	}

	private void InitView(View root) {
		// TODO Auto-generated method stub

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		GridView menu = (GridView) inflater.inflate(R.layout.menu, null);
		menu.setAdapter(new MenuGridViewAdapter(inflater));
		menu.setOnItemClickListener(this);
		InitView(menu);
		return menu;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		getFragmentManager().popBackStack();
		switch (position) {
		case 0:// 设置
			((MainActivity) getActivity()).openPreference();
			break;
		case 1:// 定时
			dtimer();// 设置对话框显示及定时器开启
			break;
		case 2:// 歌曲搜索
			((MainActivity) getActivity()).SearchLrc();
			break;
		case 3:// 调整歌词
			((MainActivity) getActivity()).showLrcAdjustbtns();
			break;
		case 4:// 删除歌词
			((MainActivity) getActivity()).delLyric();
			break;
		case 5:// 扫描SD卡
			APPUtils.ScanSD(true);
			break;
		default:
			break;
		}
		
	}
}
