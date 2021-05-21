package main.stager.Base;


import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import main.stager.R;
import main.stager.utils.LocaleController;
import main.stager.utils.ThemeController;


/**
 * Activity, но с изменяемыми ресурсами темы и локали
 */
public abstract class SmartActivity extends AppCompatActivity {
    static protected String SHOW_ACTION_BAR = "Stager.SmartActivity.ShowActionBar";
    protected FrameLayout progressBarHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeController.restoreTheme(this, savedInstanceState == null
                || savedInstanceState.getBoolean(SHOW_ACTION_BAR, true));
        LocaleController.restoreLocale(this);
        super.onCreate(savedInstanceState);
    }

    public void showLoadingScreen() {
        progressBarHolder = findViewById(R.id.loading_overlay);
        progressBarHolder.setVisibility(View.VISIBLE);
    }

    public void hideLoadingScreen() {
        progressBarHolder = findViewById(R.id.loading_overlay);
        progressBarHolder.setVisibility(View.GONE);
    }
}