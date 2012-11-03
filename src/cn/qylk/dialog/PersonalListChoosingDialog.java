package cn.qylk.dialog;

import cn.qylk.database.MediaDatabase;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class PersonalListChoosingDialog extends AlertDialog implements
		DialogInterface.OnClickListener {
	private Context context;
	private Integer[] ids;
	private CharSequence[] list;

	public PersonalListChoosingDialog(Context context) {
		super(context);
		this.context = context;
	}

	public Builder Build(Integer[] ids) {
		this.ids = ids;
		list = MediaDatabase.GetPersonalListUsingList(); // 读取所有列表
		list[list.length - 1] = "[NEW]";
		Builder builder = new Builder(context);
		builder.setTitle("Choose").setItems(list, this);
		return builder;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == list.length - 1) {// 选择了新建列表
			new NewPersonalListDialog(context, ids).Build().show();
		} else {// 选择了已存在列表
			MediaDatabase.InsertMoreToPersonalList(
					// 添加到数据库
					MediaDatabase.IsPersonalListExist(list[which].toString()),
					ids);
		}
	}
}
