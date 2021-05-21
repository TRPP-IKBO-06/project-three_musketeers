package main.stager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import main.stager.R;

public class SettingsWrapper {
    private SharedPreferences pref;

    public final String AUTO_TUNE;
    public final String LOCALE;
    public final String THEME;

    public SettingsWrapper(Context c) {
        pref = PreferenceManager.getDefaultSharedPreferences(c.getApplicationContext());
        AUTO_TUNE = c.getString(R.string.Settings__AutoTune);
        LOCALE = c.getString(R.string.Settings__Locale);
        THEME = c.getString(R.string.Settings__Theme);
    }

    public boolean isAutoTune(boolean unset) {
        return pref.getBoolean(AUTO_TUNE, unset);
    }

    public boolean isDarkTheme(boolean unset) {
        return pref.getBoolean(THEME, unset);
    }

    public String getLocale(String unset) {
        return pref.getString(LOCALE, unset);
    }
}
