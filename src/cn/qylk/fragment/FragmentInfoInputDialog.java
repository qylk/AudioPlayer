package cn.qylk.fragment;

import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import cn.qylk.MainActivity;
import cn.qylk.R;
import cn.qylk.QianQianLyrics.LyricResults;
import cn.qylk.adapter.QianQianAdapter;
import cn.qylk.app.Tasks;
import cn.qylk.app.Tasks.onPostLrcItems;
import cn.qylk.app.TrackInfo;
import cn.qylk.database.MediaDatabase;
import cn.qylk.utils.ID3;

public class FragmentInfoInputDialog extends DialogFragment implements
		OnClickListener, onPostLrcItems, OnCheckedChangeListener {
	private String dialogtitle;
	private TrackInfo info;
	private ListView lrclist;
	private Button positive, negitive, readtag1;
	private CheckBox tagflag;
	private EditText title, artist, album;
	private ProgressBar waitingbar;

	public FragmentInfoInputDialog(TrackInfo info, String title) {
		this.dialogtitle = title;
		this.info = info;
		setStyle(DialogFragment.STYLE_NORMAL,
				android.R.style.Theme_Holo_DialogWhenLarge);
	}

	private void getText() {
		info.title = title.getText().toString();
		info.artist = artist.getText().toString();
		info.album = album.getText().toString();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		((MainActivity) getActivity()).Service.setRWFlag(isChecked);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.positivebtn:
			negitive.setEnabled(false);
			waitingbar.setVisibility(View.VISIBLE);
			InputMethodManager imm = (InputMethodManager) getActivity()
					.getSystemService(Activity.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(positive.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			getDialog().setCancelable(false);
			getText();
			if (tagflag.isChecked())
				MediaDatabase.updateTrackInfo(info);
			Tasks.startLrcSearchTask(this);
			break;
		case R.id.negitivebtn:
			this.dismiss();
			break;
		case R.id.additionbtn:
			String[] ID3 = new String[3];
			boolean suc = new ID3().getID3v1(info.path, ID3);
			if (suc) {
				title.setText(ID3[0]);
				artist.setText(ID3[1]);
				album.setText(ID3[2]);
			} else
				Toast.makeText(getActivity(), "找不到ID3v1", Toast.LENGTH_SHORT)
						.show();
			break;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle(dialogtitle);
		View dialog = inflater.inflate(R.layout.infodialog, container, false);
		title = (EditText) dialog.findViewById(R.id.et_title);
		title.setText(info.title);
		lrclist = (ListView) dialog.findViewById(R.id.lrclist);
		artist = (EditText) dialog.findViewById(R.id.et_artist);
		artist.setText(info.artist);
		album = (EditText) dialog.findViewById(R.id.et_album);
		album.setText(info.album);
		readtag1 = (Button) dialog.findViewById(R.id.additionbtn);
		readtag1.setOnClickListener(this);
		tagflag = (CheckBox) dialog.findViewById(R.id.tagoption);
		tagflag.setOnCheckedChangeListener(this);
		positive = (Button) dialog.findViewById(R.id.positivebtn);
		negitive = (Button) dialog.findViewById(R.id.negitivebtn);
		waitingbar = (ProgressBar) dialog.findViewById(R.id.waitingbar);
		positive.setOnClickListener(this);
		negitive.setOnClickListener(this);
		return dialog;
	}

	@Override
	public void onLrcSearchDone(List<LyricResults> items) {
		waitingbar.setVisibility(View.GONE);
		negitive.setEnabled(true);
		setCancelable(true);
		if (items != null) {
			lrclist.setAdapter(new QianQianAdapter(items, getActivity()));
			lrclist.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					LyricResults selected = ((LyricResults) parent
							.getItemAtPosition(position));
					info.title = selected.track;
					info.artist = selected.artist;
					MediaDatabase.updateTrackInfo(info);
					((MainActivity) getActivity()).StartLoad(selected.id);
					FragmentInfoInputDialog.this.dismiss();
				}
			});
		} else
			Toast.makeText(getActivity(), "没有找到歌词", Toast.LENGTH_SHORT).show();
	}
}
