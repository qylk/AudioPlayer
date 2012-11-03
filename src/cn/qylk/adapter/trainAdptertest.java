package cn.qylk.adapter;


import cn.qylk.R;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * edittext自动完成适配器
 * 
 * @author qylk2011
 * 
 */
public class trainAdptertest extends CursorAdapter {
	private Context context;

	@SuppressWarnings("deprecation")
	public trainAdptertest(Context context, Cursor c) {
		super(context, c);
		this.context = context;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		((TextView) view.findViewById(R.id.itemdis1)).setText(cursor
				.getString(2));
		((TextView) view.findViewById(R.id.itemdis2)).setText(cursor
				.getString(1));
	}

	@Override
	public String convertToString(Cursor cursor) {
		return cursor.getString(2);
	}

	@Override
	public long getItemId(int position) {
		return super.getItemId(position);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return LayoutInflater.from(context).inflate(R.layout.dropdown_text, parent,
				false);
	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		if (constraint == null)
			return null;
		ContentResolver resolver = this.context.getContentResolver();
		String selection = "title like '%" + constraint
				+ "%'  or artist like '%" + constraint + "%' ";// 查询标题
		return resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media._ID,
						MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.TITLE }, selection, null, null);
	}
}