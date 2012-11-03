package cn.qylk.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**带TextView的EditText
 * @author qylk2012 <br>
 *         all rights reserved
 * 
 */
public class EditTextView extends LinearLayout {
	private CharSequence Text = "text";

	public EditTextView(Context context) {
		this(context, null);
	}

	public EditTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		int resouceId = -1;
		if (attrs != null)
			resouceId = attrs.getAttributeResourceValue(null, "Text", 0);
		if (resouceId > 0)
			Text = context.getResources().getText(resouceId).toString();
		CreatView(context);
	}

	private void CreatView(Context context) {
		TextView tv = new TextView(context);
		EditText et = new EditText(context);
		this.setOrientation(LinearLayout.VERTICAL);
		tv.setText(Text);
		addView(tv);
		addView(et, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
	}

	public String getEditText() {
		return ((EditText) getChildAt(1)).getText().toString();
	}

	public String getTextView() {
		return ((TextView) getChildAt(0)).getText().toString();
	}

	public void setEditText(CharSequence text) {
		((EditText) getChildAt(1)).setText(text);
	}

	public void setEditText(int textres) {
		((EditText) getChildAt(1)).setText(getContext().getResources().getText(
				textres));
	}

	public void setTextSize(float size) {
		((TextView) getChildAt(0)).setTextSize(size);
	}

	public void setTextView(CharSequence text) {
		((TextView) getChildAt(0)).setText(text);
	}

	public void setTextView(int textres) {
		((TextView) getChildAt(0)).setText(getContext().getResources().getText(
				textres));
	}
}