package org.yuttadhammo.buddhisttexts;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.InputType;
import android.view.MenuItem;


public class TextsSettingsActivity extends PreferenceActivity {
	
	private Context context;
	private SharedPreferences prefs;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		this.context = getApplicationContext();
		addPreferencesFromResource(R.xml.preferences);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		final EditTextPreference dirPref = (EditTextPreference)findPreference("archive_dir");
		if(dirPref.getText() == null || dirPref.getText().equals(""))
			dirPref.setText(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "BuddhistTexts");
		dirPref.setSummary(dirPref.getText());
		dirPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference,
					final Object newValue) {
				String ndir = (String) newValue;
					
				dirPref.setSummary(ndir);

				return true;
			}
			
		});
				
		int api = Build.VERSION.SDK_INT;	
		
		if (api >= 14) {
			getActionBar().setHomeButtonEnabled(true);
		}		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		super.onOptionsItemSelected(item);
		
		switch (item.getItemId()) {
	        case android.R.id.home:
	            // app icon in action bar clicked; go home
	            finish();
	            return true;

			default:
				return false;
	    }
	}	

}
