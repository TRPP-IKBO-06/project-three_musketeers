package main.stager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import java.util.HashSet;
import java.util.Set;
import main.stager.utils.LocaleController;
import main.stager.utils.SettingsWrapper;
import main.stager.utils.ThemeController;

public class SettingsFragment extends PreferenceFragmentCompat
implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final SettingsWrapper S;
    private final Set<String> requireRelaunchActivity;
    private final Set<String> requireRestartApp;

    private SwitchPreferenceCompat P_THEME;
    private ListPreference P_LOCALE;
    private SwitchPreferenceCompat P_AUTO_TUNE;
    private SwitchPreferenceCompat P_HIDE_EMAIL;

    private void initPrefRefs() {
        P_AUTO_TUNE = findPreference(S.AUTO_TUNE);
        P_HIDE_EMAIL = findPreference(S.HIDE_EMAIL);
        P_LOCALE = findPreference(S.LOCALE);
        P_THEME = findPreference(S.THEME);
    }

    public SettingsFragment() {
        super();
        S = StagerApplication.getSettings();
        requireRelaunchActivity = new HashSet<String>() {{
           add(S.THEME);
           add(S.LOCALE);
        }};

        requireRestartApp = new HashSet<String>() {{
           add(S.AUTO_TUNE);
        }};
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        initPrefRefs();
        autoTune();

        // Добавляем реакции на изменение различных опций
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);
        setPreferenceChangeListeners();
    }

    private void setPreferenceChangeListeners() {
        P_HIDE_EMAIL.setOnPreferenceChangeListener((preference, newValue) -> {
            if ((Boolean) newValue)
                StagerApplication.getDataProvider().deleteEmail();
            else
                StagerApplication.getDataProvider().saveEmail();
            return true;
        });
    }

    private void autoTune() {
        if (!S.isAutoTune(true)) return;
        P_THEME.setChecked(ThemeController.isDarkByDefault(getContext()));
        P_LOCALE.setValue(LocaleController.getDefaultLocale(getContext()));
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        try {
            boolean needRestartApp = requireRestartApp.contains(key);
            boolean needReload = requireRelaunchActivity.contains(key) || needRestartApp;

            if (!needReload)
                return;

            prefs.edit().commit(); // Гарантирия сохранности
            if (needRestartApp)
                StagerApplication.restart(getActivity());
            else
                getActivity().recreate();

        } catch (Throwable ignore) {}
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_settings).setVisible(false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
}