package be.patricegautot.ncrypt.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import be.patricegautot.ncrypt.R;
import be.patricegautot.ncrypt.helpers.SharedPreferencesHelper;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {


            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class ThisPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceToValue(orderBy);

        }

        private void bindPreferenceToValue(Preference pref) {
            pref.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(pref.getContext());

            String preferenceString = preferences.getString(pref.getKey(), "");
            onPreferenceChange(pref, preferenceString);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            String stringValue = o.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else if (preference instanceof SwitchPreference){

            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    }
}
