package org.yuttadhammo.buddhisttexts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.InputType;
import android.view.MenuItem;


public class TextsSettingsActivity extends PreferenceActivity {
	
	private Context context;
	private TextsSettingsActivity activity;
	private SharedPreferences prefs;
	private Preference apiPref;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		this.context = getApplicationContext();
		this.activity = this;
		addPreferencesFromResource(R.xml.preferences);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		final EditTextPreference notesPref = (EditTextPreference)findPreference("notes");
		if(notesPref.getText() == null || notesPref.getText().equals(""))
			notesPref.setText("5");
		notesPref.setSummary(notesPref.getText());
		notesPref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		notesPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference,
					final Object newValue) {
				String notes = (String) newValue;
					
				notesPref.setSummary(notes);

				return true;
			}
			
		});
		
		final EditTextPreference titlePref = (EditTextPreference)findPreference("title");
		if(titlePref.getText() == null || titlePref.getText().equals(""))
			titlePref.setText("8");
		titlePref.setSummary(titlePref.getText());
		titlePref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		titlePref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference,
					final Object newValue) {
				String title = (String) newValue;
					
				titlePref.setSummary(title);

				return true;
			}
			
		});
				
		@SuppressWarnings("deprecation")
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
