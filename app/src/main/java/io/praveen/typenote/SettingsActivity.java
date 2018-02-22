package io.praveen.typenote;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.MenuItem;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SettingsActivity extends AppCompatPreferenceActivity{

    static boolean b;
    static SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Typeface font2 = Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf");
        SpannableStringBuilder SS = new SpannableStringBuilder("Settings");
        SS.setSpan (new CustomTypefaceSpan("", font2), 0, SS.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(SS);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/whitney.ttf").setFontAttrId(R.attr.fontPath).build());
        preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
    }

    public static class MainPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            bindPreferenceSummaryToValue(findPreference("notification"));
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(SettingsActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceClickListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceClick(preference);
    }

    private static Preference.OnPreferenceClickListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceClickListener() {
        @SuppressLint("ApplySharedPref")
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference instanceof SwitchPreference) {
                if (preference.getKey().equals("notification")) {
                    b = ((SwitchPreference) preference).isChecked();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove("shortcut");
                    editor.apply();
                    if (b) {
                        editor.putBoolean("shortcut", false);
                    } else{
                        editor.putBoolean("shortcut", true);
                    }
                    editor.apply();
                }
            }
            return true;
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}