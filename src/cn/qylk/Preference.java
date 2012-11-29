package cn.qylk;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Preference extends PreferenceActivity implements
		SharedPreferences.OnSharedPreferenceChangeListener {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_Preference);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.options);
		PreferenceManager.getDefaultSharedPreferences(this)
				.registerOnSharedPreferenceChangeListener(this);
		setResult(0);
	}

	@Override
	protected void onDestroy() {
		PreferenceManager.getDefaultSharedPreferences(this)
				.unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		setResult(1);
	}
}
