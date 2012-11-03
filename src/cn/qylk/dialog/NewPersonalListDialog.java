package cn.qylk.dialog;

import cn.qylk.R;
import cn.qylk.database.MediaDatabase;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public class NewPersonalListDialog extends AlertDialog {
	private Context context;
	private Integer[] ids;

	public NewPersonalListDialog(Context context) {
		this(context, null);
	}

	public NewPersonalListDialog(Context context, Integer[] ids) {
		super(context);
		this.ids = ids;
		this.context = context;
	}

	public Builder Build() {
		final EditText inputbox = new EditText(context);
		inputbox.setSingleLine(true);
		inputbox.setText("NEW LIST");
		Builder builder = new Builder(context);
		builder.setTitle(R.string.newlist).setView(inputbox)
				.setPositiveButton(R.string.build, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String name = inputbox.getText().toString();
						if (name.length() == 0)
							return;
						if (ids != null)
							MediaDatabase.InsertMoreToPersonalList(
									MediaDatabase.NewPersonalListIDS(name), ids);
					}
				}).setNegativeButton(R.string.operation_cancel, null);

		return builder;
	}

}
