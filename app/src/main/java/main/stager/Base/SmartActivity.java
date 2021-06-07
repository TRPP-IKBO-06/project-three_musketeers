package main.stager.Base;


import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    protected void setFocusAtInput(EditText editText) {
        if (editText == null) return;
        editText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager)
                      getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        editText.postDelayed(() ->
            inputMethodManager.showSoftInput(editText,
                    InputMethodManager.SHOW_IMPLICIT), 1000);
    }
}