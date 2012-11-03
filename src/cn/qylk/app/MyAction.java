package cn.qylk.app;

/**
 * @author qylk2012
 * 
 */
public interface MyAction {

	/**
	 * 控制按钮意图
	 */
	public static final String INTENT_BTNACTION = "android.intent.qylk.btn";
	/**
	 * 程序退出通知
	 */
	public static final String INTENT_EXIT = "android.intent.qylk.exit";
	/**
	 * 列表改变通知
	 */
	public static final String INTENT_LISTCHANGED = "android.intent.qylk.regetlist";
	/**
	 * 歌曲进度
	 */
	public static final String INTENT_POSITION = "android.intent.qylk.position";
	/**
	 * 启动Service
	 */
	public static final String INTENT_START_SERVICE = "cn.qylk.audio.Service.Start";
	/**
	 * 歌曲播放状态
	 */
	public static final String INTENT_STATUS = "android.intent.qylk.statuschanged";
	/**
	 * 后台歌曲服务控制意图
	 */
	public static final String INTENT_UI_UPDATE = "android.intent.qylk.ui";
	/**
	 * 两个widget按钮产生的广播
	 */
	public static final String INTENT_WIDGET_CONTROL = "cn.qylk.audio.widget.control";
}
