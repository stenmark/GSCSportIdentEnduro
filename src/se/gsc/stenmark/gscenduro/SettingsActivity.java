package se.gsc.stenmark.gscenduro;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try{
			getPreferenceManager().setSharedPreferencesName("GSC_ENDURO_PREFERENCES");
			addPreferencesFromResource(R.xml.settings);
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}

	@Override
	protected void onResume(){
		super.onResume();
		try{
			getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		try{
			getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}  

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		try{
			Preference pref = findPreference(key);

			if (pref instanceof ListPreference) {
				ListPreference listPref = (ListPreference) pref;
				pref.setSummary(listPref.getEntry());
				return;
			}
		}
		catch( Exception e1){
			MainActivity.generateErrorMessage(e1);
		}
	}      
}