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

import main.stager.utils.LocaleController;
import main.stager.utils.SettingsWrapper;
import main.stager.utils.ThemeController;

public class SettingsFragment extends PreferenceFragmentCompat
implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final SettingsWrapper S;

    public SettingsFragment() {
        super();
        S = StagerApplication.getSettings();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        autoTune();

        // Добавляем реакции на изменение различных опций
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);
    }

    private void autoTune() {
        if (!PreferenceManager.getDefaultSharedPreferences(getContext())
                .getBoolean(S.AUTO_TUNE, true)) return;

        SwitchPreferenceCompat theme = findPreference(S.THEME);
        theme.setChecked(ThemeController.isDarkByDefault(getContext()));

        ListPreference language = findPreference(S.LOCALE);
        language.setValue(LocaleController.getDefaultLocale(getContext()));
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        // Смена темы или языка
        try {
            if (S.THEME.equals(key) ||
                S.AUTO_TUNE.equals(key) ||
                S.LOCALE.equals(key)) {

                prefs.edit().commit(); // Гарантирия сохранности
                if (S.AUTO_TUNE.equals(key))
                    StagerApplication.restart(getActivity());
                else
                    getActivity().recreate();
            }
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