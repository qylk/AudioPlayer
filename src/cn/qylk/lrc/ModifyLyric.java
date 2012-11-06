package cn.qylk.lrc;

import cn.qylk.myview.LrcPackage;
import cn.qylk.utils.FileHelper;

/**
 * 修改歌词
 * 
 * @author qylk2012 <BR>
 *         all rights resolved
 * 
 */
public class ModifyLyric {
	private int offset = 0;

	/**
	 * 将lrclist重新生成Lrc歌词格式
	 */
	private StringBuilder lrcParse(LrcPackage pac) {
		int sum = pac.list.size();// 条数
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < sum; i++) {// 逐条处理
			int begintime = pac.list.get(i).beginTime + offset;
			if (begintime < 0)
				continue;
			int min = begintime / 60000;// 分
			int sec = (begintime % 60000) / 1000;// 秒
			int ms = (begintime % 1000) / 10;// 毫秒(百进制)
			// 时间标签
			sb.append('[').append('0').append(Integer.toString(min))
					.append(':').append(Integer.toString(sec)).append('.')
					.append(Integer.toString(ms)).append(']')
					.append(pac.list.get(i).lrcBody).append("\n");// 歌词
		}
		return sb;
	}

	/**
	 * 开始修改并写回SD卡
	 */
	public void ModifyandSave(LrcPackage pac) {
		new FileHelper().WriteFile(lrcParse(pac).toString(), pac.path);
	}

	/**
	 * 设置偏移量
	 * 
	 * @param offset
	 *            可正可负（ms）
	 */
	public ModifyLyric SetOffset(int offset) {
		this.offset = offset;
		return this;
	}
}